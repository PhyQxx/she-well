<template>
  <div class="period-page">
    <el-row :gutter="24">
      <!-- 左侧：记录表单 & 列表 -->
      <el-col :span="14">
        <div class="card">
          <div class="card-header">
            <span class="card-title">🩸 经期记录</span>
          </div>

          <!-- 记录表单 -->
          <el-form :model="form" inline class="record-form">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 160px"
              />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 160px"
              />
            </el-form-item>
            <el-form-item label="流量">
              <el-select v-model="form.flowLevel" style="width: 100px">
                <el-option v-for="l in flowOptions" :key="l.value" :label="l.label" :value="l.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="addRecord">
                <el-icon><Plus /></el-icon> 记录
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 记录列表 -->
          <div v-if="records.length === 0" class="empty-state">
            <div class="empty-icon">📅</div>
            <div class="empty-text">暂无经期记录，开始记录吧~</div>
          </div>
          <el-table v-else :data="records" stripe class="records-table">
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="duration" label="持续天数" width="100" />
            <el-table-column prop="flowLevel" label="流量">
              <template #default="{ row }">
                <el-tag size="small" :type="flowTagType(row.flowLevel)">
                  {{ flowLabels[row.flowLevel] || '-' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button size="small" type="danger" plain @click="delRecord(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：预测 & 统计 -->
      <el-col :span="10">
        <!-- 预测卡片 -->
        <div class="card prediction-card">
          <div class="card-header">
            <span class="card-title">🔮 经期预测</span>
            <el-button type="primary" plain size="small" @click="calculate">
              <el-icon><Refresh /></el-icon> 重新计算
            </el-button>
          </div>

          <div v-if="prediction" class="prediction-grid">
            <div class="pred-item">
              <div class="pred-icon">📆</div>
              <div class="pred-content">
                <div class="pred-label">预测下次经期</div>
                <div class="pred-value">{{ prediction.predictedStartDate || '--' }}</div>
              </div>
            </div>
            <div class="pred-item">
              <div class="pred-icon">🎯</div>
              <div class="pred-content">
                <div class="pred-label">易孕期</div>
                <div class="pred-value">
                  {{ prediction.fertileStartDate ? `${prediction.fertileStartDate} ~ ${prediction.fertileEndDate}` : '--' }}
                </div>
              </div>
            </div>
            <div class="pred-item">
              <div class="pred-icon">💕</div>
              <div class="pred-content">
                <div class="pred-label">排卵日</div>
                <div class="pred-value primary">{{ prediction.ovulationDate || '--' }}</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">🔮</div>
            <div class="empty-text">暂无预测数据</div>
          </div>
        </div>

        <!-- 周期统计 -->
        <div class="card stats-card">
          <div class="card-header">
            <span class="card-title">📊 周期统计</span>
          </div>
          <div class="cycle-stats">
            <div class="cycle-item">
              <div class="cycle-num">{{ avgCycleLength || '--' }}</div>
              <div class="cycle-label">平均周期(天)</div>
            </div>
            <div class="cycle-item">
              <div class="cycle-num">{{ avgPeriodLength || '--' }}</div>
              <div class="cycle-label">平均经期(天)</div>
            </div>
            <div class="cycle-item">
              <div class="cycle-num">{{ records.length }}</div>
              <div class="cycle-label">记录次数</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { getPeriodRecords, addPeriodRecord, deletePeriodRecord, getPrediction, calculatePrediction } from '@/api'

const form = ref({ startDate: '', endDate: '', flowLevel: 3 })
const records = ref<any[]>([])
const prediction = ref<any>(null)

const flowLabels = ['', '极少', '少', '中', '多']
const flowOptions = [
  { value: 1, label: '极少' },
  { value: 2, label: '少' },
  { value: 3, label: '中' },
  { value: 4, label: '多' },
]

const flowTagType = (level: number) => ['', 'info', 'success', 'warning', 'danger'][level] || 'info'

// 计算平均周期
const avgCycleLength = computed(() => {
  if (records.value.length < 2) return null
  let total = 0
  for (let i = 1; i < records.value.length; i++) {
    const prev = new Date(records.value[i-1].startDate)
    const curr = new Date(records.value[i].startDate)
    total += (curr.getTime() - prev.getTime()) / (1000 * 60 * 60 * 24)
  }
  return Math.round(total / (records.value.length - 1))
})

const avgPeriodLength = computed(() => {
  if (records.value.length === 0) return null
  const total = records.value.reduce((sum, r) => sum + (r.duration || 0), 0)
  return Math.round(total / records.value.length)
})

async function addRecord() {
  if (!form.value.startDate) {
    ElMessage.warning('请选择开始日期')
    return
  }
  try {
    await addPeriodRecord({
      startDate: form.value.startDate,
      endDate: form.value.endDate,
      flowLevel: form.value.flowLevel
    })
    ElMessage.success('记录成功')
    form.value = { startDate: '', endDate: '', flowLevel: 3 }
    loadData()
  } catch {
    ElMessage.error('记录失败')
  }
}

async function delRecord(row: any) {
  try {
    await deletePeriodRecord(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function calculate() {
  try {
    const res: any = await calculatePrediction()
    prediction.value = res.data
    ElMessage.success('预测已更新')
  } catch {
    ElMessage.error('计算失败')
  }
}

async function loadData() {
  try {
    const [r, p] = await Promise.all([
      getPeriodRecords(),
      getPrediction(),
    ])
    records.value = r?.data || []
    prediction.value = p?.data
  } catch (e) {
    ElMessage.error('数据加载失败')
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.period-page {
  @include page-container;
}

.record-form {
  margin-bottom: $she-space-6;
  padding-bottom: $she-space-6;
  border-bottom: 1px solid $she-border;
}

.records-table {
  border-radius: $she-radius-lg;
  overflow: hidden;
}

.prediction-card {
  margin-bottom: $she-space-6;
}

.prediction-grid {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
}

.pred-item {
  display: flex;
  align-items: center;
  gap: $she-space-4;
  padding: $she-space-4;
  background: $she-primary-bg;
  border-radius: $she-radius-lg;
  transition: all $she-transition-normal;

  &:hover {
    background: rgba($she-primary, 0.08);
  }
}

.pred-icon {
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

.pred-content {
  flex: 1;
}

.pred-label {
  font-size: $she-font-size-xs;
  color: $she-muted;
  margin-bottom: $she-space-1;
}

.pred-value {
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-semibold;
  color: $she-text;

  &.primary {
    color: $she-primary;
  }
}

.cycle-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $she-space-4;
}

.cycle-item {
  text-align: center;
  padding: $she-space-4;
  background: $she-bg;
  border-radius: $she-radius-lg;
}

.cycle-num {
  font-size: $she-font-size-2xl;
  font-weight: $she-font-weight-bold;
  color: $she-primary;
}

.cycle-label {
  font-size: $she-font-size-xs;
  color: $she-muted;
  margin-top: $she-space-1;
}
</style>
