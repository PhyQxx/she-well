<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">🌸</div>
        <h1 class="brand-name">SheWell</h1>
        <p class="brand-slogan">女性健康生活方式管理</p>

        <div class="brand-features">
          <div class="feature-item" v-for="f in features" :key="f.icon">
            <span class="feature-icon">{{ f.icon }}</span>
            <span class="feature-text">{{ f.text }}</span>
          </div>
        </div>
      </div>

      <!-- 装饰圆形 -->
      <div class="deco-circle deco-1"></div>
      <div class="deco-circle deco-2"></div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-form-area">
      <div class="login-card">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>登录以继续您的健康管理之旅</p>
        </div>

        <el-form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label class="form-label">手机号</label>
            <el-input
              v-model="phone"
              placeholder="请输入手机号"
              size="large"
              :prefix-icon="Phone"
              maxlength="11"
            />
          </div>

          <div class="form-group">
            <label class="form-label">验证码</label>
            <div class="code-input-row">
              <el-input
                v-model="code"
                placeholder="请输入验证码"
                size="large"
                :prefix-icon="Lock"
                maxlength="6"
                @keyup.enter="handleLogin"
              />
              <el-button
                class="code-btn"
                :disabled="countdown > 0"
                @click="handleSendCode"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录 / 注册
          </el-button>

          <div class="login-tips">
            <span>登录即表示同意</span>
            <el-link type="primary">《用户协议》</el-link>
            <span>和</span>
            <el-link type="primary">《隐私政策》</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import { sendCode } from '@/api'

const router = useRouter()
const auth = useAuthStore()
const phone = ref('')
const code = ref('')
const loading = ref(false)
const countdown = ref(0)

const features = [
  { icon: '📅', text: '智能经期预测' },
  { icon: '🤰', text: '备孕怀孕全程管理' },
  { icon: '💪', text: '健康习惯打卡' },
  { icon: '📊', text: '专业健康报告' },
]

async function handleSendCode() {
  if (!phone.value || phone.value.length !== 11) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  try {
    await sendCode(phone.value)
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
  } catch (e: any) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.login-page {
  display: flex;
  min-height: 100vh;
}

// --------------------------------------------
// 左侧品牌区
// --------------------------------------------
.login-brand {
  flex: 1;
  background: $she-gradient-primary;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  @media (max-width: 768px) {
    display: none;
  }
}

.brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  padding: $she-space-8;
}

.brand-logo {
  font-size: 80px;
  margin-bottom: $she-space-4;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.brand-name {
  font-size: 48px;
  font-weight: $she-font-weight-bold;
  margin-bottom: $she-space-2;
  letter-spacing: 2px;
}

.brand-slogan {
  font-size: $she-font-size-lg;
  opacity: 0.9;
  margin-bottom: $she-space-12;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
  text-align: left;
  max-width: 280px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  padding: $she-space-3 $she-space-4;
  background: rgba(255,255,255,0.15);
  border-radius: $she-radius-lg;
  backdrop-filter: blur(10px);
  transition: all $she-transition-normal;

  &:hover {
    background: rgba(255,255,255,0.25);
    transform: translateX(4px);
  }
}

.feature-icon {
  font-size: $she-font-size-xl;
}

.feature-text {
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-medium;
}

// 装饰圆形
.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);

  &.deco-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
    animation: pulse 4s ease-in-out infinite;
  }

  &.deco-2 {
    width: 300px;
    height: 300px;
    bottom: -80px;
    left: -80px;
    animation: pulse 4s ease-in-out infinite 1s;
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

// --------------------------------------------
// 右侧登录表单区
// --------------------------------------------
.login-form-area {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $she-bg;
  padding: $she-space-8;

  @media (max-width: 768px) {
    width: 100%;
    padding: $she-space-6;
  }
}

.login-card {
  width: 100%;
  max-width: 380px;
  animation: slideUp 0.6s ease forwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: $she-space-10;

  h2 {
    font-size: $she-font-size-3xl;
    font-weight: $she-font-weight-bold;
    color: $she-title;
    margin-bottom: $she-space-2;
  }

  p {
    color: $she-muted;
    font-size: $she-font-size-md;
  }
}

.login-form {
  .form-group {
    margin-bottom: $she-space-6;
  }

  .form-label {
    display: block;
    font-size: $she-font-size-sm;
    font-weight: $she-font-weight-medium;
    color: $she-text;
    margin-bottom: $she-space-2;
  }

  .code-input-row {
    display: flex;
    gap: $she-space-3;

    .el-input {
      flex: 1;
    }

    .code-btn {
      width: 120px;
      flex-shrink: 0;
    }
  }
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: $she-font-size-lg;
  font-weight: $she-font-weight-semibold;
  margin-top: $she-space-4;
  border-radius: $she-radius-xl;
  background: $she-gradient-primary;
  border: none;
  box-shadow: $she-shadow-md;
  transition: all $she-transition-normal;

  &:hover {
    box-shadow: $she-shadow-lg;
    transform: translateY(-2px);
  }

  &:active {
    transform: translateY(0) scale(0.98);
  }
}

.login-tips {
  text-align: center;
  margin-top: $she-space-6;
  font-size: $she-font-size-xs;
  color: $she-muted;

  .el-link {
    margin: 0 $she-space-1;
  }
}
</style>
