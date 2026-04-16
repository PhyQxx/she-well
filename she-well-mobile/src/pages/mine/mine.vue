<template>
  <view class="mine-page">
    <!-- 用户信息 -->
    <view class="profile-card">
      <view class="profile-bg" />
      <view class="profile-info">
        <image class="avatar" :src="avatar || '/static/avatar-default.png'" mode="aspectFill" />
        <view class="user-details">
          <view class="nickname">{{ nickname || '未登录' }}</view>
          <view class="user-mode" v-if="modeLabel">{{ modeLabel }}</view>
        </view>
        <view class="edit-btn" @click="toSettings">⚙️</view>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-row">
      <view class="stat-item" @click="toStats('cycle')">
        <view class="stat-num">{{ statsData.checkinDays }}</view>
        <view class="stat-label">打卡天数</view>
      </view>
      <view class="stat-item" @click="toStats('period')">
        <view class="stat-num">{{ statsData.periodCount }}</view>
        <view class="stat-label">记录经期</view>
      </view>
      <view class="stat-item" @click="toCommunity">
        <view class="stat-num">{{ statsData.postCount }}</view>
        <view class="stat-label">发布帖子</view>
      </view>
      <view class="stat-item">
        <view class="stat-num">{{ statsData.achievementCount }}</view>
        <view class="stat-label">获得成就</view>
      </view>
    </view>

    <!-- 成就展示 -->
    <view class="achievement-section" @click="toAchievement">
      <view class="section-header">
        <view class="section-title">🏆 我的成就</view>
        <view class="section-more">查看全部 &gt;</view>
      </view>
      <view class="achievement-list">
        <view v-for="a in achievements" :key="a.id" class="achievement-badge" :class="{ locked: !a.unlocked }">
          <text class="badge-icon">{{ a.icon }}</text>
          <text class="badge-name">{{ a.name }}</text>
        </view>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-list">
      <view class="menu-item" @click="toKnowledge">
        <text class="menu-icon">📖</text>
        <text class="menu-label">健康知识</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toCommunity">
        <text class="menu-icon">💬</text>
        <text class="menu-label">姐妹社区</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toExpert">
        <text class="menu-icon">👩‍⚕️</text>
        <text class="menu-label">专家问答</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toReport">
        <text class="menu-icon">📊</text>
        <text class="menu-label">健康报告</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toFertility">
        <text class="menu-icon">🌈</text>
        <text class="menu-label">生育力评估</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toReminder">
        <text class="menu-icon">⏰</text>
        <text class="menu-label">健康提醒</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toNotification">
        <text class="menu-icon">🔔</text>
        <text class="menu-label">消息通知</text>
        <text class="menu-arrow">&gt;</text>
      </view>
      <view class="menu-item" @click="toSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-label">设置</text>
        <text class="menu-arrow">&gt;</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="logout" v-if="isLoggedIn">退出登录</view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { user, achievement, checkin, period as periodApi } from '@/api/index.js'

const nickname = ref('')
const avatar = ref('')
const modeLabel = ref('')
const isLoggedIn = ref(true)
const statsData = ref({ checkinDays: 0, periodCount: 0, postCount: 0, achievementCount: 0 })
const achievements = ref([])

async function loadProfile() {
  try {
    const res = await user.getProfile()
    if (res.data) {
      nickname.value = res.data.nickname || '用户'
      avatar.value = res.data.avatar || ''
      const modeMap = { period: '经期模式', trying: '备孕模式', pregnancy: '怀孕模式', nursing: '育儿模式' }
      modeLabel.value = modeMap[res.data.currentMode] || ''
    }
  } catch {}
}

async function loadStats() {
  try {
    const checkinRes = await checkin.list()
    if (checkinRes.data) statsData.value.checkinDays = checkinRes.data.length || 0
  } catch {}
  try {
    const periodRes = await periodApi.list()
    if (periodRes.data) statsData.value.periodCount = periodRes.data.length || 0
  } catch {}
}

