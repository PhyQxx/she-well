<template>
  <view class="report-page">
    <view class="header">
      <view class="header-title">📊 健康报告</view>
      <view class="header-gen" @click="generateReport">生成报告</view>
    </view>

    <!-- 报告生成选项 -->
    <view class="gen-section">
      <view class="gen-title">📋 生成健康报告</view>
      <view class="gen-types">
        <view v-for="t in reportTypes" :key="t.value" class="gen-item" :class="{ active: genType === t.value }" @click="genType = t.value">
          <view class="gen-icon">{{ t.icon }}</view>
          <view class="gen-name">{{ t.name }}</view>
        </view>
      </view>
    </view>

    <!-- 报告列表 -->
    <view class="report-list">
      <view class="section-title">📑 我的报告</view>
      <view v-if="loading" class="loading-state"><text>加载中...</text></view>
      <view v-else-if="!reports.length" class="empty-state">
        <view class="empty-icon">📊</view>
        <view class="empty-text">暂无健康报告</view>
        <view class="empty-sub">点击上方「生成报告」创建您的专属健康报告</view>
      </view>
      <view v-else>
        <view v-for="r in reports" :key="r.id" class="report-card" @click="viewReport(r)">
          <view class="report-header">
            <view class="report-type" :class="r.type">{{ typeName(r.type) }}</view>
            <view class="report-date">{{ formatDate(r.createTime || r.generatedAt) }}</view>
          </view>
          <view class="report-summary">{{ r.summary || r.symptomSummary || '健康状况总体良好' }}</view>
          <view class="report-scores" v-if="r.regularityScore">
            <view class="score-item">
              <text class="score-val">{{ r.regularityScore }}</text>
              <text class="score-label">规律评分</text>
            </view>
            <view class="score-item" v-if="r.avgCycleLength">
              <text class="score-val">{{ r.avgCycleLength }}</text>
              <text class="score-label">平均周期</text>
            </view>
            <view class="score-item" v-if="r.avgPeriodLength">
              <text class="score-val">{{ r.avgPeriodLength }}</text>
              <text class="score-label">平均经期</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 报告详情弹窗 -->
    <view class="dialog-overlay" v-if="showDetail" @click="showDetail = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">📊 {{ currentReport?.typeName || '健康报告' }}</view>
        <view class="detail-content" v-if="currentReport">
          <view class="detail-meta">
            <text>📅 {{ formatDate(currentReport.createTime || currentReport.generatedAt) }}</text>
            <text v-if="currentReport.regularityScore">📈 规律评分：{{ currentReport.regularityScore }}分</text>
          </view>
          <view class="detail-section" v-if="currentReport.symptomSummary">
            <view class="detail-label">📝 症状总结</view>
            <view class="detail-text">{{ currentReport.symptomSummary }}</view>
          </view>
          <view class="detail-section" v-if="currentReport.advice">
            <view class="detail-label">💡 健康建议</view>
            <view class="detail-text">{{ currentReport.advice }}</view>
          </view>
          <view class="detail-section" v-if="currentReport.periodStart || currentReport.periodEnd">
            <view class="detail-label">📆 周期信息</view>
            <view class="detail-text">
              周期起始：{{ currentReport.periodStart || '—' }}<br/>
              周期结束：{{ currentReport.periodEnd || '—' }}
            </view>
          </view>
          <view class="detail-section" v-if="currentReport.avgCycleLength">
            <view class="detail-label">📊 数据统计</view>
            <view class="detail-text">
              平均周期长度：{{ currentReport.avgCycleLength }}天<br/>
              平均经期长度：{{ currentReport.avgPeriodLength || '—' }}天
            </view>
          </view>
        </view>
        <view class="dialog-close" @click="showDetail = false">关闭</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { healthReport } from '@/api/index.js'

const reports = ref([])
const loading = ref(false)
const genType = ref('weekly')
const showDetail = ref(false)
const currentReport = ref(null)

const reportTypes = [
  { value: 'weekly', icon: '📅', name: '周报' },
  { value: 'monthly', icon: '🗓️', name: '月报' },
  { value: 'quarterly', icon: '📆', name: '季报' },
]

function typeName(type) {
  return { weekly: '周报', monthly: '月报', quarterly: '季报' }[type] || type || '报告'
}

