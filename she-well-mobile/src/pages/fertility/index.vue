<template>
  <view class="fertility-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">🌈</text>
        <view class="header-text">
          <text class="header-title">生育力评估</text>
          <text class="header-sub">科学备孕 · 精准推算</text>
        </view>
      </view>
    </view>

    <!-- 评估卡片 -->
    <view class="section-card assessment-card" :class="'level-' + assessment.level">
      <view class="assess-header">
        <view class="assess-icon">{{ levelIcon }}</view>
        <view class="assess-info">
          <view class="assess-title">{{ levelTitle }}</view>
          <view class="assess-subtitle">综合生育力评估</view>
        </view>
        <view class="assess-badge" :class="'badge-' + assessment.level">{{ levelBadge }}</view>
      </view>
      <view class="assess-factors">
        <view v-for="(f, i) in assessment.factors" :key="i" class="factor-item">
          <text class="factor-dot">•</text>{{ f }}
        </view>
      </view>
      <view class="assess-suggestions">
        <view class="suggestion-title">💡 改善建议</view>
        <view v-for="(s, i) in assessment.suggestions" :key="i" class="suggestion-item">
          {{ i + 1 }}. {{ s }}
        </view>
      </view>
    </view>

    <!-- 安全期 / 易孕期 -->
    <view class="period-tabs">
      <view class="period-tab" :class="{ active: activePeriod === 'safe' }" @click="activePeriod = 'safe'">🔒 安全期</view>
      <view class="period-tab" :class="{ active: activePeriod === 'fertile' }" @click="activePeriod = 'fertile'">🎯 易孕期</view>
    </view>

    <view v-if="activePeriod === 'safe'" class="period-content">
      <view class="period-card safe">
        <view class="period-card-title">🛡️ 安全期计算</view>
        <view class="period-row">
          <view class="period-label">绝对安全期</view>
          <view class="period-value highlight">{{ safePeriod.absoluteSafeStart?.slice(5) || '—' }} ~ {{ safePeriod.absoluteSafeEnd?.slice(5) || '—' }}</view>
        </view>
        <view class="period-desc">经期第1-4天，受孕概率最低</view>
        <view class="period-row">
          <view class="period-label">相对安全期</view>
          <view class="period-value">{{ safePeriod.relativeSafeStart?.slice(5) || '—' }} ~ {{ safePeriod.relativeSafeEnd?.slice(5) || '—' }}</view>
        </view>
        <view class="period-desc">易孕期结束后至下次月经前</view>
      </view>
      <view class="disclaimer">⚠️ 安全期避孕法并非100%有效，建议结合其他避孕方式</view>
    </view>

    <view v-if="activePeriod === 'fertile'" class="period-content">
      <view class="period-card fertile">
        <view class="period-card-title">🌟 易孕期提示</view>
        <view class="period-row">
          <view class="period-label">易孕期窗口</view>
          <view class="period-value highlight">{{ fertilePeriod.fertileStart?.slice(5) || '—' }} ~ {{ fertilePeriod.fertileEnd?.slice(5) || '—' }}</view>
        </view>
        <view class="period-row">
          <view class="period-label">排卵日预测</view>
          <view class="period-value highlight" style="color:$she-primary">{{ fertilePeriod.peakOvulation?.slice(5) || '—' }}</view>
        </view>
        <view class="period-desc">排卵日前后1-2天为最佳受孕时机</view>
        <view class="peak-days" v-if="fertilePeriod.peakDays?.length">
          <view class="peak-title">🔴 最佳受孕日</view>
          <view class="peak-list">
            <view v-for="d in fertilePeriod.peakDays" :key="d" class="peak-day">{{ d?.slice(5) || d }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 每日受孕几率 -->
    <view class="section-card">
      <view class="section-title">📊 今日受孕几率</view>
      <view class="chance-card" :class="'chance-' + dailyChance.chance">
        <view class="chance-icon">{{ chanceIcon }}</view>
        <view class="chance-text">{{ dailyChance.description || '加载中...' }}</view>
      </view>
      <view class="chance-legend">
        <view class="legend-item"><view class="legend-dot high"></view>高 (排卵日前后2天)</view>
        <view class="legend-item"><view class="legend-dot medium"></view>中 (排卵日前后5天)</view>
        <view class="legend-item"><view class="legend-dot low"></view>低 (其他时间)</view>
      </view>
    </view>

    <!-- AI 生育建议 -->
    <view class="section-card">
      <view class="section-title">🤖 AI 生育健康指导</view>
      <view class="ai-advice" v-if="aiAdvice">{{ aiAdvice }}</view>
      <view class="ai-loading" v-else-if="aiLoading">正在生成个性化建议...</view>
      <view class="empty-tip" v-else>基于您的周期数据获取个性化生育建议</view>
      <view class="ai-btn" @click="getAiAdvice" :class="{disabled: aiLoading}">
        {{ aiLoading ? '生成中...' : '获取 AI 生育指导' }}
      </view>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fertility, ai } from '@/api/index.js'

