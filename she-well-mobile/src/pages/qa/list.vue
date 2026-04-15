<template>
  <view class="qa-page">
    <view class="header">
      <view class="header-title">💬 姐妹问答</view>
      <view class="header-ask" @click="showAskDialog = true">提问</view>
    </view>

    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-scroll">
      <view class="category-list">
        <view v-for="c in categories" :key="c.value" class="category-item" :class="{ active: currentCategory === c.value }" @click="switchCategory(c.value)">
          {{ c.icon }} {{ c.label }}
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
      <view v-else-if="!questions.length" class="empty-state">
        <view class="empty-icon">💬</view>
        <view class="empty-text">暂无问题</view>
        <view class="empty-sub">成为第一个提问的姐妹吧！</view>
      </view>
      <view v-else>
        <view v-for="q in questions" :key="q.id" class="question-card" @click="goDetail(q)">
          <view class="q-header">
            <view class="q-category">{{ q.category || '通用' }}</view>
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

    <!-- 提问弹窗 -->
    <view class="dialog-overlay" v-if="showAskDialog" @click="showAskDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">发布问题</view>
        <view class="type-grid">
          <view v-for="c in categories" :key="c.value" class="type-item" :class="{ selected: askForm.category === c.value }" @click="askForm.category = c.value">
            {{ c.icon }} {{ c.label }}
          </view>
        </view>
        <input class="dialog-input" v-model="askForm.title" placeholder="问题标题（简明扼要）" maxlength="50" />
        <textarea class="dialog-textarea" v-model="askForm.content" placeholder="详细描述你的问题..." maxlength="500" />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showAskDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitQuestion">发布</view>
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
  { value: '', label: '全部' },
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

<style scoped>
.qa-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #00BCD4, #4DD0E1); padding: 32rpx; color: #fff; display: flex; justify-content: space-between; align-items: center }
.header-title { font-size: 36rpx; font-weight: bold }
.header-ask { font-size: 28rpx; background: rgba(255,255,255,0.25); padding: 8rpx 20rpx; border-radius: 20rpx }
.category-scroll { white-space: nowrap; padding: 16rpx 24rpx 0; background: #fff }
.category-list { display: inline-flex; gap: 12rpx; padding-bottom: 16rpx }
.category-item { padding: 10rpx 20rpx; border-radius: 20rpx; font-size: 26rpx; color: #666; background: #f5f5f5; white-space: nowrap; flex-shrink: 0 }
.category-item.active { background: linear-gradient(135deg, #00BCD4, #4DD0E1); color: #fff }
.search-bar { display: flex; gap: 12rpx; padding: 16rpx 32rpx; background: #fff }
.search-input { flex: 1; background: #f5f5f5; border-radius: 20rpx; padding: 16rpx 24rpx; font-size: 28rpx }
.search-btn { width: 80rpx; height: 80rpx; background: linear-gradient(135deg, #00BCD4, #4DD0E1); border-radius: 20rpx; display: flex; align-items: center; justify-content: center; font-size: 32rpx }
.question-list { padding: 16rpx 32rpx }
.question-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06) }
.q-header { display: flex; justify-content: space-between; margin-bottom: 12rpx }
.q-category { font-size: 22rpx; color: #00BCD4; background: #e0f7fa; padding: 2rpx 10rpx; border-radius: 8rpx }
.q-status { font-size: 22rpx; padding: 2rpx 10rpx; border-radius: 8rpx }
.q-status.resolved { background: #e8f5e9; color: #4CAF50 }
.q-status.open { background: #fff3e0; color: #FF9800 }
.q-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; line-height: 1.4 }
.q-content { font-size: 26rpx; color: #666; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 12rpx }
.q-footer { display: flex; justify-content: space-between; align-items: center }
.q-author { font-size: 24rpx; color: #999 }
.q-stats { display: flex; gap: 16rpx; font-size: 24rpx; color: #999 }
.loading-state { text-align: center; color: #999; padding: 80rpx }
.empty-state { text-align: center; padding: 80rpx 0 }
.empty-icon { font-size: 80rpx; margin-bottom: 16rpx }
.empty-text { font-size: 32rpx; font-weight: 600; color: #999 }
.empty-sub { font-size: 26rpx; color: #ccc; margin-top: 8rpx }
/* dialog */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center }
.dialog-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 600rpx; max-height: 85vh; overflow-y: auto }
.dialog-title { font-size: 32rpx; font-weight: bold; text-align: center; margin-bottom: 24rpx }
.type-grid { display: flex; flex-wrap: wrap; gap: 10rpx; margin-bottom: 16rpx }
.type-item { padding: 8rpx 16rpx; border-radius: 16rpx; font-size: 24rpx; background: #f5f5f5; color: #666; border: 2rpx solid transparent }
.type-item.selected { border-color: #00BCD4; background: #e0f7fa; color: #00BCD4 }
.dialog-input { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; box-sizing: border-box }
.dialog-textarea { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; height: 180rpx; resize: none; box-sizing: border-box }
.dialog-btns { display: flex; gap: 20rpx; margin-top: 8rpx }
.dialog-cancel { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 10rpx; color: #999; font-size: 28rpx }
.dialog-confirm { flex: 1; text-align: center; padding: 16rpx; background: linear-gradient(135deg, #00BCD4, #4DD0E1); border-radius: 10rpx; color: #fff; font-size: 28rpx }
</style>
