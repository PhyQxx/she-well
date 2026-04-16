<template>
  <div class="login-page">
    <div class="login-card">
      <h1>🌸 SheWell</h1>
      <p class="subtitle">女性健康生活方式管理</p>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="phone" placeholder="请输入手机号" size="large" />
        </el-form-item>
        <el-form-item>
          <div style="display:flex;gap:10px;width:100%">
            <el-input v-model="code" placeholder="请输入验证码" size="large" />
            <el-button size="large" :disabled="countdown > 0" @click="handleSendCode">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">
            登录 / 注册
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const auth = useAuthStore()
const phone = ref('')
const code = ref('')
const loading = ref(false)
const countdown = ref(0)

async function handleSendCode() {
  if (!phone.value || phone.value.length !== 11) {
    ElMessage.warning('请输入正确的手机号'); return
  }
  try {
    await api.post('/auth/sendCode', { phone: phone.value })
    ElMessage.success('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch {
    ElMessage.error('发送验证码失败')
  }
}

async function handleLogin() {
  if (!phone.value) { ElMessage.warning('请输入手机号'); return }
  if (!code.value) { ElMessage.warning('请输入验证码'); return }
  loading.value = true
  try {
    await auth.login(phone.value, code.value)
    router.push('/home')
  } catch {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { display: flex; align-items: center; justify-content: center; height: 100vh; background: linear-gradient(135deg, #fce4ec, #f3e5f5); }
.login-card { background: white; padding: 48px 40px; border-radius: 16px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); width: 380px; text-align: center; }
.login-card h1 { color: #e91e63; margin-bottom: 8px; font-size: 32px; }
.subtitle { color: #999; margin-bottom: 32px; font-size: 14px; }
</style>
