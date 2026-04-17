<template>
  <view class="index-page">
    <swiper class="banner" indicator-dots autoplay circular interval="3000">
      <swiper-item v-for="b in banners" :key="b.id">
        <image :src="b.imageUrl || '/static/banner1.png'" mode="scaleToFill" class="banner-img" />
      </swiper-item>
    </swiper>

    <view class="mode-tabs">
      <view
        v-for="m in modes"
        :key="m.key"
        class="mode-tab"
        :class="{ active: currentMode === m.key }"
        @click="switchMode(m.key)"
      >
        <text>{{ m.label }}</text>
      </view>
    </view>

    <view v-if="currentMode === 'period'" class="mode-content animate-fade-in">
      <view class="main-card period-card">
        <view class="card-deco"></view>
        <view class="card-content">
          <view class="card-main-num">
            {{ isPeriod ? periodDay + '天' : daysUntilNext + '天' }}
          </view>
          <view class="card-main-label">
            {{ isPeriod ? '经期进行中' : '距下次经期' }}
          </view>
          <view class="card-sub" v-if="nextPeriodDate">
            预计 {{ nextPeriodDate }} 来经
          </view>
        </view>
      </view>

      <view class="action-grid">
        <view class="action-btn pink" @click="recordPeriodStart">
          <text class="action-icon">🩸</text>
          <text class="action-label">经期开始</text>
        </view>
        <view class="action-btn purple" @click="toCheckin">
          <text class="action-icon">✅</text>
          <text class="action-label">健康打卡</text>
        </view>
        <view class="action-btn blue" @click="goToAI">
          <text class="action-icon">🤖</text>
          <text class="action-label">AI助手</text>
        </view>
      </view>

      <view class="tips-card">
        <view class="tips-header">
          <text class="tips-title">💡 今日建议</text>
        </view>
        <view class="tips-content">{{ todayTip }}</view>
      </view>
    </view>

    <view v-if="currentMode === 'trying'" class="mode-content animate-fade-in">
      <view class="main-card trying-card">
        <view class="card-deco"></view>
        <view class="card-content">
          <view class="card-main-num">{{ ovulationDate || '--' }}</view>
          <view class="card-main-label">排卵日预测</view>
          <view class="card-sub" v-if="daysToOvulation > 0">
            还有 {{ daysToOvulation }} 天
          </view>
          <view class="card-sub active" v-else-if="ovulationDate">
            今天是排卵日！🎉
          </view>
        </view>
      </view>

      <view class="action-grid">
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

    <view v-if="currentMode === 'pregnancy'" class="mode-content animate-fade-in">
      <view class="main-card pregnant-card">
        <view class="card-deco"></view>
        <view class="card-content">
          <view class="card-main-num">{{ pregWeek }}周</view>
          <view class="card-main-label">孕程进行中</view>
          <view class="card-sub" v-if="edd">预产期：{{ edd }}</view>
        </view>
      </view>

      <view class="checkup-card">
        <view class="checkup-header">
          <text>📅 下次产检</text>
        </view>
        <view class="checkup-info" v-if="nextCheckup">
          {{ nextCheckup.date }} - {{ nextCheckup.item }}
        </view>
        <view class="checkup-info empty" v-else>暂无产检安排</view>
      </view>
    </view>

    <view v-if="currentMode === 'nursing'" class="mode-content animate-fade-in">
      <view class="baby-list" v-if="babies.length">
        <view class="baby-card" v-for="baby in babies" :key="baby.id">
          <view class="baby-info">
            <text class="baby-name">{{ baby.name }}</text>
            <text class="baby-age">{{ babyAge(baby.birthDate) }}</text>
          </view>
        </view>
      </view>
      <view class="tips-card">
        <view class="tips-header">
          <text class="tips-title">🌸 产后恢复</text>
        </view>
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
  try {
    const settingsRes = await user.getSettings()
    if (settingsRes.data && settingsRes.data.currentMode) {
      currentMode.value = settingsRes.data.currentMode
    }
  } catch {}

  try {
    const bannerRes = await discovery.banners()
    if (bannerRes.data) banners.value = bannerRes.data
  } catch {}

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

