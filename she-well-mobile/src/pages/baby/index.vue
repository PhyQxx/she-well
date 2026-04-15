<template>
  <view class="baby-page">
    <view class="header">
      <view class="header-title">🍼 宝宝成长</view>
    </view>

    <!-- 宝宝卡片 -->
    <view class="baby-cards">
      <view v-for="baby in babies" :key="baby.id" class="baby-card">
        <view class="baby-avatar">{{ baby.icon }}</view>
        <view class="baby-info">
          <view class="baby-name">{{ baby.name }}</view>
          <view class="baby-age">{{ babyAge(baby.birthDate) }}</view>
          <view class="baby-birth">生日：{{ baby.birthDate }}</view>
        </view>
        <view class="baby-actions">
          <view class="action-icon" @click="addMilestone(baby)">📝</view>
          <view class="action-icon" @click="addHeight(baby)">📊</view>
        </view>
      </view>
    </view>

    <!-- 发育记录 -->
    <view class="section">
      <view class="section-title">📈 发育记录</view>
      <view class="growth-table">
        <view class="growth-header">
          <text>日期</text><text>体重(kg)</text><text>身高(cm)</text>
        </view>
        <view v-for="g in growthRecords" :key="g.date" class="growth-row">
          <text>{{ g.date }}</text>
          <text>{{ g.weight }}</text>
          <text>{{ g.height }}</text>
        </view>
      </view>
      <view class="add-growth-btn" @click="addGrowthRecord">+ 记录发育</view>
    </view>

    <!-- 里程碑 -->
    <view class="section">
      <view class="section-title">🌟 成长里程碑</view>
      <view class="milestone-list">
        <view v-for="m in milestones" :key="m.id" class="milestone-item">
          <view class="ms-icon">{{ m.icon }}</view>
          <view class="ms-info">
            <view class="ms-title">{{ m.title }}</view>
            <view class="ms-date">{{ m.date }}</view>
          </view>
        </view>
      </view>
      <view class="add-ms-btn" @click="addMilestone(null)">+ 添加里程碑</view>
    </view>

    <!-- 疫苗提醒 -->
    <view class="section">
      <view class="section-title">💉 疫苗提醒</view>
      <view v-for="v in vaccines" :key="v.id" class="vaccine-item" :class="{ done: v.done }">
        <view class="v-name">{{ v.name }}</view>
        <view class="v-date">{{ v.date }}</view>
        <view class="v-status">{{ v.done ? '已完成' : '待接种' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const babies = ref([
  { id: 1, name: '小豆芽', icon: '👶', birthDate: '2025-12-01' },
])

const growthRecords = ref([
  { date: '2026-01', weight: '3.5', height: '50' },
  { date: '2026-02', weight: '4.8', height: '55' },
  { date: '2026-03', weight: '5.6', height: '58' },
])

const milestones = ref([
  { id: 1, icon: '😊', title: '第一次微笑', date: '2026-01-15' },
  { id: 2, icon: '🗣️', title: '第一次发声', date: '2026-02-20' },
  { id: 3, icon: '🖐️', title: '第一次抓东西', date: '2026-03-10' },
])

const vaccines = ref([
  { id: 1, name: '乙肝疫苗第二针', date: '2026-01-15', done: true },
  { id: 2, name: '脊灰疫苗第一针', date: '2026-02-01', done: true },
  { id: 3, name: '百白破疫苗第一针', date: '2026-03-01', done: false },
  { id: 4, name: '麻疹疫苗', date: '2026-08-01', done: false },
])

function babyAge(birthDate) {
  const birth = new Date(birthDate)
  const now = new Date()
  const months = (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
  if (months < 1) return '不足1个月'
  if (months < 12) return months + '个月'
  return Math.floor(months / 12) + '岁' + (months % 12) + '个月'
}

function addMilestone(baby) { uni.showToast({ title: '添加里程碑', icon: 'none' }) }
function addHeight(baby) { uni.showToast({ title: '记录发育', icon: 'none' }) }
function addGrowthRecord() { uni.showToast({ title: '记录发育', icon: 'none' }) }
</script>

<style scoped>
.baby-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #2196F3, #90CAF9); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.baby-cards { padding: 24rpx 32rpx }
.baby-card { background: #fff; border-radius: 20rpx; padding: 24rpx; display: flex; align-items: center; gap: 20rpx; margin-bottom: 16rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06) }
.baby-avatar { font-size: 64rpx }
.baby-info { flex: 1 }
.baby-name { font-size: 32rpx; font-weight: 600; color: #333 }
.baby-age { font-size: 26rpx; color: #2196F3; margin-top: 4rpx }
.baby-birth { font-size: 24rpx; color: #999; margin-top: 2rpx }
.baby-actions { display: flex; gap: 12rpx }
.action-icon { font-size: 36rpx; padding: 8rpx }
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.growth-table { background: #faf8fb; border-radius: 12rpx; overflow: hidden; margin-bottom: 16rpx }
.growth-header { display: flex; background: #eee; padding: 12rpx 16rpx; font-size: 24rpx; color: #666; font-weight: 600 }
.growth-row { display: flex; padding: 12rpx 16rpx; font-size: 26rpx; border-bottom: 1rpx solid #f0f0f0 }
.growth-row text { flex: 1; text-align: center }
.growth-row:last-child { border-bottom: none }
.milestone-list { display: flex; flex-direction: column; gap: 12rpx; margin-bottom: 16rpx }
.milestone-item { display: flex; gap: 16rpx; align-items: center; padding: 12rpx; background: #faf8fb; border-radius: 12rpx }
.ms-icon { font-size: 40rpx }
.ms-title { font-size: 28rpx; font-weight: 500; color: #333 }
.ms-date { font-size: 24rpx; color: #999 }
.vaccine-item { display: flex; align-items: center; padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.vaccine-item.done { opacity: 0.6 }
.v-name { flex: 1; font-size: 28rpx; color: #333 }
.v-date { font-size: 26rpx; color: #999; margin-right: 16rpx }
.v-status { font-size: 24rpx; color: #4CAF50 }
.vaccine-item:not(.done) .v-status { color: #FF9800 }
.add-growth-btn, .add-ms-btn { text-align: center; color: #2196F3; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #90CAF9; border-radius: 12rpx }
</style>
