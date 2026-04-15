<template>
  <div class="period-page">
    <el-card shadow="hover">
      <template #header>经期记录</template>
      <el-form inline>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" /></el-form-item>
        <el-form-item label="流量"><el-select v-model="form.flowLevel" style="width:100px"><el-option v-for="l in [1,2,3,4]" :key="l" :label="['极少','少','中','多'][l-1]" :value="l" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="addRecord">记录</el-button></el-form-item>
      </el-form>
      <el-table :data="records" stripe style="margin-top: 16px">
        <el-table-column prop="startDate" label="开始" />
        <el-table-column prop="endDate" label="结束" />
        <el-table-column prop="duration" label="天数" />
        <el-table-column label="流量"><template #default="{row}">{{ ['','极少','少','中','多'][row.flowLevel] }}</template></el-table-column>
        <el-table-column label="操作"><template #default="{row}"><el-button size="small" type="danger" @click="delRecord(row)">删除</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-card shadow="hover" style="margin-top:16px">
      <template #header>经期预测</template>
      <el-button type="primary" @click="calculate">重新计算预测</el-button>
      <div v-if="prediction" style="margin-top:16px; display:flex; gap:16px">
        <div class="pred-item"><div class="pred-label">预测下次经期</div><div class="pred-val">{{ prediction.predictedStartDate }}</div></div>
        <div class="pred-item"><div class="pred-label">易孕期</div><div class="pred-val">{{ prediction.fertileStartDate }} ~ {{ prediction.fertileEndDate }}</div></div>
        <div class="pred-item"><div class="pred-label">排卵日</div><div class="pred-val">{{ prediction.ovulationDate }}</div></div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const form = ref({ startDate: '', endDate: '', flowLevel: 3 })
const records = ref<any[]>([])
const prediction = ref<any>(null)

function format(d: any) { return d ? new Date(d).toISOString().slice(0,10) : '' }

async function addRecord() {
  if (!form.value.startDate) { ElMessage.warning('请选择开始日期'); return }
  await api.post('/period/record', { ...form.value, startDate: format(form.value.startDate), endDate: format(form.value.endDate) })
  ElMessage.success('记录成功')
  loadData()
}

async function delRecord(row: any) {
  await api.delete(`/period/record/${row.id}`)
  ElMessage.success('已删除')
  loadData()
}

async function calculate() {
  const res: any = await api.post('/period/prediction/calculate')
  prediction.value = res.data
  ElMessage.success('预测已更新')
}

async function loadData() {
  const [r, p] = await Promise.all([api.get('/period/record/list'), api.get('/period/prediction')])
  records.value = r.data || []
  prediction.value = p.data
}

onMounted(loadData)
</script>

<style scoped>
.period-page { padding: 8px }
.pred-item { background: #fce4ec; border-radius: 8px; padding: 12px 16px; flex: 1; text-align: center }
.pred-label { font-size: 12px; color: #666 }
.pred-val { font-size: 14px; font-weight: bold; color: #c2185b; margin-top: 4px }
</style>