const activePeriod = ref('safe')
const assessment = ref({ level: '', factors: [], suggestions: [] })
const safePeriod = ref({})
const fertilePeriod = ref({})
const dailyChance = ref({ chance: '', description: '' })
const aiAdvice = ref('')
const aiLoading = ref(false)

const levelIcon = computed(() => ({ high: '🌟', medium: '⭐', low: '🌙' }[assessment.value.level] || '⭐'))
const levelTitle = computed(() => ({ high: '生育力良好', medium: '生育力一般', low: '需注意' }[assessment.value.level] || '评估中'))
const levelBadge = computed(() => ({ high: '优秀', medium: '一般', low: '偏低' }[assessment.value.level] || '—'))

const chanceIcon = computed(() => ({ high: '🎉', medium: '💡', low: '🌙' }[dailyChance.value.chance] || '⏳'))
const chanceText = computed(() => dailyChance.value.description || '计算中...')

async function loadData() {
  try {
    const [assessRes, safeRes, fertileRes, chanceRes] = await Promise.all([
      fertility.assessment(),
      fertility.safePeriod(),
      fertility.fertilePeriod(),
      fertility.dailyChance({}),
    ])
    assessment.value = {
      level: assessRes?.data?.level || assessRes?.data?.level || 'medium',
      factors: assessRes?.data?.factors || [],
      suggestions: assessRes?.data?.suggestions || [],
    }
    safePeriod.value = safeRes?.data || {}
    fertilePeriod.value = fertileRes?.data || {}
    dailyChance.value = {
      chance: chanceRes?.data?.chance || 'low',
      description: chanceRes?.data?.description || '',
    }
  } catch (e) {
    assessment.value = { level: 'medium', factors: ['数据加载失败，请检查网络'], suggestions: [] }
  }
}

