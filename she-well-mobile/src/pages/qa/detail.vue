<template>
  <view class="detail-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="back-btn" @click="goBack">←</text>
        <view class="header-center">
          <text class="header-icon">💬</text>
          <text class="header-title">问答详情</text>
        </view>
        <view class="header-right"></view>
      </view>
    </view>

    <!-- 问题 -->
    <view class="question-section" v-if="question.id">
      <view class="q-header">
        <view class="q-category" :class="'cat-' + question.category">{{ question.category || '通用' }}</view>
        <view class="q-status" :class="question.status">{{ question.status === 'resolved' ? '已解决' : '待回答' }}</view>
      </view>
      <view class="q-title">{{ question.title }}</view>
      <view class="q-content">{{ question.content }}</view>
      <view class="q-meta">
        <text>👤 {{ question.user?.nickname || '匿名用户' }}</text>
        <text>👁 {{ question.viewCount || 0 }}</text>
        <text>💬 {{ question.answerCount || 0 }}</text>
      </view>
    </view>

    <!-- AI 回答 -->
    <view class="ai-answer-section" v-if="aiAnswer">
      <view class="ai-badge">🤖 AI 助手</view>
      <view class="ai-text">{{ aiAnswer }}</view>
    </view>

    <!-- 回答列表 -->
    <view class="answers-section">
      <view class="section-title">💬 姐妹回答 ({{ answers.length }})</view>
      <view v-if="!answers.length && !aiAnswer" class="empty-tip">还没有人回答，来做第一个回答者吧！</view>

      <view v-for="a in answers" :key="a.id" class="answer-card" :class="{ accepted: a.isAccepted }">
        <view class="answer-header">
          <view class="answer-author">👤 {{ a.nickname || '姐妹' }}</view>
          <view class="answer-time">{{ formatTime(a.createTime) }}</view>
          <view class="accepted-badge" v-if="a.isAccepted">✓ 已采纳</view>
        </view>
        <view class="answer-content">{{ a.content }}</view>
        <view class="answer-footer">
          <view class="like-btn" @click="likeAnswer(a)" :class="{ liked: a.liked }">
            ❤️ {{ a.likeCount || 0 }}
          </view>
          <view class="accept-btn" v-if="canAccept && !a.isAccepted && !question.acceptedAnswerId" @click="acceptAnswer(a)">
            采纳
          </view>
        </view>
      </view>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 底部输入框 -->
    <view class="input-bar">
      <textarea
        class="answer-input"
        v-model="myAnswer"
        placeholder="写下你的回答..."
        :show-confirm-bar="false"
        adjust-position
      />
      <view class="send-btn" @click="submitAnswer" :class="{disabled: !myAnswer.trim()}">发送</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { qa, ai } from '@/api/index.js'

const questionId = ref(0)
const question = ref({})
const answers = ref([])
const myAnswer = ref('')
const aiAnswer = ref('')
const canAccept = ref(false)

function goBack() { uni.navigateBack() }

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

async function loadDetail() {
  try {
    const res = await qa.detail(questionId.value)
    const data = res?.data || res
    question.value = {
      id: data.id,
      title: data.title,
      content: data.content,
      category: data.category,
      viewCount: data.viewCount,
      answerCount: data.answerCount,
      likeCount: data.likeCount,
      status: data.status,
      acceptedAnswerId: data.acceptedAnswerId,
      user: data.user,
    }

    const ansRes = await qa.answers(questionId.value, { page: 1, size: 50 })
    answers.value = (ansRes?.data?.list || ansRes?.data || []).map(a => ({
      id: a.id,
      questionId: a.questionId,
      content: a.content,
      likeCount: a.likeCount,
      isAccepted: a.isAccepted,
      createTime: a.createTime,
      liked: false,
    }))

    if (data.aiAnswer) aiAnswer.value = data.aiAnswer
  } catch (e) {
    console.error('loadDetail error', e)
  }
}

async function submitAnswer() {
  if (!myAnswer.value.trim()) return
  try {
    await qa.answerCreate({ questionId: questionId.value, content: myAnswer.value.trim() })
    uni.showToast({ title: '回答成功', icon: 'success' })
    myAnswer.value = ''
    await loadDetail()
  } catch (e) {
    uni.showToast({ title: '回答失败', icon: 'none' })
  }
}

