<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">👩‍⚕️ 专家管理</span>
    </template>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="professionalTitle" label="职称" width="120" />
      <el-table-column prop="hospital" label="医院" min-width="150" />
      <el-table-column prop="specialty" label="擅长领域" min-width="150" />
      <el-table-column prop="certificationStatus" label="认证状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ pending: 'warning', approved: 'success', rejected: 'danger' }[row.certificationStatus]" size="small">
            {{ { pending: '待审核', approved: '已认证', rejected: '已拒绝' }[row.certificationStatus] || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="answerCount" label="回答数" width="80" />
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="row.certificationStatus === 'pending'">
            <el-button size="small" type="success" @click="approve(row)">通过</el-button>
            <el-button size="small" type="danger" @click="reject(row)">拒绝</el-button>
          </template>
          <span v-else style="color:#999">已处理</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExpertList, approveExpert, rejectExpert } from '@/api'

const list = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await getExpertList()
    list.value = res.data || []
  } catch { ElMessage.error('加载专家列表失败') }
  finally { loading.value = false }
}

async function approve(row) {
  try {
    await approveExpert(row.id)
    ElMessage.success(`已通过专家认证：${row.realName}`)
    loadData()
  } catch { ElMessage.error('操作失败') }
}

async function reject(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝专家申请', { inputPlaceholder: '拒绝原因' })
    await rejectExpert(row.id, value || '')
    ElMessage.info(`已拒绝：${row.realName}`)
    loadData()
  } catch {}
}

onMounted(() => loadData())
</script>
