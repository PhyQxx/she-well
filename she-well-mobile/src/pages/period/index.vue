<template>
  <view class="period-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">🌸</text>
        <view class="header-text">
          <text class="header-title">周期日历</text>
          <text class="header-sub">{{ currentMonth }}</text>
        </view>
      </view>
    </view>

    <!-- 概览卡片 -->
    <view class="overview-card">
      <view class="overview-item">
        <view class="overview-num">{{ avgCycle || '--' }}</view>
        <view class="overview-label">平均周期(天)</view>
      </view>
      <view class="overview-divider"></view>
      <view class="overview-item">
        <view class="overview-num">{{ avgDuration || '--' }}</view>
        <view class="overview-label">平均经期(天)</view>
      </view>
      <view class="overview-divider"></view>
      <view class="overview-item">
        <view class="overview-num primary">{{ cycleDay || '--' }}</view>
        <view class="overview-label">当前周期(天)</view>
      </view>
    </view>

    <!-- 日历 -->
    <view class="calendar-card">
      <view class="cal-header">
        <view class="cal-nav" @click="prevMonth">
          <text class="nav-arrow">&lt;</text>
        </view>
        <text class="cal-month">{{ currentMonth }}</text>
        <view class="cal-nav" @click="nextMonth">
          <text class="nav-arrow">&gt;</text>
        </view>
      </view>

      <view class="cal-weekdays">
        <view v-for="d in weekdays" :key="d" class="wd">{{ d }}</view>
      </view>

      <view class="cal-days">
        <view
          v-for="day in calendarDays"
          :key="day.date"
          class="cal-day"
          :class="{
            'is-period': day.isPeriod,
            'is-predicted': day.isPredicted && !day.isPeriod,
            'is-ovulation': day.isOvulation && !day.isPeriod,
            'is-today': day.isToday,
            'empty': !day.day
          }"
          @click="onDayClick(day)"
        >
          <text class="day-num">{{ day.day || '' }}</text>
          <view v-if="day.isPeriod" class="day-indicator period"></view>
          <view v-else-if="day.isOvulation" class="day-indicator ovulation"></view>
          <view v-else-if="day.isPredicted" class="day-indicator predicted"></view>
        </view>
      </view>

      <!-- 图例 -->
      <view class="legend">
        <view class="legend-item">
          <view class="legend-dot period"></view>
          <text class="legend-text">经期</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot predicted"></view>
          <text class="legend-text">预测</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot ovulation"></view>
          <text class="legend-text">排卵</text>
        </view>
      </view>
    </view>

    <!-- 记录列表 -->
    <view class="records-card">
      <view class="section-header">
        <text class="section-title">📝 经期记录</text>
        <text class="section-count">共 {{ records.length }} 条</text>
      </view>

      <view v-if="records.length === 0" class="empty-state">
        <text class="empty-icon">📅</text>
        <text class="empty-text">暂无经期记录，点击日期开始记录~</text>
      </view>

      <view v-else class="record-list">
        <view v-for="r in records" :key="r.id" class="record-item">
          <view class="record-left">
            <view class="record-date">{{ r.startDate }}</view>
            <view class="record-duration">
              <text>持续</text>
              <text class="duration-num">{{ r.duration || '-' }}</text>
              <text>天</text>
            </view>
          </view>
          <view class="record-right">
            <view class="flow-badge" :class="'flow-' + (r.flowLevel || 3)">
              {{ ['', '轻', '中', '重'][r.flowLevel || 3] || '中' }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="bottom-placeholder"></view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { period } from '@/api/index.js'

const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonthIdx = ref(now.getMonth() + 1)
const currentMonth = ref('')
const avgCycle = ref(0)
const avgDuration = ref(0)
const cycleDay = ref(0)
const calendarDays = ref([])
const records = ref([])

const periodDaySet = new Set()
const predictedDaySet = new Set()
const ovulationDaySet = new Set()

function updateMonthLabel() {
  currentMonth.value = `${currentYear.value}年${currentMonthIdx.value}月`
}

function buildCalendarDays() {
  const y = currentYear.value
  const m = currentMonthIdx.value
  const firstDay = new Date(y, m - 1, 1).getDay()
  const daysInMonth = new Date(y, m, 0).getDate()
  const today = new Date()
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  const days = []
  for (let i = 0; i < firstDay; i++) days.push({ date: '', day: 0 })
  for (let d = 1; d <= daysInMonth; d++) {
    const date = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({
      date, day: d,
      isPeriod: periodDaySet.has(date),
      isPredicted: predictedDaySet.has(date),
      isOvulation: ovulationDaySet.has(date),
      isToday: date === todayStr
    })
  }
  calendarDays.value = days
}

function prevMonth() {
  currentMonthIdx.value--
  if (currentMonthIdx.value < 1) { currentMonthIdx.value = 12; currentYear.value-- }
  updateMonthLabel()
  buildCalendarDays()
}

function nextMonth() {
  currentMonthIdx.value++
  if (currentMonthIdx.value > 12) { currentMonthIdx.value = 1; currentYear.value++ }
  updateMonthLabel()
  buildCalendarDays()
}

function onDayClick(day) {
  if (!day.day) return
  uni.showActionSheet({
    itemList: ['记录为经期开始日'],
    success: async (res) => {
      if (res.tapIndex === 0) {
        try {
          await period.create({ startDate: day.date })
          uni.showToast({ title: '记录成功', icon: 'success' })
          loadData()
        } catch { uni.showToast({ title: '记录失败', icon: 'none' }) }
      }
    }
  })
}

function dateRange(startStr, endStr) {
  const dates = []
  if (!startStr || !endStr) return dates
  const s = new Date(startStr)
  const e = new Date(endStr)
  for (let d = new Date(s); d <= e; d.setDate(d.getDate() + 1)) {
    dates.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`)
  }
  return dates
}

async function loadData() {
  periodDaySet.clear()
  predictedDaySet.clear()
  ovulationDaySet.clear()

  try {
    const res = await period.list()
    if (res.data) {
      records.value = res.data
      for (const r of res.data) {
        if (r.startDate) {
          const endDate = r.endDate || (r.duration ? new Date(new Date(r.startDate).getTime() + (r.duration - 1) * 86400000).toISOString().slice(0, 10) : r.startDate)
          for (const d of dateRange(r.startDate, endDate)) {
            periodDaySet.add(d)
          }
        }
      }
    }
  } catch {}

  try {
    const predRes = await period.prediction()
    if (predRes.data) {
      const pred = predRes.data
      avgCycle.value = pred.cycleLength || 0
      avgDuration.value = pred.periodLength || 0
      if (pred.predictedNextDate) {
        const endDate = new Date(new Date(pred.predictedNextDate).getTime() + ((pred.periodLength || 5) - 1) * 86400000).toISOString().slice(0, 10)
        for (const d of dateRange(pred.predictedNextDate, endDate)) {
          predictedDaySet.add(d)
        }
      }
      if (pred.ovulationDate) {
        ovulationDaySet.add(pred.ovulationDate)
      }
      if (pred.predictedStartDate) {
        const start = new Date(pred.predictedStartDate)
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        start.setHours(0, 0, 0, 0)
        cycleDay.value = Math.floor((today - start) / 86400000) + 1
      }
    }
  } catch {}

  buildCalendarDays()
}

updateMonthLabel()
onMounted(() => loadData())
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.period-page {
  min-height: 100vh;
  background: $she-bg;
}

// 头部
.page-header {
  position: relative;
}

.header-bg {
  height: 260rpx;
  background: $she-gradient-primary;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.header-icon {
  font-size: 64rpx;
}

.header-text {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
}

.header-sub {
  font-size: 26rpx;
  color: rgba(255,255,255,0.85);
  margin-top: 4rpx;
}

// 概览卡片
.overview-card {
  display: flex;
  align-items: center;
  background: $she-white;
  margin: -80rpx 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx 16rpx;
  box-shadow: $she-shadow-md;
  position: relative;
  z-index: 1;
}

.overview-item {
  flex: 1;
  text-align: center;
}

.overview-num {
  font-size: 44rpx;
  font-weight: 700;
  color: $she-title;

  &.primary { color: $she-primary; }
}

.overview-label {
  font-size: 22rpx;
  color: $she-muted;
  margin-top: 4rpx;
}

.overview-divider {
  width: 1rpx;
  height: 60rpx;
  background: $she-border;
}

// 日历卡片
.calendar-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $she-shadow-md;
}

.cal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.cal-nav {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $she-bg;
  border-radius: 32rpx;
  &:active { background: rgba($she-primary, 0.1); }
}

.nav-arrow {
  font-size: 32rpx;
  color: $she-primary;
  font-weight: 600;
}

.cal-month {
  font-size: 32rpx;
  font-weight: 600;
  color: $she-title;
}

.cal-weekdays {
  display: flex;
  margin-bottom: 16rpx;
}

.wd {
  flex: 1;
  text-align: center;
  font-size: 24rpx;
  color: $she-muted;
  padding: 8rpx 0;
}

.cal-days {
  display: flex;
  flex-wrap: wrap;
}

.cal-day {
  width: 14.28%;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;

  &.empty { pointer-events: none; }

  &.is-today {
    .day-num {
      background: rgba($she-primary, 0.1);
      color: $she-primary;
      border-radius: 50%;
      font-weight: 700;
    }
  }

  &.is-period {
    .day-num {
      background: $she-period;
      color: #fff;
      border-radius: 50%;
    }
  }

  &.is-predicted {
    .day-num {
      background: #f8bbd0;
      color: #fff;
      border-radius: 50%;
    }
  }

  &.is-ovulation {
    .day-num {
      background: $she-trying;
      color: #fff;
      border-radius: 50%;
    }
  }
}

.day-num {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: $she-text;
}

.day-indicator {
  position: absolute;
  bottom: 8rpx;
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;

  &.period { background: $she-period; }
  &.predicted { background: #f8bbd0; }
  &.ovulation { background: $she-trying; }
}

// 图例
.legend {
  display: flex;
  justify-content: center;
  gap: 40rpx;
  margin-top: 24rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid $she-border;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.legend-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  &.period { background: $she-period; }
  &.predicted { background: #f8bbd0; }
  &.ovulation { background: $she-trying; }
}

.legend-text {
  font-size: 24rpx;
  color: $she-sub;
}

// 记录卡片
.records-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $she-shadow-md;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $she-title;
}

.section-count {
  font-size: 24rpx;
  color: $she-muted;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0;
  gap: 16rpx;
}

.empty-icon {
  font-size: 64rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 26rpx;
  color: $she-muted;
  text-align: center;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: $she-bg;
  border-radius: 16rpx;
  transition: all 0.2s ease;

  &:active {
    background: rgba($she-primary, 0.06);
  }
}

.record-left {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.record-date {
  font-size: 28rpx;
  font-weight: 600;
  color: $she-text;
}

.record-duration {
  font-size: 24rpx;
  color: $she-muted;
}

.duration-num {
  color: $she-primary;
  font-weight: 600;
  margin: 0 4rpx;
}

.flow-badge {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;

  &.flow-1 { background: #E8F5E9; color: #4CAF50; }
  &.flow-2 { background: #FFF3E0; color: #FF9800; }
  &.flow-3, &.flow-4 { background: #FCE4EC; color: $she-primary; }
}

.bottom-placeholder {
  height: 48rpx;
}
</style>
