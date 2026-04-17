<template>
  <div class="activity-page">
    <div class="page-header">
      <h1 class="page-title">🎁 活动管理</h1>
      <p class="page-subtitle">创建和管理用户参与活动</p>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" size="default">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="active">进行中</el-radio-button>
        <el-radio-button value="ended">已结束</el-radio-button>
      </el-radio-group>
      <el-button type="primary" @click="openDialog()">+ 创建活动</el-button>
    </div>

    <!-- Table Card -->
    <div class="card">
      <el-table :data="filteredList" stripe v-loading="loading" row-key="id">
        <el-table-column prop="coverEmoji" label="封面" width="80" align="center">
          <template #default="{ row }">
            <div class="cover-emoji">{{ row.coverEmoji || '🎯' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="活动名称" min-width="160">
          <template #default="{ row }">
            <span class="activity-title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="140" />
        <el-table-column prop="endTime" label="结束时间" width="140" />
        <el-table-column prop="participantCount" label="参与人数" width="100" align="center">
          <template #default="{ row }">
            <span class="participant-count">{{ row.participantCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small" round>
              {{ row.status === 'active' ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="deleteRow(row)">删除</el-button>
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

    <!-- Add/Edit Activity Dialog -->
    <el-dialog v-model="showDialog" :title="form.id ? '编辑活动' : '创建活动'" width="520px" destroy-on-close>
      <el-form :model="form" label-position="top" class="activity-form">
        <el-form-item label="活动封面">
          <div class="emoji-selector">
            <div
              v-for="emoji in emojiList"
              :key="emoji"
              class="emoji-option"
              :class="{ active: form.coverEmoji === emoji }"
              @click="form.coverEmoji = emoji"
            >
              {{ emoji }}
            </div>
          </div>
        </el-form-item>
        <el-form-item label="活动名称" required>
          <el-input v-model="form.title" placeholder="请输入活动名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="form.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option label="打卡挑战" value="打卡挑战" />
            <el-option label="健康知识赛" value="健康知识赛" />
            <el-option label="社区互动" value="社区互动" />
            <el-option label="福利活动" value="福利活动" />
          </el-select>
        </el-form-item>
        <div class="time-range">
          <el-form-item label="开始时间" class="time-item">
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择开始时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="结束时间" class="time-item">
            <el-date-picker
              v-model="form.endTime"
              type="datetime"
              placeholder="选择结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="活动描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动描述..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, createActivity, updateActivity, deleteActivity } from '@/api'

const emojiList = ['🎯', '🏆', '🎁', '💪', '🌸', '💝', '✨', '🎉', '📚', '🏃', '🧘', '💕']

const list = ref([])
const loading = ref(false)
const filterStatus = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredList.value.length)

const showDialog = ref(false)
const form = ref({ id: null, title: '', type: '', startTime: '', endTime: '', description: '', coverEmoji: '🎯' })

const filteredList = computed(() => {
  if (filterStatus.value === 'all') return list.value
  return list.value.filter(item => item.status === filterStatus.value)
})

async function loadData() {
  loading.value = true
  try {
    const res = await getActivityList({ page: currentPage.value, pageSize: pageSize.value })
    list.value = res?.data?.records || res?.data || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { id: null, title: '', type: '', startTime: '', endTime: '', description: '', coverEmoji: '🎯' }
  }
  showDialog.value = true
}

async function submitForm() {
  if (!form.value.title) {
    ElMessage.warning('请填写活动名称')
    return
  }
  try {
    if (form.value.id) {
      await updateActivity(form.value.id, form.value)
    } else {
      await createActivity(form.value)
    }
    showDialog.value = false
    ElMessage.success('保存成功')
    loadData()
  } catch {
    ElMessage.error('保存失败')
  }
}

async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该活动？', '提示')
  try {
    await deleteActivity(row.id)
    list.value = list.value.filter(a => a.id !== row.id)
    ElMessage.success('已删除')
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import '@/styles/global.scss';

.activity-page {
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

.cover-emoji {
  font-size: 32px;
  line-height: 1;
}

.activity-title {
  font-weight: 500;
  color: $admin-text;
}

.participant-count {
  font-weight: 600;
  color: $admin-primary;
}

.pagination-wrap {
  margin-top: $admin-space-4;
  display: flex;
  justify-content: flex-end;
}

.activity-form {
  .emoji-selector {
    display: flex;
    flex-wrap: wrap;
    gap: $admin-space-2;

    .emoji-option {
      width: 44px;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      border: 2px solid $admin-border;
      border-radius: $admin-radius-md;
      cursor: pointer;
      transition: all $admin-transition-fast;

      &:hover {
        border-color: $admin-primary-light;
      }

      &.active {
        border-color: $admin-primary;
        background: $admin-primary-bg;
      }
    }
  }

  .time-range {
    display: flex;
    gap: $admin-space-4;

    .time-item {
      flex: 1;
    }
  }
}
</style>
