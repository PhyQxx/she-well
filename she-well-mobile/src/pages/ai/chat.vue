<template>
  <view class="ai-chat-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="back-btn" @click="goBack">←</text>
        <view class="header-center">
          <text class="header-icon">🤖</text>
          <view class="header-text">
            <text class="header-title">AI 健康助手</text>
            <text class="header-sub">SheWell 智能咨询</text>
          </view>
        </view>
        <view class="model-select" @click="showModelPicker = true">
          <text class="model-name">{{ currentModel }}</text>
        </view>
      </view>
    </view>

    <!-- 免责声明 -->
    <view class="disclaimer-banner" v-if="showDisclaimer">
      <text class="disclaimer-icon">⚠️</text>
      <text class="disclaimer-text">AI 仅供参考，不能替代医生诊断。如有不适请就医。</text>
      <text class="disclaimer-close" @click="showDisclaimer = false">×</text>
    </view>

    <!-- 消息列表 -->
    <scroll-view
      class="message-list"
      :scroll-y="true"
      :scroll-top="scrollTop"
      :style="{ height: messageListHeight }"
    >
      <!-- 欢迎界面 -->
      <view class="welcome-screen" v-if="messages.length === 0">
        <view class="welcome-icon">🤖</view>
        <view class="welcome-title">你好，我是 SheWell AI 助手</view>
        <view class="welcome-desc">我可以帮你解答经期、备孕、怀孕等方面的健康问题</view>
        <view class="quick-chips">
          <view class="chips-title">💡 快捷问题</view>
          <view class="chips-list">
            <view
              v-for="q in quickQuestions"
              :key="q"
              class="chip-item"
              @click="sendQuickQuestion(q)"
            >{{ q }}</view>
          </view>
        </view>
      </view>

      <!-- 聊天消息 -->
      <view
        v-for="(msg, idx) in messages"
        :key="idx"
        :class="['message-item', msg.role]"
      >
        <view class="msg-avatar">{{ msg.role === 'user' ? '👩' : '🤖' }}</view>
        <view class="msg-content">
          <view class="msg-bubble">{{ msg.content }}</view>
          <view class="msg-time">{{ msg.time }}</view>
        </view>
      </view>

      <!-- 打字中 -->
      <view class="message-item assistant" v-if="isTyping">
        <view class="msg-avatar">🤖</view>
        <view class="msg-content">
          <view class="msg-bubble typing">
            <view class="typing-dot"></view>
            <view class="typing-dot"></view>
            <view class="typing-dot"></view>
          </view>
        </view>
      </view>

      <view id="scroll-bottom"></view>
    </scroll-view>

    <!-- 底部输入区 -->
    <view class="input-area">
      <view class="input-main">
        <textarea
          v-model="inputText"
          class="chat-input"
          placeholder="输入你的健康问题..."
          :cursor-spacing="10"
          :show-confirm-bar="false"
          adjust-position
          @focus="onInputFocus"
          @blur="onInputBlur"
        />
        <view class="input-actions">
          <view class="action-btn" @click="attachImage" v-if="showAttach">📎</view>
          <view
            class="send-btn"
            :class="{ active: canSend }"
            @click="sendMessage"
          >发送</view>
        </view>
      </view>

      <!-- 快捷操作 -->
      <view class="quick-actions">
        <view class="qa-item" @click="switchTab('advice')">
          <text class="qa-icon">💡</text>
          <text class="qa-label">每日建议</text>
        </view>
        <view class="qa-item" @click="switchTab('strip')">
          <text class="qa-icon">🧪</text>
          <text class="qa-label">试纸识别</text>
        </view>
        <view class="qa-item" @click="switchTab('cycle')">
          <text class="qa-icon">📊</text>
          <text class="qa-label">周期分析</text>
        </view>
      </view>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 模型选择弹窗 -->
    <view class="dialog-overlay" v-if="showModelPicker" @click="showModelPicker = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showModelPicker = false">取消</text>
          <text class="sheet-title">选择 AI 模型</text>
          <text class="sheet-confirm"></text>
        </view>
        <view
          v-for="m in models"
          :key="m.name"
          class="picker-item"
          :class="{ active: m.name === currentModel }"
          @click="selectModel(m.name)"
        >
          <view class="picker-info">
            <view class="picker-name">{{ m.name }}</view>
            <view class="picker-desc">{{ m.description }}</view>
          </view>
          <view class="picker-check" v-if="m.name === currentModel">✓</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { ai } from '@/api/index.js'

const inputText = ref('')
const messages = ref([])
const isTyping = ref(false)
const showDisclaimer = ref(true)
const showModelPicker = ref(false)
const showAttach = ref(false)
const scrollTop = ref(0)
const inputFocused = ref(false)

