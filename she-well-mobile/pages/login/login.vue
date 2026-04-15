<template>
  <view class="login-page">
    <view class="logo-area">
      <text class="logo">🌸 SheWell</text>
      <text class="slogan">女性健康生活方式管理</text>
    </view>
    <view class="form-area">
      <input class="input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" />
      <view class="code-row">
        <input class="input code-input" v-model="code" type="number" maxlength="6" placeholder="验证码" />
        <button class="code-btn" :disabled="countdown > 0" @click="sendCode">
          {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
        </button>
      </view>
      <button class="login-btn" :loading="loading" @click="handleLogin">登录 / 注册</button>
      <text class="tips">演示模式：任意手机号 + 验证码123456即可登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { login as apiLogin, sendCode as apiSendCode } from '@/api/index.js'
import { useAuthStore } from '@/store/auth.js'

const phone = ref('')
const code = ref('')
const loading = ref(false)
const countdown = ref(0)
const auth = useAuthStore()

function sendCode() {
  if (!phone.value || phone.value.length !== 11) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' }); return
  }
  apiSendCode(phone.value).then(() => {
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  }).catch(() => {})
}

function handleLogin() {
  if (!phone.value) { uni.showToast({ title: '请输入手机号', icon: 'none' }); return }
  loading.value = true
  apiLogin(phone.value).then(data => {
    auth.setAuth(data)
    uni.reLaunch({ url: '/pages/index/index' })
  }).finally(() => { loading.value = false })
}
</script>

<style scoped>
.login-page { padding: 120rpx 60rpx 0; background: linear-gradient(180deg, #FFF5F7 0%, #FFFFFF 100%); min-height: 100vh; }
.logo-area { text-align: center; margin-bottom: 80rpx; }
.logo { display: block; font-size: 72rpx; color: #E91E63; font-weight: bold; }
.slogan { display: block; color: #999; font-size: 28rpx; margin-top: 16rpx; }
.form-area { padding: 0 20rpx; }
.input { background: white; border: 1px solid #F0F0F0; border-radius: 16rpx; padding: 28rpx 32rpx; font-size: 30rpx; margin-bottom: 32rpx; }
.code-row { display: flex; gap: 20rpx; margin-bottom: 32rpx; }
.code-input { flex: 1; margin-bottom: 0; }
.code-btn { background: #F48FB1; color: white; border: none; border-radius: 16rpx; font-size: 26rpx; padding: 0 24rpx; white-space: nowrap; }
.login-btn { background: #E91E63; color: white; border: none; border-radius: 48rpx; font-size: 34rpx; padding: 24rpx 0; margin-bottom: 32rpx; }
.tips { display: block; text-align: center; color: #CCC; font-size: 24rpx; }
</style>
