<template>
  <view class="mine-page">
    <!-- 用户信息 -->
    <view class="user-card">
      <view class="user-info">
        <image class="avatar" :src="profile?.avatarUrl || '/static/default-avatar.png'" mode="aspectFill" />
        <view class="user-detail">
          <text class="nickname">{{ profile?.nickname || '未登录' }}</text>
          <text class="user-mode">{{ modeText }}</text>
        </view>
      </view>
      <button v-if="!isLoggedIn" class="login-btn" @click="goLogin">登录</button>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-card">
      <view class="menu-item" @click="goSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-label">设置</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goNotifications">
        <text class="menu-icon">🔔</text>
        <text class="menu-label">通知</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goAchievements">
        <text class="menu-icon">🏆</text>
        <text class="menu-label">成就</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goHealthReport">
        <text class="menu-icon">📊</text>
        <text class="menu-label">健康报告</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-card">
      <view class="menu-item" @click="goBabies">
        <text class="menu-icon">👶</text>
        <text class="menu-label">宝宝信息</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goExpert">
        <text class="menu-icon">👩‍⚕️</text>
        <text class="menu-label">咨询专家</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-card">
      <view class="menu-item" @click="goKnowledge">
        <text class="menu-icon">📖</text>
        <text class="menu-label">健康知识</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goCommunity">
        <text class="menu-icon">💬</text>
        <text class="menu-label">姐妹社区</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <button v-if="isLoggedIn" class="logout-btn" @click="logout">退出登录</button>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProfile } from '@/api/index.js'
import { useAuthStore } from '@/store/auth.js'

const auth = useAuthStore()
const profile = ref(null)

const isLoggedIn = computed(() => auth.isLoggedIn)
const modeText = computed(() => {
  const m = profile.value?.menstrualCycle
  if (!m) return '未设置模式'
  return `经期模式 · 周期${m}天`
})

function goLogin() { uni.navigateTo({ url: '/pages/login/login' }) }
function goSettings() { uni.showToast({ title: '设置功能开发中', icon: 'none' }) }
function goNotifications() { uni.showToast({ title: '通知功能开发中', icon: 'none' }) }
function goAchievements() { uni.showToast({ title: '成就功能开发中', icon: 'none' }) }
function goHealthReport() { uni.showToast({ title: '健康报告开发中', icon: 'none' }) }
function goBabies() { uni.showToast({ title: '宝宝信息开发中', icon: 'none' }) }
function goExpert() { uni.showToast({ title: '咨询专家开发中', icon: 'none' }) }
function goKnowledge() { uni.showToast({ title: '健康知识开发中', icon: 'none' }) }
function goCommunity() { uni.showToast({ title: '姐妹社区开发中', icon: 'none' }) }

function logout() {
  uni.showModal({ title: '确认退出', content: '确定要退出登录吗？' }).then(res => {
    if (res.confirm) {
      auth.clearAuth()
      uni.reLaunch({ url: '/pages/login/login' })
    }
  })
}

onMounted(() => {
  if (isLoggedIn.value) {
    getProfile().then(data => {
      profile.value = data
      auth.setProfile(data)
    }).catch(() => {})
  }
})
</script>

<style scoped>
.mine-page { padding: 24rpx; background: #F5F5F5; min-height: 100vh; }
.user-card { background: linear-gradient(135deg, #E91E63, #F48FB1); border-radius: 20rpx; padding: 40rpx 32rpx; display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.user-info { display: flex; align-items: center; gap: 20rpx; }
.avatar { width: 100rpx; height: 100rpx; border-radius: 50%; background: white; border: 4rpx solid white; }
.user-detail { display: flex; flex-direction: column; gap: 8rpx; }
.nickname { font-size: 36rpx; font-weight: 600; color: white; }
.user-mode { font-size: 24rpx; color: rgba(255,255,255,0.8); }
.login-btn { background: white; color: #E91E63; border: none; border-radius: 32rpx; font-size: 26rpx; padding: 16rpx 28rpx; }
.menu-card { background: white; border-radius: 16rpx; margin-bottom: 24rpx; overflow: hidden; }
.menu-item { display: flex; align-items: center; padding: 32rpx 28rpx; border-bottom: 1px solid #F5F5F5; }
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 36rpx; margin-right: 20rpx; }
.menu-label { flex: 1; font-size: 30rpx; color: #333; }
.menu-arrow { color: #CCC; font-size: 32rpx; }
.logout-btn { background: white; color: #FF5252; border: none; border-radius: 48rpx; padding: 24rpx; font-size: 30rpx; margin-top: 16rpx; }
</style>
