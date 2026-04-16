<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">🎁 活动管理</span>
      <el-button type="primary" size="small" style="float:right" @click="openDialog()">+ 创建活动</el-button>
    </template>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="活动名称" min-width="150" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="startTime" label="开始时间" width="120" />
      <el-table-column prop="endTime" label="结束时间" width="120" />
      <el-table-column prop="participantCount" label="参与人数" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="{ active: 'success', ended: 'info' }[row.status]" size="small">
            {{ row.status === 'active' ? '进行中' : '已结束' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑活动' : '创建活动'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="活动名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="打卡挑战" value="打卡挑战" />
            <el-option label="健康知识赛" value="健康知识赛" />
            <el-option label="社区互动" value="社区互动" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="活动描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, createActivity, updateActivity, deleteActivity } from '@/api'

const list = ref([])
const loading = ref(false)
const showDialog = ref(false)
const form = ref({ id: null, title: '', type: '', startTime: '', endTime: '', description: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await getActivityList()
    list.value = res.data || []
  } catch { ElMessage.error('加载活动列表失败') }
  finally { loading.value = false }
}

function openDialog(row) {
  form.value = row ? { ...row } : { id: null, title: '', type: '', startTime: '', endTime: '', description: '' }
  showDialog.value = true
}

async function submitForm() {
  if (!form.value.title) { ElMessage.warning('请填写活动名称'); return }
  try {
    if (form.value.id) {
      await updateActivity(form.value.id, form.value)
    } else {
      await createActivity(form.value)
    }
    showDialog.value = false
    ElMessage.success('保存成功')
    loadData()
  } catch { ElMessage.error('保存失败') }
}

async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除？', '提示')
  try {
    await deleteActivity(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => loadData())
</script>