function formatDate(d) {
  if (!d) return '—'
  const date = new Date(d)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

async function loadData() {
  loading.value = true
  try {
    const res = await healthReport.list({})
    reports.value = (res?.data || []).map(r => ({
      id: r.id,
      type: r.type,
      summary: r.summary || r.symptomSummary,
      regularityScore: r.regularityScore,
      avgCycleLength: r.avgCycleLength,
      avgPeriodLength: r.avgPeriodLength,
      symptomSummary: r.symptomSummary,
      advice: r.advice,
      periodStart: r.periodStart,
      periodEnd: r.periodEnd,
      createTime: r.createTime,
      generatedAt: r.generatedAt,
    }))
  } catch (e) {
    console.error('loadData error', e)
  } finally {
    loading.value = false
  }
}

async function generateReport() {
  uni.showLoading({ title: '生成中...' })
  try {
    const res = await healthReport.generate(genType.value)
    uni.hideLoading()
    uni.showToast({ title: '报告已生成', icon: 'success' })
    await loadData()
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: '生成失败', icon: 'none' })
  }
}

async function viewReport(r) {
  try {
    const res = await healthReport.detail(r.id)
    currentReport.value = res?.data || r
    showDetail.value = true
  } catch (e) {
    currentReport.value = r
    showDetail.value = true
  }
}

onMounted(loadData)
</script>

<style scoped>
.report-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #4CAF50, #81C784); padding: 32rpx; color: #fff; display: flex; justify-content: space-between; align-items: center }
.header-title { font-size: 36rpx; font-weight: bold }
.header-gen { font-size: 28rpx; background: rgba(255,255,255,0.25); padding: 8rpx 20rpx; border-radius: 20rpx }
.gen-section { background: #fff; margin: 24rpx 32rpx; border-radius: 16rpx; padding: 24rpx }
.gen-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx }
.gen-types { display: flex; gap: 16rpx }
.gen-item { flex: 1; background: #faf8fb; border-radius: 12rpx; padding: 20rpx; text-align: center; border: 2rpx solid transparent }
.gen-item.active { border-color: #4CAF50; background: #e8f5e9 }
.gen-icon { font-size: 36rpx }
.gen-name { font-size: 24rpx; color: #666; margin-top: 4rpx }
.report-list { margin: 0 32rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.loading-state { text-align: center; color: #999; padding: 60rpx }
.empty-state { text-align: center; padding: 60rpx 0 }
.empty-icon { font-size: 80rpx; margin-bottom: 16rpx }
.empty-text { font-size: 32rpx; font-weight: 600; color: #999 }
.empty-sub { font-size: 26rpx; color: #ccc; margin-top: 8rpx }
.report-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06) }
.report-header { display: flex; justify-content: space-between; margin-bottom: 12rpx }
.report-type { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 10rpx; font-weight: 500 }
.report-type.weekly { background: #e3f2fd; color: #2196F3 }
.report-type.monthly { background: #e8f5e9; color: #4CAF50 }
.report-type.quarterly { background: #fff3e0; color: #FF9800 }
.report-date { font-size: 24rpx; color: #999 }
.report-summary { font-size: 26rpx; color: #555; line-height: 1.5; margin-bottom: 12rpx }
.report-scores { display: flex; gap: 24rpx; padding-top: 12rpx; border-top: 1rpx solid #f5f5f5 }
.score-item { display: flex; flex-direction: column; align-items: center }
.score-val { font-size: 28rpx; font-weight: bold; color: #4CAF50 }
.score-label { font-size: 20rpx; color: #999; margin-top: 2rpx }
/* dialog */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center }
.dialog-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 620rpx; max-height: 80vh; overflow-y: auto }
.dialog-title { font-size: 32rpx; font-weight: bold; text-align: center; margin-bottom: 24rpx }
.detail-content { display: flex; flex-direction: column; gap: 16rpx }
.detail-meta { display: flex; gap: 16rpx; font-size: 24rpx; color: #999; margin-bottom: 8rpx }
.detail-section { background: #faf8fb; border-radius: 12rpx; padding: 16rpx }
.detail-label { font-size: 26rpx; font-weight: 600; color: #4CAF50; margin-bottom: 8rpx }
.detail-text { font-size: 26rpx; color: #555; line-height: 1.7 }
.dialog-close { text-align: center; background: #f5f5f5; color: #666; padding: 16rpx; border-radius: 12rpx; font-size: 28rpx; margin-top: 16rpx }
</style>
