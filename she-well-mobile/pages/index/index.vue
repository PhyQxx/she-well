<template>
  <view class="index-page">
    <!-- Banner -->
    <swiper class="banner" indicator-dots autoplay indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#E91E63">
      <swiper-item v-for="b in banners" :key="b.id">
        <image :src="b.imageUrl" mode="aspectFill" class="banner-img" @click="onBannerClick(b)" />
      </swiper-item>
    </swiper>

    <!-- 今日概览卡片 -->
    <view class="overview-card">
      <view class="card-header">
        <text class="card-title">📅 今日概览</text>
        <text class="date">{{ today }}</text>
      </view>
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ stats.cycleDay || '--' }}</text>
          <text class="stat-label">周期第几天</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-num">{{ stats.daysToPeriod || '--' }}</text>
          <text class="stat-label">距下次经期</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-num">{{ stats.daysToOvulation || '--' }}</text>
          <text class="stat-label">距排卵日</text>
        </view>
      </view>
    </view>

    <!-- AI 健康建议 -->
    <view class="advice-card" v-if="advice">
      <view class="card-header">
        <text class="card-title">🤖 AI 健康助手</text>
      </view>
      <text class="advice-text">{{ advice }}</text>
    </view>

    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-item" @click="goPeriod">
        <text class="action-icon">📅</text>
        <text class="action-label">经期</text>
      </view>
      <view class="action-item" @click="goCheckin">
        <text class="action-icon">✅</text>
        <text class="action-label">打卡</text>
      </view>
      <view class="action-item" @click="goCommunity">
        <text class="action-icon">💬</text>
        <text class="action-label">社区</text>
      </view>
      <view class="action-item" @click="goKnowledge">
        <text class="action-icon">📖</text>
        <text class="action-label">知识</text>
      </view>
    </view>

    <!-- 近期打卡 -->
    <view class="section-card">
      <view class="card-header">
        <text class="card-title">🎯 今日打卡</text>
        <text class="card-more" @click="goCheckin">查看全部 ›</text>
      </view>
      <view v-if="todayCheckins.length === 0" class="empty-tips">今日还未打卡，快去记录吧~</view>
      <view v-for="c in todayCheckins" :key="c.id" class="checkin-item">
        <text>{{ c.habitName || '习惯' }}</text>
        <text class="checkin-value">{{ c.value || c.symptom || '已完成' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBanners, getPrediction, getHealthAdvice, getTodayCheckins } from '@/api/index.js'
import dayjs from 'dayjs'

const banners = ref([])
const stats = ref({})
const advice = ref('')
const todayCheckins = ref([])
const today = dayjs().format('M月D日')

onMounted(() => {
  getBanners().then(data => { banners.value = data || [] }).catch(() => {})
  getPrediction().then(data => {
    if (data) {
      const pred = data.predictedStartDate ? new Date(data.predictedStartDate) : null
      const ovul = data.ovulationDate ? new Date(data.ovulationDate) : null
      const now = new Date()
      stats.value = {
        daysToPeriod: pred ? Math.max(0, Math.ceil((pred - now) / 86400000)) : '--',
        daysToOvulation: ovul ? Math.max(0, Math.ceil((ovul - now) / 86400000)) : '--',
        cycleDay: data.cycleLength ? data.cycleLength - (pred ? Math.max(0, Math.ceil((pred - now) / 86400000)) : 0) : '--'
      }
    }
  }).catch(() => {})
  getHealthAdvice().then(data => { advice.value = data?.advice || '' }).catch(() => {})
  getTodayCheckins().then(data => { todayCheckins.value = data || [] }).catch(() => {})
})

function onBannerClick(b) { if (b.linkValue) { uni.navigateTo({ url: b.linkValue }) } }
function goPeriod() { uni.switchTab({ url: '/pages/period/index' }) }
function goCheckin() { uni.switchTab({ url: '/pages/checkin/index' }) }
function goCommunity() { uni.showToast({ title: '社区功能开发中', icon: 'none' }) }
function goKnowledge() { uni.showToast({ title: '知识库开发中', icon: 'none' }) }
</script>

<style scoped>
.index-page { padding: 24rpx; background: #F5F5F5; min-height: 100vh; }
.banner { height: 300rpx; border-radius: 16rpx; overflow: hidden; margin-bottom: 24rpx; }
.banner-img { width: 100%; height: 100%; }
.overview-card, .advice-card, .section-card { background: white; border-radius: 16rpx; padding: 28rpx; margin-bottom: 24rpx; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.card-title { font-size: 32rpx; font-weight: 600; color: #333; }
.date { font-size: 26rpx; color: #999; }
.stats-row { display: flex; align-items: center; justify-content: space-around; }
.stat-item { text-align: center; flex: 1; }
.stat-num { display: block; font-size: 48rpx; font-weight: bold; color: #E91E63; }
.stat-label { display: block; font-size: 24rpx; color: #999; margin-top: 4rpx; }
.stat-divider { width: 1px; height: 60rpx; background: #F0F0F0; }
.advice-text { font-size: 28rpx; color: #666; line-height: 1.8; }
.quick-actions { display: flex; gap: 24rpx; margin-bottom: 24rpx; }
.action-item { flex: 1; background: white; border-radius: 16rpx; padding: 28rpx 0; text-align: center; }
.action-icon { display: block; font-size: 48rpx; margin-bottom: 8rpx; }
.action-label { display: block; font-size: 26rpx; color: #666; }
.card-more { font-size: 26rpx; color: #E91E63; }
.empty-tips { text-align: center; color: #CCC; padding: 20rpx 0; font-size: 28rpx; }
.checkin-item { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1px solid #F5F5F5; font-size: 28rpx; }
.checkin-item:last-child { border-bottom: none; }
.checkin-value { color: #E91E63; }
</style>
