<template>
  <div class="login-page">
    <div class="login-box">
      <h2>🌸 SheWell 运营后台</h2>
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="管理员账号" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" native-type="submit">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码'); return
  }
  loading.value = true
  try {
    const res = await api.post('/auth/admin-login', {
      username: form.value.username,
      password: form.value.password
    })
    if (res.data && res.data.token) {
      localStorage.setItem('sw_admin_token', res.data.token)
      router.push('/dashboard')
    } else {
      ElMessage.error('登录失败')
    }
  } catch {
    ElMessage.error('账号或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #E91E63, #F48FB1);
}
.login-box {
  background: white; border-radius: 12px; padding: 48px 40px;
  width: 380px; box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
h2 { text-align: center; margin-bottom: 32px; color: #E91E63 }
</style>
