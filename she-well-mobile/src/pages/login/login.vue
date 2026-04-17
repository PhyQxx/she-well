<template>
  <view class="login-page">
    <view class="login-header">
      <view class="header-deco">
        <view class="deco-circle circle-1"></view>
        <view class="deco-circle circle-2"></view>
        <view class="deco-circle circle-3"></view>
      </view>
      <view class="header-content">
        <text class="app-logo">🌸</text>
        <text class="app-name">SheWell</text>
        <text class="app-slogan">女性健康助手</text>
      </view>
    </view>
    <view class="login-form">
      <view class="form-title">欢迎回来</view>
      <view class="form-subtitle">登录以继续您的健康管理之旅</view>
      <view class="input-group">
        <view class="input-label">手机号</view>
        <view class="phone-row">
          <text class="phone-prefix">+86</text>
          <input class="text-input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" placeholder-class="placeholder" />
        </view>
      </view>
      <view class="input-group">
        <view class="input-label">验证码</view>
        <view class="code-row">
          <input class="text-input" v-model="code" type="number" maxlength="6" placeholder="请输入验证码" placeholder-class="placeholder" />
          <view class="code-btn" :class="{ disabled: countdown > 0 }" @click="handleSendCode">
            {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
          </view>
        </view>
      </view>
      <button class="login-btn" :class="{ loading }" :disabled="loading" @click="doLogin">
        <text v-if="!loading">登录</text>
        <text v-else>登录中...</text>
      </button>
      <view class="agreement">
        登录即表示同意
        <text class="link" @click="openAgreement('user')">《用户协议》</text>
        和
        <text class="link" @click="openAgreement('privacy')">《隐私政策》</text>
      </view>
    </view>
    <view class="features-row">
      <view class="feature-item" v-for="f in features" :key="f.icon">
        <text class="feature-icon">{{ f.icon }}</text>
        <text class="feature-text">{{ f.text }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { auth } from '@/api/index.js'

const phone = ref('')
const code = ref('')
const countdown = ref(0)
const loading = ref(false)

const features = [
  { icon: '📅', text: '智能预测' },
  { icon: '🔒', text: '隐私保护' },
  { icon: '🤖', text: 'AI助手' },
]

async function handleSendCode() {
  if (countdown.value > 0) return
  if (!phone.value || phone.value.length !== 11) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  try {
    await auth.sendCode(phone.value)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {
    uni.showToast({ title: '发送失败', icon: 'none' })
  }
}

async function doLogin() {
  if (!phone.value || phone.value.length !== 11) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }
  if (!code.value || code.value.length < 4) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await auth.login(phone.value, code.value)
    if (res && res.data) {
      uni.setStorageSync('sw_token', res.data.token)
      uni.setStorageSync('sw_user', JSON.stringify({
        userId: res.data.userId,
        nickname: res.data.nickname,
        phone: phone.value
      }))
      uni.switchTab({ url: '/pages/index/index' })
    }
  } catch (e) {
    uni.showToast({ title: '登录失败，请检查验证码', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function openAgreement(type) {
  const title = type === 'user' ? '用户协议' : '隐私政策'
  uni.showToast({ title: '查看' + title, icon: 'none' })
}
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.login-page {
  min-height: 100vh;
  background: $she-bg;
  display: flex;
  flex-direction: column;
}

.login-header {
  position: relative;
  background: $she-gradient-primary;
  padding: 80rpx 48rpx 60rpx;
  overflow: hidden;
}

.header-deco {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
  &.circle-1 {
    width: 300rpx;
    height: 300rpx;
    top: -100rpx;
    right: -80rpx;
    animation: float 4s ease-in-out infinite;
  }
  &.circle-2 {
    width: 200rpx;
    height: 200rpx;
    bottom: -60rpx;
    left: -40rpx;
    animation: float 4s ease-in-out infinite 1s;
  }
  &.circle-3 {
    width: 150rpx;
    height: 150rpx;
    top: 40%;
    right: 20%;
    animation: pulse 3s ease-in-out infinite;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15rpx) rotate(5deg); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.15); opacity: 0.8; }
}

.header-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: slideUp 0.6s ease forwards;
}

.app-logo {
  font-size: 100rpx;
  margin-bottom: 16rpx;
  animation: float 3s ease-in-out infinite;
}

.app-name {
  font-size: 56rpx;
  font-weight: 700;
  color: #fff;
  letter-spacing: 3px;
  margin-bottom: 8rpx;
}

.app-slogan {
  font-size: 28rpx;
  color: rgba(255,255,255,0.85);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.login-form {
  flex: 1;
  padding: 48rpx;
  margin-top: -30rpx;
  background: $she-white;
  border-radius: 32rpx 32rpx 0 0;
  position: relative;
  z-index: 2;
}

.form-title {
  font-size: 40rpx;
  font-weight: 600;
  color: $she-title;
  margin-bottom: 8rpx;
}

.form-subtitle {
  font-size: 26rpx;
  color: $she-muted;
  margin-bottom: 48rpx;
}

.input-group {
  margin-bottom: 32rpx;
}

.input-label {
  font-size: 26rpx;
  font-weight: 500;
  color: $she-text;
  margin-bottom: 16rpx;
}

.phone-row,
.code-row {
  display: flex;
  align-items: center;
  border-bottom: 2rpx solid $she-border;
  padding-bottom: 24rpx;
  &:focus-within { border-color: $she-primary; }
}

.phone-prefix {
  font-size: 32rpx;
  color: $she-text;
  font-weight: 500;
  margin-right: 16rpx;
  padding-right: 16rpx;
  border-right: 2rpx solid $she-border;
}

.text-input {
  flex: 1;
  font-size: 32rpx;
  color: $she-text;
  height: 80rpx;
  &::placeholder { color: $she-muted; }
}

.code-btn {
  padding: 16rpx 24rpx;
  background: $she-gradient-primary;
  color: #fff;
  border-radius: 30rpx;
  font-size: 24rpx;
  font-weight: 500;
  white-space: nowrap;
  &.disabled { background: #ddd; color: #999; }
}

.login-btn {
  width: 100%;
  height: 96rpx;
  background: $she-gradient-primary;
  color: #fff;
  border-radius: 48rpx;
  font-size: 34rpx;
  font-weight: 600;
  border: none;
  margin-top: 48rpx;
  box-shadow: 0 8rpx 32rpx rgba($she-primary, 0.3);
  transition: all 0.25s ease;
  &:active { transform: scale(0.98); }
  &.loading { opacity: 0.7; }
  &[disabled] { opacity: 0.6; }
}

.agreement {
  text-align: center;
  font-size: 22rpx;
  color: $she-muted;
  margin-top: 32rpx;
  line-height: 1.8;
  .link { color: $she-primary; }
}

.features-row {
  display: flex;
  justify-content: space-around;
  padding: 32rpx 48rpx 48rpx;
  background: $she-white;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  .feature-icon { font-size: 40rpx; }
  .feature-text { font-size: 22rpx; color: $she-muted; }
}
</style>
