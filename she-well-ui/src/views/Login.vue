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
          <el-input v-model="code" placeholder="验证码（演示填123456）" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">
            登录 / 注册
          </el-button>
        </el-form-item>
      </el-form>
      <p class="tips">演示模式：任意手机号 + 验证码123456即可登录</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const phone = ref('')
const code = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!phone.value) { ElMessage.warning('请输入手机号'); return }
  loading.value = true
  try {
    await auth.login(phone.value)
    router.push('/home')
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
.tips { color: #bbb; font-size: 12px; margin-top: 16px; }
</style>
