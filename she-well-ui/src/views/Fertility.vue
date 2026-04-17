<template>
  <div class="fertility-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">🌈</span>
          生育力评估
        </h1>
        <p class="page-desc">科学评估生育力，助您好孕</p>
      </div>
    </div>

    <div class="content-grid">
      <!-- 左侧主内容 -->
      <div class="main-col">
        <!-- 生育力评估卡片 -->
        <el-card class="assessment-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="title-icon">📊</span>
                <span>生育力综合评估</span>
              </div>
              <el-tag :type="levelType" size="large" effect="dark">
                {{ levelBadge }}
              </el-tag>
            </div>
          </template>
          
          <div class="assessment-content" v-if="assessment.level">
            <div class="level-display">
              <div class="level-visual">
                <div class="level-circle" :class="'level-' + assessment.level">
                  <span class="level-icon">{{ levelIcon }}</span>
                </div>
              </div>
              <div class="level-info">
                <h3 class="level-title">{{ levelTitle }}</h3>
                <p class="level-desc">基于您的周期数据综合评估</p>
                <div class="level-score">
                  <span class="score-label">综合评分</span>
                  <span class="score-value">{{ assessment.score || 85 }}</span>
                </div>
              </div>
            </div>
            
            <div class="factors-section">
              <div class="section-title">
                <span class="title-icon">📊</span>
                评估因素
              </div>
              <div class="factors-tags">
                <el-tag 
                  v-for="(f, i) in assessment.factors" 
                  :key="i" 
                  class="factor-tag" 
                  type="info" 
                  size="default"
                >
                  {{ f }}
                </el-tag>
              </div>
            </div>
            
            <div class="suggestions-section">
              <div class="section-title">
                <span class="title-icon">💡</span>
                改善建议
              </div>
              <div class="suggestions-list">
                <div v-for="(s, i) in assessment.suggestions" :key="i" class="suggestion-item">
                  <div class="suggestion-num">{{ i + 1 }}</div>
                  <div class="suggestion-text">{{ s }}</div>
                </div>
              </div>
            </div>
          </div>
          
          <el-empty v-else description="暂无评估数据，请先记录经期">
            <el-button type="primary" @click="$router.push('/period')">去记录</el-button>
          </el-empty>
        </el-card>

        <!-- 安全期/易孕期切换 -->
        <el-card class="period-card" shadow="hover">
          <template #header>
            <el-radio-group v-model="activePeriod" class="period-tabs">
              <el-radio-button value="safe">
                <span class="tab-icon">🔒</span> 安全期
              </el-radio-button>
              <el-radio-button value="fertile">
                <span class="tab-icon">🎯</span> 易孕期
              </el-radio-button>
            </el-radio-group>
          </template>

          <!-- 安全期 -->
          <div v-if="activePeriod === 'safe'" class="period-content safe">
            <el-descriptions :column="1" border class="period-desc">
              <el-descriptions-item label="绝对安全期">
                <div class="date-range">
                  <span class="date-highlight">{{ safePeriod.absoluteSafeStart?.slice(5,10) || '—' }}</span>
                  <span class="date-separator">至</span>
                  <span class="date-highlight">{{ safePeriod.absoluteSafeEnd?.slice(5,10) || '—' }}</span>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="相对安全期">
                <div class="date-range">
                  <span>{{ safePeriod.relativeSafeStart?.slice(5,10) || '—' }}</span>
                  <span class="date-separator">至</span>
                  <span>{{ safePeriod.relativeSafeEnd?.slice(5,10) || '—' }}</span>
                </div>
              </el-descriptions-item>
            </el-descriptions>
            
            <div class="info-alert">
              <div class="alert-icon">⚠️</div>
              <div class="alert-content">
                <strong>温馨提示</strong>
                <p>安全期避孕并非100%有效，建议结合其他方式</p>
              </div>
            </div>
          </div>

          <!-- 易孕期 -->
          <div v-if="activePeriod === 'fertile'" class="period-content fertile">
            <el-descriptions :column="1" border class="period-desc">
              <el-descriptions-item label="易孕期窗口">
                <div class="date-range">
                  <span class="date-highlight fertile">{{ fertilePeriod.fertileStart?.slice(5,10) || '—' }}</span>
                  <span class="date-separator">至</span>
                  <span class="date-highlight fertile">{{ fertilePeriod.fertileEnd?.slice(5,10) || '—' }}</span>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="排卵日预测">
                <div class="date-range">
                  <el-tag type="danger" size="large">{{ fertilePeriod.peakOvulation?.slice(5,10) || '—' }}</el-tag>
                </div>
              </el-descriptions-item>
            </el-descriptions>
            
            <div class="peak-days" v-if="fertilePeriod.peakDays?.length">
              <div class="peak-header">
                <span class="peak-icon">🔴</span>
                <span class="peak-title">最佳受孕日</span>
              </div>
              <div class="peak-tags">
                <el-tag 
                  v-for="d in fertilePeriod.peakDays" 
                  :key="d" 
                  type="danger" 
                  size="large"
                  effect="dark"
                >
                  {{ d?.slice(5,10) || d }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧边栏 -->
      <div class="side-col">
        <!-- 今日受孕几率 -->
        <el-card class="chance-card" shadow="hover">
          <template #header>
            <div class="card-header no-border">
              <div class="header-title">
                <span class="title-icon">📊</span>
                <span>今日受孕几率</span>
              </div>
            </div>
          </template>
          
          <div class="chance-display" :class="'chance-' + dailyChance.chance">
            <div class="chance-visual">
              <div class="chance-circle">
                <span class="chance-icon">{{ chanceIcon }}</span>
              </div>
            </div>
            <div class="chance-info">
              <div class="chance-level">{{ chanceLevel }}</div>
              <div class="chance-desc">{{ dailyChance.description || '计算中...' }}</div>
            </div>
          </div>
          
          <div class="chance-legend">
            <div class="legend-item">
              <span class="dot high"></span>
              <span>高（排卵前后2天）</span>
            </div>
            <div class="legend-item">
              <span class="dot medium"></span>
              <span>中（排卵前后5天）</span>
            </div>
            <div class="legend-item">
              <span class="dot low"></span>
              <span>低（其他时间）</span>
            </div>
          </div>
        </el-card>

        <!-- AI 建议 -->
        <el-card class="ai-card" shadow="hover">
          <template #header>
            <div class="card-header no-border">
              <div class="header-title">
                <span class="title-icon">🤖</span>
                <span>AI 生育指导</span>
              </div>
              <el-button size="small" type="primary" @click="getAiAdvice" :loading="aiLoading">
                生成建议
              </el-button>
            </div>
          </template>
          
          <div class="ai-content">
            <div class="ai-advice" v-if="aiAdvice">{{ aiAdvice }}</div>
            <el-empty v-else-if="!aiLoading" description="点击按钮获取个性化生育建议" :image-size="60" />
            <el-skeleton v-else :rows="3" animated />
          </div>
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

const levelType = computed(() => ({ 
  high: 'success', 
  medium: 'warning', 
  low: 'danger' 
}[assessment.value.level] || 'info'))

const levelBadge = computed(() => ({ 
  high: '优秀', 
  medium: '一般', 
  low: '偏低' 
}[assessment.value.level] || '评估中'))

const levelIcon = computed(() => ({ 
  high: '🌟', 
  medium: '⭐', 
  low: '🌙' 
}[assessment.value.level] || '⭐'))

const levelTitle = computed(() => ({ 
  high: '生育力良好', 
  medium: '生育力一般', 
  low: '需注意' 
}[assessment.value.level] || '评估中'))

const chanceIcon = computed(() => ({ 
  high: '🎉', 
  medium: '💡', 
  low: '🌙' 
}[dailyChance.value.chance] || '⏳'))

const chanceLevel = computed(() => ({
  high: '易孕期',
  medium: '排卵期附近',
  low: '安全期'
}[dailyChance.value.chance] || '计算中'))

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
    const apiModule = await import('@/api')
    const api = apiModule.default
    const res = await api.post('/ai/consult', {
      question: '请根据我的生育力数据，给出备孕建议和注意事项',
      context: JSON.stringify({ fertilePeriod: fertilePeriod.value, assessment: assessment.value })
    })
    aiAdvice.value = res?.data?.answer || res?.data || '暂无建议'
  } catch (e) {
    ElMessage.error('AI 服务暂不可用')
  } finally {
    aiLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.fertility-page {
  padding: $she-space-6;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  padding: $she-space-5 $she-space-6;
  background: linear-gradient(135deg, #FCE4EC 0%, #F8BBD9 100%);
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-6;
}

.page-title {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  font-size: $she-font-size-2xl;
  font-weight: $she-font-weight-bold;
  color: $she-title;
  margin: 0;
  
  .title-icon {
    font-size: 28px;
  }
}

.page-desc {
  color: $she-sub;
  font-size: $she-font-size-sm;
  margin-top: $she-space-1;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: $she-space-6;
}

.main-col, .side-col {
  display: flex;
  flex-direction: column;
  gap: $she-space-6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  &.no-border {
    padding-bottom: 0;
  }
}

.header-title {
  display: flex;
  align-items: center;
  gap: $she-space-2;
  font-size: $she-font-size-lg;
  font-weight: $she-font-weight-semibold;
  
  .title-icon {
    font-size: 20px;
  }
}

// Assessment Card
.assessment-card {
  border-radius: $she-radius-xl;
  border: none;
}

.assessment-content {
  display: flex;
  flex-direction: column;
  gap: $she-space-6;
}

.level-display {
  display: flex;
  gap: $she-space-6;
  align-items: center;
}

.level-visual {
  flex-shrink: 0;
}

.level-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.level-high {
    background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  }
  
  &.level-medium {
    background: linear-gradient(135deg, #FFF3E0, #FFE0B2);
  }
  
  &.level-low {
    background: linear-gradient(135deg, #FFEBEE, #FFCDD2);
  }
  
  .level-icon {
    font-size: 56px;
  }
}

.level-info {
  flex: 1;
}

.level-title {
  font-size: $she-font-size-xl;
  font-weight: $she-font-weight-bold;
  color: $she-title;
  margin: 0 0 $she-space-2;
}

.level-desc {
  font-size: $she-font-size-sm;
  color: $she-sub;
  margin-bottom: $she-space-3;
}

.level-score {
  display: inline-flex;
  align-items: center;
  gap: $she-space-2;
  padding: $she-space-2 $she-space-4;
  background: $she-primary-bg;
  border-radius: $she-radius-full;
  
  .score-label {
    font-size: $she-font-size-xs;
    color: $she-sub;
  }
  
  .score-value {
    font-size: $she-font-size-lg;
    font-weight: $she-font-weight-bold;
    color: $she-primary;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: $she-space-2;
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-semibold;
  color: $she-title;
  margin-bottom: $she-space-3;
  
  .title-icon {
    font-size: 18px;
  }
}

.factors-section {
  padding: $she-space-4;
  background: var(--el-fill-color-light);
  border-radius: $she-radius-lg;
}

.factors-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $she-space-2;
}

.factor-tag {
  margin-right: 0;
}

.suggestions-section {
  // empty for now
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-3;
}

.suggestion-item {
  display: flex;
  gap: $she-space-3;
  align-items: flex-start;
}

.suggestion-num {
  width: 24px;
  height: 24px;
  background: $she-primary-bg;
  color: $she-primary;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $she-font-size-xs;
  font-weight: $she-font-weight-bold;
  flex-shrink: 0;
}

.suggestion-text {
  font-size: $she-font-size-sm;
  color: $she-text;
  line-height: 1.6;
}

// Period Card
.period-card {
  border-radius: $she-radius-xl;
  border: none;
}

.period-tabs {
  :deep(.el-radio-button__inner) {
    display: flex;
    align-items: center;
    gap: $she-space-2;
  }
  
  .tab-icon {
    font-size: 16px;
  }
}

.period-content {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
}

.period-desc {
  :deep(.el-descriptions__label) {
    font-weight: $she-font-weight-medium;
  }
}

.date-range {
  display: flex;
  align-items: center;
  gap: $she-space-2;
}

.date-highlight {
  font-weight: $she-font-weight-bold;
  color: $she-primary;
  
  &.fertile {
    color: $she-danger;
  }
}

.date-separator {
  color: $she-muted;
}

.info-alert {
  display: flex;
  gap: $she-space-3;
  padding: $she-space-4;
  background: #FFF3E0;
  border-radius: $she-radius-lg;
  
  .alert-icon {
    font-size: 24px;
    flex-shrink: 0;
  }
  
  .alert-content {
    strong {
      font-size: $she-font-size-sm;
      color: $she-title;
    }
    
    p {
      font-size: $she-font-size-xs;
      color: $she-sub;
      margin: $she-space-1 0 0;
    }
  }
}

.peak-days {
  padding: $she-space-4;
  background: #FFEBEE;
  border-radius: $she-radius-lg;
}

.peak-header {
  display: flex;
  align-items: center;
  gap: $she-space-2;
  margin-bottom: $she-space-3;
  
  .peak-icon {
    font-size: 20px;
  }
  
  .peak-title {
    font-size: $she-font-size-sm;
    font-weight: $she-font-weight-semibold;
    color: $she-danger;
  }
}

.peak-tags {
  display: flex;
  gap: $she-space-2;
  flex-wrap: wrap;
}

// Chance Card
.chance-card {
  border-radius: $she-radius-xl;
  border: none;
}

.chance-display {
  display: flex;
  align-items: center;
  gap: $she-space-4;
  padding: $she-space-4;
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-4;
  
  &.chance-high {
    background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  }
  
  &.chance-medium {
    background: linear-gradient(135deg, #FFF3E0, #FFE0B2);
  }
  
  &.chance-low {
    background: #f5f5f5;
  }
}

.chance-visual {
  flex-shrink: 0;
}

.chance-circle {
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chance-icon {
  font-size: 40px;
}

.chance-info {
  flex: 1;
}

.chance-level {
  font-size: $she-font-size-lg;
  font-weight: $she-font-weight-bold;
  color: $she-title;
  margin-bottom: $she-space-1;
}

.chance-desc {
  font-size: $she-font-size-sm;
  color: $she-sub;
}

.chance-legend {
  display: flex;
  flex-direction: column;
  gap: $she-space-2;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: $she-space-2;
  font-size: $she-font-size-sm;
  color: $she-sub;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  
  &.high { background: #4CAF50; }
  &.medium { background: #FF9800; }
  &.low { background: #ccc; }
}

// AI Card
.ai-card {
  border-radius: $she-radius-xl;
  border: none;
}

.ai-content {
  min-height: 150px;
}

.ai-advice {
  font-size: $she-font-size-sm;
  line-height: 1.8;
  color: $she-text;
  white-space: pre-wrap;
}
</style>
