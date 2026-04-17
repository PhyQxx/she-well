<template>
  <div class="community-page">
    <!-- 欢迎横幅 -->
    <div class="community-banner">
      <div class="banner-content">
        <h2 class="banner-title">💬 姐妹社区</h2>
        <p class="banner-sub">分享经验、解答疑惑，与姐妹们一起成长</p>
      </div>
      <div class="banner-stats">
        <div class="stat-item">
          <span class="stat-num">{{ topics.length }}</span>
          <span class="stat-label">话题</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ totalPosts }}</span>
          <span class="stat-label">帖子</span>
        </div>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：话题导航 -->
      <el-col :span="6">
        <div class="card topics-card">
          <div class="card-header">
            <span class="card-title">🏷️ 话题分类</span>
          </div>
          <div class="topics-list">
            <div
              v-for="topic in topics"
              :key="topic.id"
              class="topic-item"
              :class="{ active: selectedTopic === topic.id }"
              @click="selectTopic(topic)"
            >
              <div class="topic-info">
                <span class="topic-name"># {{ topic.name }}</span>
                <span class="topic-count">{{ topic.postCount || 0 }} 篇</span>
              </div>
              <el-icon class="topic-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧：内容区 -->
      <el-col :span="18">
        <!-- Tab 切换 -->
        <div class="card content-card">
          <el-tabs v-model="activeTab" @tab-change="onTabChange">
            <el-tab-pane label="帖子" name="posts">
              <template #label>
                <span class="tab-label">
                  <el-icon><Document /></el-icon> 帖子
                </span>
              </template>

              <div class="content-header">
                <el-button type="primary" @click="showCreatePost = true">
                  <el-icon><Plus /></el-icon> 发布帖子
                </el-button>
                <div class="filter-info" v-if="selectedTopicName">
                  <el-tag type="danger" closable @close="clearTopicFilter">
                    话题: {{ selectedTopicName }}
                  </el-tag>
                </div>
              </div>

              <!-- 帖子列表 -->
              <div v-if="posts.length > 0" class="posts-list">
                <div
                  v-for="post in posts"
                  :key="post.id"
                  class="post-card"
                  @click="openPost(post)"
                >
                  <div class="post-header">
                    <div class="post-author">
                      <div class="author-avatar">{{ getAvatar(post.nickname) }}</div>
                      <span class="author-name">{{ post.nickname || '匿名用户' }}</span>
                    </div>
                    <el-tag v-if="post.topicName" size="small" type="danger" effect="plain">
                      {{ post.topicName }}
                    </el-tag>
                  </div>
                  <div class="post-title">{{ post.title }}</div>
                  <div class="post-content">{{ post.content?.slice(0, 120) }}{{ post.content?.length > 120 ? '...' : '' }}</div>
                  <div class="post-footer">
                    <span class="footer-item">
                      <el-icon><View /></el-icon> {{ post.viewCount || 0 }}
                    </span>
                    <span class="footer-item" @click.stop="toggleLike(post)">
                      <el-icon><Star /></el-icon> {{ post.likeCount || 0 }}
                    </span>
                    <span class="footer-item">
                      <el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }}
                    </span>
                    <span class="post-time">{{ post.createTime }}</span>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无帖子" :image-size="80" />
            </el-tab-pane>

            <el-tab-pane label="问答" name="qa">
              <template #label>
                <span class="tab-label">
                  <el-icon><QuestionFilled /></el-icon> 问答
                </span>
              </template>

              <div v-if="questions.length > 0" class="qa-list">
                <div
                  v-for="q in questions"
                  :key="q.id"
                  class="qa-card"
                  @click="openQuestion(q)"
                >
                  <div class="qa-header">
                    <span class="qa-tag">问</span>
                    <span class="qa-title">{{ q.title }}</span>
                  </div>
                  <div class="qa-preview" v-if="q.content">{{ q.content.slice(0, 80) }}...</div>
                  <div class="qa-footer">
                    <div class="qa-stats">
                      <span><el-icon><View /></el-icon> {{ q.viewCount || 0 }} 浏览</span>
                      <span><el-icon><ChatLineRound /></el-icon> {{ q.answerCount || 0 }} 回答</span>
                    </div>
                    <div class="qa-accepted" v-if="q.acceptedAnswer">
                      <el-icon><CircleCheckFilled /></el-icon> 已采纳
                    </div>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无问答" :image-size="80" />
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>

    <!-- 发帖弹窗 -->
    <el-dialog v-model="showCreatePost" title="发布帖子" width="600px" class="she-dialog">
      <el-form :model="postForm" label-width="80px" class="post-form">
        <el-form-item label="话题">
          <el-select v-model="postForm.topicId" placeholder="选择话题" style="width: 100%">
            <el-option
              v-for="t in topics"
              :key="t.id"
              :label="t.name"
              :value="t.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="请输入标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的经验或想法..."
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreatePost = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <!-- 帖子详情 -->
    <el-dialog v-model="showPostDetail" :title="currentPost?.title" width="700px" class="she-dialog">
      <div v-if="currentPost" class="post-detail">
        <div class="detail-header">
          <div class="detail-author">
            <div class="author-avatar large">{{ getAvatar(currentPost.nickname) }}</div>
            <div class="author-info">
              <span class="author-name">{{ currentPost.nickname }}</span>
              <span class="author-time">{{ currentPost.createTime }}</span>
            </div>
          </div>
          <el-tag v-if="currentPost.topicName" type="danger">{{ currentPost.topicName }}</el-tag>
        </div>
        <el-divider />
        <div class="detail-content">{{ currentPost.content }}</div>
        <div class="detail-actions">
          <span class="action-item" @click="toggleLike(currentPost)">
            <el-icon><Star /></el-icon> {{ currentPost.likeCount || 0 }}
          </span>
        </div>
        <el-divider content-position="left">评论 {{ comments.length }}</el-divider>
        <div class="comments-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div class="comment-avatar">{{ getAvatar(c.nickname) }}</div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="comment-author">{{ c.nickname }}</span>
                <span class="comment-time">{{ c.createTime }}</span>
              </div>
              <div class="comment-content">{{ c.content }}</div>
            </div>
          </div>
          <div v-if="!comments.length" class="no-comments">暂无评论，快来抢沙发~</div>
        </div>
        <div class="comment-input">
          <el-input
            v-model="commentContent"
            placeholder="写下你的评论..."
            @keyup.enter="submitComment"
          >
            <template #append>
              <el-button @click="submitComment" :disabled="!commentContent.trim()">
                <el-icon><Promotion /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </el-dialog>

    <!-- 问答详情 -->
    <el-dialog v-model="showQaDetail" :title="currentQuestion?.title" width="700px" class="she-dialog">
      <div v-if="currentQuestion" class="qa-detail">
        <el-alert type="info" :closable="false" class="qa-meta">
          <template #title>
            {{ currentQuestion.category }} · {{ currentQuestion.viewCount || 0 }} 浏览 · {{ currentQuestion.answerCount || 0 }} 回答
          </template>
        </el-alert>
        <div class="qa-question">{{ currentQuestion.content }}</div>
        <el-divider content-position="left">回答 {{ answers.length }}</el-divider>
        <div class="answers-list">
          <div
            v-for="a in answers"
            :key="a.id"
            class="answer-item"
            :class="{ accepted: a.isAccepted }"
          >
            <div class="answer-header">
              <div class="answer-author">
                <div class="author-avatar small">{{ getAvatar(a.nickname) }}</div>
                <span class="author-name">{{ a.nickname }}</span>
              </div>
              <el-tag v-if="a.isAccepted" type="success" size="small">✓ 已采纳</el-tag>
            </div>
            <div class="answer-content">{{ a.content }}</div>
            <div class="answer-footer">
              <span class="answer-time">{{ a.createTime }}</span>
              <span class="answer-likes" :class="{ liked: a.liked }" @click="doLikeAnswer(a)">
                <el-icon><Star /></el-icon> {{ a.likeCount || 0 }}
              </span>
            </div>
          </div>
          <div v-if="!answers.length" class="no-answers">暂无回答，快来帮助她~</div>
        </div>
        <el-divider content-position="left">我要回答</el-divider>
        <el-input
          v-model="answerContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的回答..."
        />
        <el-button
          type="primary"
          @click="submitAnswer"
          :disabled="!answerContent.trim()"
          style="margin-top: 12px"
        >
          提交回答
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ArrowRight,
  Document,
  Plus,
  View,
  Star,
  ChatDotRound,
  ChatLineRound,
  QuestionFilled,
  CircleCheckFilled,
  Promotion
} from '@element-plus/icons-vue'
import {
  getTopics,
  getPosts,
  getComments,
  createPost,
  addComment,
  getQuestionList,
  getAnswerList,
  createAnswer,
  likeAnswer,
  likePost,
  unlikePost,
  getPostDetail,
  deletePost
} from '@/api'

