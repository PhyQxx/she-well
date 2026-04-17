<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="profile-header">
      <view class="profile-bg"></view>
      <view class="profile-content">
        <view class="profile-main">
          <image class="avatar" :src="avatar || '/static/avatar-default.png'" mode="aspectFill" />
          <view class="user-info">
            <view class="nickname">{{ nickname || '未登录' }}</view>
            <view class="user-mode" v-if="modeLabel">
              <text class="mode-tag">{{ modeLabel }}</text>
            </view>
          </view>
          <view class="edit-btn" @click="toSettings">
            <text>⚙️</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="stats-row">
      <view class="stat-item" @click="toStats('cycle')">
        <view class="stat-num">{{ statsData.checkinDays }}</view>
        <view class="stat-label">打卡天数</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @click="toStats('period')">
        <view class="stat-num">{{ statsData.periodCount }}</view>
        <view class="stat-label">记录经期</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @click="toCommunity">
        <view class="stat-num">{{ statsData.postCount }}</view>
        <view class="stat-label">发布帖子</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <view class="stat-num">{{ statsData.achievementCount }}</view>
        <view class="stat-label">获得成就</view>
      </view>
    </view>

    <!-- 成就展示 -->
    <view class="section-card" @click="toAchievement">
      <view class="section-header">
        <text class="section-title">🏆 我的成就</text>
        <text class="section-more">查看全部 &gt;</text>
      </view>
      <scroll-view class="achievement-scroll" scroll-x>
        <view class="achievement-list">
          <view
            v-for="a in achievements"
            :key="a.id"
            class="achievement-badge"
            :class="{ locked: !a.unlocked }"
          >
            <text class="badge-icon">{{ a.icon || '🏆' }}</text>
            <text class="badge-name">{{ a.name }}</text>
          </view>
          <view class="achievement-empty" v-if="!achievements.length">
            <text class="empty-text">暂无成就，继续努力~</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-card">
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
    <view class="logout-btn" @click="logout" v-if="isLoggedIn">
      <text>退出登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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
      achievements.value = res.data.slice(0, 6).map(a => ({
        id: a.achievementId,
        icon: a.icon || '🏆',
        name: a.name || '成就',
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
    title: '提示',
    content: '确定退出登录？',
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
  if (!token) {
    uni.reLaunch({ url: '/pages/login/login' })
  } else {
    loadProfile()
    loadStats()
    loadAchievements()
  }
})
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.mine-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 用户信息头部
.profile-header {
  position: relative;
}

.profile-bg {
  height: 280rpx;
  background: $she-gradient-primary;
  border-radius: 0 0 48rpx 48rpx;
}

.profile-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 0 32rpx;
}

.profile-main {
  display: flex;
  align-items: center;
  padding-top: 120rpx;
}

.avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 70rpx;
  border: 6rpx solid rgba(255,255,255,0.9);
  background: $she-white;
  box-shadow: 0 8rpx 32rpx rgba($she-primary, 0.3);
}

.user-info {
  flex: 1;
  margin-left: 28rpx;
}

.nickname {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8rpx;
}

.mode-tag {
  display: inline-block;
  padding: 6rpx 20rpx;
  background: rgba(255,255,255,0.25);
  color: #fff;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.edit-btn {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.2);
  border-radius: 36rpx;
  font-size: 36rpx;
}

// 统计行
.stats-row {
  display: flex;
  align-items: center;
  background: $she-white;
  margin: -60rpx 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx 16rpx;
  box-shadow: $she-shadow-md;
  position: relative;
  z-index: 1;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-num {
  font-size: 40rpx;
  font-weight: 700;
  color: $she-primary;
}

.stat-label {
  font-size: 22rpx;
  color: $she-muted;
  margin-top: 4rpx;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: $she-border;
}

// 功能卡片
.section-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: $she-shadow-md;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $she-title;
}

.section-more {
  font-size: 24rpx;
  color: $she-muted;
}

.achievement-scroll {
  width: 100%;
}

.achievement-list {
  display: flex;
  gap: 24rpx;
  padding-bottom: 8rpx;
}

.achievement-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  min-width: 120rpx;
  padding: 20rpx 16rpx;
  background: $she-bg;
  border-radius: 16rpx;

  &.locked {
    opacity: 0.4;
    filter: grayscale(1);
  }
}

.badge-icon {
  font-size: 48rpx;
}

.badge-name {
  font-size: 20rpx;
  color: $she-sub;
  text-align: center;
}

.achievement-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
}

.empty-text {
  font-size: 24rpx;
  color: $she-muted;
}

// 菜单卡片
.menu-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: $she-shadow-md;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid $she-border;
  transition: all 0.2s ease;

  &:active {
    background: rgba($she-primary, 0.04);
  }

  &:last-child {
    border-bottom: none;
  }
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.menu-label {
  flex: 1;
  font-size: 30rpx;
  color: $she-text;
}

.menu-arrow {
  font-size: 28rpx;
  color: $she-border;
}

// 退出按钮
.logout-btn {
  background: $she-white;
  margin: 8rpx 32rpx;
  border-radius: 40rpx;
  text-align: center;
  padding: 28rpx;
  font-size: 32rpx;
  color: $she-muted;
  box-shadow: $she-shadow-sm;

  &:active {
    background: rgba($she-danger, 0.04);
    color: $she-danger;
  }
}
</style>
