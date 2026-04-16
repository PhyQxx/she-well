<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">❓ 问答管理</span>
    </template>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="问题" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ pending: 'warning', answered: 'success', closed: 'info' }[row.status]" size="small">
            {{ { pending: '待回答', answered: '已回答', closed: '已关闭' }[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="answerCount" label="回答" width="80" />
      <el-table-column prop="createTime" label="提问时间" width="160" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestionList, deleteQuestion } from '@/api'

const list = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await getQuestionList()
    list.value = res.data || []
  } catch { ElMessage.error('加载问答列表失败') }
  finally { loading.value = false }
}

async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该问答？', '提示')
  try {
    await deleteQuestion(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => loadData())
</script>
