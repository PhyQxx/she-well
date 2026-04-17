<template>
  <div class="question-page">
    <div class="page-header">
      <h1 class="page-title">❓ 问答管理</h1>
      <p class="page-subtitle">管理用户提问和专家回复</p>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" size="default">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="pending">待回复</el-radio-button>
        <el-radio-button value="answered">已回复</el-radio-button>
      </el-radio-group>
    </div>

    <!-- Table Card -->
    <div class="card">
      <el-table :data="filteredList" stripe v-loading="loading" row-key="id">
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="question-title">{{ row.title }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'answered' ? 'success' : 'warning'" size="small" round>
              {{ row.status === 'answered' ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openAnswerDialog(row)">回复</el-button>
            <el-button
              v-if="row.status === 'answered'"
              size="small"
              type="success"
              plain
              @click="markResolved(row)"
            >已解决</el-button>
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

    <!-- Answer Dialog -->
    <el-dialog v-model="showAnswerDialog" title="回复问答" width="500px" destroy-on-close>
      <div class="answer-form">
        <div class="question-info">
          <h4>{{ currentQuestion?.title }}</h4>
          <p class="question-meta">用户：{{ currentQuestion?.userName }} | 分类：{{ currentQuestion?.category }}</p>
        </div>
        <el-divider />
        <el-form :model="answerForm" label-position="top">
          <el-form-item label="回复内容">
            <el-input
              v-model="answerForm.content"
              type="textarea"
              :rows="5"
              placeholder="请输入回复内容..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showAnswerDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAnswer">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestionList, replyQuestion, deleteQuestion } from '@/api'

const list = ref([])
const loading = ref(false)
const filterStatus = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredList.value.length)

const showAnswerDialog = ref(false)
const currentQuestion = ref(null)
const answerForm = ref({ content: '' })

const filteredList = computed(() => {
  if (filterStatus.value === 'all') return list.value
  return list.value.filter(item => item.status === filterStatus.value)
})

async function loadData() {
  loading.value = true
  try {
    const res = await getQuestionList({ page: currentPage.value, pageSize: pageSize.value })
    list.value = res?.data?.records || res?.data || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function openAnswerDialog(row) {
  currentQuestion.value = row
  answerForm.value.content = ''
  showAnswerDialog.value = true
}

async function submitAnswer() {
  if (!answerForm.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await replyQuestion(currentQuestion.value.id, answerForm.value.content)
    const idx = list.value.findIndex(q => q.id === currentQuestion.value.id)
    if (idx !== -1) {
      list.value[idx].status = 'answered'
    }
    ElMessage.success('回复成功')
    showAnswerDialog.value = false
  } catch {
    ElMessage.error('回复失败')
  }
}

function markResolved(row) {
  ElMessage.success(`问题「${row.title}」已标记为已解决`)
}

async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该问答？', '提示')
  try {
    await deleteQuestion(row.id)
    list.value = list.value.filter(q => q.id !== row.id)
    ElMessage.success('已删除')
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import '@/styles/global.scss';

.question-page {
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

  :deep(.el-radio-group) {
    .el-radio-button__inner {
      border-radius: $admin-radius-md;
    }
  }
}

.card {
  background: $admin-white;
  border-radius: $admin-radius-xl;
  padding: $admin-space-5;
  box-shadow: $admin-shadow-sm;
}

.question-title {
  font-weight: 500;
  color: $admin-text;
}

.question-meta {
  font-size: $admin-font-size-xs;
  color: $admin-muted;
  margin: $admin-space-2 0 0;
}

.answer-form {
  .question-info {
    h4 {
      margin: 0;
      font-size: $admin-font-size-md;
      color: $admin-text;
    }
  }
}

.pagination-wrap {
  margin-top: $admin-space-4;
  display: flex;
  justify-content: flex-end;
}
</style>
