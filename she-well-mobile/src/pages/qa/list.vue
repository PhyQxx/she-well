<template>
  <view class="qa-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">💬</text>
        <view class="header-text">
          <text class="header-title">姐妹问答</text>
          <text class="header-sub">互相帮助 · 共同成长</text>
        </view>
        <view class="header-ask" @click="showAskDialog = true">
          <text class="ask-icon">✏️</text>
          <text class="ask-text">提问</text>
        </view>
      </view>
    </view>

    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-scroll">
      <view class="category-list">
        <view v-for="c in categories" :key="c.value" class="category-item" :class="{ active: currentCategory === c.value }" @click="switchCategory(c.value)">
          <text class="cat-icon">{{ c.icon }}</text>
          <text class="cat-label">{{ c.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 搜索 -->
    <view class="search-bar">
      <input class="search-input" v-model="keyword" placeholder="搜索问题..." @confirm="loadData" />
      <view class="search-btn" @click="loadData">🔍</view>
    </view>

    <!-- 问题列表 -->
    <view class="question-list">
      <view v-if="loading" class="loading-state">
        <text>加载中...</text>
      </view>
      <view v-else-if="!questions.length" class="she-empty">
        <text class="empty-icon">💬</text>
        <text class="empty-text">暂无问题</text>
        <text class="empty-sub">成为第一个提问的姐妹吧！</text>
      </view>
      <view v-else>
        <view v-for="q in questions" :key="q.id" class="question-card" @click="goDetail(q)">
          <view class="q-header">
            <view class="q-category" :class="'cat-' + q.category">{{ q.category || '通用' }}</view>
            <view class="q-status" :class="q.status">{{ q.status === 'resolved' ? '已解决' : '待回答' }}</view>
          </view>
          <view class="q-title">{{ q.title }}</view>
          <view class="q-content">{{ q.content }}</view>
          <view class="q-footer">
            <view class="q-author">👤 {{ q.user?.nickname || '匿名用户' }}</view>
            <view class="q-stats">
              <text>👁 {{ q.viewCount || 0 }}</text>
              <text>💬 {{ q.answerCount || 0 }}</text>
              <text>❤️ {{ q.likeCount || 0 }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 提问弹窗 -->
    <view class="dialog-overlay" v-if="showAskDialog" @click="showAskDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showAskDialog = false">取消</text>
          <text class="sheet-title">发布问题</text>
          <text class="sheet-confirm" @click="submitQuestion">发布</text>
        </view>
        <view class="sheet-body">
          <view class="type-grid">
            <view v-for="c in categories" :key="c.value" class="type-item" :class="{ selected: askForm.category === c.value }" @click="askForm.category = c.value">
              <text class="type-icon">{{ c.icon }}</text>
              <text class="type-name">{{ c.label }}</text>
            </view>
          </view>
          <input class="sheet-input" v-model="askForm.title" placeholder="问题标题（简明扼要）" maxlength="50" />
          <textarea class="sheet-textarea" v-model="askForm.content" placeholder="详细描述你的问题..." maxlength="500" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { qa } from '@/api/index.js'

const questions = ref([])
const loading = ref(false)
const currentCategory = ref('')
const keyword = ref('')
const showAskDialog = ref(false)
const askForm = ref({ category: '经期问题', title: '', content: '' })

const categories = [
  { value: '', label: '全部', icon: '📋' },
  { value: '经期问题', label: '经期', icon: '🌸' },
  { value: '备孕怀孕', label: '备孕', icon: '🤰' },
  { value: '妇科疾病', label: '妇科', icon: '🏥' },
  { value: '两性健康', label: '两性', icon: '💕' },
  { value: '心理情绪', label: '心理', icon: '🧘' },
  { value: '生活养生', label: '养生', icon: '🌿' },
]

function switchCategory(cat) {
  currentCategory.value = cat
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (currentCategory.value) params.category = currentCategory.value
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await qa.list(params)
    questions.value = (res?.data?.list || res?.data || []).map(q => ({
      id: q.id,
      title: q.title,
      content: q.content,
      category: q.category,
      viewCount: q.viewCount,
      answerCount: q.answerCount,
      likeCount: q.likeCount,
      status: q.status,
      user: q.user,
    }))
  } catch (e) {
    console.error('loadData error', e)
  } finally {
    loading.value = false
  }
}

async function submitQuestion() {
  if (!askForm.value.title.trim()) { uni.showToast({ title: '请输入标题', icon: 'none' }); return }
  if (!askForm.value.content.trim()) { uni.showToast({ title: '请输入内容', icon: 'none' }); return }
  try {
    await qa.create({ title: askForm.value.title, content: askForm.value.content, category: askForm.value.category })
    uni.showToast({ title: '发布成功', icon: 'success' })
    showAskDialog.value = false
    askForm.value = { category: '经期问题', title: '', content: '' }
    loadData()
  } catch (e) { uni.showToast({ title: '发布失败', icon: 'none' }) }
}

function goDetail(q) {
  uni.navigateTo({ url: '/pages/qa/detail?id=' + q.id + '&title=' + encodeURIComponent(q.title) })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.qa-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 220rpx;
  background: $she-gradient-primary;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0; left: 0; right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.header-icon { font-size: 48rpx; }

.header-text {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
}

.header-sub {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
  margin-top: 4rpx;
}

.header-ask {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: rgba(255,255,255,0.25);
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
}

.ask-icon { font-size: 24rpx; }
.ask-text { font-size: 26rpx; color: #fff; }

// 分类
.category-scroll {
  white-space: nowrap;
  padding: 16rpx 24rpx 0;
  background: $she-white;
  margin: -40rpx 32rpx 0;
  border-radius: 16rpx;
  position: relative;
  z-index: 1;
}

.category-list { display: inline-flex; gap: 12rpx; padding-bottom: 16rpx; }

.category-item {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: $she-sub;
  background: $she-bg;
  white-space: nowrap;
  flex-shrink: 0;
  transition: all 0.2s;

  &.active {
    background: $she-gradient-primary;
    color: #fff;
  }
}

.cat-icon { font-size: 24rpx; }
.cat-label { font-size: 26rpx; }

// 搜索
.search-bar {
  display: flex;
  gap: 12rpx;
  padding: 16rpx 32rpx;
  background: $she-white;
}

.search-input {
  flex: 1;
  background: $she-bg;
  border-radius: 40rpx;
  padding: 16rpx 24rpx;
  font-size: 28rpx;
}

.search-btn {
  width: 80rpx;
  height: 80rpx;
  background: $she-gradient-primary;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba($she-primary, 0.3);
}

// 问题列表
.question-list { padding: 16rpx 32rpx; }

.loading-state {
  text-align: center;
  color: $she-muted;
  padding: 80rpx;
}

.question-card {
  background: $she-white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: $she-shadow-sm;
  transition: all 0.2s;

  &:active { transform: scale(0.99); }
}

.q-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
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
  font-size: 30rpx;
  font-weight: 600;
  color: $she-title;
  margin-bottom: 8rpx;
  line-height: 1.4;
}

.q-content {
  font-size: 26rpx;
  color: $she-sub;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12rpx;
}

.q-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.q-author { font-size: 24rpx; color: $she-muted; }
.q-stats { display: flex; gap: 16rpx; font-size: 24rpx; color: $she-muted; }

.bottom-placeholder { height: 48rpx; }

// 弹窗
.dialog-sheet { background: $she-white; border-radius: 32rpx 32rpx 0 0; padding-bottom: env(safe-area-inset-bottom); }

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid $she-border;
}

.sheet-cancel { font-size: 30rpx; color: $she-muted; min-width: 100rpx; }
.sheet-title { font-size: 32rpx; font-weight: 600; color: $she-text; }
.sheet-confirm { font-size: 30rpx; color: $she-primary; min-width: 100rpx; text-align: right; }

.sheet-body { padding: 24rpx; }

.type-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-bottom: 16rpx;
}

.type-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  border-radius: 16rpx;
  font-size: 24rpx;
  background: $she-bg;
  color: $she-sub;
  border: 2rpx solid transparent;
  transition: all 0.2s;

  &.selected {
    border-color: $she-primary;
    background: rgba($she-primary, 0.08);
    color: $she-primary;
  }
}

.type-icon { font-size: 24rpx; }
.type-name { font-size: 24rpx; }

.sheet-input {
  border: 1rpx solid $she-border;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: $she-text;
  margin-bottom: 16rpx;
  width: 100%;
  box-sizing: border-box;
}

.sheet-textarea {
  border: 1rpx solid $she-border;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: $she-text;
  margin-bottom: 16rpx;
  width: 100%;
  height: 180rpx;
  resize: none;
  box-sizing: border-box;
}
</style>