async function likeAnswer(a) {
  if (a.liked) return
  try {
    await qa.answerLike(a.id)
    a.likeCount = (a.likeCount || 0) + 1
    a.liked = true
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

async function acceptAnswer(a) {
  try {
    await qa.accept(questionId.value, a.id)
    uni.showToast({ title: '已采纳', icon: 'success' })
    await loadDetail()
  } catch (e) {
    uni.showToast({ title: '采纳失败', icon: 'none' })
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const opts = current.$page?.options || current.options || {}
  questionId.value = Number(opts.id || 0)
  loadDetail()
})
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.detail-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 120rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 180rpx;
  background: $she-gradient-primary;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0; left: 0; right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn { font-size: 44rpx; color: #fff; min-width: 60rpx; }

.header-center {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.header-icon { font-size: 36rpx; }

.header-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}

.header-right { min-width: 60rpx; }

// 问题
.question-section {
  background: $she-white;
  margin: -40rpx 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: $she-shadow-md;
  position: relative;
  z-index: 1;
}

.q-header {
  display: flex;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.q-category {
  font-size: 22rpx;
  padding: 2rpx 10rpx;
  border-radius: 8rpx;
  font-weight: 500;
}

.cat-经期问题 { background: rgba($she-primary, 0.1); color: $she-primary; }
.cat-备孕怀孕 { background: rgba($she-trying, 0.1); color: $she-trying; }
.cat-妇科疾病 { background: rgba($she-danger, 0.1); color: $she-danger; }
.cat-两性健康 { background: rgba(#E91E63, 0.1); color: #E91E63; }
.cat-心理情绪 { background: rgba($she-info, 0.1); color: $she-info; }
.cat-生活养生 { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
.cat-通用 { background: $she-bg; color: $she-sub; }

.q-status {
  font-size: 22rpx;
  padding: 2rpx 10rpx;
  border-radius: 8rpx;
}

.q-status.resolved { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
.q-status.open { background: rgba($she-nursing, 0.1); color: $she-nursing; }

.q-title {
  font-size: 32rpx;
  font-weight: bold;
  color: $she-title;
  margin-bottom: 16rpx;
  line-height: 1.4;
}

.q-content {
  font-size: 28rpx;
  color: $she-sub;
  line-height: 1.7;
  margin-bottom: 20rpx;
}

.q-meta {
  display: flex;
  gap: 16rpx;
  font-size: 24rpx;
  color: $she-muted;
}

// AI回答
.ai-answer-section {
  background: rgba($she-pregnant, 0.08);
  margin: 0 32rpx 24rpx;
  border-radius: 20rpx;
  padding: 24rpx;
  border-left: 6rpx solid $she-pregnant;
}

.ai-badge {
  font-size: 24rpx;
  color: $she-pregnant;
  font-weight: 600;
  margin-bottom: 12rpx;
}

.ai-text {
  font-size: 26rpx;
  color: $she-text;
  line-height: 1.7;
}

// 回答
.answers-section { margin: 0 32rpx; }

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 20rpx;
  color: $she-title;
}

.empty-tip {
  text-align: center;
  color: $she-muted;
  font-size: 26rpx;
  padding: 40rpx 0;
}

.answer-card {
  background: $she-white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: $she-shadow-sm;
  transition: all 0.2s;

  &.accepted {
    border: 2rpx solid $she-pregnant;
    background: rgba($she-pregnant, 0.02);
  }
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 14rpx;
}

.answer-author {
  font-size: 26rpx;
  color: $she-text;
  font-weight: 500;
}

.answer-time {
  font-size: 22rpx;
  color: $she-muted;
  flex: 1;
}

.accepted-badge {
  font-size: 22rpx;
  color: $she-pregnant;
  background: rgba($she-pregnant, 0.1);
  padding: 2rpx 10rpx;
  border-radius: 8rpx;
}

.answer-content {
  font-size: 28rpx;
  color: $she-sub;
  line-height: 1.7;
  margin-bottom: 16rpx;
}

.answer-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.like-btn {
  font-size: 24rpx;
  color: $she-muted;
  padding: 6rpx 14rpx;
  border-radius: 12rpx;
  background: $she-bg;
  transition: all 0.2s;

  &.liked { color: $she-primary; }
  &:active { transform: scale(0.95); }
}

.accept-btn {
  font-size: 24rpx;
  color: $she-pregnant;
  background: rgba($she-pregnant, 0.1);
  padding: 6rpx 14rpx;
  border-radius: 12rpx;

  &:active { opacity: 0.7; }
}

.bottom-placeholder { height: 32rpx; }

// 输入栏
.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $she-white;
  padding: 16rpx 32rpx;
  padding-bottom: 60rpx;
  display: flex;
  gap: 16rpx;
  align-items: flex-end;
  box-shadow: 0 -4rpx 24rpx rgba(0,0,0,0.08);
}

.answer-input {
  flex: 1;
  background: $she-bg;
  border-radius: 40rpx;
  padding: 18rpx 24rpx;
  font-size: 28rpx;
  max-height: 200rpx;
  min-height: 72rpx;
  line-height: 1.5;
}

.send-btn {
  background: $she-gradient-primary;
  color: #fff;
  padding: 18rpx 32rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba($she-primary, 0.3);
  transition: all 0.2s;

  &.disabled { opacity: 0.5; }
  &:active { transform: scale(0.96); }
}
</style>
