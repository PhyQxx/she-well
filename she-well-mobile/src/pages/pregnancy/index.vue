<template>
  <view class="pregnancy-page">
    <view class="header">
      <view class="header-title">🤱 怀孕日记</view>
    </view>

    <!-- 孕周进度 -->
    <view class="week-card">
      <view class="week-ring">
        <view class="week-num">{{ pregWeek }}</view>
        <view class="week-label">孕周</view>
      </view>
      <view class="week-info">
        <view class="week-detail">第 {{ pregWeek }} 周第 {{ pregDay }} 天</view>
        <view class="edd">预产期：{{ edd }}</view>
        <view class="days-left">距预产期还有 {{ daysToEdd }} 天</view>
      </view>
    </view>

    <!-- 产检提醒 -->
    <view class="section">
      <view class="section-title">📅 下次产检</view>
      <view class="checkup-card" v-if="nextCheckup">
        <view class="checkup-date">{{ nextCheckup.date }}</view>
        <view class="checkup-item">{{ nextCheckup.item }}</view>
        <view class="checkup-hospital">{{ nextCheckup.hospital }}</view>
      </view>
      <view class="empty-tip" v-else>暂无产检安排</view>
      <view class="checkup-history">
        <view class="history-title">产检记录</view>
        <view v-for="c in checkupHistory" :key="c.date" class="history-item">
          <view class="h-date">{{ c.date }}</view>
          <view class="h-item">{{ c.item }}</view>
        </view>
      </view>
    </view>

    <!-- 孕期日记 -->
    <view class="section">
      <view class="section-title">📝 孕期日记</view>
      <view class="diary-list">
        <view v-for="d in diaries" :key="d.id" class="diary-item">
          <view class="diary-date">{{ d.date }}</view>
          <view class="diary-content">{{ d.content }}</view>
        </view>
      </view>
      <view class="write-diary-btn" @click="writeDiary">✏️ 写日记</view>
    </view>

    <!-- 宝宝发育 -->
    <view class="section">
      <view class="section-title">👶 宝宝发育</view>
      <view class="baby-info">
        <view class="baby-size">身长约 {{ babyLength }} cm</view>
        <view class="baby-weight">体重约 {{ babyWeight }} g</view>
      </view>
      <view class="baby-desc">{{ babyDesc }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const pregWeek = ref(16)
const pregDay = ref(3)
const edd = ref('2026-10-15')
const daysToEdd = ref(183)

const nextCheckup = ref({ date: '2026-04-25', item: '唐筛检查', hospital: '北京妇产医院' })

const checkupHistory = ref([
  { date: '2026-01-10', item: '早孕B超确认' },
  { date: '2026-02-15', item: 'NT检查' },
  { date: '2026-03-20', item: '无创DNA' },
])

const diaries = ref([
  { id: 1, date: '2026-04-12', content: '今天第一次感受到胎动，好神奇！' },
  { id: 2, date: '2026-04-10', content: '孕吐减轻了很多，胃口也慢慢好起来了。' },
])

const babyLength = ref(11.6)
const babyWeight = ref(100)
const babyDesc = ref('宝宝现在大约有一个牛油果大小，开始长眉毛和睫毛了，TA会做鬼脸和打哈欠了。')

function writeDiary() { uni.showToast({ title: '写日记', icon: 'none' }) }
</script>

<style scoped>
.pregnancy-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #4CAF50, #A5D6A7); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.week-card { background: linear-gradient(135deg, #4CAF50, #A5D6A7); margin: 24rpx 32rpx; border-radius: 24rpx; padding: 32rpx; color: #fff; display: flex; gap: 32rpx; align-items: center }
.week-ring { width: 160rpx; height: 160rpx; border-radius: 50%; background: rgba(255,255,255,0.25); display: flex; flex-direction: column; align-items: center; justify-content: center; flex-shrink: 0 }
.week-num { font-size: 52rpx; font-weight: bold }
.week-label { font-size: 24rpx; opacity: 0.9 }
.week-info { flex: 1 }
.week-detail { font-size: 30rpx; font-weight: 600 }
.edd { font-size: 26rpx; opacity: 0.9; margin-top: 8rpx }
.days-left { font-size: 26rpx; opacity: 0.9 }
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.checkup-card { background: #f0f9eb; border-radius: 12rpx; padding: 20rpx; margin-bottom: 16rpx }
.checkup-date { font-size: 28rpx; font-weight: 600; color: #4CAF50 }
.checkup-item { font-size: 26rpx; color: #333; margin-top: 4rpx }
.checkup-hospital { font-size: 24rpx; color: #999; margin-top: 4rpx }
.history-title { font-size: 26rpx; color: #666; margin-bottom: 8rpx }
.history-item { display: flex; gap: 16rpx; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.h-date { font-size: 26rpx; color: #999; min-width: 120rpx }
.h-item { font-size: 26rpx; color: #333 }
.diary-list { display: flex; flex-direction: column; gap: 16rpx; margin-bottom: 16rpx }
.diary-item { background: #faf8fb; border-radius: 12rpx; padding: 16rpx }
.diary-date { font-size: 24rpx; color: #999; margin-bottom: 8rpx }
.diary-content { font-size: 28rpx; color: #333; line-height: 1.6 }
.write-diary-btn { text-align: center; color: #4CAF50; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #A5D6A7; border-radius: 12rpx }
.baby-info { display: flex; gap: 24rpx; margin-bottom: 12rpx }
.baby-size, .baby-weight { flex: 1; background: #f0f9eb; border-radius: 12rpx; padding: 16rpx; text-align: center; font-size: 28rpx; color: #4CAF50; font-weight: 500 }
.baby-desc { font-size: 26rpx; color: #666; line-height: 1.8 }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 24rpx }
</style>
