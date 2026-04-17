<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="deco-circle circle-1"></div>
      <div class="deco-circle circle-2"></div>
      <div class="deco-circle circle-3"></div>
      <div class="deco-circle circle-4"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-container">
      <div class="login-left">
        <div class="brand-section">
          <div class="brand-logo">🌸</div>
          <h1 class="brand-name">SheWell</h1>
          <p class="brand-slogan">女性健康管理系统</p>
        </div>

        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon">📊</span>
            <span class="feature-text">数据可视化分析</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">👩‍⚕️</span>
            <span class="feature-text">专业健康顾问</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔒</span>
            <span class="feature-text">隐私数据保护</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🤖</span>
            <span class="feature-text">AI 智能助手</span>
          </div>
        </div>

        <div class="brand-footer">
          <span>© 2024 SheWell. All rights reserved.</span>
        </div>
      </div>

      <div class="login-right">
        <div class="login-card">
          <div class="login-card-header">
            <h2 class="login-title">管理员登录</h2>
            <p class="login-subtitle">请输入您的账号信息</p>
          </div>

          <el-form :model="form" class="login-form" @submit.prevent="handleLogin">
            <el-form-item>
              <div class="input-wrapper">
                <span class="input-icon">👤</span>
                <el-input
                  v-model="form.username"
                  placeholder="请输入管理员账号"
                  size="large"
                  :prefix-icon="UserIcon"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  :prefix-icon="LockIcon"
                  @keyup.enter="handleLogin"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <span class="forgot-link">忘记密码？</span>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const rememberMe = ref(false)

// 使用浅引用避免组件实例问题
const UserIcon = shallowRef(User)
const LockIcon = shallowRef(Lock)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await api.post('/auth/admin-login', {
      username: form.value.username,
      password: form.value.password
    })
    if (res.data && res.data.token) {
      localStorage.setItem('sw_admin_token', res.data.token)
      if (rememberMe.value) {
        localStorage.setItem('sw_admin_remember', '1')
      }
      ElMessage.success('登录成功')
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

<style lang="scss" scoped>
@import '@/styles/global.scss';

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $admin-bg;
  position: relative;
  overflow: hidden;
}

// 背景装饰
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;

  &.circle-1 {
    width: 400px;
    height: 400px;
    background: rgba($admin-primary, 0.08);
    top: -100px;
    right: -100px;
    animation: float 6s ease-in-out infinite;
  }

  &.circle-2 {
    width: 300px;
    height: 300px;
    background: rgba($admin-primary, 0.06);
    bottom: -50px;
    left: -50px;
    animation: float 6s ease-in-out infinite 2s;
  }

  &.circle-3 {
    width: 200px;
    height: 200px;
    background: rgba($admin-primary, 0.1);
    top: 40%;
    left: 10%;
    animation: pulse 4s ease-in-out infinite;
  }

  &.circle-4 {
    width: 150px;
    height: 150px;
    background: rgba($admin-primary, 0.08);
    bottom: 20%;
    right: 15%;
    animation: pulse 4s ease-in-out infinite 1s;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.6; }
  50% { transform: scale(1.15); opacity: 0.8; }
}

// 登录容器
.login-container {
  display: flex;
  width: 900px;
  max-width: 95vw;
  background: $admin-white;
  border-radius: $admin-radius-2xl;
  box-shadow: $admin-shadow-lg;
  overflow: hidden;
  position: relative;
  z-index: 1;
  animation: slideUp 0.6s ease forwards;
}

// 左侧品牌区
.login-left {
  flex: 1;
  background: $admin-gradient;
  padding: $admin-space-12;
  display: flex;
  flex-direction: column;
  color: white;
}

.brand-section {
  text-align: center;
  margin-bottom: $admin-space-10;
}

.brand-logo {
  font-size: 72px;
  margin-bottom: $admin-space-3;
  animation: float 3s ease-in-out infinite;
}

.brand-name {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 $admin-space-2;
  letter-spacing: 2px;
}

.brand-slogan {
  font-size: 16px;
  opacity: 0.85;
  margin: 0;
}

.feature-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $admin-space-4;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: $admin-space-3;
  padding: $admin-space-3 0;
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 15px;
  opacity: 0.9;
}

.brand-footer {
  text-align: center;
  font-size: 12px;
  opacity: 0.6;
  margin-top: auto;
  padding-top: $admin-space-6;
}

// 右侧登录表单
.login-right {
  width: 440px;
  padding: $admin-space-12 $admin-space-10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
}

.login-card-header {
  margin-bottom: $admin-space-8;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: $admin-text;
  margin: 0 0 $admin-space-2;
}

.login-subtitle {
  font-size: 14px;
  color: $admin-muted;
  margin: 0;
}

.login-form {
  .el-form-item {
    margin-bottom: $admin-space-5;
  }
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  z-index: 1;
  pointer-events: none;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-link {
  font-size: 13px;
  color: $admin-primary;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: $admin-radius-lg;
  border: none;
  background: $admin-gradient;
  box-shadow: 0 4px 16px rgba($admin-primary, 0.3);
  transition: all $admin-transition-normal;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba($admin-primary, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

// 响应式
@media (max-width: 768px) {
  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
    padding: $admin-space-8;
  }
}
</style>