async function loadAchievements() {
  try {
    const res = await achievement.my()
    if (res.data) {
      statsData.value.achievementCount = res.data.length
      achievements.value = res.data.slice(0, 5).map(a => ({
        id: a.achievementId,
        icon: '🏆',
        name: '成就',
        unlocked: a.status === 1
      }))
    }
  } catch {}
}

function toStats(type) { uni.showToast({ title: '数据统计', icon: 'none' }) }
function toCommunity() { uni.navigateTo({ url: '/pages/qa/list' }) }
function toExpert() { uni.showToast({ title: '专家问答', icon: 'none' }) }
function toReport() { uni.navigateTo({ url: '/pages/report/index' }) }
function toKnowledge() { uni.showToast({ title: '健康知识', icon: 'none' }) }
function toNotification() { uni.showToast({ title: '消息通知', icon: 'none' }) }
function toSettings() { uni.showToast({ title: '设置', icon: 'none' }) }
function toAchievement() { uni.showToast({ title: '成就系统', icon: 'none' }) }
function toFertility() { uni.navigateTo({ url: '/pages/fertility/index' }) }
function toReminder() { uni.navigateTo({ url: '/pages/reminder/index' }) }

function logout() {
  uni.showModal({
    title: '提示', content: '确定退出登录？',
    success: res => {
      if (res.confirm) {
        uni.removeStorageSync('sw_token')
        isLoggedIn.value = false
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}

onMounted(() => {
  const token = uni.getStorageSync('sw_token')
  if (!token) { uni.reLaunch({ url: '/pages/login/login' }) }
  else { loadProfile(); loadStats(); loadAchievements() }
})
</script>

<style scoped>
.mine-page { min-height: 100vh; background: #f5f5f5; padding-bottom: 48rpx }
.profile-card { position: relative; background: linear-gradient(135deg, #E91E63, #F48FB1); padding-bottom: 80rpx }
.profile-bg { height: 200rpx }
.profile-info { display: flex; align-items: center; padding: 0 32rpx; margin-top: -60rpx }
.avatar { width: 120rpx; height: 120rpx; border-radius: 60rpx; border: 4rpx solid #fff; background: #fff }
.user-details { flex: 1; margin-left: 24rpx }
.nickname { font-size: 36rpx; font-weight: bold; color: #fff }
.user-mode { font-size: 24rpx; color: rgba(255,255,255,0.85); margin-top: 4rpx }
.edit-btn { font-size: 40rpx }
.stats-row { display: flex; background: #fff; margin: -40rpx 32rpx 24rpx; border-radius: 20rpx; padding: 32rpx 0; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08) }
.stat-item { flex: 1; text-align: center }
.stat-num { font-size: 36rpx; font-weight: bold; color: #E91E63 }
.stat-label { font-size: 22rpx; color: #999; margin-top: 4rpx }
.achievement-section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx }
.section-title { font-size: 30rpx; font-weight: 600 }
.section-more { font-size: 24rpx; color: #999 }
.achievement-list { display: flex; gap: 24rpx; overflow-x: auto; padding-bottom: 8rpx }
.achievement-badge { display: flex; flex-direction: column; align-items: center; gap: 4rpx; min-width: 100rpx }
.achievement-badge.locked { opacity: 0.4 }
.badge-icon { font-size: 48rpx }
.badge-name { font-size: 20rpx; color: #666; text-align: center }
.menu-list { background: #fff; margin: 0 32rpx; border-radius: 20rpx; overflow: hidden }
.menu-item { display: flex; align-items: center; padding: 32rpx; border-bottom: 1rpx solid #f5f5f5 }
.menu-item:last-child { border-bottom: none }
.menu-icon { font-size: 36rpx; margin-right: 16rpx }
.menu-label { flex: 1; font-size: 30rpx; color: #333 }
.menu-arrow { font-size: 28rpx; color: #ccc }
.logout-btn { background: #fff; margin: 24rpx 32rpx; border-radius: 40rpx; text-align: center; padding: 28rpx; font-size: 32rpx; color: #999 }
</style>
