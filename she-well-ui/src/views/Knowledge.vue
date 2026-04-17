<template>
  <div class="knowledge-page">
    <!-- 欢迎横幅 -->
    <div class="knowledge-banner">
      <div class="banner-content">
        <h2 class="banner-title">📖 健康知识</h2>
        <p class="banner-sub">专业可靠的健康科普，守护您的每一刻</p>
      </div>
      <div class="banner-decoration">📚</div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧分类导航 -->
      <el-col :span="6">
        <div class="card categories-card">
          <div class="card-header">
            <span class="card-title">📂 知识分类</span>
          </div>
          <div class="categories-list">
            <div
              class="category-item"
              :class="{ active: selectedCat === null }"
              @click="selectCat(null)"
            >
              <span class="category-icon">🌟</span>
              <span class="category-name">全部文章</span>
              <span class="category-count">{{ totalArticles }}</span>
            </div>
            <div
              v-for="c in categories"
              :key="c.id"
              class="category-item"
              :class="{ active: selectedCat === c.id }"
              @click="selectCat(c.id)"
            >
              <span class="category-icon">{{ c.icon || '📁' }}</span>
              <span class="category-name">{{ c.name }}</span>
              <span class="category-count">{{ c.articleCount || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- 热门标签 -->
        <div class="card tags-card">
          <div class="card-header">
            <span class="card-title">🏷️ 热门标签</span>
          </div>
          <div class="tags-list">
            <el-tag
              v-for="tag in hotTags"
              :key="tag"
              type="danger"
              effect="plain"
              class="tag-item"
              @click="searchByTag(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </el-col>

      <!-- 右侧文章列表 -->
      <el-col :span="18">
        <div class="card articles-card">
          <div class="card-header">
            <span class="card-title">{{ currentCatName }}</span>
            <el-input
              v-model="keyword"
              placeholder="搜索文章..."
              size="small"
              style="width: 200px"
              @keyup.enter="searchArticle"
              clearable
              @clear="searchArticle"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 空状态 -->
          <div v-else-if="articles.length === 0" class="empty-state">
            <div class="empty-icon">📭</div>
            <div class="empty-text">暂无文章</div>
            <el-button type="primary" plain @click="resetFilter" style="margin-top: 12px">
              重置筛选
            </el-button>
          </div>

          <!-- 文章列表 -->
          <div v-else class="articles-list">
            <div
              v-for="article in articles"
              :key="article.id"
              class="article-card"
              @click="viewArticle(article)"
            >
              <div class="article-content">
                <div class="article-header">
                  <el-tag v-if="article.categoryName" size="small" type="danger" effect="plain">
                    {{ article.categoryName }}
                  </el-tag>
                  <span class="article-title">{{ article.title }}</span>
                </div>
                <div class="article-summary">
                  {{ article.summary || article.content?.slice(0, 150) + '...' }}
                </div>
                <div class="article-footer">
                  <div class="article-meta">
                    <span class="meta-item">
                      <el-icon><View /></el-icon> {{ article.viewCount || 0 }}
                    </span>
                    <span class="meta-item">
                      <el-icon><Star /></el-icon> {{ article.likeCount || 0 }}
                    </span>
                    <span class="meta-item">
                      <el-icon><Calendar /></el-icon> {{ article.publishTime || article.createTime }}
                    </span>
                  </div>
                  <el-button type="primary" link size="small">
                    阅读全文 <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </div>
              </div>
              <el-image
                v-if="article.coverImage"
                :src="article.coverImage"
                class="article-cover"
                fit="cover"
              />
            </div>
          </div>

          <!-- 分页 -->
          <el-pagination
            v-if="total > pageSize"
            layout="prev, pager, next, total"
            :total="total"
            :page-size="pageSize"
            v-model:current-page="page"
            @current-change="loadArticles"
            class="article-pagination"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 文章详情弹窗 -->
    <el-dialog
      v-model="showArticle"
      :title="currentArticle?.title"
      width="800px"
      top="5vh"
      class="she-dialog"
    >
      <div v-if="currentArticle" class="article-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag v-if="currentArticle.categoryName" type="danger">
              {{ currentArticle.categoryName }}
            </el-tag>
            <span class="meta-text">
              <el-icon><View /></el-icon> {{ currentArticle.viewCount || 0 }} 阅读
            </span>
            <span class="meta-text">
              <el-icon><Calendar /></el-icon> {{ currentArticle.publishTime || currentArticle.createTime }}
            </span>
          </div>
        </div>
        <el-divider />
        <div class="detail-cover" v-if="currentArticle.coverImage">
          <el-image :src="currentArticle.coverImage" fit="cover" />
        </div>
        <div class="detail-body" v-html="currentArticle.content || currentArticle.body"></div>
        <el-divider />
        <div class="detail-actions">
          <el-button :type="articleLiked ? 'danger' : 'default'" @click="toggleArticleLike">
            <el-icon><Star /></el-icon> 点赞 {{ currentArticle.likeCount || 0 }}
          </el-button>
          <el-button type="default" @click="shareArticle">
            <el-icon><Share /></el-icon> 分享
          </el-button>
        </div>
        <el-divider content-position="left">免责声明</el-divider>
        <el-alert type="warning" :closable="false" show-icon>
          <template #title>
            ⚠️ 本文内容仅供参考，不能替代专业医生的诊断和治疗。如有健康问题，请及时就医。
          </template>
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, View, Star, Calendar, ArrowRight, Share } from '@element-plus/icons-vue'
import { getCategories, getArticles } from '@/api'

const categories = ref([])
const articles = ref([])
const selectedCat = ref(null)
const keyword = ref('')
const loading = ref(false)
const showArticle = ref(false)
const currentArticle = ref(null)
const articleLiked = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const hotTags = ['经期护理', '排卵期', '备孕指南', '孕期营养', '产后恢复', '妇科疾病']

const currentCatName = computed(() => {
  if (keyword.value) return `搜索: "${keyword.value}"`
  if (!selectedCat.value) return '全部文章'
  const cat = categories.value.find(c => c.id === selectedCat.value)
  return cat ? cat.name : '全部文章'
})

const totalArticles = computed(() => {
  return categories.value.reduce((sum, c) => sum + (c.articleCount || 0), 0)
})

async function selectCat(id) {
  selectedCat.value = id
  page.value = 1
  keyword.value = ''
  await loadArticles()
}

async function searchArticle() {
  page.value = 1
  await loadArticles()
}

async function searchByTag(tag) {
  keyword.value = tag
  page.value = 1
  await loadArticles()
}

function resetFilter() {
  selectedCat.value = null
  keyword.value = ''
  page.value = 1
  loadArticles()
}

async function loadArticles() {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize.value }
    if (selectedCat.value) params.categoryId = selectedCat.value
    if (keyword.value) params.keyword = keyword.value

    let res = await getArticles(params)
    let data = res?.data || res || []
    if (Array.isArray(data)) {
      articles.value = data
      total.value = data.length
    } else if (data && typeof data === 'object') {
      articles.value = data.records || data.list || data.data || []
      total.value = data.total || articles.value.length
    } else {
      articles.value = []
    }
  } catch {
    articles.value = []
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

async function viewArticle(a) {
  currentArticle.value = a
  articleLiked.value = false
  showArticle.value = true
}

function toggleArticleLike() {
  articleLiked.value = !articleLiked.value
  if (currentArticle.value) {
    currentArticle.value.likeCount = (currentArticle.value.likeCount || 0) + (articleLiked.value ? 1 : -1)
  }
  ElMessage.success(articleLiked.value ? '点赞成功' : '取消点赞')
}

function shareArticle() {
  ElMessage.success('分享链接已复制到剪贴板')
}

onMounted(async () => {
  try {
    const catsRes = await getCategories()
    categories.value = Array.isArray(catsRes) ? catsRes : (catsRes?.data || [])
  } catch {
    categories.value = []
  }
  await loadArticles()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.knowledge-page {
  @include page-container;
  max-width: 1400px;
  margin: 0 auto;
}

// --------------------------------------------
// 欢迎横幅
// --------------------------------------------
.knowledge-banner {
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

  .banner-decoration {
    font-size: 80px;
    opacity: 0.3;
  }
}

// --------------------------------------------
// 分类卡片
// --------------------------------------------
.categories-card {
  margin-bottom: $she-space-4;

  .categories-list {
    margin-top: $she-space-2;
  }

  .category-item {
    @include flex-between;
    padding: $she-space-3 $she-space-4;
    border-radius: $she-radius-lg;
    cursor: pointer;
    transition: all $she-transition-fast;
    margin-bottom: $she-space-1;

    &:hover {
      background: $she-primary-bg;
    }

    &.active {
      background: $she-primary-bg;

      .category-name {
        color: $she-primary;
        font-weight: $she-font-weight-semibold;
      }

      .category-count {
        background: $she-primary;
        color: #fff;
      }
    }
  }

  .category-icon {
    font-size: $she-font-size-lg;
  }

  .category-name {
    flex: 1;
    font-size: $she-font-size-sm;
    color: $she-text;
    margin-left: $she-space-2;
  }

  .category-count {
    font-size: $she-font-size-xs;
    color: $she-muted;
    background: $she-bg;
    padding: 2px 8px;
    border-radius: $she-radius-full;
    transition: all $she-transition-fast;
  }
}

// --------------------------------------------
// 标签卡片
// --------------------------------------------
.tags-card {
  .tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: $she-space-2;
    margin-top: $she-space-2;
  }

  .tag-item {
    cursor: pointer;
    transition: all $she-transition-fast;

    &:hover {
      transform: scale(1.05);
    }
  }
}

// --------------------------------------------
// 文章卡片
// --------------------------------------------
.articles-card {
  .loading-state {
    padding: $she-space-4;
  }

  .empty-state {
    @include flex-center;
    flex-direction: column;
    padding: $she-space-10;
    color: $she-muted;

    .empty-icon {
      font-size: 64px;
      margin-bottom: $she-space-4;
    }

    .empty-text {
      font-size: $she-font-size-md;
    }
  }
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
}

.article-card {
  display: flex;
  gap: $she-space-4;
  padding: $she-space-5;
  background: $she-bg;
  border-radius: $she-radius-xl;
  cursor: pointer;
  transition: all $she-transition-normal;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $she-shadow-md;

    .article-title {
      color: $she-primary;
    }
  }

  .article-content {
    flex: 1;
  }

  .article-header {
    display: flex;
    align-items: center;
    gap: $she-space-2;
    margin-bottom: $she-space-2;
  }

  .article-title {
    font-size: $she-font-size-lg;
    font-weight: $she-font-weight-semibold;
    color: $she-title;
    transition: color $she-transition-fast;
    line-height: $she-line-height-tight;
  }

  .article-summary {
    font-size: $she-font-size-sm;
    color: $she-sub;
    line-height: $she-line-height-loose;
    margin-bottom: $she-space-3;
    @include text-ellipsis(2);
  }

  .article-footer {
    @include flex-between;

    .article-meta {
      display: flex;
      gap: $she-space-4;

      .meta-item {
        display: flex;
        align-items: center;
        gap: $she-space-1;
        font-size: $she-font-size-xs;
        color: $she-muted;
      }
    }
  }

  .article-cover {
    width: 160px;
    height: 120px;
    border-radius: $she-radius-lg;
    flex-shrink: 0;
  }
}

.article-pagination {
  margin-top: $she-space-6;
  justify-content: center;
}

// --------------------------------------------
// 文章详情
// --------------------------------------------
.article-detail {
  .detail-header {
    margin-bottom: $she-space-2;
  }

  .detail-meta {
    display: flex;
    align-items: center;
    gap: $she-space-4;

    .meta-text {
      display: flex;
      align-items: center;
      gap: $she-space-1;
      font-size: $she-font-size-sm;
      color: $she-muted;
    }
  }

  .detail-cover {
    margin: $she-space-4 0;
    border-radius: $she-radius-lg;
    overflow: hidden;

    .el-image {
      width: 100%;
    }
  }

  .detail-body {
    font-size: $she-font-size-md;
    line-height: $she-line-height-loose;
    color: $she-text;
    min-height: 200px;

    :deep(p) {
      margin-bottom: $she-space-4;
    }

    :deep(img) {
      max-width: 100%;
      border-radius: $she-radius-md;
    }
  }

  .detail-actions {
    display: flex;
    gap: $she-space-3;
    margin-bottom: $she-space-4;
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

  .categories-card {
    margin-bottom: $she-space-4;
  }

  .article-card {
    flex-direction: column;

    .article-cover {
      width: 100%;
      height: 200px;
    }
  }
}
</style>