const currentModel = ref('deepseek')
const models = ref([
  { name: 'deepseek', description: 'DeepSeek 深度思考模型' },
  { name: 'glm', description: '智谱 GLM 模型' },
  { name: 'minimax', description: 'MiniMax 模型' },
])

const quickQuestions = [
  '经期推迟是怎么回事？',
  '排卵试纸怎么用？',
  '备孕期间怎么补充叶酸？',
  '孕早期注意事项有哪些？',
]

const canSend = computed(() => inputText.value.trim().length > 0)

const messageListHeight = computed(() => {
  const headerHeight = 88
  const disclaimerHeight = showDisclaimer.value ? 72 : 0
  const inputHeight = inputFocused.value ? 320 : 200
  return `calc(100vh - ${headerHeight + disclaimerHeight + inputHeight}rpx)`
})

function goBack() { uni.navigateBack() }

function scrollToBottom() {
  scrollTop.value = scrollTop.value + 1
}

function onInputFocus() { inputFocused.value = true }
function onInputBlur() { inputFocused.value = false }

function addMessage(role, content) {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
  messages.value.push({ role, content, time })
  nextTick(scrollToBottom)
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || isTyping.value) return

  inputText.value = ''
  addMessage('user', text)
  isTyping.value = true

  try {
    const res = await ai.consult(text, '')
    addMessage('assistant', res.answer || res.data?.answer || '抱歉，我没有理解你的问题，请换个方式描述。')
  } catch (err) {
    addMessage('assistant', '网络请求失败，请检查网络后重试。')
  } finally {
    isTyping.value = false
  }
}

async function sendQuickQuestion(q) {
  showDisclaimer.value = false
  addMessage('user', q)
  isTyping.value = true

  try {
    const res = await ai.consult(q, '')
    addMessage('assistant', res.answer || res.data?.answer || '抱歉，我没有理解你的问题。')
  } catch (err) {
    addMessage('assistant', '网络请求失败，请稍后重试。')
  } finally {
    isTyping.value = false
  }
}

function attachImage() {
  uni.chooseImage({
    count: 1,
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFile = res.tempFilePaths[0]
      addMessage('user', '[图片]')
      isTyping.value = true

      uni.getFileSystemManager().readFile({
        filePath: tempFile,
        encoding: 'base64',
        success: (readRes) => {
          ai.analyzeStrip(readRes.data).then(r => {
            addMessage('assistant', `试纸识别结果：${r.result || r.data?.result || '无法识别'}`)
          }).catch(() => {
            addMessage('assistant', '试纸识别失败，请上传清晰的试纸照片。')
          }).finally(() => { isTyping.value = false })
        },
        fail: () => { isTyping.value = false }
      })
    }
  })
}

function switchTab(tab) {
  if (tab === 'advice') {
    addMessage('user', '给我今日健康建议')
    isTyping.value = true
    ai.advice().then(r => {
      addMessage('assistant', r.advice || r.data?.advice || '暂无建议')
    }).catch(() => {
      addMessage('assistant', '获取建议失败')
    }).finally(() => { isTyping.value = false })
  } else if (tab === 'strip') {
    uni.showToast({ title: '请在输入框发送图片', icon: 'none' })
  } else if (tab === 'cycle') {
    addMessage('user', '给我周期分析报告')
    isTyping.value = true
    ai.analyzePeriod({ months: 3 }).then(r => {
      addMessage('assistant', r.analysis || r.data?.analysis || '暂无分析数据')
    }).catch(() => {
      addMessage('assistant', '周期分析需要至少3个月的经期数据')
    }).finally(() => { isTyping.value = false })
  }
}

function selectModel(name) {
  currentModel.value = name
  showModelPicker.value = false
  uni.showToast({ title: `已切换至 ${name}`, icon: 'none' })
}

onMounted(async () => {
  try {
    const modelList = await ai.chat('', '')
    if (modelList && modelList.length) {
      models.value = modelList.map(m => ({
        name: m.name || m.model,
        description: m.description || m.name || ''
      }))
    }
  } catch (e) { /* use defaults */ }
})
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.ai-chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: $she-bg;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 220rpx;
  background: $she-gradient-primary;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0; left: 0; right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.back-btn {
  font-size: 44rpx;
  color: #fff;
  min-width: 60rpx;
}

.header-center {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.header-icon { font-size: 44rpx; }

.header-text { display: flex; flex-direction: column; }

.header-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #fff;
}

.header-sub {
  font-size: 22rpx;
  color: rgba(255,255,255,0.8);
}

.model-select {
  min-width: 100rpx;
  text-align: right;
}

