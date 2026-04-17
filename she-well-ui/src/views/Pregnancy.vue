<template>
  <div class="pregnancy-page">
    <!-- 顶部欢迎区域 -->
    <div class="hero-section" :class="{ 'has-record': !!record }">
      <div class="hero-content">
        <div class="hero-icon">🤰</div>
        <div class="hero-text">
          <h1>{{ record ? '怀孕进行中' : '记录孕期旅程' }}</h1>
          <p v-if="!record">确认怀孕后开始记录孕期，陪伴您每一个珍贵时刻</p>
          <p v-else>孕 {{ record.currentWeek || 0 }} 周 + {{ record.currentDay || 0 }} 天</p>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="deco-circle circle-1"></div>
        <div class="deco-circle circle-2"></div>
        <div class="deco-circle circle-3"></div>
      </div>
    </div>

    <!-- 怀孕进行中状态 -->
    <template v-if="record">
      <el-card class="record-card" shadow="hover">
        <div class="record-header">
          <div class="record-badge">
            <span class="badge-dot"></span>
            孕期中
          </div>
          <el-tag type="success" size="large">距离预产期 {{ dueDays }} 天</el-tag>
        </div>
        
        <div class="progress-section">
          <div class="progress-info">
            <span>孕期进度</span>
            <span class="progress-percent">{{ Math.round(pregnancyProgress) }}%</span>
          </div>
          <el-progress :percentage="pregnancyProgress" :stroke-width="12" :show-text="false" color="#4CAF50" />
          <div class="progress-labels">
            <span>孕早期</span>
            <span>孕中期</span>
            <span>孕晚期</span>
          </div>
        </div>

        <el-row :gutter="20" class="stats-row">
          <el-col :span="8">
            <div class="stat-box">
              <div class="stat-value">{{ record.currentWeek || 0 }}</div>
              <div class="stat-name">孕周</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-box">
              <div class="stat-value">{{ record.currentDay || 0 }}</div>
              <div class="stat-name">孕天</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-box">
              <div class="stat-value due-date">{{ formatDate(record.dueDate) }}</div>
              <div class="stat-name">预产期</div>
            </div>
          </el-col>
        </el-row>

        <div class="record-actions">
          <el-button type="success" size="large" @click="showBabyDialog = true">
            🎉 宝宝已出生
          </el-button>
        </div>
      </el-card>
    </template>

    <!-- 创建怀孕记录 -->
    <el-card class="create-card" shadow="hover">
      <template #header>
        <div class="card-header-custom">
          <div class="header-icon">📝</div>
          <span>创建怀孕记录</span>
        </div>
      </template>
      
      <div class="create-content">
        <div class="create-intro" v-if="!record">
          <p>准确记录末次月经日期，帮助我们为您计算预产期和当前孕周</p>
        </div>
        <el-form :model="pregForm" inline class="preg-form">
          <el-form-item label="末次月经开始日期">
            <el-date-picker 
              v-model="pregForm.lastPeriodStart" 
              type="date" 
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled="!!record"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="createRecord" :disabled="!!record">
              {{ record ? '已创建记录' : '开始记录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 宝宝出生弹窗 -->
    <el-dialog v-model="showBabyDialog" title="宝宝已出生" width="480px" :close-on-click-modal="false">
      <div class="birth-celebration">
        <div class="celebration-icon">🎊</div>
        <p class="celebration-text">恭喜您！宝宝平安降生</p>
      </div>
      <el-form :model="babyForm" label-width="100px" class="birth-form">
        <el-form-item label="宝宝姓名">
          <el-input v-model="babyForm.name" placeholder="为宝宝取个名字" />
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker 
            v-model="babyForm.birthDate" 
            type="date" 
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="babyForm.gender">
            <el-radio label="male">👦 男宝宝</el-radio>
            <el-radio label="female">👧 女宝宝</el-radio>
            <el-radio label="unknown">🤔 未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分娩方式">
          <el-radio-group v-model="babyForm.deliveryType">
            <el-radio label="natural">自然分娩</el-radio>
            <el-radio label="cesarean">剖宫产</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBabyDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmBabyBorn">确认宝宝出生</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPregnancyRecord, addPregnancyRecord, babyBorn } from '@/api'

const record = ref<any>(null)
const showBabyDialog = ref(false)
const pregForm = ref({ lastPeriodStart: '' })
const babyForm = ref({ name: '', birthDate: '', gender: 'unknown', deliveryType: 'natural' })

// 计算孕期进度 (40周为满期)
const pregnancyProgress = computed(() => {
  if (!record.value?.currentWeek) return 0
  const totalWeeks = 40
  const currentWeeks = (record.value.currentWeek || 0) + ((record.value.currentDay || 0) / 7)
  return Math.min(100, (currentWeeks / totalWeeks) * 100)
})

// 计算距离预产期天数
const dueDays = computed(() => {
  if (!record.value?.dueDate) return 0
  const due = new Date(record.value.dueDate)
  const today = new Date()
  const diff = due.getTime() - today.getTime()
  return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
})

function formatDate(date: string) {
  if (!date) return '—'
  return date.slice(5, 10).replace('-', '月') + '日'
}

async function createRecord() {
  if (!pregForm.value.lastPeriodStart) { 
    ElMessage.warning('请选择末次月经日期') 
    return 
  }
  try {
    await addPregnancyRecord({ lastPeriodStart: pregForm.value.lastPeriodStart })
    ElMessage.success('怀孕记录已创建')
    loadData()
  } catch {
    ElMessage.error('创建失败')
  }
}

async function confirmBabyBorn() {
  if (!babyForm.value.birthDate) { 
    ElMessage.warning('请选择出生日期') 
    return 
  }
  try {
    await babyBorn({ 
      ...babyForm.value, 
      birthDate: babyForm.value.birthDate 
    })
    ElMessage.success('已记录宝宝出生，切换至妈妈模式')
    showBabyDialog.value = false
    loadData()
  } catch {
    ElMessage.error('操作失败')
  }
}

async function loadData() {
  try {
    const r = await getPregnancyRecord()
    record.value = r?.data
    if (r?.data?.lastPeriodStart) {
      pregForm.value.lastPeriodStart = r.data.lastPeriodStart
    }
  } catch {
    ElMessage.error('加载怀孕记录失败')
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.pregnancy-page {
  padding: $she-space-6;
  max-width: 900px;
  margin: 0 auto;
}

// Hero Section
.hero-section {
  background: linear-gradient(135deg, #FCE4EC 0%, #F8BBD9 100%);
  border-radius: $she-radius-2xl;
  padding: $she-space-8;
  margin-bottom: $she-space-6;
  position: relative;
  overflow: hidden;

  &.has-record {
    background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
  }
}

.hero-content {
  display: flex;
  align-items: center;
  gap: $she-space-6;
  position: relative;
  z-index: 1;
}

.hero-icon {
  font-size: 64px;
}

.hero-text {
  h1 {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    color: $she-title;
    margin-bottom: $she-space-2;
  }
  
  p {
    color: $she-sub;
    font-size: $she-font-size-md;
  }
}

.hero-decoration {
  position: absolute;
  right: -20px;
  top: 50%;
  transform: translateY(-50%);
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  
  &.circle-1 {
    width: 200px;
    height: 200px;
    background: $she-primary;
    right: 20px;
    top: -100px;
  }
  
  &.circle-2 {
    width: 150px;
    height: 150px;
    background: $she-primary;
    right: 100px;
    top: 50px;
  }
  
  &.circle-3 {
    width: 80px;
    height: 80px;
    background: $she-primary;
    right: 0;
    top: 80px;
  }
}

// Record Card
.record-card {
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-6;
  border: none;
  box-shadow: $she-shadow-md;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $she-space-6;
}

.record-badge {
  display: flex;
  align-items: center;
  gap: $she-space-2;
  font-size: $she-font-size-lg;
  font-weight: $she-font-weight-semibold;
  color: $she-pregnant;
}

.badge-dot {
  width: 10px;
  height: 10px;
  background: $she-pregnant;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.2); }
}

.progress-section {
  margin-bottom: $she-space-6;
  padding: $she-space-4;
  background: #f8faf8;
  border-radius: $she-radius-lg;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: $she-space-2;
  font-size: $she-font-size-sm;
  color: $she-sub;
}

.progress-percent {
  font-weight: $she-font-weight-bold;
  color: $she-pregnant;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  margin-top: $she-space-2;
  font-size: $she-font-size-xs;
  color: $she-muted;
}

.stats-row {
  margin: $she-space-6 0;
}

.stat-box {
  text-align: center;
  padding: $she-space-4;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  border-radius: $she-radius-lg;
  
  .stat-value {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    color: $she-pregnant;
    
    &.due-date {
      font-size: $she-font-size-xl;
    }
  }
  
  .stat-name {
    font-size: $she-font-size-sm;
    color: $she-sub;
    margin-top: $she-space-1;
  }
}

.record-actions {
  display: flex;
  justify-content: center;
  padding-top: $she-space-4;
  border-top: 1px solid $she-border;
}

// Create Card
.create-card {
  border-radius: $she-radius-xl;
  border: none;
  box-shadow: $she-shadow-md;
}

.card-header-custom {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  font-weight: $she-font-weight-semibold;
  font-size: $she-font-size-lg;
  
  .header-icon {
    font-size: 24px;
  }
}

.create-content {
  padding: $she-space-4 0;
}

.create-intro {
  margin-bottom: $she-space-4;
  
  p {
    color: $she-sub;
    font-size: $she-font-size-sm;
    line-height: $she-line-height-loose;
  }
}

.preg-form {
  display: flex;
  align-items: flex-end;
  gap: $she-space-4;
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

// Birth Dialog
.birth-celebration {
  text-align: center;
  padding: $she-space-6 0;
  margin-bottom: $she-space-4;
  background: linear-gradient(135deg, #FFF9C4, #FFF59D);
  border-radius: $she-radius-lg;
}

.celebration-icon {
  font-size: 48px;
  margin-bottom: $she-space-2;
}

.celebration-text {
  font-size: $she-font-size-lg;
  font-weight: $she-font-weight-semibold;
  color: $she-title;
}

.birth-form {
  :deep(.el-form-item__label) {
    font-weight: $she-font-weight-medium;
  }
}
</style>
