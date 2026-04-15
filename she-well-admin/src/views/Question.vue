<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">❓ 问答管理</span>
    </template>
    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="问题" min-width="200" />
      <el-table-column prop="userNickname" label="提问者" width="100" />
      <el-table-column prop="expertName" label="回答专家" width="100">
        <template #default="{ row }">{{ row.expertName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ pending: 'warning', answered: 'success', closed: 'info' }[row.status]" size="small">
            {{ { pending: '待回答', answered: '已回答', closed: '已关闭' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="createTime" label="提问时间" width="160" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([
  { id: 1, title: '经期推迟了一周还没来，是什么原因？', userNickname: '小红', expertName: '王医生', status: 'answered', viewCount: 234, createTime: '2026-04-10 14:00' },
  { id: 2, title: '备孕三个月没成功，需要做哪些检查？', userNickname: '花开', expertName: '', status: 'pending', viewCount: 156, createTime: '2026-04-12 09:00' },
  { id: 3, title: '孕早期出血怎么办？', userNickname: 'Lily', expertName: '李医生', status: 'answered', viewCount: 412, createTime: '2026-04-08 11:00' },
])

async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该问答？', '提示')
  list.value = list.value.filter(i => i.id !== row.id)
  ElMessage.success('已删除')
}
function viewDetail(row) { ElMessage.info('查看详情（开发中）') }
</script>
