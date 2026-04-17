<template>
  <view class="report-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">📊</text>
        <view class="header-text">
          <text class="header-title">健康报告</text>
          <text class="header-sub">全方位了解您的健康状况</text>
        </view>
      </view>
    </view>

    <!-- 概览评分卡片 -->
    <view class="overview-cards">
      <view class="overview-card main-score" v-if="latestReport">
        <view class="score-circle">
          <view class="score-inner">
            <view class="score-val">{{ latestReport.overallScore || latestReport.regularityScore || '--' }}</view>
            <view class="score-label">综合评分</view>
          </view>
        </view>
        <view class="score-desc">{{ latestReport.summary || latestReport.symptomSummary || '健康状况总体良好' }}</view>
      </view>
      <view class="overview-card main-score empty-score" v-else>
        <view class="score-circle">
          <view class="score-inner">
            <view class="score-val">--</view>
            <view class="score-label">综合评分</view>
          </view>
        </view>
        <view class="score-desc">暂无报告数据</view>
      </view>
    </view>

    <!-- 评分明细 -->
    <view class="score-details">
      <view class="score-item" @click="showDetail('period')">
        <view class="score-icon">🌸</view>
        <view class="score-info">
          <view class="score-name">月经评分</view>
          <view class="score-bar">
            <view class="score-bar-fill" :style="{ width: (latestReport?.periodScore || 0) + '%' }"></view>
          </view>
        </view>
        <view class="score-num">{{ latestReport?.periodScore || '--' }}</view>
      </view>
      <view class="score-item" @click="showDetail('sleep')">
        <view class="score-icon">😴</view>
        <view class="score-info">
          <view class="score-name">睡眠评分</view>
          <view class="score-bar">
            <view class="score-bar-fill sleep" :style="{ width: (latestReport?.sleepScore || 0) + '%' }"></view>
          </view>
        </view>
        <view class="score-num">{{ latestReport?.sleepScore || '--' }}</view>
      </view>
      <view class="score-item" @click="showDetail('mood')">
        <view class="score-icon">😊</view>
        <view class="score-info">
          <view class="score-name">情绪评分</view>
          <view class="score-bar">
            <view class="score-bar-fill mood" :style="{ width: (latestReport?.moodScore || 0) + '%' }"></view>
          </view>
        </view>
        <view class="score-num">{{ latestReport?.moodScore || '--' }}</view>
      </view>
    </view>

    <!-- 生成报告选项 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">📋 生成健康报告</text>
      </view>
      <view class="gen-types">
        <view v-for="t in reportTypes" :key="t.value" class="gen-item" :class="{ active: genType === t.value }" @click="genType = t.value">
          <view class="gen-icon">{{ t.icon }}</view>
          <view class="gen-name">{{ t.name }}</view>
        </view>
      </view>
      <view class="gen-btn" @click="generateReport">
        <text>📊 生成报告</text>
      </view>
    </view>

    <!-- 报告列表 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">📑 我的报告</text>
      </view>
      <view v-if="loading" class="loading-state"><text>加载中...</text></view>
      <view v-else-if="!reports.length" class="she-empty">
        <text class="empty-icon">📊</text>
        <text class="empty-text">暂无健康报告</text>
        <text class="empty-sub">点击上方「生成报告」创建您的专属健康报告</text>
      </view>
      <view v-else class="report-list">
        <view v-for="r in reports" :key="r.id" class="report-card" @click="viewReport(r)">
          <view class="report-header">
            <view class="report-type" :class="'type-' + r.type">{{ typeName(r.type) }}</view>
            <view class="report-date">{{ formatDate(r.createTime || r.generatedAt) }}</view>
          </view>
          <view class="report-summary">{{ r.summary || r.symptomSummary || '健康状况总体良好' }}</view>
          <view class="report-scores" v-if="r.regularityScore">
            <view class="mini-score">
              <text class="ms-val">{{ r.regularityScore }}</text>
              <text class="ms-label">规律</text>
            </view>
            <view class="mini-score" v-if="r.avgCycleLength">
              <text class="ms-val">{{ r.avgCycleLength }}</text>
              <text class="ms-label">周期天</text>
            </view>
            <view class="mini-score" v-if="r.avgPeriodLength">
              <text class="ms-val">{{ r.avgPeriodLength }}</text>
              <text class="ms-label">经期天</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 报告详情弹窗 -->
    <view class="dialog-overlay" v-if="showDetail" @click="showDetail = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showDetail = false">关闭</text>
          <text class="sheet-title">📊 {{ currentReport?.typeName || '健康报告' }}</text>
          <text class="sheet-confirm" @click="exportReport">导出</text>
        </view>
        <view class="sheet-body" v-if="currentReport">
          <view class="detail-meta">
            <text>📅 {{ formatDate(currentReport.createTime || currentReport.generatedAt) }}</text>
            <text v-if="currentReport.overallScore">📈 综合评分：{{ currentReport.overallScore }}分</text>
          </view>
          <view class="detail-section" v-if="currentReport.symptomSummary">
            <view class="detail-label">📝 症状总结</view>
            <view class="detail-text">{{ currentReport.symptomSummary }}</view>
          </view>
          <view class="detail-section" v-if="currentReport.periodScore">
            <view class="detail-label">🌸 月经分析</view>
            <view class="detail-text">
              规律评分：{{ currentReport.periodScore }}分{{ currentReport.avgCycleLength ? ` | 平均周期：${currentReport.avgCycleLength}天` : '' }}{{ currentReport.avgPeriodLength ? ` | 平均经期：${currentReport.avgPeriodLength}天` : '' }}
            </view>
          </view>
          <view class="detail-section" v-if="currentReport.sleepScore">
            <view class="detail-label">😴 睡眠分析</view>
            <view class="detail-text">睡眠评分：{{ currentReport.sleepScore }}分{{ currentReport.sleepDuration ? ` | 平均时长：${currentReport.sleepDuration}小时` : '' }}</view>
          </view>
          <view class="detail-section" v-if="currentReport.moodScore">
            <view class="detail-label">😊 情绪分析</view>
            <view class="detail-text">情绪评分：{{ currentReport.moodScore }}分</view>
          </view>
          <view class="detail-section" v-if="currentReport.advice">
            <view class="detail-label">💡 健康建议</view>
            <view class="detail-text">{{ currentReport.advice }}</view>
          </view>
        </view>
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
const latestReport = ref(null)

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
    const res = await healthReport.history()
    const list = res?.data || []
    reports.value = list.map(r => ({
      id: r.id,
      type: r.type,
      summary: r.summary || r.symptomSummary,
      overallScore: r.overallScore,
      regularityScore: r.regularityScore,
      periodScore: r.periodScore,
      sleepScore: r.sleepScore,
      moodScore: r.moodScore,
      avgCycleLength: r.avgCycleLength,
      avgPeriodLength: r.avgPeriodLength,
      sleepDuration: r.sleepDuration,
      symptomSummary: r.symptomSummary,
      advice: r.advice,
      periodStart: r.periodStart,
      periodEnd: r.periodEnd,
      createTime: r.createTime,
      generatedAt: r.generatedAt,
    }))
    if (list.length > 0) {
      latestReport.value = reports.value[0]
    }
  } catch (e) {
    console.error('loadData error', e)
  } finally {
    loading.value = false
  }
}

