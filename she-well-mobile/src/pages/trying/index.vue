<template>
  <view class="trying-page">
    <view class="header">
      <view class="header-title">🤰 备孕中心</view>
    </view>

    <!-- 排卵追踪卡片 -->
    <view class="ovu-card">
      <view class="ovu-main">
        <view class="ovu-label">🎯 排卵日</view>
        <view class="ovu-date">{{ ovulationDate }}</view>
        <view class="ovu-status" :class="ovuStatusClass">{{ ovuStatusText }}</view>
      </view>
      <view class="ovu-countdown" v-if="daysToOvulation > 0">
        <view class="countdown-num">{{ daysToOvulation }}</view>
        <view class="countdown-label">天后</view>
      </view>
    </view>

    <!-- LH试纸记录 -->
    <view class="section">
      <view class="section-title">🧪 排卵试纸记录</view>
      <view class="strip-timeline">
        <view v-for="s in stripRecords" :key="s.id" class="strip-item">
          <view class="strip-date">{{ s.date }}</view>
          <view class="strip-result" :class="'strip-' + s.result">{{ s.resultText }}</view>
        </view>
        <view v-if="!stripRecords.length" class="empty-tip">暂无试纸记录</view>
      </view>
      <view class="add-strip-btn" @click="addStripRecord">+ 记录试纸</view>
    </view>

    <!-- 同房记录 -->
    <view class="section">
      <view class="section-title">💕 同房记录</view>
      <view class="ic-list">
        <view v-for="r in icRecords" :key="r.id" class="ic-item">
          <view class="ic-date">{{ r.date }}</view>
          <view class="ic-time">{{ r.time || '全天' }}</view>
        </view>
        <view v-if="!icRecords.length" class="empty-tip">暂无同房记录</view>
      </view>
      <view class="add-ic-btn" @click="addIcRecord">+ 记录同房</view>
    </view>

    <!-- 基础体温 -->
    <view class="section">
      <view class="section-title">🌡️ 基础体温曲线</view>
      <view class="bbt-chart-placeholder">
        <view class="chart-placeholder">近7天体温趋势图</view>
        <view class="bbt-list">
          <view v-for="b in bbtRecords" :key="b.date" class="bbt-item">
            <text class="bbt-date">{{ b.date }}</text>
            <text class="bbt-temp">{{ b.temp }}°C</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const ovulationDate = ref('2026-04-22')
const daysToOvulation = ref(5)
const ovuStatusClass = ref('')
const ovuStatusText = ref('距排卵日 5 天')

const stripRecords = ref([
  { id: 1, date: '04-18', result: 'negative', resultText: '阴性(-)' },
  { id: 2, date: '04-19', result: 'weak', resultText: '弱阳性(±)' },
  { id: 3, date: '04-20', result: 'positive', resultText: '阳性(+)' },
  { id: 4, date: '04-21', result: 'strong', resultText: '强阳性(++)' },
])

const icRecords = ref([
  { id: 1, date: '2026-04-19', time: '晚上' },
  { id: 2, date: '2026-04-20', time: '上午' },
  { id: 3, date: '2026-04-21', time: '晚上' },
])

const bbtRecords = ref([
  { date: '4/15', temp: '36.2' },
  { date: '4/16', temp: '36.3' },
  { date: '4/17', temp: '36.2' },
  { date: '4/18', temp: '36.4' },
  { date: '4/19', temp: '36.5' },
  { date: '4/20', temp: '36.8' },
  { date: '4/21', temp: '36.9' },
])

function addStripRecord() { uni.showToast({ title: '记录试纸', icon: 'none' }) }
function addIcRecord() { uni.showToast({ title: '记录同房', icon: 'none' }) }
</script>

<style scoped>
.trying-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #9C27B0, #CE93D8); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.ovu-card { background: linear-gradient(135deg, #9C27B0, #CE93D8); margin: 24rpx 32rpx; border-radius: 24rpx; padding: 32rpx; color: #fff; display: flex; justify-content: space-between; align-items: center }
.ovu-label { font-size: 26rpx; opacity: 0.9 }
.ovu-date { font-size: 48rpx; font-weight: bold; margin: 8rpx 0 }
.ovu-status { font-size: 24rpx; background: rgba(255,255,255,0.3); padding: 6rpx 16rpx; border-radius: 20rpx; display: inline-block }
.countdown-num { font-size: 56rpx; font-weight: bold }
.countdown-label { font-size: 24rpx; opacity: 0.9 }
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.strip-timeline { display: flex; gap: 16rpx; overflow-x: auto; padding-bottom: 8rpx }
.strip-item { display: flex; flex-direction: column; align-items: center; gap: 4rpx; min-width: 80rpx }
.strip-date { font-size: 22rpx; color: #999 }
.strip-result { font-size: 22rpx; padding: 6rpx 12rpx; border-radius: 8rpx; font-weight: 500 }
.strip-negative { background: #f5f5f5; color: #999 }
.strip-weak { background: #fff3e0; color: #ff9800 }
.strip-positive { background: #fce4ec; color: #E91E63 }
.strip-strong { background: #E91E63; color: #fff }
.ic-list { display: flex; flex-direction: column; gap: 12rpx; margin-bottom: 16rpx }
.ic-item { display: flex; justify-content: space-between; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.ic-date { font-size: 28rpx; color: #333 }
.ic-time { font-size: 26rpx; color: #999 }
.bbt-chart-placeholder { background: #faf8fb; border-radius: 12rpx; padding: 24rpx }
.chart-placeholder { text-align: center; color: #ccc; font-size: 24rpx; margin-bottom: 16rpx }
.bbt-list { display: flex; gap: 8rpx; justify-content: space-around }
.bbt-item { display: flex; flex-direction: column; align-items: center; gap: 4rpx }
.bbt-date { font-size: 22rpx; color: #999 }
.bbt-temp { font-size: 26rpx; font-weight: 600; color: #9C27B0 }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 24rpx }
.add-strip-btn, .add-ic-btn { text-align: center; color: #9C27B0; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #CE93D8; border-radius: 12rpx; margin-top: 8rpx }
</style>
