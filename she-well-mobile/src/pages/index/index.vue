<template>
  <view class="index-page">
    <!-- Banner -->
    <swiper class="banner" indicator-dots autoplay circular interval="3000">
      <swiper-item v-for="b in banners" :key="b.id">
        <image :src="b.imageUrl || '/static/banner1.png'" mode="scaleToFill" class="banner-img" />
      </swiper-item>
    </swiper>

    <!-- 模式切换 Tabs -->
    <view class="mode-tabs">
      <view
        v-for="m in modes" :key="m.key"
        class="mode-tab"
        :class="{ active: currentMode === m.key }"
        @click="switchMode(m.key)"
      >{{ m.label }}</view>
    </view>

    <!-- 经期模式 -->
    <view v-if="currentMode === 'period'" class="mode-content">
      <view class="date-ring">
        <view class="ring-title">{{ isPeriod ? '经期第 ' + periodDay + ' 天' : '距下次经期还有 ' + daysUntilNext + ' 天' }}</view>
        <view class="ring-subtitle" v-if="nextPeriodDate">{{ nextPeriodDate }} 预计来经</view>
        <view class="ring-subtitle" v-else>暂无预测数据</view>
      </view>
      <view class="quick-actions">
        <view class="action-btn pink" @click="recordPeriodStart">
          <text class="action-icon">🩸</text>
          <text class="action-label">经期开始</text>
        </view>
        <view class="action-btn purple" @click="toCheckin">
          <text class="action-icon">✅</text>
          <text class="action-label">健康打卡</text>
        </view>
      </view>
      <view class="tips-card">
        <view class="tips-header">
          <view class="tips-title">💡 今日建议</view>
          <view class="tips-ai-btn" @click="goToAI">AI助手 🤖</view>
        </view>
        <view class="tips-content">{{ todayTip }}</view>
      </view>
    </view>

    <!-- 备孕模式 -->
    <view v-if="currentMode === 'trying'" class="mode-content">
      <view class="ovulation-card">
        <view class="ovu-title">🎯 排卵日预测</view>
        <view class="ovu-date">{{ ovulationDate || '暂无数据' }}</view>
        <view class="ovu-days" v-if="daysToOvulation > 0">还有 {{ daysToOvulation }} 天</view>
        <view class="ovu-days active" v-else-if="ovulationDate">今天是排卵日！</view>
      </view>
      <view class="quick-actions">
        <view class="action-btn green" @click="recordBbt">
          <text class="action-icon">🌡️</text>
          <text class="action-label">记录体温</text>
        </view>
        <view class="action-btn blue" @click="recordStrip">
          <text class="action-icon">🧪</text>
          <text class="action-label">排卵试纸</text>
        </view>
        <view class="action-btn orange" @click="recordIntercourse">
          <text class="action-icon">💕</text>
          <text class="action-label">同房记录</text>
        </view>
      </view>
    </view>

    <!-- 怀孕模式 -->
    <view v-if="currentMode === 'pregnancy'" class="mode-content">
      <view class="preg-card">
        <view class="preg-week">孕第 {{ pregWeek }} 周</view>
        <view class="preg-day">第 {{ pregDay }} 天</view>
        <view class="preg-edd">预产期：{{ edd || '暂未设置' }}</view>
      </view>
      <view class="preg-checkup">
        <view class="checkup-title">📅 下次产检</view>
        <view class="checkup-info" v-if="nextCheckup">{{ nextCheckup.date }} - {{ nextCheckup.item }}</view>
        <view class="checkup-info" v-else>暂无产检安排</view>
      </view>
    </view>

    <!-- 妈妈模式 -->
    <view v-if="currentMode === 'nursing'" class="mode-content">
      <view class="baby-card" v-if="babies.length">
        <view v-for="baby in babies" :key="baby.id" class="baby-item">
          <view class="baby-name">{{ baby.name }}</view>
          <view class="baby-age">{{ babyAge(baby.birthDate) }}</view>
        </view>
      </view>
      <view class="nursing-tips">
        <view class="tips-title">🌸 产后恢复</view>
        <view class="tips-content">保持心情愉悦，适当运动，注意营养补充</view>
      </view>
    </view>

    <view class="tabbar-placeholder" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { period, user, discovery, pregnancy, baby } from '@/api/index.js'

