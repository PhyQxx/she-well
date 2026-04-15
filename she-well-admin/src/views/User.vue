<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">👥 用户管理</span>
    </template>
    <el-form :inline="true" style="margin-bottom:12px">
      <el-form-item label="手机号"><el-input v-model="filters.phone" placeholder="搜索手机号" clearable style="width:140px" /></el-form-item>
      <el-form-item><el-button @click="loadData">搜索</el-button></el-form-item>
    </el-form>
    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="mode" label="当前模式" width="100">
        <template #default="{ row }">
          {{ { period: '经期', pregnancy: '怀孕', trying: '备孕', nursing: '育儿' }[row.mode] || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="lastPeriodDate" label="最近经期" width="120" />
      <el-table-column prop="postCount" label="发帖数" width="80" />
      <el-table-column prop="checkinDays" label="打卡天数" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-button size="small" :type="row.status === 1 ? 'danger' : 'primary'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination layout="total, prev, pager, next" :total="total" :page-size="20" v-model:current-page="page" style="margin-top:16px;justify-content:center" />
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const filters = ref({ phone: '' })

function loadData() {
  list.value = [
    { id: 1, nickname: '小红', phone: '138****1234', mode: 'period', lastPeriodDate: '2026-04-10', postCount: 12, checkinDays: 45, status: 1, createTime: '2026-01-15 10:00' },
    { id: 2, nickname: '花开', phone: '139****5678', mode: 'pregnancy', lastPeriodDate: '2025-12-20', postCount: 5, checkinDays: 30, status: 1, createTime: '2026-02-01 10:00' },
    { id: 3, nickname: 'Lily', phone: '136****9012', mode: 'trying', lastPeriodDate: '2026-04-05', postCount: 8, checkinDays: 60, status: 1, createTime: '2026-01-20 10:00' },
  ]
  total.value = list.value.length
}

function viewDetail(row) { ElMessage.info(`查看用户详情：${row.nickname}`) }
function toggleStatus(row) {
  row.status = row.status === 1 ? 0 : 1
  ElMessage.success(`用户已${row.status === 1 ? '启用' : '禁用'}`)
}

loadData()
</script>
