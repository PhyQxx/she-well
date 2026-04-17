<template>
  <div class="community-page">
    <div class="page-header">
      <h1 class="page-title">💬 社区管理</h1>
      <p class="page-subtitle">管理用户帖子和互动内容</p>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" size="default">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="pending">待审核</el-radio-button>
        <el-radio-button value="published">已发布</el-radio-button>
        <el-radio-button value="deleted">已删除</el-radio-button>
      </el-radio-group>
    </div>

    <!-- Table Card -->
    <div class="card">
      <el-table :data="filteredList" stripe v-loading="loading" row-key="id">
        <el-table-column prop="content" label="内容预览" min-width="280">
          <template #default="{ row }">
            <div class="content-preview" @click="openDetailDialog(row)">
              <p>{{ row.content }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <span class="user-name">{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column prop="likeCount" label="点赞数" width="90" align="center">
          <template #default="{ row }">
            <span class="stat-count">❤️ {{ row.likeCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="commentCount" label="评论数" width="90" align="center">
          <template #default="{ row }">
            <span class="stat-count">💬 {{ row.commentCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="{ pending: 'warning', published: 'success', deleted: 'info' }[row.status]"
              size="small"
              round
            >
              {{ { pending: '待审核', published: '已发布', deleted: '已删除' }[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openDetailDialog(row)">查看</el-button>
            <template v-if="row.status === 'pending'">
              <el-button size="small" type="success" plain @click="approvePost(row)">通过</el-button>
            </template>
            <template v-else-if="row.status === 'published'">
              <el-button size="small" type="danger" plain @click="deletePost(row)">删除</el-button>
            </template>
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

    <!-- Post Detail Dialog -->
    <el-dialog v-model="showDetailDialog" title="帖子详情" width="600px" destroy-on-close>
      <div v-if="currentPost" class="post-detail">
        <div class="detail-header">
          <div class="detail-user">
            <span class="user-avatar">{{ currentPost.userAvatar || '👤' }}</span>
            <span class="user-name">{{ currentPost.userName }}</span>
          </div>
          <span class="detail-time">{{ currentPost.createTime }}</span>
        </div>
        <el-divider />
        <div class="detail-content">
          <p>{{ currentPost.content }}</p>
        </div>
        <div class="detail-stats">
          <span>❤️ 点赞 {{ currentPost.likeCount }}</span>
          <span>💬 评论 {{ currentPost.commentCount }}</span>
        </div>
        <div class="detail-status">
          <el-tag
            :type="{ pending: 'warning', published: 'success', deleted: 'info' }[currentPost.status]"
            size="small"
            round
          >
            {{ { pending: '待审核', published: '已发布', deleted: '已删除' }[currentPost.status] }}
          </el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <template v-if="currentPost?.status === 'pending'">
          <el-button type="success" @click="approvePost(currentPost); showDetailDialog = false">通过</el-button>
          <el-button type="danger" @click="banPost(currentPost); showDetailDialog = false">下架</el-button>
        </template>
        <template v-else-if="currentPost?.status === 'published'">
          <el-button type="danger" @click="deletePost(currentPost); showDetailDialog = false">删除</el-button>
          <el-button type="warning" @click="banPost(currentPost); showDetailDialog = false">封禁</el-button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostList, approvePost as approvePostApi, deletePost as deletePostApi, banPost as banPostApi } from '@/api'

const list = ref([])
const loading = ref(false)
const filterStatus = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => filteredList.value.length)

const showDetailDialog = ref(false)
const currentPost = ref(null)

const filteredList = computed(() => {
  if (filterStatus.value === 'all') return list.value
  const statusMap = { pending: 'pending', published: 'published', deleted: 'deleted' }
  return list.value.filter(item => item.status === statusMap[filterStatus.value])
})

async function loadData() {
  loading.value = true
  try {
    const res = await getPostList({ page: currentPage.value, pageSize: pageSize.value })
    list.value = res?.data?.records || res?.data || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function openDetailDialog(row) {
  currentPost.value = { ...row }
  showDetailDialog.value = true
}

async function approvePostAction(post) {
  try {
    await approvePostApi(post.id)
    const idx = list.value.findIndex(p => p.id === post.id)
    if (idx !== -1) {
      list.value[idx].status = 'published'
    }
    ElMessage.success('已通过审核')
    showDetailDialog.value = false
  } catch {
    ElMessage.error('操作失败')
  }
}

async function deletePostAction(post) {
  await ElMessageBox.confirm('确认删除该帖子？', '提示')
  try {
    await deletePostApi(post.id)
    const idx = list.value.findIndex(p => p.id === post.id)
    if (idx !== -1) {
      list.value[idx].status = 'deleted'
    }
    ElMessage.success('已删除')
  } catch {
    ElMessage.error('删除失败')
  }
}

async function banPostAction(post) {
  await ElMessageBox.confirm('确认封禁该帖子？', '提示')
  try {
    await banPostApi(post.id)
    const idx = list.value.findIndex(p => p.id === post.id)
    if (idx !== -1) {
      list.value[idx].status = 'deleted'
    }
    ElMessage.success('已封禁')
  } catch {
    ElMessage.error('封禁失败')
  }
}

// Keep old function names for template compatibility
const approvePost = approvePostAction
const deletePost = deletePostAction
const banPost = banPostAction

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import '@/styles/global.scss';

.community-page {
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

.content-preview {
  cursor: pointer;
  transition: color $admin-transition-fast;

  p {
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    font-size: $admin-font-size-sm;
    color: $admin-text;
    line-height: 1.6;
  }

  &:hover p {
    color: $admin-primary;
  }
}

.user-info {
  .user-name {
    font-weight: 500;
    color: $admin-text;
  }
}

.stat-count {
  font-size: $admin-font-size-xs;
  color: $admin-text-secondary;
}

.pagination-wrap {
  margin-top: $admin-space-4;
  display: flex;
  justify-content: flex-end;
}

.post-detail {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .detail-user {
      display: flex;
      align-items: center;
      gap: $admin-space-2;

      .user-avatar {
        font-size: 24px;
      }

      .user-name {
        font-weight: 500;
        color: $admin-text;
      }
    }

    .detail-time {
      font-size: $admin-font-size-xs;
      color: $admin-muted;
    }
  }

  .detail-content {
    padding: $admin-space-4 0;

    p {
      margin: 0;
      font-size: $admin-font-size-md;
      color: $admin-text;
      line-height: 1.8;
      white-space: pre-wrap;
    }
  }

  .detail-stats {
    display: flex;
    gap: $admin-space-6;
    padding: $admin-space-3 0;
    color: $admin-text-secondary;
    font-size: $admin-font-size-sm;
  }

  .detail-status {
    padding-top: $admin-space-3;
  }
}
</style>
