<template>
  <div class="reminder-page">
    <div class="page-header">
      <h2>⏰ 健康提醒</h2>
    </div>

    <!-- 统计 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num">{{ enabledCount }}</div>
          <div class="stat-label">已开启</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num">{{ reminderList.length }}</div>
          <div class="stat-label">共设置</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card water">
          <div class="stat-num">{{ typeCount('water') }}</div>
          <div class="stat-label">喝水提醒</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card exercise">
          <div class="stat-num">{{ typeCount('exercise') }}</div>
          <div class="stat-label">运动提醒</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 提醒列表 -->
    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>提醒列表</span>
          <el-button type="primary" size="small" @click="showDialog = true">+ 添加提醒</el-button>
        </div>
      </template>
      <el-empty v-if="!reminderList.length" description="暂无提醒，点击上方添加" />
      <el-table :data="reminderList" v-else stripe>
        <el-table-column label="类型" width="140">
          <template #default="{ row }">
            <span>{{ typeIcon(row.type) }} {{ typeName(row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="remindTime" label="提醒时间" width="120" />
        <el-table-column prop="content" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.enabled" @change="toggleReminder(row)" active-text="开" inactive-text="关" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" size="small" text @click="deleteReminder(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 快速添加 -->
    <el-card class="quick-card" shadow="hover">
      <template #header><span>💡 快速添加</span></template>
      <div class="quick-list">
        <div v-for="q in quickTypes" :key="q.type" class="quick-item" @click="quickAdd(q)">
          <div class="quick-icon">{{ q.icon }}</div>
          <div class="quick-name">{{ q.name }}</div>
        </div>
      </div>
    </el-card>

    <!-- 添加弹窗 -->
    <el-dialog v-model="showDialog" title="添加提醒" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="提醒类型">
          <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
            <el-option v-for="t in typeOptions" :key="t.type" :label="t.name" :value="t.type">
              <span>{{ t.icon }} {{ t.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="提醒标题">
          <el-input v-model="form.title" :placeholder="'如：' + (typeOptions.find(t=>t.type===form.type)?.name || '自定义提醒')" />
        </el-form-item>
        <el-form-item label="提醒时间">
          <el-time-picker v-model="form.remindTime" format="HH:mm" value-format="HH:mm" style="width: 100%" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.content" type="textarea" :rows="2" placeholder="可选备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReminder">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getReminderList, createReminder, updateReminder, deleteReminder as deleteReminderApi, toggleReminder as toggleReminderApi } from '@/api/index'
import { ElMessage, ElMessageBox } from 'element-plus'

const reminderList = ref([])
const showDialog = ref(false)
const form = ref({ type: 'water', title: '', remindTime: '09:00', content: '' })

const typeOptions = [
  { type: 'water', icon: '💧', name: '喝水提醒' },
  { type: 'exercise', icon: '🏃', name: '运动提醒' },
  { type: 'sleep', icon: '😴', name: '早睡提醒' },
  { type: 'medicine', icon: '💊', name: '吃药提醒' },
  { type: 'period', icon: '🌸', name: '经期提醒' },
  { type: 'ovulation', icon: '🧪', name: '排卵提醒' },
  { type: 'checkup', icon: '📋', name: '产检提醒' },
  { type: 'custom', icon: '✨', name: '自定义' },
]
const quickTypes = [
  { type: 'water', icon: '💧', name: '喝水' },
  { type: 'exercise', icon: '🏃', name: '运动' },
  { type: 'sleep', icon: '😴', name: '早睡' },
  { type: 'medicine', icon: '💊', name: '吃药' },
]

function typeIcon(type) { return { water:'💧', exercise:'🏃', sleep:'😴', medicine:'💊', period:'🌸', ovulation:'🧪', checkup:'📋', custom:'✨' }[type] || '⏰' }
function typeName(type) { return { water:'喝水提醒', exercise:'运动提醒', sleep:'早睡提醒', medicine:'吃药提醒', period:'经期提醒', ovulation:'排卵提醒', checkup:'产检提醒', custom:'自定义' }[type] || '提醒' }
const enabledCount = computed(() => reminderList.value.filter(r => r.enabled).length)
function typeCount(type) { return reminderList.value.filter(r => r.type === type && r.enabled).length }

async function loadData() {
  try {
    const res = await getReminderList()
    reminderList.value = (res?.data || []).map(r => ({
      id: r.id, type: r.type || 'custom', title: r.title || '',
      content: r.content || '', remindTime: r.remindTime || '08:00', enabled: r.enabled !== false,
    }))
  } catch (e) { ElMessage.error('加载失败') }
}

async function toggleReminder(row) {
  try {
    await toggleReminderApi(row.id, !row.enabled)
    row.enabled = !row.enabled
  } catch (e) { ElMessage.error('切换失败') }
}

async function deleteReminder(row) {
  await ElMessageBox.confirm(`确定删除「${row.title || typeName(row.type)}」？`, '提示', { type: 'warning' })
  try {
    await deleteReminderApi(row.id)
    reminderList.value = reminderList.value.filter(r => r.id !== row.id)
    ElMessage.success('已删除')
  } catch (e) { ElMessage.error('删除失败') }
}

async function quickAdd(q) {
  try {
    const timeMap = { water: '09:00', exercise: '07:00', sleep: '22:00', medicine: '08:00' }
    await createReminder({ type: q.type, title: q.name, remindTime: timeMap[q.type] || '08:00', enabled: true })
    reminderList.value.push({ id: Date.now(), type: q.type, title: q.name, content: '', remindTime: timeMap[q.type] || '08:00', enabled: true })
    ElMessage.success('已添加')
  } catch (e) { ElMessage.error('添加失败') }
}

async function submitReminder() {
  if (!form.value.remindTime) { ElMessage.warning('请选择时间'); return }
  try {
    const payload = { type: form.value.type, title: form.value.title || typeName(form.value.type), content: form.value.content, remindTime: form.value.remindTime, enabled: true }
    await createReminder(payload)
    reminderList.value.push({ id: Date.now(), ...payload })
    showDialog.value = false
    form.value = { type: 'water', title: '', remindTime: '09:00', content: '' }
    ElMessage.success('已添加')
  } catch (e) { ElMessage.error('添加失败') }
}

onMounted(loadData)
</script>

<style scoped>
.reminder-page { padding: 24px }
.page-header { margin-bottom: 24px }
.page-header h2 { font-size: 24px; font-weight: 600; color: var(--el-text-color-primary) }
.stats-row { margin-bottom: 20px }
.stat-card { text-align: center; padding: 8px 0 }
.stat-num { font-size: 32px; font-weight: bold; color: #FF9800 }
.stat-label { font-size: 13px; color: var(--el-text-color-secondary); margin-top: 4px }
.stat-card.water .stat-num { color: #2196F3 }
.stat-card.exercise .stat-num { color: #4CAF50 }
.card-header { display: flex; justify-content: space-between; align-items: center }
.quick-list { display: flex; gap: 16px }
.quick-item { flex: 1; background: var(--el-fill-color-light); border-radius: 12px; padding: 20px; text-align: center; cursor: pointer; border: 2px solid transparent; transition: all 0.2s }
.quick-item:hover { border-color: #FF9800; background: var(--el-color-warning-light-9) }
.quick-icon { font-size: 32px }
.quick-name { font-size: 14px; color: var(--el-text-color-secondary); margin-top: 6px }
</style>
