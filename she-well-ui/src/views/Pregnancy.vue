<template>
  <div class="pregnancy-page">
    <el-alert v-if="!record" title="暂无怀孕记录" type="info" :closable="false" style="margin-bottom: 16px">
      如已确认怀孕，请在下方创建怀孕记录
    </el-alert>
    <el-card v-if="record" shadow="hover">
      <template #header>🤰 怀孕进行中</template>
      <el-row :gutter="16">
        <el-col :span="8"><div class="stat-card"><div class="stat-num">{{ record.currentWeek || 0 }}</div><div class="stat-label">孕周</div></div></el-col>
        <el-col :span="8"><div class="stat-card"><div class="stat-num">{{ record.currentDay || 0 }}</div><div class="stat-label">孕天</div></div></el-col>
        <el-col :span="8"><div class="stat-card"><div class="stat-num">{{ record.dueDate }}</div><div class="stat-label">预产期</div></div></el-col>
      </el-row>
      <div style="margin-top:16px">
        <el-button type="success" @click="showBabyDialog = true">🤱 宝宝已出生</el-button>
      </div>
    </el-card>
    <el-card shadow="hover" style="margin-top:16px">
      <template #header>创建怀孕记录</template>
      <el-form inline>
        <el-form-item label="末次月经"><el-date-picker v-model="pregForm.lastPeriodStart" type="date" /></el-form-item>
        <el-form-item><el-button type="primary" @click="createRecord">开始记录</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-dialog v-model="showBabyDialog" title="宝宝已出生" width="400px">
      <el-form label-width="80px">
        <el-form-item label="宝宝姓名"><el-input v-model="babyForm.name" /></el-form-item>
        <el-form-item label="出生日期"><el-date-picker v-model="babyForm.birthDate" type="date" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="babyForm.gender"><el-option label="男" value="male" /><el-option label="女" value="female" /><el-option label="未知" value="unknown" /></el-select></el-form-item>
        <el-form-item label="分娩方式"><el-select v-model="babyForm.deliveryType"><el-option label="顺产" value="natural" /><el-option label="剖腹产" value="cesarean" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="showBabyDialog=false">取消</el-button><el-button type="primary" @click="confirmBabyBorn">确认</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const record = ref<any>(null)
const showBabyDialog = ref(false)
const pregForm = ref({ lastPeriodStart: '' })
const babyForm = ref({ name: '', birthDate: '', gender: 'unknown', deliveryType: 'natural' })

function fmt(d: any) { return d ? new Date(d).toISOString().slice(0,10) : '' }

async function createRecord() {
  if (!pregForm.value.lastPeriodStart) { ElMessage.warning('请选择末次月经日期'); return }
  await api.post('/pregnancy/record', { ...pregForm.value, lastPeriodStart: fmt(pregForm.value.lastPeriodStart) })
  ElMessage.success('怀孕记录已创建')
  loadData()
}

async function confirmBabyBorn() {
  if (!babyForm.value.birthDate) { ElMessage.warning('请选择出生日期'); return }
  await api.post('/pregnancy/baby-born', { ...babyForm.value, birthDate: fmt(babyForm.value.birthDate) })
  ElMessage.success('已记录宝宝出生，切换至妈妈模式')
  showBabyDialog.value = false
  loadData()
}

async function loadData() {
  try {
    const r = await api.get('/pregnancy/record')
    record.value = r.data
  } catch {}
}

onMounted(loadData)
</script>

<style scoped>
.pregnancy-page { padding: 8px }
.stat-card { background: linear-gradient(135deg, #e8f5e9, #c8e6c9); border-radius: 12px; padding: 20px; text-align: center }
.stat-num { font-size: 28px; font-weight: bold; color: #2e7d32; }
.stat-label { color: #666; font-size: 12px; margin-top: 4px }
</style>
