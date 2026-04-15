<template>
  <view class="detail-page">
    <view class="header">
      <view class="back-btn" @click="uni.navigateBack()">← 返回</view>
    </view>

    <!-- 问题 -->
    <view class="question-section" v-if="question.id">
      <view class="q-header">
        <view class="q-category">{{ question.category || '通用' }}</view>
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

    <!-- AI 回答（如果有） -->
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

    <!-- 底部输入框 -->
    <view class="input-bar">
      <textarea class="answer-input" v-model="myAnswer" placeholder="写下你的回答..." :show-confirm-bar="false" />
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

    // 加载回答
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

    // 如果有 AI 回答（由后端生成）
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

<style scoped>
.detail-page { min-height: 100vh; background: #faf8fb; padding-bottom: 120rpx }
.header { background: linear-gradient(135deg, #00BCD4, #4DD0E1); padding: 24rpx 32rpx }
.back-btn { color: #fff; font-size: 30rpx }
.question-section { background: #fff; margin: 24rpx 32rpx; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06) }
.q-header { display: flex; gap: 12rpx; margin-bottom: 12rpx }
.q-category { font-size: 22rpx; color: #00BCD4; background: #e0f7fa; padding: 2rpx 10rpx; border-radius: 8rpx }
.q-status { font-size: 22rpx; padding: 2rpx 10rpx; border-radius: 8rpx }
.q-status.resolved { background: #e8f5e9; color: #4CAF50 }
.q-status.open { background: #fff3e0; color: #FF9800 }
.q-title { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 12rpx; line-height: 1.4 }
.q-content { font-size: 28rpx; color: #555; line-height: 1.7; margin-bottom: 16rpx }
.q-meta { display: flex; gap: 16rpx; font-size: 24rpx; color: #999 }
.ai-answer-section { background: #e8f5e9; margin: 0 32rpx 24rpx; border-radius: 16rpx; padding: 20rpx }
.ai-badge { font-size: 24rpx; color: #4CAF50; font-weight: 600; margin-bottom: 8rpx }
.ai-text { font-size: 26rpx; color: #333; line-height: 1.7 }
.answers-section { margin: 0 32rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 24rpx 0 }
.answer-card { background: #fff; border-radius: 16rpx; padding: 20rpx; margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05) }
.answer-card.accepted { border: 2rpx solid #4CAF50 }
.answer-header { display: flex; align-items: center; gap: 12rpx; margin-bottom: 12rpx }
.answer-author { font-size: 26rpx; color: #333; font-weight: 500 }
.answer-time { font-size: 22rpx; color: #999; flex: 1 }
.accepted-badge { font-size: 22rpx; color: #4CAF50; background: #e8f5e9; padding: 2rpx 10rpx; border-radius: 8rpx }
.answer-content { font-size: 28rpx; color: #555; line-height: 1.7; margin-bottom: 12rpx }
.answer-footer { display: flex; justify-content: space-between; align-items: center }
.like-btn { font-size: 24rpx; color: #999; padding: 4rpx 12rpx; border-radius: 12rpx; background: #f5f5f5 }
.like-btn.liked { color: #E91E63 }
.accept-btn { font-size: 24rpx; color: #4CAF50; background: #e8f5e9; padding: 4rpx 12rpx; border-radius: 12rpx }
.input-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 16rpx 32rpx; display: flex; gap: 12rpx; align-items: flex-end; box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.08) }
.answer-input { flex: 1; background: #f5f5f5; border-radius: 20rpx; padding: 16rpx 20rpx; font-size: 28rpx; max-height: 200rpx; min-height: 72rpx }
.send-btn { background: linear-gradient(135deg, #00BCD4, #4DD0E1); color: #fff; padding: 16rpx 28rpx; border-radius: 20rpx; font-size: 28rpx }
.send-btn.disabled { opacity: 0.5 }
</style>
