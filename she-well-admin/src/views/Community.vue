<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">💬 社区管理</span>
    </template>
    <el-tabs v-model="tab">
      <el-tab-pane label="帖子管理" name="posts">
        <el-table :data="posts" stripe style="margin-top:12px">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="nickname" label="作者" width="100" />
          <el-table-column prop="topicName" label="话题" width="100" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column prop="commentCount" label="评论" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="{ published: 'success', pending: 'warning', deleted: 'danger' }[row.status]" size="small">
                {{ { published: '已发布', pending: '待审核', deleted: '已删除' }[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewPost(row)">查看</el-button>
              <el-button v-if="row.status === 'pending'" size="small" type="success" @click="approve(row)">通过</el-button>
              <el-button size="small" type="danger" @click="deletePost(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="话题管理" name="topics">
        <div style="margin:12px 0">
          <el-button size="small" @click="showTopicDialog = true">+ 添加话题</el-button>
        </div>
        <el-table :data="topics" stripe>
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
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" @click="toggleTopic(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tab = ref('posts')
const posts = ref([
  { id: 1, title: '经期护肤小技巧分享', nickname: '小红', topicName: '护肤心得', likeCount: 45, commentCount: 12, status: 'published', createTime: '2026-04-10 14:00' },
  { id: 2, title: '备孕期间的营养补充', nickname: '花开', topicName: '备孕必看', likeCount: 23, commentCount: 8, status: 'published', createTime: '2026-04-11 10:00' },
  { id: 3, title: '孕吐严重怎么办', nickname: 'Lily', topicName: '孕期日记', likeCount: 5, commentCount: 2, status: 'pending', createTime: '2026-04-12 09:00' },
])
const topics = ref([
  { id: 1, name: '经期护理', postCount: 234, status: 1 },
  { id: 2, name: '备孕必看', postCount: 189, status: 1 },
  { id: 3, name: '孕期日记', postCount: 156, status: 1 },
  { id: 4, name: '护肤心得', postCount: 312, status: 1 },
])
const showTopicDialog = ref(false)

function approve(row) { row.status = 'published'; ElMessage.success('已通过审核') }
async function deletePost(row) {
  await ElMessageBox.confirm('确认删除该帖子？', '提示')
  posts.value = posts.value.filter(i => i.id !== row.id)
  ElMessage.success('已删除')
}
function viewPost(row) { ElMessage.info('查看帖子详情（开发中）') }
function toggleTopic(row) { row.status = row.status === 1 ? 0 : 1; ElMessage.success('已更新') }
</script>
