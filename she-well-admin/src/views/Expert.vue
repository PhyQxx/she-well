<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">👩‍⚕️ 专家管理</span>
    </template>
    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="title" label="职称" width="120" />
      <el-table-column prop="hospital" label="医院" min-width="150" />
      <el-table-column prop="specialty" label="擅长领域" min-width="150" />
      <el-table-column prop="status" label="认证状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ pending: 'warning', approved: 'success', rejected: 'danger' }[row.status]" size="small">
            {{ { pending: '待审核', approved: '已认证', rejected: '已拒绝' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="answerCount" label="回答数" width="80" />
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'pending'">
            <el-button size="small" type="success" @click="approve(row)">通过</el-button>
            <el-button size="small" type="danger" @click="reject(row)">拒绝</el-button>
          </template>
          <el-button v-else size="small" @click="viewDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const list = ref([
  { id: 1, name: '王医生', title: '副主任医师', hospital: '北京协和医院', specialty: '妇科内分泌', status: 'approved', answerCount: 156, createTime: '2026-02-01 10:00' },
  { id: 2, name: '李医生', title: '主治医师', hospital: '上海第一妇婴保健院', specialty: '围产医学', status: 'approved', answerCount: 89, createTime: '2026-02-10 10:00' },
  { id: 3, name: '张医生', title: '主任医师', hospital: '广州医科大学附属第三医院', specialty: '生殖医学', status: 'pending', answerCount: 0, createTime: '2026-04-10 10:00' },
  { id: 4, name: '陈医生', title: '主治医师', hospital: '成都华西第二医院', specialty: '中医妇科', status: 'pending', answerCount: 0, createTime: '2026-04-12 10:00' },
])

function approve(row) {
  row.status = 'approved'
  ElMessage.success(`已通过专家认证：${row.name}`)
}
function reject(row) {
  row.status = 'rejected'
  ElMessage.info(`已拒绝：${row.name}`)
}
function viewDetail(row) { ElMessage.info(`查看详情：${row.name}`) }
</script>
