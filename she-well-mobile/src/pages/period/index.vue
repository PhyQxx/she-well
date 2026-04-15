<template>
  <view class="period-page">
    <view class="header">
      <view class="header-title">📅 周期日历</view>
      <view class="header-sub">{{ currentMonth }}</view>
    </view>

    <!-- 周期概览 -->
    <view class="overview-card">
      <view class="overview-item">
        <view class="overview-num">{{ avgCycle }}</view>
        <view class="overview-label">平均周期(天)</view>
      </view>
      <view class="overview-divider" />
      <view class="overview-item">
        <view class="overview-num">{{ avgDuration }}</view>
        <view class="overview-label">平均经期(天)</view>
      </view>
      <view class="overview-divider" />
      <view class="overview-item">
        <view class="overview-num">{{ cycleDay }}</view>
        <view class="overview-label">当前周期(天)</view>
      </view>
    </view>

    <!-- 日历 -->
    <view class="calendar">
      <view class="cal-header">
        <view class="cal-nav" @click="prevMonth">&lt;</view>
        <view class="cal-month">{{ currentMonth }}</view>
        <view class="cal-nav" @click="nextMonth">&gt;</view>
      </view>
      <view class="cal-weekdays">
        <view v-for="d in weekdays" :key="d" class="wd">{{ d }}</view>
      </view>
      <view class="cal-days">
        <view
          v-for="day in calendarDays" :key="day.date"
          class="cal-day"
          :class="{
            'is-period': day.isPeriod,
            'is-predicted': day.isPredicted,
            'is-ovulation': day.isOvulation,
            'is-today': day.isToday,
            'empty': !day.day
          }"
          @click="onDayClick(day)"
        >
          <text>{{ day.day || '' }}</text>
          <view v-if="day.isPeriod" class="day-dot period" />
          <view v-if="day.isPredicted" class="day-dot predicted" />
          <view v-if="day.isOvulation" class="day-dot ovulation" />
        </view>
      </view>
    </view>

    <!-- 图例 -->
    <view class="legend">
      <view class="legend-item"><view class="legend-dot period" />经期</view>
      <view class="legend-item"><view class="legend-dot predicted" />预测</view>
      <view class="legend-item"><view class="legend-dot ovulation" />排卵</view>
    </view>

    <!-- 经期记录列表 -->
    <view class="records-section">
      <view class="section-title">📝 经期记录</view>
      <view v-for="r in records" :key="r.id" class="record-item">
        <view class="record-left">
          <view class="record-date">{{ r.startDate }}</view>
          <view class="record-duration">持续 {{ r.duration }} 天</view>
        </view>
        <view class="record-right">
          <view class="record-amount">{{ ['轻', '中', '重'][r.amount - 1] || '中' }}</view>
        </view>
      </view>
      <view v-if="!records.length" class="empty-tip">暂无经期记录</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const currentMonth = ref('2026年4月')
const avgCycle = ref(28)
const avgDuration = ref(5)
const cycleDay = ref(14)
const currentYear = 2026
const currentMonthIdx = 3  // 4月

const periodDays = [10, 11, 12, 13, 14]  // 经期日期
const predictedDays = [6, 7, 8]  // 预测经期
const ovulationDays = [22]  // 排卵日

const records = ref([
  { id: 1, startDate: '2026-04-10', duration: 5, amount: 2 },
  { id: 2, startDate: '2026-03-13', duration: 5, amount: 2 },
  { id: 3, startDate: '2026-02-14', duration: 6, amount: 1 },
])

function generateCalendarDays(year, month) {
  const firstDay = new Date(year, month - 1, 1).getDay()
  const daysInMonth = new Date(year, month, 0).getDate()
  const days = []
  for (let i = 0; i < firstDay; i++) days.push({ date: '', day: 0 })
  for (let d = 1; d <= daysInMonth; d++) {
    const date = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({
      date, day: d,
      isPeriod: periodDays.includes(d),
      isPredicted: predictedDays.includes(d),
      isOvulation: ovulationDays.includes(d),
      isToday: d === 14
    })
  }
  return days
}

const calendarDays = ref(generateCalendarDays(currentYear, currentMonthIdx))

function prevMonth() {}
function nextMonth() {}
function onDayClick(day) {
  if (!day.day) return
}
</script>

<style scoped>
.period-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #E91E63, #F48FB1); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.header-sub { font-size: 26rpx; opacity: 0.9; margin-top: 8rpx }
.overview-card { display: flex; background: #fff; margin: -40rpx 32rpx 24rpx; border-radius: 20rpx; padding: 32rpx 0; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08) }
.overview-item { flex: 1; text-align: center }
.overview-num { font-size: 40rpx; font-weight: bold; color: #E91E63 }
.overview-label { font-size: 22rpx; color: #999; margin-top: 4rpx }
.overview-divider { width: 1rpx; background: #f0f0f0 }
.calendar { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.cal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx }
.cal-nav { padding: 8rpx 24rpx; font-size: 32rpx; color: #E91E63 }
.cal-month { font-size: 30rpx; font-weight: 600 }
.cal-weekdays { display: flex }
.wd { flex: 1; text-align: center; font-size: 24rpx; color: #999; padding: 8rpx 0 }
.cal-days { display: flex; flex-wrap: wrap }
.cal-day { width: 14.28%; aspect-ratio: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; font-size: 28rpx; color: #333; position: relative }
.cal-day.empty { }
.cal-day.is-today { background: #fce4ec; border-radius: 50%; font-weight: bold; color: #E91E63 }
.cal-day.is-period { background: #F48FB1; border-radius: 50%; color: #fff }
.cal-day.is-predicted { background: #f8bbd0; border-radius: 50%; color: #fff }
.cal-day.is-ovulation { background: #9C27B0; border-radius: 50%; color: #fff }
.day-dot { width: 8rpx; height: 8rpx; border-radius: 50%; position: absolute; bottom: 8rpx }
.day-dot.period { background: #E91E63 }
.day-dot.predicted { background: #F48FB1 }
.day-dot.ovulation { background: #9C27B0 }
.legend { display: flex; justify-content: center; gap: 32rpx; margin: 0 32rpx 24rpx }
.legend-item { display: flex; align-items: center; gap: 8rpx; font-size: 24rpx; color: #666 }
.legend-dot { width: 16rpx; height: 16rpx; border-radius: 50% }
.legend-dot.period { background: #F48FB1 }
.legend-dot.predicted { background: #f8bbd0 }
.legend-dot.ovulation { background: #9C27B0 }
.records-section { background: #fff; margin: 0 32rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.record-item { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.record-left {}
.record-date { font-size: 28rpx; font-weight: 500; color: #333 }
.record-duration { font-size: 24rpx; color: #999 }
.record-right {}
.record-amount { font-size: 28rpx; color: #E91E63; font-weight: 500 }
.empty-tip { text-align: center; color: #999; font-size: 26rpx; padding: 24rpx }
</style>