const activeTab = ref('posts')
const topics = ref([])
const posts = ref([])
const questions = ref([])
const selectedTopic = ref(null)
const selectedTopicName = ref('')
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

const totalPosts = computed(() => {
  return topics.value.reduce((sum, t) => sum + (t.postCount || 0), 0)
})

const likedPosts = ref(new Set())

function getAvatar(name) {
  if (!name) return '👤'
  return name.charAt(0).toUpperCase()
}

function selectTopic(topic) {
  if (selectedTopic.value === topic.id) {
    selectedTopic.value = null
    selectedTopicName.value = ''
    loadPosts(null)
  } else {
    selectedTopic.value = topic.id
    selectedTopicName.value = topic.name
    loadPosts(topic.id)
  }
}

function clearTopicFilter() {
  selectedTopic.value = null
  selectedTopicName.value = ''
  loadPosts(null)
}

async function loadPosts(topicId) {
  try {
    const res = await getPosts({ topicId })
    posts.value = res?.data?.list || res?.data || res || []
  } catch {
    posts.value = []
  }
}

async function loadQuestions() {
  try {
    const res = await getQuestionList({ page: 1, size: 20 })
    questions.value = res?.data?.list || res?.data || []
  } catch {
    questions.value = []
  }
}

async function submitPost() {
  if (!postForm.value.title || !postForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  try {
    await createPost(postForm.value)
    ElMessage.success('发布成功')
    showCreatePost.value = false
    postForm.value = { topicId: null, title: '', content: '' }
    await loadPosts(selectedTopic.value)
  } catch {
    ElMessage.error('发布失败')
  }
}

async function openPost(post) {
  currentPost.value = post
  showPostDetail.value = true
  try {
    const res = await getComments(post.id)
    comments.value = res?.data || []
  } catch {
    comments.value = []
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) return
  try {
    await addComment({ postId: currentPost.value.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    await openPost(currentPost.value)
  } catch {
    ElMessage.error('评论失败')
  }
}

async function openQuestion(q) {
  currentQuestion.value = q
  showQaDetail.value = true
  try {
    const res = await getAnswerList(q.id, { page: 1, size: 50 })
    answers.value = (res?.data?.list || res?.data || []).map(a => ({ ...a, liked: false }))
  } catch {
    answers.value = []
  }
}

async function submitAnswer() {
  if (!answerContent.value.trim()) return
  try {
    await createAnswer({ questionId: currentQuestion.value.id, content: answerContent.value })
    ElMessage.success('回答成功')
    answerContent.value = ''
    await openQuestion(currentQuestion.value)
  } catch {
    ElMessage.error('回答失败')
  }
}

async function doLikeAnswer(a) {
  if (a.liked) return
  try {
    await likeAnswer(a.id)
    a.likeCount = (a.likeCount || 0) + 1
    a.liked = true
  } catch {
    ElMessage.error('操作失败')
  }
}

async function toggleLike(post) {
  if (likedPosts.value.has(post.id)) {
    try {
      await unlikePost(post.id)
      post.likeCount = Math.max(0, (post.likeCount || 1) - 1)
      likedPosts.value.delete(post.id)
    } catch {
      ElMessage.error('取消点赞失败')
    }
  } else {
    try {
      await likePost(post.id)
      post.likeCount = (post.likeCount || 0) + 1
      likedPosts.value.add(post.id)
    } catch {
      ElMessage.error('点赞失败')
    }
  }
}

function onTabChange(tab) {
  if (tab === 'qa') loadQuestions()
}

onMounted(async () => {
  try {
    const topicsData = await getTopics()
    topics.value = Array.isArray(topicsData) ? topicsData : (topicsData?.data || [])
    await loadPosts(null)
  } catch {
    ElMessage.error('加载社区数据失败')
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.community-page {
  @include page-container;
  max-width: 1400px;
  margin: 0 auto;
}

// --------------------------------------------
// 欢迎横幅
// --------------------------------------------
.community-banner {
  @include gradient-card($she-primary, $she-primary-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $she-space-6;
  color: #fff;

  .banner-title {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    margin-bottom: $she-space-1;
  }

  .banner-sub {
    font-size: $she-font-size-md;
    opacity: 0.9;
  }

  .banner-stats {
    display: flex;
    gap: $she-space-8;

    .stat-item {
      text-align: center;

      .stat-num {
        display: block;
        font-size: $she-font-size-3xl;
        font-weight: $she-font-weight-bold;
      }

      .stat-label {
        font-size: $she-font-size-sm;
        opacity: 0.85;
      }
    }
  }
}

// --------------------------------------------
// 话题卡片
// --------------------------------------------
.topics-card {
  .topics-list {
    margin-top: $she-space-2;
  }

  .topic-item {
    @include flex-between;
    padding: $she-space-4;
    border-radius: $she-radius-lg;
    cursor: pointer;
    transition: all $she-transition-fast;
    margin-bottom: $she-space-2;

    &:hover {
      background: $she-primary-bg;

      .topic-arrow {
        opacity: 1;
        transform: translateX(0);
      }
    }

    &.active {
      background: $she-primary-bg;

      .topic-name {
        color: $she-primary;
        font-weight: $she-font-weight-semibold;
      }

      .topic-arrow {
        opacity: 1;
        color: $she-primary;
      }
    }
  }

  .topic-info {
    display: flex;
    flex-direction: column;
    gap: $she-space-1;
  }

  .topic-name {
    font-size: $she-font-size-md;
    color: $she-text;
  }

  .topic-count {
    font-size: $she-font-size-xs;
    color: $she-muted;
  }

  .topic-arrow {
    color: $she-muted;
    opacity: 0;
    transform: translateX(-4px);
    transition: all $she-transition-fast;
  }
}

// --------------------------------------------
// 内容卡片
// --------------------------------------------
.content-card {
  .tab-label {
    display: flex;
    align-items: center;
    gap: $she-space-2;
  }

  .content-header {
    @include flex-between;
    margin-bottom: $she-space-4;

    .filter-info {
      display: flex;
      gap: $she-space-2;
    }
  }
}

// --------------------------------------------
// 帖子列表
// --------------------------------------------
.posts-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
}

.post-card {
  padding: $she-space-5;
  background: $she-bg;
  border-radius: $she-radius-xl;
  cursor: pointer;
  transition: all $she-transition-normal;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $she-shadow-md;
  }

  .post-header {
    @include flex-between;
    margin-bottom: $she-space-3;
  }

  .post-author {
    display: flex;
    align-items: center;
    gap: $she-space-2;

    .author-avatar {
      @include flex-center;
      width: 36px;
      height: 36px;
      background: $she-gradient-primary;
      border-radius: $she-radius-full;
      color: #fff;
      font-size: $she-font-size-sm;
      font-weight: $she-font-weight-semibold;

      &.large {
        width: 48px;
        height: 48px;
        font-size: $she-font-size-md;
      }

      &.small {
        width: 28px;
        height: 28px;
        font-size: $she-font-size-xs;
      }
    }

    .author-name {
      font-size: $she-font-size-sm;
      font-weight: $she-font-weight-medium;
      color: $she-text;
    }
  }

  .post-title {
    font-size: $she-font-size-lg;
    font-weight: $she-font-weight-semibold;
    color: $she-title;
    margin-bottom: $she-space-2;
  }

  .post-content {
    font-size: $she-font-size-sm;
    color: $she-sub;
    line-height: $she-line-height-loose;
    margin-bottom: $she-space-3;
  }

  .post-footer {
    @include flex-between;
    font-size: $she-font-size-xs;
    color: $she-muted;

    .footer-item {
      display: flex;
      align-items: center;
      gap: $she-space-1;
      cursor: pointer;

      &:hover {
        color: $she-primary;
      }
    }

    .post-time {
      color: $she-muted;
    }
  }
}

// --------------------------------------------
// 问答列表
// --------------------------------------------
.qa-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
}

.qa-card {
  padding: $she-space-5;
  background: $she-bg;
  border-radius: $she-radius-xl;
  cursor: pointer;
  transition: all $she-transition-normal;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $she-shadow-md;
  }

  .qa-header {
    display: flex;
    align-items: flex-start;
    gap: $she-space-2;
    margin-bottom: $she-space-2;
  }

  .qa-tag {
    @include flex-center;
    width: 24px;
    height: 24px;
    background: $she-primary;
    color: #fff;
    border-radius: $she-radius-sm;
    font-size: $she-font-size-xs;
    font-weight: $she-font-weight-bold;
    flex-shrink: 0;
  }

  .qa-title {
    font-size: $she-font-size-md;
    font-weight: $she-font-weight-semibold;
    color: $she-title;
    line-height: $she-line-height-tight;
  }

  .qa-preview {
    font-size: $she-font-size-sm;
    color: $she-sub;
    line-height: $she-line-height-normal;
    margin-bottom: $she-space-3;
  }

  .qa-footer {
    @include flex-between;

    .qa-stats {
      display: flex;
      gap: $she-space-4;
      font-size: $she-font-size-xs;
      color: $she-muted;

      span {
        display: flex;
        align-items: center;
        gap: $she-space-1;
      }
    }

    .qa-accepted {
      display: flex;
      align-items: center;
      gap: $she-space-1;
      font-size: $she-font-size-xs;
      color: $she-success;
      font-weight: $she-font-weight-medium;
    }
  }
}

// --------------------------------------------
// 帖子详情
// --------------------------------------------
.post-detail {
  .detail-header {
    @include flex-between;
    margin-bottom: $she-space-4;
  }

  .detail-author {
    display: flex;
    align-items: center;
    gap: $she-space-3;

    .author-info {
      display: flex;
      flex-direction: column;
    }

    .author-name {
      font-weight: $she-font-weight-semibold;
      color: $she-text;
    }

    .author-time {
      font-size: $she-font-size-xs;
      color: $she-muted;
    }
  }

  .detail-content {
    font-size: $she-font-size-md;
    line-height: $she-line-height-loose;
    color: $she-text;
    padding: $she-space-4 0;
  }

  .detail-actions {
    display: flex;
    gap: $she-space-4;

    .action-item {
      display: flex;
      align-items: center;
      gap: $she-space-1;
      color: $she-muted;
      cursor: pointer;

      &:hover {
        color: $she-primary;
      }
    }
  }
}

// --------------------------------------------
// 评论区
// --------------------------------------------
.comments-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: $she-space-4;

  @include custom-scrollbar;
}

.comment-item {
  display: flex;
  gap: $she-space-3;
  padding: $she-space-3 0;
  border-bottom: 1px solid $she-border;

  &:last-child {
    border-bottom: none;
  }
}

.comment-avatar {
  @include flex-center;
  width: 32px;
  height: 32px;
  background: $she-gradient-primary;
  border-radius: $she-radius-full;
  color: #fff;
  font-size: $she-font-size-xs;
  font-weight: $she-font-weight-semibold;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;

  .comment-header {
    @include flex-between;
    margin-bottom: $she-space-1;
  }

  .comment-author {
    font-size: $she-font-size-sm;
    font-weight: $she-font-weight-medium;
    color: $she-text;
  }

  .comment-time {
    font-size: $she-font-size-xs;
    color: $she-muted;
  }

  .comment-content {
    font-size: $she-font-size-sm;
    color: $she-sub;
    line-height: $she-line-height-normal;
  }
}

.no-comments,
.no-answers {
  text-align: center;
  padding: $she-space-6;
  color: $she-muted;
  font-size: $she-font-size-sm;
}

// --------------------------------------------
// 问答详情
// --------------------------------------------
.qa-detail {
  .qa-meta {
    margin-bottom: $she-space-4;
  }

  .qa-question {
    font-size: $she-font-size-md;
    line-height: $she-line-height-loose;
    color: $she-text;
    padding: $she-space-4 0;
  }
}

.answers-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: $she-space-4;

  @include custom-scrollbar;
}

.answer-item {
  padding: $she-space-4;
  background: $she-bg;
  border-radius: $she-radius-lg;
  margin-bottom: $she-space-3;
  border-left: 3px solid transparent;

  &.accepted {
    border-left-color: $she-success;
    background: rgba($she-success, 0.05);
  }

  .answer-header {
    @include flex-between;
    margin-bottom: $she-space-2;

    .answer-author {
      display: flex;
      align-items: center;
      gap: $she-space-2;
    }
  }

  .answer-content {
    font-size: $she-font-size-sm;
    line-height: $she-line-height-loose;
    color: $she-text;
    margin-bottom: $she-space-2;
  }

  .answer-footer {
    @include flex-between;

    .answer-time {
      font-size: $she-font-size-xs;
      color: $she-muted;
    }

    .answer-likes {
      display: flex;
      align-items: center;
      gap: $she-space-1;
      font-size: $she-font-size-sm;
      color: $she-muted;
      cursor: pointer;

      &:hover {
        color: $she-primary;
      }

      &.liked {
        color: $she-primary;
      }
    }
  }
}

// --------------------------------------------
// 响应式
// --------------------------------------------
@media (max-width: 992px) {
  .el-col-6 {
    width: 100% !important;
  }

  .el-col-18 {
    width: 100% !important;
  }

  .topics-card {
    margin-bottom: $she-space-4;
  }
}
</style>
