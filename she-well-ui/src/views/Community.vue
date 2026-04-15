<template>
  <div class="community-page">
    <el-page-header @back="() => $router.back()" title="姐妹社区">
      <template #content><span class="page-title">💬 姐妹社区</span></template>
    </el-page-header>

    <el-tabs v-model="activeTab" style="margin-top: 16px" @tab-change="onTabChange">
      <el-tab-pane label="话题" name="topics">
        <div v-for="topic in topics" :key="topic.id" class="topic-card" @click="loadPosts(topic.id)">
          <span class="topic-name"># {{ topic.name }}</span>
          <span class="topic-count">{{ topic.postCount || 0 }} 篇</span>
        </div>
      </el-tab-pane>

      <el-tab-pane label="帖子" name="posts">
        <el-button type="primary" size="small" @click="showCreatePost = true" style="margin-bottom:12px">发布帖子</el-button>
        <div v-for="post in posts" :key="post.id" class="post-card" @click="openPost(post)">
          <div class="post-header">
            <span class="post-author">{{ post.nickname || '匿名用户' }}</span>
            <el-tag size="small" v-if="post.topicName">{{ post.topicName }}</el-tag>
          </div>
          <div class="post-title">{{ post.title }}</div>
          <div class="post-content">{{ post.content?.slice(0, 100) }}{{ post.content?.length > 100 ? '...' : '' }}</div>
          <div class="post-footer">
            <span>❤️ {{ post.likeCount || 0 }}</span>
            <span style="margin-left:16px">💬 {{ post.commentCount || 0 }}</span>
          </div>
        </div>
        <div v-if="!posts.length" style="text-align:center;color:#999;padding:40px">暂无帖子</div>
      </el-tab-pane>

      <el-tab-pane label="问答" name="qa">
        <div v-for="q in questions" :key="q.id" class="qa-card" @click="openQuestion(q)">
          <div class="qa-q">Q: {{ q.title }}</div>
          <div class="qa-a" v-if="q.acceptedAnswer">✓ {{ q.acceptedAnswer }}</div>
          <div class="qa-meta">
            <span>{{ q.viewCount || 0 }} 浏览</span>
            <span style="margin-left:12px">{{ q.answerCount || 0 }} 回答</span>
          </div>
        </div>
        <div v-if="!questions.length" style="text-align:center;color:#999;padding:40px">暂无问答</div>
      </el-tab-pane>
    </el-tabs>

    <!-- 发帖弹窗 -->
    <el-dialog v-model="showCreatePost" title="发布帖子" width="600px">
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="话题">
          <el-select v-model="postForm.topicId" placeholder="选择话题">
            <el-option v-for="t in topics" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="postForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="postForm.content" type="textarea" :rows="5" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreatePost = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <!-- 帖子详情 -->
    <el-dialog v-model="showPostDetail" :title="currentPost?.title" width="700px">
      <div v-if="currentPost">
        <div class="post-author" style="margin-bottom:12px">{{ currentPost.nickname }} · {{ currentPost.createTime }}</div>
        <div style="line-height:1.8">{{ currentPost.content }}</div>
        <el-divider />
        <div class="comments-section">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <span class="comment-author">{{ c.nickname }}：</span>
            <span>{{ c.content }}</span>
          </div>
          <div v-if="!comments.length" style="color:#999;text-align:center;padding:12px">暂无评论</div>
        </div>
        <el-input v-model="commentContent" placeholder="写下你的评论..." style="margin-top:12px">
          <template #append><el-button @click="submitComment">发送</el-button></template>
        </el-input>
      </div>
    </el-dialog>

    <!-- 问答详情弹窗 -->
    <el-dialog v-model="showQaDetail" :title="currentQuestion?.title" width="700px">
      <div v-if="currentQuestion">
        <el-alert type="info" :closable="false" style="margin-bottom:16px">
          <template #title>{{ currentQuestion.category }} · {{ currentQuestion.viewCount || 0 }} 浏览 · {{ currentQuestion.answerCount || 0 }} 回答</template>
        </el-alert>
        <div style="line-height:1.8;margin-bottom:20px;font-size:15px">{{ currentQuestion.content }}</div>
        <el-divider>回答</el-divider>
        <div v-for="a in answers" :key="a.id" class="answer-item">
          <div class="answer-header">
            <el-tag v-if="a.isAccepted" type="success" size="small">✓ 已采纳</el-tag>
            <span class="answer-likes" @click="doLikeAnswer(a)">❤️ {{ a.likeCount || 0 }}</span>
          </div>
          <div class="answer-content">{{ a.content }}</div>
          <div class="answer-time">{{ a.createTime }}</div>
        </div>
        <div v-if="!answers.length" style="text-align:center;color:#999;padding:20px">暂无回答</div>
        <el-input v-model="answerContent" type="textarea" :rows="3" placeholder="写下你的回答..." style="margin-top:16px">
          <template #append><el-button @click="submitAnswer">发送</el-button></template>
        </el-input>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTopics, getPosts, getComments, createPost, addComment, getQuestionList, getQuestionDetail, getAnswerList, createAnswer, likeAnswer } from '@/api'

