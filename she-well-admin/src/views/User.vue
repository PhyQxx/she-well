<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">👥 用户管理</span>
    </template>
    <el-form :inline="true" style="margin-bottom:12px">
      <el-form-item label="关键词"><el-input v-model="filters.keyword" placeholder="昵称/手机号" clearable style="width:160px" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" placeholder="全部" clearable style="width:100px">
          <el-option label="正常" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="模式">
        <el-select v-model="filters.mode" placeholder="全部" clearable style="width:100px">
          <el-option label="经期" value="period" />
          <el-option label="备孕" value="trying" />
          <el-option label="怀孕" value="pregnancy" />
          <el-option label="育儿" value="nursing" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
    </el-form>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="currentMode" label="当前模式" width="100">
        <template #default="{ row }">
          {{ { period: '经期', pregnancy: '怀孕', trying: '备孕', nursing: '育儿' }[row.currentMode] || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-button size="small" :type="row.status === 1 ? 'danger' : 'primary'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="warning" @click="resetPwd(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination layout="total, prev, pager, next" :total="total" :page-size="20" v-model:current-page="page" @current-change="loadData" style="margin-top:16px;justify-content:center" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus, resetUserPassword } from '@/api'

const list = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)
const filters = ref({ keyword: '', status: '', mode: '' })

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: 20 }
    if (filters.value.keyword) params.keyword = filters.value.keyword
    if (filters.value.status) params.status = filters.value.status
    if (filters.value.mode) params.mode = filters.value.mode
    const res = await getUserList(params)
    if (res.data) {
      list.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

function viewDetail(row) {
  ElMessage.info(`用户详情：${row.nickname} (${row.phone})`)
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`用户已${newStatus === 1 ? '启用' : '禁用'}`)
  } catch {
    ElMessage.error('操作失败')
  }
}

async function resetPwd(row) {
  try {
    await ElMessageBox.confirm(`确认重置用户 ${row.nickname} 的密码为默认密码？`, '重置密码', { type: 'warning' })
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为默认密码')
  } catch {}
}

onMounted(() => loadData())
</script>
