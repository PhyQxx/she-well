<template>
  <view class="login-page">
    <view class="login-bg">
      <view class="logo-area">
        <text class="logo">🌸</text>
        <view class="app-name">SheWell</view>
        <view class="app-slogan">女性健康助手</view>
      </view>
    </view>

    <view class="login-form">
      <view class="phone-row">
        <text class="phone-prefix">+86</text>
        <input class="phone-input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" />
      </view>
      <view class="code-row">
        <input class="code-input" v-model="code" type="number" maxlength="6" placeholder="请输入验证码" />
        <view class="code-btn" :class="{ disabled: countdown > 0 }" @click="sendCode">
          {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
        </view>
      </view>
      <button class="login-btn" @click="doLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      <view class="agreement">
        登录即表示同意
        <text class="link" @click="toAgreement('user')">《用户协议》</text>
        和
        <text class="link" @click="toAgreement('privacy')">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const phone = ref('')
const code = ref('')
const countdown = ref(0)
const loading = ref(false)

function sendCode() {
  if (!phone.value || phone.value.length !== 11) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' }); return
  }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  uni.showToast({ title: '验证码已发送', icon: 'success' })
}

function doLogin() {
  if (!phone.value || phone.value.length !== 11) {
    uni.showToast({ title: '请输入手机号', icon: 'none' }); return
  }
  if (!code.value || code.value.length < 4) {
    uni.showToast({ title: '请输入验证码', icon: 'none' }); return
  }
  loading.value = true
  // 演示模式：任意6位验证码均可登录
  setTimeout(() => {
    uni.setStorageSync('sw_token', 'demo_token_' + Date.now())
    uni.setStorageSync('sw_user', JSON.stringify({ phone: phone.value, nickname: '用户' + phone.value.slice(-4) }))
    loading.value = false
    uni.switchTab({ url: '/pages/index/index' })
  }, 1000)
}

function toAgreement(type) { uni.showToast({ title: type === 'user' ? '用户协议' : '隐私政策', icon: 'none' }) }
</script>

<style scoped>
.login-page { min-height: 100vh; background: #fff }
.login-bg { background: linear-gradient(135deg, #E91E63, #F48FB1); height: 500rpx; display: flex; flex-direction: column; align-items: center; justify-content: center }
.logo-area { text-align: center }
.logo { font-size: 120rpx; display: block }
.app-name { font-size: 56rpx; font-weight: bold; color: #fff; margin-top: 16rpx }
.app-slogan { font-size: 28rpx; color: rgba(255,255,255,0.85); margin-top: 8rpx }
.login-form { margin: 60rpx 48rpx 0 }
.phone-row, .code-row { display: flex; align-items: center; border-bottom: 2rpx solid #f0f0f0; padding: 24rpx 0; margin-bottom: 16rpx }
.phone-prefix { font-size: 32rpx; color: #333; margin-right: 16rpx; font-weight: 500 }
.phone-input, .code-input { flex: 1; font-size: 32rpx; color: #333 }
.code-btn { padding: 12rpx 24rpx; background: #E91E63; color: #fff; border-radius: 30rpx; font-size: 26rpx; white-space: nowrap }
.code-btn.disabled { background: #ccc }
.login-btn { background: linear-gradient(135deg, #E91E63, #F48FB1); color: #fff; border-radius: 40rpx; height: 96rpx; line-height: 96rpx; font-size: 34rpx; font-weight: 600; border: none; margin-top: 32rpx }
.login-btn[disabled] { opacity: 0.7 }
.agreement { text-align: center; font-size: 24rpx; color: #999; margin-top: 24rpx }
.link { color: #E91E63 }
</style>
