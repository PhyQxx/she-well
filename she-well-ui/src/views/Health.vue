<template>
  <div class="health-page">
    <el-page-header @back="() => $router.back()" title="健康数据">
      <template #content><span class="page-title">📊 健康数据</span></template>
    </el-page-header>

    <el-row :gutter="16" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>体重记录</template>
          <div ref="weightChartRef" style="height: 250px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>基础体温记录</template>
          <div ref="bbtChartRef" style="height: 250px" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>🏥 产检记录</template>
      <el-button type="primary" size="small" @click="showAddCheckup = true">添加产检</el-button>
      <el-table :data="checkupRecords" stripe style="margin-top: 12px" v-if="checkupRecords.length">
        <el-table-column prop="checkupDate" label="日期" width="120" />
        <el-table-column prop="weekNumber" label="孕周" width="80" />
        <el-table-column prop="hospital" label="医院" />
        <el-table-column prop="doctor" label="医生" width="100" />
        <el-table-column prop="diagnosis" label="诊断" />
      </el-table>
      <div v-else style="color:#999;text-align:center;padding:20px">暂无产检记录</div>
    </el-card>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>🤰 宝宝信息</template>
      <div v-if="babies.length">
        <div v-for="baby in babies" :key="baby.id" class="baby-card">
          <div class="baby-name">{{ baby.name || '宝宝' }}</div>
          <div class="baby-info">
            {{ baby.birthDate ? '生日：' + baby.birthDate : '' }}
            {{ baby.gender ? ' | 性别：' + (baby.gender === 1 ? '男' : '女') : '' }}
          </div>
        </div>
      </div>
      <div v-else style="color:#999;text-align:center;padding:20px">暂无宝宝记录</div>
    </el-card>

    <el-dialog v-model="showAddCheckup" title="添加产检记录" width="500px">
      <el-form :model="checkupForm" label-width="90px">
        <el-form-item label="产检日期"><el-date-picker v-model="checkupForm.checkupDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="孕周"><el-input-number v-model="checkupForm.weekNumber" :min="1" :max="42" /></el-form-item>
        <el-form-item label="医院"><el-input v-model="checkupForm.hospital" /></el-form-item>
        <el-form-item label="医生"><el-input v-model="checkupForm.doctor" /></el-form-item>
        <el-form-item label="诊断"><el-input v-model="checkupForm.diagnosis" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddCheckup = false">取消</el-button>
        <el-button type="primary" @click="addCheckup">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getBabies, addPregnancyRecord, getPregnancyRecord } from '@/api'

const babies = ref([])
const checkupRecords = ref([])
const showAddCheckup = ref(false)
const checkupForm = ref({ checkupDate: '', weekNumber: 1, hospital: '', doctor: '', diagnosis: '' })
const weightChartRef = ref(null)
const bbtChartRef = ref(null)

async function addCheckup() {
  if (!checkupForm.value.checkupDate) { ElMessage.warning('请选择日期'); return }
  try {
    await addPregnancyRecord(checkupForm.value)
    ElMessage.success('添加成功')
    showAddCheckup.value = false
    await loadCheckups()
  } catch { ElMessage.error('添加失败') }
}

async function loadCheckups() {
  try {
    const res = await getPregnancyRecord()
    checkupRecords.value = res?.data?.checkupRecords || []
  } catch {}
}

function initWeightChart() {
  if (!weightChartRef.value) return
  const chart = echarts.init(weightChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['4/1', '4/8', '4/15', '4/22', '4/29'] },
    yAxis: { type: 'value', name: 'kg' },
    series: [{ data: [52.0, 51.8, 52.1, 51.9, 52.2], type: 'line', smooth: true, color: '#E91E63' }]
  })
}

function initBbtChart() {
  if (!bbtChartRef.value) return
  const chart = echarts.init(bbtChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['4/1', '4/2', '4/3', '4/4', '4/5', '4/6', '4/7'] },
    yAxis: { type: 'value', name: '℃', min: 35.8, max: 37.2 },
    series: [{
      data: [36.2, 36.3, 36.4, 36.5, 36.4, 36.8, 36.9],
      type: 'line', smooth: true, color: '#F48FB1'
    }]
  })
}

onMounted(async () => {
  initWeightChart()
  initBbtChart()
  try {
    const babyRes = await getBabies()
    babies.value = babyRes?.data?.list || babyRes?.data || []
  } catch {}
  await loadCheckups()
})
</script>

<style scoped>
.health-page { padding: 8px }
.page-title { font-size: 18px; font-weight: 600 }
.baby-card { padding: 12px; border-bottom: 1px solid #f0f0f0 }
.baby-card:last-child { border-bottom: none }
.baby-name { font-size: 16px; font-weight: 600; color: #333 }
.baby-info { font-size: 13px; color: #999; margin-top: 4px }
</style>
