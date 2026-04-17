<template>
  <div class="health-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">📊 健康数据</h1>
      <p class="page-subtitle">记录追踪您的健康指标</p>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：图表 -->
      <el-col :span="16">
        <!-- 体重记录 -->
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">⚖️ 体重记录</span>
            <el-button type="primary" plain size="small" @click="showAddWeight = true">
              <el-icon><Plus /></el-icon> 记录体重
            </el-button>
          </div>
          <div ref="weightChartRef" style="height: 280px" />
          <div v-if="!weightData.length" class="empty-state">
            <div class="empty-icon">⚖️</div>
            <div class="empty-text">暂无体重记录</div>
          </div>
        </div>

        <!-- 基础体温 -->
        <div class="card chart-card">
          <div class="card-header">
            <span class="card-title">🌡️ 基础体温记录</span>
            <el-tag type="primary" size="small">最近14天</el-tag>
          </div>
          <div ref="bbtChartRef" style="height: 280px" />
          <div v-if="!bbtData.length" class="empty-state">
            <div class="empty-icon">🌡️</div>
            <div class="empty-text">暂无体温记录</div>
          </div>
        </div>
      </el-col>

      <!-- 右侧：信息卡片 -->
      <el-col :span="8">
        <!-- 宝宝信息 -->
        <div class="card info-card">
          <div class="card-header">
            <span class="card-title">👶 宝宝信息</span>
            <el-button type="primary" link size="small" @click="showAddBaby = true">
              <el-icon><Plus /></el-icon> 添加
            </el-button>
          </div>
          <div v-if="babies.length" class="baby-list">
            <div v-for="baby in babies" :key="baby.id" class="baby-item">
              <div class="baby-avatar">👶</div>
              <div class="baby-info">
                <div class="baby-name">{{ baby.name || '宝宝' }}</div>
                <div class="baby-meta">
                  <span v-if="baby.birthDate">{{ baby.birthDate }}</span>
                  <span v-if="baby.gender" class="baby-gender">
                    {{ baby.gender === 1 ? '👦 男' : '👧 女' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">👶</div>
            <div class="empty-text">暂无宝宝记录</div>
          </div>
        </div>

        <!-- 产检记录 -->
        <div class="card info-card">
          <div class="card-header">
            <span class="card-title">🏥 产检记录</span>
            <el-button type="primary" link size="small" @click="showAddCheckup = true">
              <el-icon><Plus /></el-icon> 添加
            </el-button>
          </div>
          <div v-if="checkupRecords.length" class="checkup-list">
            <div v-for="c in checkupRecords" :key="c.id" class="checkup-item">
              <div class="checkup-date">{{ c.checkupDate }}</div>
              <div class="checkup-info">
                <span class="checkup-week">第{{ c.weekNumber }}周</span>
                <span class="checkup-hospital">{{ c.hospital }}</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">🏥</div>
            <div class="empty-text">暂无产检记录</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 添加体重弹窗 -->
    <el-dialog v-model="showAddWeight" title="记录体重" width="420px">
      <el-form :model="weightForm" label-width="80px">
        <el-form-item label="日期">
          <el-date-picker
            v-model="weightForm.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="weightForm.value" :min="30" :max="200" :step="0.1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddWeight = false">取消</el-button>
        <el-button type="primary" @click="addWeight">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加产检弹窗 -->
    <el-dialog v-model="showAddCheckup" title="添加产检记录" width="500px">
      <el-form :model="checkupForm" label-width="90px">
        <el-form-item label="产检日期">
          <el-date-picker
            v-model="checkupForm.checkupDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="孕周">
          <el-input-number v-model="checkupForm.weekNumber" :min="1" :max="42" />
        </el-form-item>
        <el-form-item label="医院">
          <el-input v-model="checkupForm.hospital" placeholder="请输入医院名称" />
        </el-form-item>
        <el-form-item label="医生">
          <el-input v-model="checkupForm.doctor" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="诊断">
          <el-input v-model="checkupForm.diagnosis" type="textarea" rows="3" placeholder="请输入诊断内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddCheckup = false">取消</el-button>
        <el-button type="primary" @click="addCheckup">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加宝宝弹窗 -->
    <el-dialog v-model="showAddBaby" title="添加宝宝" width="420px">
      <el-form :model="babyForm" label-width="80px">
        <el-form-item label="宝宝姓名">
          <el-input v-model="babyForm.name" placeholder="请输入宝宝姓名" />
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker
            v-model="babyForm.birthDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="babyForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddBaby = false">取消</el-button>
        <el-button type="primary" @click="addBaby">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getBabies, addPregnancyRecord, getPregnancyRecord } from '@/api'
import api from '@/api'

const loading = ref(false)
const babies = ref([])
const checkupRecords = ref([])
const showAddWeight = ref(false)
const showAddCheckup = ref(false)
const showAddBaby = ref(false)

const weightForm = ref({ date: '', value: 50 })
const checkupForm = ref({ checkupDate: '', weekNumber: 1, hospital: '', doctor: '', diagnosis: '' })
const babyForm = ref({ name: '', birthDate: '', gender: '' })

const weightChartRef = ref(null)
const bbtChartRef = ref(null)
const weightData = ref([])
const bbtData = ref([])

// ECharts 主题色
const primaryColor = '#E91E63'
const primaryLight = '#F48FB1'

async function addWeight() {
  if (!weightForm.value.date) {
    ElMessage.warning('请选择日期')
    return
  }
  try {
    await api.post('/checkin/metrics', {
      metricType: 'weight',
      date: weightForm.value.date,
      value: weightForm.value.value
    })
    ElMessage.success('记录成功')
    showAddWeight.value = false
    loadWeightChart()
  } catch {
    ElMessage.error('记录失败')
  }
}

async function addCheckup() {
  if (!checkupForm.value.checkupDate) {
    ElMessage.warning('请选择日期')
    return
  }
  try {
    await addPregnancyRecord(checkupForm.value)
    ElMessage.success('添加成功')
    showAddCheckup.value = false
    loadCheckups()
  } catch {
    ElMessage.error('添加失败')
  }
}

async function addBaby() {
  try {
    await api.post('/baby', babyForm.value)
    ElMessage.success('添加成功')
    showAddBaby.value = false
    loadBabies()
  } catch {
    ElMessage.error('添加失败')
  }
}

async function loadCheckups() {
  try {
    const res = await getPregnancyRecord()
    checkupRecords.value = res?.data?.checkupRecords || []
  } catch {
    ElMessage.error('加载产检记录失败')
  }
}

async function loadBabies() {
  try {
    const res = await getBabies()
    babies.value = res?.data?.list || res?.data || []
  } catch {
    ElMessage.error('加载宝宝数据失败')
  }
}

async function loadWeightChart() {
  loading.value = true
  try {
    const res = await api.get('/checkin/metrics/list', { params: { metricType: 'weight' } })
    const data = res?.data || []
    weightData.value = data
    if (data.length > 0 && weightChartRef.value) {
      await nextTick()
      const chart = echarts.init(weightChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#eee', textStyle: { color: '#333' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.date?.slice(5) || ''), axisLine: { lineStyle: { color: '#eee' } } },
        yAxis: { type: 'value', name: 'kg', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f5f5f5' } } },
        series: [{ data: data.map(d => d.value), type: 'line', smooth: true, color: primaryColor, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(233,30,99,0.3)' }, { offset: 1, color: 'rgba(233,30,99,0.05)' }] } }, symbol: 'circle', symbolSize: 6 }]
      })
    }
  } catch {
    ElMessage.error('加载体重数据失败')
  } finally {
    loading.value = false
  }
}

async function loadBbtChart() {
  try {
    const res = await api.get('/period/bbt/list')
    const data = (res?.data || []).slice(0, 14).reverse()
    bbtData.value = data
    if (data.length > 0 && bbtChartRef.value) {
      await nextTick()
      const chart = echarts.init(bbtChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#eee', textStyle: { color: '#333' }, formatter: (params) => `${params[0].name}<br/>体温: ${params[0].value}℃` },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.measureDate?.slice(5) || ''), axisLine: { lineStyle: { color: '#eee' } } },
        yAxis: { type: 'value', name: '℃', min: 35.8, max: 37.2, axisLine: { show: false }, splitLine: { lineStyle: { color: '#f5f5f5' } } },
        series: [{ data: data.map(d => d.temperature), type: 'line', smooth: true, color: primaryLight, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(244,143,177,0.3)' }, { offset: 1, color: 'rgba(244,143,177,0.05)' }] } }, symbol: 'circle', symbolSize: 6 }]
      })
    }
  } catch {
    ElMessage.error('加载体温数据失败')
  }
}

onMounted(async () => {
  loadWeightChart()
  loadBbtChart()
  loadBabies()
  await loadCheckups()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.health-page {
  @include page-container;
}

.page-header {
  margin-bottom: $she-space-6;

  .page-title {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    color: $she-title;
    margin-bottom: $she-space-1;
  }

  .page-subtitle {
    font-size: $she-font-size-sm;
    color: $she-muted;
  }
}

.chart-card {
  margin-bottom: $she-space-6;
}

.info-card {
  margin-bottom: $she-space-6;
}

.baby-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-3;
}

.baby-item {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  padding: $she-space-3;
  background: $she-bg;
  border-radius: $she-radius-lg;
  transition: all $she-transition-fast;

  &:hover {
    background: rgba($she-primary, 0.04);
  }
}

.baby-avatar {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $she-white;
  border-radius: $she-radius-md;
  box-shadow: $she-shadow-sm;
}

.baby-name {
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-semibold;
  color: $she-text;
}

.baby-meta {
  font-size: $she-font-size-xs;
  color: $she-muted;
  margin-top: $she-space-1;
  display: flex;
  gap: $she-space-2;
}

.checkup-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-3;
}

.checkup-item {
  padding: $she-space-3;
  background: $she-bg;
  border-radius: $she-radius-lg;
  border-left: 3px solid $she-primary;
  transition: all $she-transition-fast;

  &:hover {
    background: rgba($she-primary, 0.04);
  }
}

.checkup-date {
  font-size: $she-font-size-sm;
  font-weight: $she-font-weight-semibold;
  color: $she-text;
  margin-bottom: $she-space-1;
}

.checkup-info {
  display: flex;
  gap: $she-space-3;
  font-size: $she-font-size-xs;
  color: $she-muted;
}

.checkup-week {
  color: $she-primary;
  font-weight: $she-font-weight-medium;
}
</style>
