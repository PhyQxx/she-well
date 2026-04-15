<template>
  <div class="knowledge-page">
    <el-page-header @back="() => $router.back()" title="健康知识">
      <template #content><span class="page-title">📖 健康知识</span></template>
    </el-page-header>

    <el-row :gutter="16" style="margin-top: 16px">
      <!-- 左侧分类 -->
      <el-col :span="6">
        <el-card shadow="hover" :body-style="{ padding: '0' }">
          <template #header>知识分类</template>
          <div
            v-for="c in categories" :key="c.id"
            class="cat-item"
            :class="{ active: selectedCat === c.id }"
            @click="selectCat(c.id)"
          >
            <span>{{ c.icon || '📂' }}</span>
            <span>{{ c.name }}</span>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧文章列表 -->
      <el-col :span="18">
        <el-card shadow="hover">
          <template #header>
            <span>{{ currentCatName || '全部文章' }}</span>
            <el-input
              v-model="keyword"
              placeholder="搜索文章..."
              size="small"
              style="width:200px;float:right"
              @keyup.enter="searchArticle"
              clearable
            />
          </template>

          <div v-if="loading" style="text-align:center;padding:40px;color:#999">加载中...</div>
          <div v-else-if="articles.length === 0" style="text-align:center;padding:60px;color:#999">
            暂无文章
          </div>
          <div v-else>
            <div
              v-for="a in articles" :key="a.id"
              class="article-item"
              @click="viewArticle(a)"
            >
              <div class="article-top">
                <span class="article-title">{{ a.title }}</span>
                <el-tag v-if="a.categoryName" size="small" type="danger">{{ a.categoryName }}</el-tag>
              </div>
              <div class="article-summary">{{ a.summary || a.content?.slice(0, 120) + '...' }}</div>
              <div class="article-meta">
                <span>👁 {{ a.viewCount || 0 }}</span>
                <span style="margin-left:12px">❤️ {{ a.likeCount || 0 }}</span>
                <span style="margin-left:12px">📅 {{ a.publishTime || a.createTime }}</span>
              </div>
            </div>
            <el-pagination
              v-if="total > pageSize"
              layout="prev, pager, next"
              :total="total"
              :page-size="pageSize"
              v-model:current-page="page"
              @current-change="loadArticles"
              style="margin-top:16px;justify-content:center"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 文章详情弹窗 -->
    <el-dialog v-model="showArticle" :title="currentArticle?.title" width="700px" top="5vh">
      <div v-if="currentArticle" class="article-detail">
        <div class="article-meta" style="margin-bottom:16px">
          <span>分类：{{ currentArticle.categoryName }}</span>
          <span style="margin-left:16px">阅读：{{ currentArticle.viewCount || 0 }}</span>
          <span style="margin-left:16px">{{ currentArticle.publishTime || currentArticle.createTime }}</span>
        </div>
        <el-divider />
        <div class="article-body" v-html="currentArticle.content || currentArticle.body"></div>
        <el-divider />
        <div class="article-disclaimer">
          ⚠️ <strong>免责声明</strong>：本文内容仅供参考，不能替代专业医生的诊断和治疗。如有健康问题，请及时就医。
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCategories, getArticles } from '@/api'

const categories = ref([])
const articles = ref([])
const selectedCat = ref(null)
const keyword = ref('')
const loading = ref(false)
const showArticle = ref(false)
const currentArticle = ref(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const currentCatName = computed(() => {
  if (!selectedCat.value) return '全部文章'
  const cat = categories.value.find(c => c.id === selectedCat.value)
  return cat ? cat.name : '全部文章'
})

async function selectCat(id) {
  selectedCat.value = selectedCat.value === id ? null : id
  page.value = 1
  await loadArticles()
}

async function searchArticle() {
  page.value = 1
  await loadArticles()
}

async function loadArticles() {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize.value }
    if (selectedCat.value) params.categoryId = selectedCat.value
    if (keyword.value) params.keyword = keyword.value

    let data = await getArticles(params)
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
  } finally {
    loading.value = false
  }
}

async function viewArticle(a) {
  currentArticle.value = a
  showArticle.value = true
}

onMounted(async () => {
  try {
    const cats = await getCategories()
    categories.value = Array.isArray(cats) ? cats : []
  } catch {}
  await loadArticles()
})
</script>

<style scoped>
.knowledge-page { padding: 8px }
.page-title { font-size: 18px; font-weight: 600 }
.cat-item {
  display: flex; align-items: center; gap: 8px;
  padding: 12px 16px; cursor: pointer;
  color: #666; font-size: 14px; border-bottom: 1px solid #f5f5f5
}
.cat-item:hover { background: #fff5f7 }
.cat-item.active { background: #fce4ec; color: #e91e63; font-weight: 500 }
.article-item {
  padding: 14px 0; border-bottom: 1px solid #f5f5f5; cursor: pointer
}
.article-item:hover .article-title { color: #e91e63 }
.article-top { display: flex; align-items: center; gap: 8px; margin-bottom: 6px }
.article-title { font-size: 16px; font-weight: 500; color: #333; flex: 1 }
.article-title:hover { color: #e91e63 }
.article-summary { font-size: 13px; color: #888; margin: 4px 0; line-height: 1.6 }
.article-meta { font-size: 12px; color: #bbb }
.article-detail { font-size: 15px; line-height: 1.9 }
.article-body { color: #444; min-height: 200px }
.article-disclaimer {
  background: #fff5f7; border: 1px solid #fce4ec;
  border-radius: 8px; padding: 12px 16px;
  font-size: 13px; color: #c2185b; line-height: 1.6
}
</style>