async function getAiAdvice() {
  if (aiLoading.value) return
  aiLoading.value = true
  try {
    const res = await ai.healthAdvice({ type: 'fertility', level: assessment.value.level })
    aiAdvice.value = res?.data?.advice || res?.advice || '暂无建议'
  } catch (e) {
    aiAdvice.value = '获取建议失败，请稍后再试'
  } finally {
    aiLoading.value = false
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.fertility-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 260rpx;
  background: $she-gradient-trying;
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

.header-icon { font-size: 64rpx; }

.header-text { display: flex; flex-direction: column; }

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

// 评估卡片
.section-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: $she-shadow-md;
}

.assessment-card {
  margin: -80rpx 32rpx 24rpx;
  position: relative;
  z-index: 1;
}

.level-high { border-left: 8rpx solid $she-pregnant; }
.level-medium { border-left: 8rpx solid $she-nursing; }
.level-low { border-left: 8rpx solid $she-primary; }

.assess-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.assess-icon { font-size: 56rpx; }
.assess-info { flex: 1; }
.assess-title { font-size: 32rpx; font-weight: bold; color: $she-title; }
.assess-subtitle { font-size: 24rpx; color: $she-muted; }

.assess-badge {
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.badge-high { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
.badge-medium { background: rgba($she-nursing, 0.1); color: $she-nursing; }
.badge-low { background: rgba($she-primary, 0.1); color: $she-primary; }

.assess-factors {
  background: $she-bg;
  border-radius: 12rpx;
  padding: 16rpx;
  margin-bottom: 16rpx;
}

.factor-item {
  font-size: 26rpx;
  color: $she-sub;
  line-height: 1.8;
  display: flex;
  gap: 8rpx;
}

.factor-dot { color: $she-trying; }

.assess-suggestions {
  background: rgba($she-pregnant, 0.06);
  border-radius: 12rpx;
  padding: 16rpx;
}

.suggestion-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $she-pregnant;
  margin-bottom: 8rpx;
}

.suggestion-item {
  font-size: 26rpx;
  color: $she-sub;
  line-height: 1.7;
}

// 安全期/易孕期
.period-tabs {
  display: flex;
  margin: 0 32rpx 16rpx;
  background: $she-white;
  border-radius: 16rpx;
  padding: 6rpx;
  box-shadow: $she-shadow-sm;
}

.period-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: $she-muted;
  transition: all 0.2s;

  &.active {
    background: $she-gradient-trying;
    color: #fff;
    font-weight: 600;
  }
}

.period-content { margin: 0 32rpx 24rpx; }

.period-card {
  background: $she-white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: $she-shadow-md;
}

.period-card.safe { border-top: 4rpx solid $she-pregnant; }
.period-card.fertile { border-top: 4rpx solid $she-primary; }

.period-card-title {
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
  color: $she-title;
}

.period-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  border-bottom: 1rpx solid $she-border;
}

.period-label { font-size: 28rpx; color: $she-sub; }
.period-value { font-size: 28rpx; color: $she-text; font-weight: 500 }
.period-value.highlight { color: $she-primary; font-weight: 700 }
.period-desc { font-size: 24rpx; color: $she-muted; margin-top: 8rpx }

.peak-days { margin-top: 16rpx; }
.peak-title { font-size: 26rpx; color: $she-primary; font-weight: 600; margin-bottom: 8rpx }
.peak-list { display: flex; gap: 12rpx; flex-wrap: wrap; }
.peak-day {
  background: rgba($she-primary, 0.1);
  color: $she-primary;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  font-weight: 600;
}

.disclaimer {
  font-size: 24rpx;
  color: $she-nursing;
  margin-top: 12rpx;
  text-align: center;
  background: rgba($she-nursing, 0.08);
  padding: 12rpx;
  border-radius: 12rpx;
}

// 每日几率
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
  color: $she-title;
}

.chance-card {
  background: $she-bg;
  border-radius: 20rpx;
  padding: 28rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.chance-icon { font-size: 48rpx; }
.chance-text { font-size: 28rpx; color: $she-text; font-weight: 500 }

.chance-high { border-left: 8rpx solid $she-pregnant; }
.chance-medium { border-left: 8rpx solid $she-nursing; }
.chance-low { border-left: 8rpx solid $she-border; }

.chance-legend {
  display: flex;
  gap: 24rpx;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  color: $she-muted;
}

.legend-dot { width: 16rpx; height: 16rpx; border-radius: 50%; }
.legend-dot.high { background: $she-pregnant; }
.legend-dot.medium { background: $she-nursing; }
.legend-dot.low { background: $she-border; }

// AI
.ai-advice {
  background: $she-bg;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 26rpx;
  color: $she-sub;
  line-height: 1.7;
  margin-bottom: 16rpx;
}

.ai-loading {
  text-align: center;
  color: $she-trying;
  font-size: 26rpx;
  padding: 20rpx;
}

.empty-tip {
  text-align: center;
  color: $she-muted;
  font-size: 26rpx;
  padding: 12rpx;
  margin-bottom: 16rpx;
}

.ai-btn {
  width: 100%;
  text-align: center;
  background: $she-gradient-trying;
  color: #fff;
  padding: 20rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  transition: all 0.2s ease;

  &:active { transform: scale(0.98); }
  &.disabled { opacity: 0.6; }
}

.bottom-placeholder { height: 32rpx; }
</style>