const currentMode = ref('period')
const banners = ref([])
const periodDay = ref(0)
const isPeriod = ref(false)
const daysUntilNext = ref(0)
const nextPeriodDate = ref('')
const ovulationDate = ref('')
const daysToOvulation = ref(0)
const pregWeek = ref(0)
const pregDay = ref(0)
const edd = ref('')
const nextCheckup = ref(null)
const babies = ref([])
const todayTip = ref('记录每日健康状况，获得个性化建议。')

const modes = [
  { key: 'period', label: '📅 经期' },
  { key: 'trying', label: '🤰 备孕' },
  { key: 'pregnancy', label: '🤱 怀孕' },
  { key: 'nursing', label: '🍼 妈妈' },
]

async function switchMode(mode) {
  currentMode.value = mode
  try { await user.switchMode(mode) } catch {}
}

async function loadData() {
  // 加载用户设置（当前模式）
  try {
    const settingsRes = await user.getSettings()
    if (settingsRes.data && settingsRes.data.currentMode) {
      currentMode.value = settingsRes.data.currentMode
    }
  } catch {}

  // 加载轮播图
  try {
    const bannerRes = await discovery.banners()
    if (bannerRes.data) banners.value = bannerRes.data
  } catch {}

  // 加载经期预测数据
  try {
    const predRes = await period.prediction()
    if (predRes.data) {
      const pred = predRes.data
      if (pred.predictedStartDate) {
        const start = new Date(pred.predictedStartDate)
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        start.setHours(0, 0, 0, 0)
        const diffDays = Math.floor((today - start) / 86400000)
        if (diffDays >= 0 && diffDays < (pred.periodLength || 5)) {
          isPeriod.value = true
          periodDay.value = diffDays + 1
        }
      }
      if (pred.predictedNextDate) {
        nextPeriodDate.value = pred.predictedNextDate
        const next = new Date(pred.predictedNextDate)
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        next.setHours(0, 0, 0, 0)
        const diff = Math.ceil((next - today) / 86400000)
        daysUntilNext.value = diff > 0 ? diff : 0
      }
      if (pred.ovulationDate) {
        ovulationDate.value = pred.ovulationDate
        const ovu = new Date(pred.ovulationDate)
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        ovu.setHours(0, 0, 0, 0)
        daysToOvulation.value = Math.ceil((ovu - today) / 86400000)
      }
    }
  } catch {}

  // 加载怀孕记录（如果是怀孕模式）
  if (currentMode.value === 'pregnancy') {
    try {
      const pregRes = await pregnancy.getRecord()
      if (pregRes.data) {
        const preg = pregRes.data
        if (preg.dueDate) edd.value = preg.dueDate
        if (preg.lmpDate) {
          const lmp = new Date(preg.lmpDate)
          const now = new Date()
          const days = Math.floor((now - lmp) / 86400000)
          pregWeek.value = Math.floor(days / 7)
          pregDay.value = days % 7
        }
      }
    } catch {}
  }

  // 加载宝宝列表（如果是妈妈模式）
  if (currentMode.value === 'nursing') {
    try {
      const babyRes = await baby.list()
      if (babyRes.data) babies.value = babyRes.data
    } catch {}
  }
}

function recordPeriodStart() {
  uni.showModal({
    title: '记录经期',
    content: '确认记录今天为经期开始日？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await period.create({ startDate: new Date().toISOString().slice(0, 10) })
          uni.showToast({ title: '记录成功', icon: 'success' })
          loadData()
        } catch { uni.showToast({ title: '记录失败', icon: 'none' }) }
      }
    }
  })
}

function toCheckin() { uni.switchTab({ url: '/pages/checkin/index' }) }
function recordBbt() { uni.navigateTo({ url: '/pages/checkin/index?type=bbt' }) }
function recordStrip() { uni.navigateTo({ url: '/pages/checkin/index?type=strip' }) }
function recordIntercourse() { uni.navigateTo({ url: '/pages/checkin/index?type=intercourse' }) }
function goToAI() { uni.navigateTo({ url: '/pages/ai/chat' }) }

