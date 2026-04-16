<template>
  <view class="period-page">
    <view class="header">
      <view class="header-title">📅 周期日历</view>
      <view class="header-sub">{{ currentMonth }}</view>
    </view>

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

    <view class="legend">
      <view class="legend-item"><view class="legend-dot period" />经期</view>
      <view class="legend-item"><view class="legend-dot predicted" />预测</view>
      <view class="legend-item"><view class="legend-dot ovulation" />排卵</view>
    </view>

    <view class="records-section">
      <view class="section-title">📝 经期记录</view>
      <view v-for="r in records" :key="r.id" class="record-item">
        <view class="record-left">
          <view class="record-date">{{ r.startDate }}</view>
          <view class="record-duration">持续 {{ r.duration || '-' }} 天</view>
        </view>
        <view class="record-right">
          <view class="record-amount">{{ r.flowLevel ? ['轻', '中', '重'][r.flowLevel - 1] || '中' : '-' }}</view>
        </view>
      </view>
      <view v-if="!records.length" class="empty-tip">暂无经期记录</view>
    </view>
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

// 生成日期范围（start 到 end 之间的所有日期字符串）
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

  // 加载经期记录
  try {
    const res = await period.list()
    if (res.data) {
      records.value = res.data
      // 标记经期日
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

  // 加载预测数据
  try {
    const predRes = await period.prediction()
    if (predRes.data) {
      const pred = predRes.data
      avgCycle.value = pred.cycleLength || 0
      avgDuration.value = pred.periodLength || 0

      // 标记预测经期日
      if (pred.predictedNextDate) {
        const endDate = new Date(new Date(pred.predictedNextDate).getTime() + ((pred.periodLength || 5) - 1) * 86400000).toISOString().slice(0, 10)
        for (const d of dateRange(pred.predictedNextDate, endDate)) {
          predictedDaySet.add(d)
        }
      }
      // 标记排卵日
      if (pred.ovulationDate) {
        ovulationDaySet.add(pred.ovulationDate)
      }
      // 计算当前周期天数
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
.record-date { font-size: 28rpx; font-weight: 500; color: #333 }
.record-duration { font-size: 24rpx; color: #999 }
.record-amount { font-size: 28rpx; color: #E91E63; font-weight: 500 }
.empty-tip { text-align: center; color: #999; font-size: 26rpx; padding: 24rpx }
</style>
