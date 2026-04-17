<template>
  <div class="expert-page">
    <div class="page-header">
      <h1 class="page-title">👩‍⚕️ 专家管理</h1>
      <p class="page-subtitle">管理专家用户认证和资料</p>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" size="default">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="pending">待审核</el-radio-button>
        <el-radio-button value="approved">已认证</el-radio-button>
        <el-radio-button value="rejected">已拒绝</el-radio-button>
      </el-radio-group>
    </div>

    <!-- Table Card -->
    <div class="card">
      <el-table :data="filteredList" stripe v-loading="loading" row-key="id">
        <el-table-column prop="avatar" label="头像" width="80" align="center">
          <template #default="{ row }">
            <div class="avatar-emoji">{{ row.avatarEmoji || '👩‍⚕️' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="120">
          <template #default="{ row }">
            <span class="expert-name">{{ row.realName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="professionalTitle" label="职称" width="140" />
        <el-table-column prop="specialty" label="擅长领域" min-width="160">
          <template #default="{ row }">
            <div class="specialty-tags">
              <el-tag size="small" type="info">{{ row.specialty }}</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="hospital" label="医院" min-width="150" />
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column prop="certificationStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="{ pending: 'warning', approved: 'success', rejected: 'danger' }[row.certificationStatus]"
              size="small"
              round
            >
              {{ { pending: '待审核', approved: '已认证', rejected: '已拒绝' }[row.certificationStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.certificationStatus === 'pending'">
              <el-button size="small" type="success" @click="approve(row)">通过</el-button>
              <el-button size="small" type="danger" @click="reject(row)">拒绝</el-button>
            </template>
            <el-button v-else size="small" type="primary" plain @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Pagination -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        background
      />
    </div>

    <!-- Expert Detail Drawer -->
    <el-drawer v-model="showDetail" title="专家详情" size="400px" direction="rtl">
      <div v-if="currentExpert" class="expert-detail">
        <div class="detail-header">
          <div class="detail-avatar">{{ currentExpert.avatarEmoji || '👩‍⚕️' }}</div>
          <div class="detail-info">
            <h3>{{ currentExpert.realName }}</h3>
            <p>{{ currentExpert.professionalTitle }}</p>
          </div>
        </div>
        <el-divider />
        <div class="detail-item">
          <span class="detail-label">擅长领域</span>
          <span class="detail-value">{{ currentExpert.specialty }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">医院</span>
          <span class="detail-value">{{ currentExpert.hospital }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">回答数</span>
          <span class="detail-value">{{ currentExpert.answerCount || 0 }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">注册时间</span>
          <span class="detail-value">{{ currentExpert.createTime }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态</span>
          <el-tag
            :type="{ pending: 'warning', approved: 'success', rejected: 'danger' }[currentExpert.certificationStatus]"
            size="small"
            round
          >
            {{ { pending: '待审核', approved: '已认证', rejected: '已拒绝' }[currentExpert.certificationStatus] }}
          </el-tag>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExpertList, approveExpert, rejectExpert } from '@/api'

const list = ref([])
const loading = ref(false)
const filterStatus = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredList.value.length)

const showDetail = ref(false)
const currentExpert = ref(null)

const filteredList = computed(() => {
  if (filterStatus.value === 'all') return list.value
  return list.value.filter(item => item.certificationStatus === filterStatus.value)
})

async function loadData() {
  loading.value = true
  try {
    const res = await getExpertList({ page: currentPage.value, pageSize: pageSize.value })
    list.value = res?.data?.records || res?.data || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function approve(row) {
  try {
    await approveExpert(row.id)
    const idx = list.value.findIndex(e => e.id === row.id)
    if (idx !== -1) {
      list.value[idx].certificationStatus = 'approved'
    }
    ElMessage.success(`已通过专家认证：${row.realName}`)
  } catch {
    ElMessage.error('操作失败')
  }
}

async function reject(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝专家申请', {
      inputPlaceholder: '拒绝原因（选填）'
    })
    await rejectExpert(row.id, value || '')
    const idx = list.value.findIndex(e => e.id === row.id)
    if (idx !== -1) {
      list.value[idx].certificationStatus = 'rejected'
    }
    ElMessage.info(`已拒绝：${row.realName}${value ? '，原因：' + value : ''}`)
  } catch {}
}

function viewDetail(row) {
  currentExpert.value = { ...row }
  showDetail.value = true
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import '@/styles/global.scss';

.expert-page {
  animation: fadeIn 0.3s ease;
}

.page-header {
  margin-bottom: $admin-space-6;
}

.page-title {
  font-size: $admin-font-size-2xl;
  font-weight: 600;
  color: $admin-text;
  margin: 0 0 $admin-space-2;
}

.page-subtitle {
  font-size: $admin-font-size-sm;
  color: $admin-muted;
  margin: 0;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $admin-space-4;
}

.card {
  background: $admin-white;
  border-radius: $admin-radius-xl;
  padding: $admin-space-5;
  box-shadow: $admin-shadow-sm;
}

.avatar-emoji {
  font-size: 32px;
  line-height: 1;
}

.expert-name {
  font-weight: 500;
  color: $admin-text;
}

.specialty-tags {
  display: flex;
  gap: $admin-space-1;
}

.pagination-wrap {
  margin-top: $admin-space-4;
  display: flex;
  justify-content: flex-end;
}

.expert-detail {
  .detail-header {
    display: flex;
    align-items: center;
    gap: $admin-space-4;

    .detail-avatar {
      font-size: 48px;
    }

    .detail-info {
      h3 {
        margin: 0;
        font-size: $admin-font-size-lg;
        color: $admin-text;
      }
      p {
        margin: $admin-space-1 0 0;
        font-size: $admin-font-size-sm;
        color: $admin-muted;
      }
    }
  }

  .detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $admin-space-3 0;
    border-bottom: 1px solid $admin-border;

    &:last-child {
      border-bottom: none;
    }

    .detail-label {
      font-size: $admin-font-size-sm;
      color: $admin-muted;
    }

    .detail-value {
      font-size: $admin-font-size-sm;
      color: $admin-text;
      font-weight: 500;
    }
  }
}
</style>