const activeTab = ref('topics')
const topics = ref([])
const posts = ref([])
const questions = ref([])
const showCreatePost = ref(false)
const showPostDetail = ref(false)
const showQaDetail = ref(false)
const currentPost = ref(null)
const currentQuestion = ref(null)
const answers = ref([])
const comments = ref([])
const commentContent = ref('')
const postForm = ref({ topicId: null, title: '', content: '' })
const answerContent = ref('')

async function loadPosts(topicId) {
  activeTab.value = 'posts'
  try {
    const res = await getPosts({ topicId })
    posts.value = res?.data?.list || res?.data || res || []
  } catch {}
}

async function loadQuestions() {
  try {
    const res = await getQuestionList({ page: 1, size: 20 })
    questions.value = res?.data?.list || res?.data || []
  } catch {}
}

async function submitPost() {
  if (!postForm.value.title || !postForm.value.content) { ElMessage.warning('请填写标题和内容'); return }
  try {
    await createPost(postForm.value)
    ElMessage.success('发布成功')
    showCreatePost.value = false
    postForm.value = { topicId: null, title: '', content: '' }
    await loadPosts()
  } catch { ElMessage.error('发布失败') }
}

async function openPost(post) {
  currentPost.value = post
  showPostDetail.value = true
  try {
    const res = await getComments(post.id)
    comments.value = res?.data || []
  } catch { comments.value = [] }
}

async function submitComment() {
  if (!commentContent.value.trim()) return
  try {
    await addComment({ postId: currentPost.value.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    await openPost(currentPost.value)
  } catch { ElMessage.error('评论失败') }
}

async function openQuestion(q) {
  currentQuestion.value = q
  showQaDetail.value = true
  try {
    const res = await getAnswerList(q.id, { page: 1, size: 50 })
    answers.value = (res?.data?.list || res?.data || []).map(a => ({ ...a, liked: false }))
  } catch { answers.value = [] }
}

async function submitAnswer() {
  if (!answerContent.value.trim()) return
  try {
    await createAnswer({ questionId: currentQuestion.value.id, content: answerContent.value })
    ElMessage.success('回答成功')
    answerContent.value = ''
    await openQuestion(currentQuestion.value)
  } catch { ElMessage.error('回答失败') }
}

async function doLikeAnswer(a) {
  if (a.liked) return
  try {
    await likeAnswer(a.id)
    a.likeCount = (a.likeCount || 0) + 1
    a.liked = true
  } catch { ElMessage.error('操作失败') }
}

function onTabChange(tab) {
  if (tab === 'qa') loadQuestions()
}

onMounted(async () => {
  try {
    topics.value = await getTopics() || []
    posts.value = await getPosts({}) || []
  } catch {}
})
</script>

<style scoped>
.community-page { padding: 8px }
.page-title { font-size: 18px; font-weight: 600 }
.topic-card { display: flex; justify-content: space-between; padding: 14px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer }
.topic-name { color: #E91E63; font-weight: 500 }
.topic-count { color: #999; font-size: 13px }
.post-card { background: #fff; border-radius: 8px; padding: 16px; margin-bottom: 12px; cursor: pointer; box-shadow: 0 1px 4px rgba(0,0,0,0.08) }
.post-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px }
.post-author { font-size: 13px; color: #666 }
.post-title { font-size: 16px; font-weight: 600; margin-bottom: 6px }
.post-content { font-size: 14px; color: #666; line-height: 1.6 }
.post-footer { margin-top: 8px; font-size: 13px; color: #999 }
.qa-card { background: #fff; border-radius: 8px; padding: 16px; margin-bottom: 12px; cursor: pointer }
.qa-q { font-size: 15px; font-weight: 500; color: #333; margin-bottom: 8px }
.qa-a { background: #f0f9eb; color: #67c23a; padding: 8px 12px; border-radius: 4px; font-size: 14px }
.qa-meta { margin-top: 8px; font-size: 13px; color: #999 }
.comment-item { padding: 8px 0; border-bottom: 1px solid #f5f5f5; font-size: 14px }
.comment-author { font-weight: 500; color: #666 }
.answer-item { background: #fafafa; border-radius: 8px; padding: 16px; margin-bottom: 12px }
.answer-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px }
.answer-likes { color: #999; font-size: 13px; cursor: pointer }
.answer-content { font-size: 14px; line-height: 1.7; color: #333 }
.answer-time { font-size: 12px; color: #999; margin-top: 8px }
</style>
