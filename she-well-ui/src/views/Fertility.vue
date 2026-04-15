<template>
  <div class="fertility-page">
    <div class="page-header">
      <h2>🌈 生育力评估</h2>
    </div>

    <div class="content-grid">
      <!-- 左侧：评估 + 安全期/易孕期 -->
      <div class="left-col">
        <!-- 生育力评估卡片 -->
        <el-card class="assessment-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>生育力综合评估</span>
              <el-tag :type="levelType">{{ levelBadge }}</el-tag>
            </div>
          </template>
          <div class="assessment-content" v-if="assessment.level">
            <div class="level-display">
              <div class="level-icon">{{ levelIcon }}</div>
              <div class="level-info">
                <div class="level-title">{{ levelTitle }}</div>
                <div class="level-desc">基于您的周期数据综合评估</div>
              </div>
            </div>
            <div class="factors-list">
              <div class="factor-title">📊 评估因素</div>
              <el-tag v-for="(f, i) in assessment.factors" :key="i" class="factor-tag" type="info" size="small">{{ f }}</el-tag>
            </div>
            <div class="suggestions-list">
              <div class="suggestion-title">💡 改善建议</div>
              <div v-for="(s, i) in assessment.suggestions" :key="i" class="suggestion-item">
                <span class="num">{{ i + 1 }}</span>
                <span>{{ s }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无评估数据，请先记录经期" />
        </el-card>

        <!-- 安全期/易孕期切换 -->
        <el-card class="period-card" shadow="hover">
          <template #header>
            <el-radio-group v-model="activePeriod">
              <el-radio-button value="safe">🔒 安全期</el-radio-button>
              <el-radio-button value="fertile">🎯 易孕期</el-radio-button>
            </el-radio-group>
          </template>

          <div v-if="activePeriod === 'safe'" class="period-content safe">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="绝对安全期">
                <span class="highlight">{{ safePeriod.absoluteSafeStart?.slice(5,10) || '—' }} ~ {{ safePeriod.absoluteSafeEnd?.slice(5,10) || '—' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="相对安全期">
                {{ safePeriod.relativeSafeStart?.slice(5,10) || '—' }} ~ {{ safePeriod.relativeSafeEnd?.slice(5,10) || '—' }}
              </el-descriptions-item>
            </el-descriptions>
            <el-alert type="warning" :closable="false" show-icon>
              <template #title>安全期避孕并非100%有效，建议结合其他方式
              </template>
            </el-alert>
          </div>

          <div v-if="activePeriod === 'fertile'" class="period-content fertile">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="易孕期窗口">
                <span class="highlight">{{ fertilePeriod.fertileStart?.slice(5,10) || '—' }} ~ {{ fertilePeriod.fertileEnd?.slice(5,10) || '—' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="排卵日预测">
                <span class="highlight red">{{ fertilePeriod.peakOvulation?.slice(5,10) || '—' }}</span>
              </el-descriptions-item>
            </el-descriptions>
            <div class="peak-days" v-if="fertilePeriod.peakDays?.length">
              <div class="peak-title">🔴 最佳受孕日</div>
              <el-tag v-for="d in fertilePeriod.peakDays" :key="d" type="danger" size="large">{{ d?.slice(5,10) || d }}</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：每日受孕几率 + AI 建议 -->
      <div class="right-col">
        <!-- 每日受孕几率 -->
        <el-card class="chance-card" shadow="hover">
          <template #header><span>📊 今日受孕几率</span></template>
          <div class="chance-display" :class="'chance-' + dailyChance.chance">
            <div class="chance-icon">{{ chanceIcon }}</div>
            <div class="chance-text">{{ dailyChance.description || '计算中...' }}</div>
          </div>
          <div class="chance-legend">
            <div class="legend-item"><span class="dot high"></span>高（排卵前后2天）</div>
            <div class="legend-item"><span class="dot medium"></span>中（排卵前后5天）</div>
            <div class="legend-item"><span class="dot low"></span>低（其他时间）</div>
          </div>
        </el-card>

        <!-- AI 建议 -->
        <el-card class="ai-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🤖 AI 生育健康指导</span>
              <el-button size="small" type="primary" @click="getAiAdvice" :loading="aiLoading">生成建议</el-button>
            </div>
          </template>
          <div class="ai-advice" v-if="aiAdvice">{{ aiAdvice }}</div>
          <el-empty v-else-if="!aiLoading" description="点击按钮获取个性化生育建议" :image-size="60" />
          <el-skeleton v-else :rows="3" animated />
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getFertilityAssessment, getSafePeriod, getFertilePeriod, getDailyChance } from '@/api/index'
import { ElMessage } from 'element-plus'

const activePeriod = ref('safe')
const assessment = ref({})
const safePeriod = ref({})
const fertilePeriod = ref({})
const dailyChance = ref({})
const aiAdvice = ref('')
const aiLoading = ref(false)

const levelType = computed(() => ({ high: 'success', medium: 'warning', low: 'danger' }[assessment.value.level] || 'info'))
const levelBadge = computed(() => ({ high: '优秀', medium: '一般', low: '偏低' }[assessment.value.level] || '评估中'))
const levelIcon = computed(() => ({ high: '🌟', medium: '⭐', low: '🌙' }[assessment.value.level] || '⭐'))
const levelTitle = computed(() => ({ high: '生育力良好', medium: '生育力一般', low: '需注意' }[assessment.value.level] || '评估中'))
const chanceIcon = computed(() => ({ high: '🎉', medium: '💡', low: '🌙' }[dailyChance.value.chance] || '⏳'))

async function loadData() {
  try {
    const [aRes, sRes, fRes, cRes] = await Promise.all([
      getFertilityAssessment(),
      getSafePeriod(),
      getFertilePeriod(),
      getDailyChance(),
    ])
    assessment.value = aRes?.data || {}
    safePeriod.value = sRes?.data || {}
    fertilePeriod.value = fRes?.data || {}
    dailyChance.value = cRes?.data || {}
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

async function getAiAdvice() {
  aiLoading.value = true
  try {
    await new Promise(r => setTimeout(r, 1500))
    aiAdvice.value = `根据您的周期数据分析：\n\n1. 您的易孕期预计在 ${fertilePeriod.value.fertileStart?.slice(5,10) || '—'} ~ ${fertilePeriod.value.fertileEnd?.slice(5,10) || '—'} 之间，排卵日约为 ${fertilePeriod.value.peakOvulation?.slice(5,10) || '—'}。\n\n2. 建议在此期间保持规律作息，避免熬夜，提高受孕几率。\n\n3. 每日补充叶酸 0.4mg，提前3个月开始服用效果最佳。\n\n4. 如有需要，可使用排卵试纸辅助监测排卵日。`
  } catch (e) {
    ElMessage.error('生成失败')
  } finally {
    aiLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.fertility-page { padding: 24px }
.page-header { margin-bottom: 24px }
.page-header h2 { font-size: 24px; font-weight: 600; color: var(--el-text-color-primary) }
.content-grid { display: grid; grid-template-columns: 1fr 380px; gap: 20px }
.left-col, .right-col { display: flex; flex-direction: column; gap: 20px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.assessment-content { display: flex; flex-direction: column; gap: 20px }
.level-display { display: flex; gap: 20px; align-items: center }
.level-icon { font-size: 56px }
.level-title { font-size: 22px; font-weight: 600; color: var(--el-text-color-primary) }
.level-desc { font-size: 14px; color: var(--el-text-color-secondary); margin-top: 4px }
.factors-list, .suggestions-list { display: flex; flex-direction: column; gap: 8px }
.factor-title, .suggestion-title { font-size: 14px; font-weight: 600; color: var(--el-text-color-secondary) }
.factor-tag { margin-right: 8px }
.suggestion-item { display: flex; gap: 10px; font-size: 14px; line-height: 1.6; color: var(--el-text-color-regular) }
.num { width: 20px; height: 20px; background: var(--el-color-primary-light-8); color: var(--el-color-primary); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; flex-shrink: 0 }
.highlight { font-weight: 600; color: var(--el-color-primary) }
.highlight.red { color: #E91E63 }
.period-content { display: flex; flex-direction: column; gap: 16px }
.peak-days { display: flex; flex-direction: column; gap: 10px; margin-top: 8px }
.peak-title { font-size: 14px; font-weight: 600; color: #E91E63 }
.chance-display { display: flex; align-items: center; gap: 16px; padding: 20px; border-radius: 12px; margin-bottom: 16px }
.chance-high { background: linear-gradient(135deg, #e8f5e9, #c8e6c9) }
.chance-medium { background: linear-gradient(135deg, #fff3e0, #ffe0b2) }
.chance-low { background: #f5f5f5 }
.chance-icon { font-size: 40px }
.chance-text { font-size: 16px; font-weight: 500 }
.chance-legend { display: flex; gap: 20px; font-size: 13px; color: var(--el-text-color-secondary) }
.legend-item { display: flex; align-items: center; gap: 6px }
.dot { width: 12px; height: 12px; border-radius: 50% }
.dot.high { background: #4CAF50 }
.dot.medium { background: #FF9800 }
.dot.low { background: #ccc }
.ai-advice { font-size: 14px; line-height: 1.8; color: var(--el-text-color-regular); white-space: pre-wrap }
</style>