<style lang="scss" scoped>
@import '@/uni.scss';

.index-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 120rpx;
}

.banner {
  height: 320rpx;
  overflow: hidden;
  .banner-img { width: 100%; height: 100%; }
}

.mode-tabs {
  display: flex;
  padding: 24rpx 32rpx;
  gap: 16rpx;
}

.mode-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  border-radius: 20rpx;
  background: $she-white;
  font-size: 26rpx;
  color: $she-sub;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
  transition: all 0.25s ease;
  &.active {
    background: $she-gradient-primary;
    color: #fff;
    box-shadow: 0 8rpx 24rpx rgba($she-primary, 0.3);
    &:active { transform: scale(0.97); }
  }
}

.mode-content { padding: 0 32rpx; }

.main-card {
  border-radius: 32rpx;
  padding: 48rpx 40rpx;
  color: #fff;
  position: relative;
  overflow: hidden;
  margin-bottom: 24rpx;
  &.period-card { background: linear-gradient(135deg, $she-period, #F8BBD9); }
  &.trying-card { background: linear-gradient(135deg, $she-trying, #CE93D8); }
  &.pregnant-card { background: linear-gradient(135deg, $she-pregnant, #A5D6A7); }
  &.nursing-card { background: linear-gradient(135deg, $she-nursing, #FFCC80); }
}

.card-deco {
  position: absolute;
  top: -60rpx;
  right: -60rpx;
  width: 240rpx;
  height: 240rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
}

.card-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.card-main-num { font-size: 72rpx; font-weight: 700; margin-bottom: 8rpx; }
.card-main-label { font-size: 28rpx; opacity: 0.9; margin-bottom: 12rpx; }
.card-sub {
  font-size: 24rpx; opacity: 0.8;
  &.active {
    display: inline-block;
    background: rgba(255,255,255,0.25);
    padding: 8rpx 24rpx;
    border-radius: 20rpx;
    margin-top: 8rpx;
  }
}

.action-grid { display: flex; gap: 24rpx; margin-bottom: 24rpx; }

.action-btn {
  flex: 1;
  background: $she-white;
  border-radius: 24rpx;
  padding: 32rpx 0;
  text-align: center;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.06);
  transition: all 0.25s ease;
  &:active { transform: scale(0.97); }
  .action-icon { display: block; font-size: 48rpx; margin-bottom: 8rpx; }
  .action-label { font-size: 24rpx; color: $she-sub; }
  &.pink:active { background: rgba($she-period, 0.08); }
  &.purple:active { background: rgba($she-trying, 0.08); }
  &.blue:active { background: rgba($she-info, 0.08); }
  &.green:active { background: rgba($she-pregnant, 0.08); }
  &.orange:active { background: rgba($she-nursing, 0.08); }
}

.tips-card {
  background: $she-white;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.06);
  margin-bottom: 24rpx;
}

.tips-header { margin-bottom: 12rpx; }
.tips-title { font-size: 28rpx; font-weight: 600; color: $she-title; }
.tips-content { font-size: 26rpx; color: $she-sub; line-height: 1.8; }

.checkup-card {
  background: $she-white;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.06);
  margin-bottom: 24rpx;
}

.checkup-header { font-size: 28rpx; font-weight: 600; color: $she-title; margin-bottom: 12rpx; }
.checkup-info { font-size: 26rpx; color: $she-sub; &.empty { color: $she-muted; } }

.baby-list { margin-bottom: 24rpx; }
.baby-card {
  background: $she-white;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.06);
  margin-bottom: 16rpx;
}
.baby-info { display: flex; justify-content: space-between; align-items: center; }
.baby-name { font-size: 32rpx; font-weight: 600; color: $she-title; }
.baby-age { font-size: 26rpx; color: $she-muted; }

.tabbar-placeholder { height: 120rpx; }
</style>