function babyAge(birthDate) {
  if (!birthDate) return ''
  const birth = new Date(birthDate)
  const now = new Date()
  const months = (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
  if (months < 1) return '不足1个月'
  if (months < 12) return months + '个月'
  return Math.floor(months / 12) + '岁' + (months % 12 > 0 ? (months % 12) + '个月' : '')
}

onMounted(() => {
  const token = uni.getStorageSync('sw_token')
  if (!token) { uni.reLaunch({ url: '/pages/login/login' }) }
  else { loadData() }
})
</script>

<style scoped>
.index-page { min-height: 100vh; background: #faf8fb; padding-bottom: 120rpx }
.banner { height: 320rpx; overflow: hidden; border-radius: 0 0 24rpx 24rpx }
.banner-img { width: 100%; height: 100% }
.mode-tabs { display: flex; padding: 24rpx 32rpx; gap: 16rpx }
.mode-tab { flex: 1; text-align: center; padding: 16rpx 0; border-radius: 20rpx; background: #fff; font-size: 26rpx; color: #666; font-weight: 500 }
.mode-tab.active { background: linear-gradient(135deg, #E91E63, #F48FB1); color: #fff }
.mode-content { padding: 0 32rpx }
.date-ring { background: linear-gradient(135deg, #E91E63, #F48FB1); border-radius: 24rpx; padding: 40rpx; text-align: center; color: #fff; margin-bottom: 24rpx }
.ring-title { font-size: 48rpx; font-weight: bold; margin-bottom: 12rpx }
.ring-subtitle { font-size: 26rpx; opacity: 0.9 }
.quick-actions { display: flex; gap: 24rpx; margin-bottom: 24rpx }
.action-btn { flex: 1; background: #fff; border-radius: 20rpx; padding: 32rpx 0; text-align: center; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06) }
.action-icon { display: block; font-size: 48rpx; margin-bottom: 8rpx }
.action-label { font-size: 24rpx; color: #666 }
.tips-card { background: #fff; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); margin-bottom: 24rpx }
.tips-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx }
.tips-title { font-size: 28rpx; font-weight: 600 }
.tips-ai-btn { font-size: 24rpx; color: #E91E63; background: #fff5f7; padding: 6rpx 16rpx; border-radius: 20rpx }
.tips-content { font-size: 26rpx; color: #666; line-height: 1.8 }
.ovulation-card { background: linear-gradient(135deg, #9C27B0, #CE93D8); border-radius: 24rpx; padding: 40rpx; text-align: center; color: #fff; margin-bottom: 24rpx }
.ovu-title { font-size: 28rpx; opacity: 0.9; margin-bottom: 12rpx }
.ovu-date { font-size: 52rpx; font-weight: bold; margin-bottom: 8rpx }
.ovu-days { font-size: 26rpx; opacity: 0.9 }
.ovu-days.active { background: #fff; color: #9C27B0; border-radius: 20rpx; display: inline-block; padding: 8rpx 24rpx; margin-top: 12rpx }
.preg-card { background: linear-gradient(135deg, #4CAF50, #A5D6A7); border-radius: 24rpx; padding: 40rpx; text-align: center; color: #fff; margin-bottom: 24rpx }
.preg-week { font-size: 36rpx; font-weight: bold }
.preg-day { font-size: 64rpx; font-weight: bold; margin: 8rpx 0 }
.preg-edd { font-size: 26rpx; opacity: 0.9 }
.preg-checkup { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx }
.checkup-title { font-size: 28rpx; font-weight: 600; margin-bottom: 12rpx }
.checkup-info { font-size: 26rpx; color: #666 }
.baby-card { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx }
.baby-item { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0 }
.baby-item:last-child { border-bottom: none }
.baby-name { font-size: 30rpx; font-weight: 600; color: #333 }
.baby-age { font-size: 26rpx; color: #999 }
.nursing-tips { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx }
.tabbar-placeholder { height: 120rpx }
</style>