async function generateReport() {
  uni.showLoading({ title: '生成中...' })
  try {
    await healthReport.generate(genType.value)
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
  } catch (e) {
    currentReport.value = r
  }
  showDetail.value = true
}

function showDetailTab(tab) {
  // Could navigate to detailed view
}

function exportReport() {
  uni.showToast({ title: '导出功能开发中', icon: 'none' })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.report-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 260rpx;
  background: $she-gradient-pregnant;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0; left: 0; right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.header-icon { font-size: 56rpx; }

.header-text { display: flex; flex-direction: column; }

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
}

.header-sub {
  font-size: 24rpx;
  color: rgba(255,255,255,0.85);
  margin-top: 4rpx;
}

// 概览卡片
.overview-cards {
  margin: -80rpx 32rpx 24rpx;
  position: relative;
  z-index: 1;
}

.overview-card {
  background: $she-white;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: $she-shadow-md;
}

.main-score {
  display: flex;
  align-items: center;
  gap: 28rpx;
}

.score-circle {
  width: 160rpx;
  height: 160rpx;
  border-radius: 80rpx;
  background: $she-gradient-pregnant;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.empty-score .score-circle {
  background: $she-bg;
}

.score-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
}

.score-val {
  font-size: 48rpx;
  font-weight: 700;
}

