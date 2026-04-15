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
      <div class="tips">演示账号：admin / admin123</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码'); return
  }
  // 演示模式：固定账号通过
  if (form.value.username === 'admin' && form.value.password === 'admin123') {
    localStorage.setItem('sw_admin_token', 'demo_token_admin')
    router.push('/dashboard')
  } else {
    ElMessage.error('账号或密码错误')
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
.tips { text-align: center; color: #999; font-size: 13px; margin-top: 8px }
</style>