.model-name {
  font-size: 22rpx;
  color: #fff;
  background: rgba(255,255,255,0.25);
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

// 免责声明
.disclaimer-banner {
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #FFF3E0;
  padding: 16rpx 32rpx;
  margin: 16rpx 32rpx 0;
  border-radius: 16rpx;
}

.disclaimer-icon { font-size: 28rpx; }
.disclaimer-text { flex: 1; font-size: 22rpx; color: #E65100; line-height: 1.4 }
.disclaimer-close { font-size: 32rpx; color: #FF9800; padding: 0 8rpx }

// 消息列表
.message-list {
  flex: 1;
  padding: 24rpx 32rpx;
  box-sizing: border-box;
}

.welcome-screen {
  text-align: center;
  padding: 48rpx 0;
}

.welcome-icon { font-size: 80rpx; margin-bottom: 24rpx }
.welcome-title { font-size: 36rpx; font-weight: 600; color: $she-title; margin-bottom: 12rpx }
.welcome-desc { font-size: 26rpx; color: $she-muted; margin-bottom: 40rpx; line-height: 1.6 }

.quick-chips { text-align: left; }
.chips-title { font-size: 26rpx; color: $she-sub; margin-bottom: 16rpx; font-weight: 500 }
.chips-list { display: flex; flex-direction: column; gap: 12rpx }
.chip-item {
  background: $she-white;
  border-radius: 16rpx;
  padding: 20rpx 24rpx;
  font-size: 26rpx;
  color: $she-primary;
  text-align: left;
  border: 2rpx solid $she-primary-light;
  transition: all 0.2s;
  box-shadow: $she-shadow-sm;
  &:active { background: $she-primary-bg; }
}

.message-item {
  display: flex;
  gap: 16rpx;
  margin-bottom: 32rpx;
  align-items: flex-start;

  &.user { flex-direction: row-reverse }
}

.msg-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: $she-bg;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  flex-shrink: 0;
}

.message-item.assistant .msg-avatar { background: $she-primary-bg; }

.msg-content { max-width: 72%; display: flex; flex-direction: column; gap: 8rpx }

.msg-bubble {
  background: $she-white;
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  color: $she-text;
  line-height: 1.7;
  word-break: break-word;
  box-shadow: $she-shadow-sm;
}

.message-item.user .msg-bubble {
  background: $she-gradient-primary;
  color: #fff;
}

.msg-time { font-size: 20rpx; color: $she-muted; padding: 0 8rpx }
.message-item.user .msg-time { text-align: right }

.typing {
  display: flex;
  gap: 8rpx;
  align-items: center;
  padding: 24rpx 32rpx;
  min-width: 120rpx;
}

.typing-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: $she-primary;
  animation: typing-bounce 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(2) { animation-delay: 0.2s }
.typing-dot:nth-child(3) { animation-delay: 0.4s }

@keyframes typing-bounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4 }
  30% { transform: translateY(-8rpx); opacity: 1 }
}

// 输入区
.input-area {
  background: $she-white;
  padding: 16rpx 32rpx;
  padding-bottom: 60rpx;
  flex-shrink: 0;
  box-shadow: 0 -4rpx 24rpx rgba(0,0,0,0.06);
}

.input-main { display: flex; align-items: flex-end; gap: 16rpx; margin-bottom: 16rpx }

.chat-input {
  flex: 1;
  background: $she-bg;
  border-radius: 40rpx;
  padding: 20rpx 28rpx;
  font-size: 28rpx;
  min-height: 72rpx;
  max-height: 200rpx;
  line-height: 1.5;
  box-sizing: border-box;
}

.input-actions { display: flex; align-items: center; gap: 12rpx }
.action-btn { font-size: 44rpx; color: $she-muted }

.send-btn {
  background: $she-border;
  color: $she-white;
  border-radius: 40rpx;
  padding: 16rpx 32rpx;
  font-size: 28rpx;
  font-weight: 500;

  &.active { background: $she-gradient-primary; }
}

.quick-actions {
  display: flex;
  justify-content: space-around;
}

.qa-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 12rpx 0;
}

.qa-icon { font-size: 36rpx; }
.qa-label { font-size: 22rpx; color: $she-muted; }

.bottom-placeholder { height: 32rpx; }

// 模型选择弹窗
.dialog-sheet { background: $she-white; border-radius: 32rpx 32rpx 0 0; padding-bottom: env(safe-area-inset-bottom); }

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid $she-border;
}

.sheet-cancel { font-size: 30rpx; color: $she-muted; min-width: 100rpx; }
.sheet-title { font-size: 32rpx; font-weight: 600; color: $she-text; }
.sheet-confirm { font-size: 30rpx; color: $she-primary; min-width: 100rpx; text-align: right; }

.picker-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 40rpx;
  transition: background 0.2s;
  border-bottom: 1rpx solid $she-border;

  &:active { background: $she-bg; }
  &.active { background: $she-primary-bg; }
}

.picker-name { font-size: 28rpx; color: $she-text; font-weight: 500 }
.picker-desc { font-size: 22rpx; color: $she-muted; margin-top: 4rpx }
.picker-check { color: $she-primary; font-size: 32rpx; }
</style>