.empty-score .score-val { color: $she-muted; }

.score-label {
  font-size: 22rpx;
  opacity: 0.85;
}

.score-desc {
  flex: 1;
  font-size: 28rpx;
  color: $she-sub;
  line-height: 1.6;
}

// 评分明细
.score-details {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 20rpx;
  padding: 8rpx 0;
  box-shadow: $she-shadow-sm;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  border-bottom: 1rpx solid $she-border;
  transition: background 0.2s;

  &:last-child { border-bottom: none; }
  &:active { background: $she-bg; }
}

.score-icon { font-size: 36rpx; }

.score-info { flex: 1; }

.score-name {
  font-size: 28rpx;
  color: $she-text;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.score-bar {
  height: 8rpx;
  background: $she-bg;
  border-radius: 4rpx;
  overflow: hidden;
}

.score-bar-fill {
  height: 100%;
  background: $she-gradient-pregnant;
  border-radius: 4rpx;
  transition: width 0.3s ease;

  &.sleep { background: $she-gradient-trying; }
  &.mood { background: $she-gradient-nursing; }
}

.score-num {
  font-size: 32rpx;
  font-weight: 700;
  color: $she-pregnant;
  min-width: 60rpx;
  text-align: right;
}

// Section
.section-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $she-shadow-md;
}

.section-header {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $she-title;
}

.loading-state {
  text-align: center;
  color: $she-muted;
  padding: 60rpx;
}

// 生成类型
.gen-types {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.gen-item {
  flex: 1;
  background: $she-bg;
  border-radius: 16rpx;
  padding: 20rpx 0;
  text-align: center;
  border: 2rpx solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: $she-pregnant;
    background: rgba($she-pregnant, 0.06);
  }
}

.gen-icon { font-size: 36rpx; }
.gen-name { font-size: 24rpx; color: $she-sub; margin-top: 4rpx; }

.gen-btn {
  width: 100%;
  text-align: center;
  background: $she-gradient-pregnant;
  color: #fff;
  padding: 20rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba($she-pregnant, 0.3);
  transition: all 0.2s;

  &:active { transform: scale(0.98); }
}

// 报告列表
.report-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.report-card {
  background: $she-bg;
  border-radius: 16rpx;
  padding: 20rpx;
  transition: all 0.2s;

  &:active { transform: scale(0.99); }
}

.report-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.report-type {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
  font-weight: 500;
}

.type-weekly { background: rgba(#2196F3, 0.1); color: #2196F3; }
.type-monthly { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
.type-quarterly { background: rgba($she-nursing, 0.1); color: $she-nursing; }

.report-date { font-size: 24rpx; color: $she-muted; }

.report-summary {
  font-size: 26rpx;
  color: $she-sub;
  line-height: 1.5;
  margin-bottom: 12rpx;
}

.report-scores {
  display: flex;
  gap: 24rpx;
  padding-top: 12rpx;
  border-top: 1rpx solid $she-border;
}

.mini-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ms-val {
  font-size: 28rpx;
  font-weight: bold;
  color: $she-pregnant;
}

.ms-label {
  font-size: 20rpx;
  color: $she-muted;
  margin-top: 2rpx;
}

.bottom-placeholder { height: 32rpx; }

// 弹窗
.dialog-sheet { background: $she-white; border-radius: 32rpx 32rpx 0 0; padding-bottom: env(safe-area-inset-bottom); }

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid $she-border;
}

.sheet-cancel { font-size: 30rpx; color: $she-muted; min-width: 100rpx; }
.sheet-title { font-size: 32rpx; font-weight: 600; color: $she-text; }
.sheet-confirm { font-size: 30rpx; color: $she-pregnant; min-width: 100rpx; text-align: right; }

.sheet-body { padding: 24rpx; }

.detail-meta {
  display: flex;
  gap: 16rpx;
  font-size: 24rpx;
  color: $she-muted;
  margin-bottom: 20rpx;
}

.detail-section {
  background: $she-bg;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}

.detail-label {
  font-size: 26rpx;
  font-weight: 600;
  color: $she-pregnant;
  margin-bottom: 10rpx;
}

.detail-text {
  font-size: 26rpx;
  color: $she-sub;
  line-height: 1.7;
}
</style>
