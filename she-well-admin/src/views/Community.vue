<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">💬 社区管理</span>
    </template>
    <el-tabs v-model="tab" @tab-change="onTabChange">
      <el-tab-pane label="帖子管理" name="posts">
        <el-table :data="posts" stripe v-loading="loading" style="margin-top:12px">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column prop="commentCount" label="评论" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="{ 1: 'success', 0: 'warning', '-1': 'danger' }[row.status]" size="small">
                {{ { 1: '已发布', 0: '待审核', '-1': '已删除' }[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="160" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="deletePost(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="话题管理" name="topics">
        <el-table :data="topics" stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="话题名称" />
          <el-table-column prop="postCount" label="帖子数" width="100" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostList, deletePost as deletePostApi } from '@/api'
import api from '@/api'

const tab = ref('posts')
const posts = ref([])
const topics = ref([])
const loading = ref(false)

async function loadPosts() {
  loading.value = true
  try {
    const res = await getPostList()
    posts.value = res.data || []
  } catch { ElMessage.error('加载帖子列表失败') }
  finally { loading.value = false }
}

async function loadTopics() {
  loading.value = true
  try {
    const res = await api.get('/community/topics')
    topics.value = res.data || []
  } catch { ElMessage.error('加载话题列表失败') }
  finally { loading.value = false }
}

async function deletePost(row) {
  await ElMessageBox.confirm('确认删除该帖子？', '提示')
  try {
    await deletePostApi(row.id)
    ElMessage.success('已删除')
    loadPosts()
  } catch { ElMessage.error('删除失败') }
}

function onTabChange(name) {
  if (name === 'posts') loadPosts()
  else if (name === 'topics') loadTopics()
}

onMounted(() => loadPosts())
</script>
