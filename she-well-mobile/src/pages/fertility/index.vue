<template>
  <view class="fertility-page">
    <view class="header">
      <view class="header-title">🌈 生育力评估</view>
    </view>

    <!-- 生育力评估卡片 -->
    <view class="assessment-card" :class="'level-' + assessment.level">
      <view class="assess-header">
        <view class="assess-icon">{{ levelIcon }}</view>
        <view class="assess-info">
          <view class="assess-title">{{ levelTitle }}</view>
          <view class="assess-subtitle">综合生育力评估</view>
        </view>
        <view class="assess-badge">{{ levelBadge }}</view>
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
          <view class="period-value highlight" style="color:#E91E63">{{ fertilePeriod.peakOvulation?.slice(5) || '—' }}</view>
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
    <view class="chance-section">
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
    <view class="section">
      <view class="section-title">🤖 AI 生育健康指导</view>
      <view class="ai-advice" v-if="aiAdvice">{{ aiAdvice }}</view>
      <view class="ai-loading" v-else-if="aiLoading">正在生成个性化建议...</view>
      <view class="empty-tip" v-else>基于您的周期数据获取个性化生育建议</view>
      <view class="ai-btn" @click="getAiAdvice" :class="{disabled: aiLoading}">
        {{ aiLoading ? '生成中...' : '获取 AI 生育指导' }}
      </view>
    </view>
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

<style scoped>
.fertility-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #9C27B0, #E91E63); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }

/* 评估卡片 */
.assessment-card { margin: 24rpx 32rpx; border-radius: 24rpx; padding: 28rpx; background: #fff; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08) }
.level-high { border-left: 8rpx solid #4CAF50 }
.level-medium { border-left: 8rpx solid #FF9800 }
.level-low { border-left: 8rpx solid #E91E63 }
.assess-header { display: flex; align-items: center; gap: 16rpx; margin-bottom: 20rpx }
.assess-icon { font-size: 56rpx }
.assess-info { flex: 1 }
.assess-title { font-size: 32rpx; font-weight: bold; color: #333 }
.assess-subtitle { font-size: 24rpx; color: #999 }
.assess-badge { padding: 6rpx 16rpx; border-radius: 20rpx; font-size: 24rpx; font-weight: 600 }
.level-high .assess-badge { background: #e8f5e9; color: #4CAF50 }
.level-medium .assess-badge { background: #fff3e0; color: #FF9800 }
.level-low .assess-badge { background: #fce4ec; color: #E91E63 }
.assess-factors { background: #faf8fb; border-radius: 12rpx; padding: 16rpx; margin-bottom: 16rpx }
.factor-item { font-size: 26rpx; color: #555; line-height: 1.8; display: flex; gap: 8rpx }
.factor-dot { color: #9C27B0 }
.assess-suggestions { background: #f0f9eb; border-radius: 12rpx; padding: 16rpx }
.suggestion-title { font-size: 26rpx; font-weight: 600; color: #4CAF50; margin-bottom: 8rpx }
.suggestion-item { font-size: 26rpx; color: #555; line-height: 1.7 }

/* 安全期/易孕期 */
.period-tabs { display: flex; margin: 0 32rpx 16rpx; background: #fff; border-radius: 16rpx; padding: 8rpx }
.period-tab { flex: 1; text-align: center; padding: 16rpx; border-radius: 12rpx; font-size: 28rpx; color: #999; transition: all 0.2s }
.period-tab.active { background: linear-gradient(135deg, #9C27B0, #E91E63); color: #fff; font-weight: 600 }
.period-content { margin: 0 32rpx 24rpx }
.period-card { background: #fff; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06) }
.period-card.safe { border-top: 4rpx solid #4CAF50 }
.period-card.fertile { border-top: 4rpx solid #E91E63 }
.period-card-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.period-row { display: flex; justify-content: space-between; align-items: center; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.period-label { font-size: 28rpx; color: #666 }
.period-value { font-size: 28rpx; color: #333; font-weight: 500 }
.period-value.highlight { color: #E91E63; font-weight: 700 }
.period-desc { font-size: 24rpx; color: #999; margin-top: 8rpx }
.peak-days { margin-top: 16rpx }
.peak-title { font-size: 26rpx; color: #E91E63; font-weight: 600; margin-bottom: 8rpx }
.peak-list { display: flex; gap: 12rpx }
.peak-day { background: #fce4ec; color: #E91E63; padding: 8rpx 20rpx; border-radius: 20rpx; font-size: 26rpx; font-weight: 600 }
.disclaimer { font-size: 24rpx; color: #FF9800; margin-top: 12rpx; text-align: center }

/* 每日几率 */
.chance-section { margin: 0 32rpx 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.chance-card { background: #fff; border-radius: 20rpx; padding: 28rpx; display: flex; align-items: center; gap: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06) }
.chance-icon { font-size: 48rpx }
.chance-text { font-size: 28rpx; color: #333; font-weight: 500 }
.chance-high { border-left: 8rpx solid #4CAF50 }
.chance-medium { border-left: 8rpx solid #FF9800 }
.chance-low { border-left: 8rpx solid #ccc }
.chance-legend { display: flex; gap: 24rpx; margin-top: 12rpx; justify-content: center }
.legend-item { display: flex; align-items: center; gap: 8rpx; font-size: 22rpx; color: #999 }
.legend-dot { width: 16rpx; height: 16rpx; border-radius: 50% }
.legend-dot.high { background: #4CAF50 }
.legend-dot.medium { background: #FF9800 }
.legend-dot.low { background: #ccc }

/* AI */
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.ai-advice { background: #faf8fb; border-radius: 12rpx; padding: 20rpx; font-size: 26rpx; color: #555; line-height: 1.7; margin-bottom: 16rpx }
.ai-loading { text-align: center; color: #9C27B0; font-size: 26rpx; padding: 20rpx }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 12rpx }
.ai-btn { text-align: center; background: linear-gradient(135deg, #9C27B0, #E91E63); color: #fff; padding: 20rpx; border-radius: 12rpx; font-size: 28rpx }
.ai-btn.disabled { opacity: 0.6 }
</style>
