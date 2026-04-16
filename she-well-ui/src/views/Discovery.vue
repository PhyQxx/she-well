<template>
  <div class="discovery-page">
    <div class="page-header">
      <h2>🔍 发现</h2>
      <el-input v-model="keyword" placeholder="搜索内容..." prefix-icon="Search" clearable style="width:280px" @keyup.enter="search" />
    </div>

    <el-row :gutter="16">
      <!-- 左侧：内容 -->
      <el-col :span="16">
        <!-- Banner 轮播 -->
        <el-card shadow="hover" class="banner-card">
          <el-carousel trigger="click" height="220px" interval="4000">
            <el-carousel-item v-for="b in banners" :key="b.id">
              <div class="banner-item" :style="{ background: b.bg }" @click="bannerClick(b)">
                <div class="banner-title">{{ b.title }}</div>
                <div class="banner-sub">{{ b.sub }}</div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-card>

        <!-- 热门活动 -->
        <el-card shadow="hover" style="margin-top:16px">
          <template #header>
            <span style="font-weight:600">🎁 热门活动</span>
            <el-button text size="small" style="float:right" @click="$router.push('/activity')">查看全部</el-button>
          </template>
          <el-row :gutter="12">
            <el-col :span="8" v-for="a in activities" :key="a.id">
              <div class="activity-card" :style="{ background: a.bg }" @click="activityClick(a)">
                <div class="act-title">{{ a.name }}</div>
                <div class="act-date">{{ a.date }}</div>
                <div class="act-count">{{ a.participants }}人参与</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 个性化推荐文章 -->
        <el-card shadow="hover" style="margin-top:16px">
          <template #header>
            <span style="font-weight:600">📖 为你推荐</span>
          </template>
          <div class="article-grid">
            <div v-for="a in articles" :key="a.id" class="article-item" @click="viewArticle(a)">
              <div class="article-cover" :style="{ background: a.coverBg }">
                <span class="article-icon">{{ a.icon }}</span>
              </div>
              <div class="article-info">
                <div class="article-title">{{ a.title }}</div>
                <div class="article-meta">
                  <span>{{ a.category }}</span>
                  <span>👁 {{ a.views }}</span>
                  <span>❤️ {{ a.likes }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：边栏 -->
      <el-col :span="8">
        <!-- 模式切换 -->
        <el-card shadow="hover" class="mode-card">
          <template #header>
            <span style="font-weight:600">⚡ 当前模式</span>
          </template>
          <div class="mode-list">
            <div
              v-for="m in modes" :key="m.key"
              class="mode-item"
              :class="{ active: currentMode === m.key }"
              @click="switchMode(m.key)"
            >
              <span class="mode-icon">{{ m.icon }}</span>
              <span class="mode-label">{{ m.label }}</span>
            </div>
          </div>
        </el-card>

        <!-- 今日健康任务 -->
        <el-card shadow="hover" style="margin-top:16px">
          <template #header>
            <span style="font-weight:600">📋 今日任务</span>
          </template>
          <div class="task-list">
            <div v-for="task in todayTasks" :key="task.id" class="task-item">
              <el-checkbox v-model="task.done" :label="task.label" size="small" />
              <span class="task-reward">+{{ task.reward }}积分</span>
            </div>
          </div>
        </el-card>

        <!-- 孕育百科 -->
        <el-card shadow="hover" style="margin-top:16px">
          <template #header>
            <span style="font-weight:600">📚 孕育百科</span>
          </template>
          <div class="wiki-list">
            <div v-for="w in wikiList" :key="w.id" class="wiki-item" @click="viewArticle(w)">
              <span class="wiki-icon">{{ w.icon }}</span>
              <span class="wiki-title">{{ w.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBanners, getActivities, getHotArticles2 } from '@/api'

const keyword = ref('')
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const [bannerRes, activityRes, articleRes] = await Promise.all([
      getBanners(),
      getActivities(),
      getHotArticles2(4),
    ])
    banners.value = (bannerRes?.data || bannerRes || []).map(b => ({
      id: b.id,
      title: b.title || 'SheWell 资讯',
      sub: b.subtitle || b.description || '',
      bg: b.bgColor || 'linear-gradient(135deg, #E91E63, #F48FB1)',
      link: b.linkUrl || '',
    }))
    activities.value = (activityRes?.data || activityRes || []).map(a => ({
      id: a.id,
      name: a.name || '活动',
      date: a.startDate ? `${a.startDate.slice(5)}-${a.endDate?.slice(5) || ''}` : '长期',
      participants: a.participantCount || 0,
      bg: 'linear-gradient(135deg, #E91E63, #F48FB1)',
    }))
    articles.value = (articleRes?.data || articleRes || []).map(a => ({
      id: a.id,
      title: a.title,
      icon: '📖',
      category: a.categoryName || a.category || '健康',
      views: a.viewCount || 0,
      likes: a.likeCount || 0,
      coverBg: 'linear-gradient(135deg, #E91E63, #F48FB1)',
    }))
  } catch (err) {
    ElMessage.error('加载发现页数据失败')
  } finally {
    loading.value = false
  }
}

const banners = ref([])

const activities = ref([])

const articles = ref([])

const modes = [
  { key: 'period', label: '经期模式', icon: '📅' },
  { key: 'trying', label: '备孕模式', icon: '🤰' },
  { key: 'pregnancy', label: '怀孕模式', icon: '🤱' },
  { key: 'nursing', label: '妈妈模式', icon: '🍼' },
]
const currentMode = ref('period')

const todayTasks = ref([])

const wikiList = ref([])

function search() {
  if (!keyword.value) return
  ElMessage.info(`搜索：${keyword.value}`)
}
function bannerClick(b) { if (b.link) window.location.hash = b.link }
function activityClick(a) { ElMessage.info(`活动：${a.name}`) }
function viewArticle(a) { if (a.id) window.location.hash = `/knowledge/${a.id}` }
async function switchMode(key) {
  currentMode.value = key
  try {
    const api = (await import('@/api')).default
    await api.put('/settings/mode', { mode: key })
  } catch {}
}

onMounted(loadData)
</script>

<style scoped>
.discovery-page { padding: 24px }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px }
.page-header h2 { margin: 0; font-size: 24px }
.banner-item { height: 220px; border-radius: 12px; display: flex; flex-direction: column; justify-content: center; padding: 32px; cursor: pointer }
.banner-title { font-size: 28px; font-weight: bold; color: white; margin-bottom: 8px }
.banner-sub { font-size: 16px; color: rgba(255,255,255,0.85) }
.activity-card { border-radius: 12px; padding: 20px; color: white; cursor: pointer; min-height: 100px; display: flex; flex-direction: column; justify-content: flex-end }
.act-title { font-size: 16px; font-weight: 600; margin-bottom: 4px }
.act-date { font-size: 13px; opacity: 0.85; margin-bottom: 4px }
.act-count { font-size: 12px; opacity: 0.75 }
.article-grid { display: flex; flex-direction: column; gap: 12px }
.article-item { display: flex; gap: 12px; cursor: pointer; padding: 8px; border-radius: 8px; transition: background 0.2s }
.article-item:hover { background: #faf8fb }
.article-cover { width: 80px; height: 80px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0 }
.article-icon { font-size: 36px }
.article-info { flex: 1; display: flex; flex-direction: column; justify-content: center; gap: 8px }
.article-title { font-size: 15px; font-weight: 500; color: #333; line-height: 1.4 }
.article-meta { display: flex; gap: 12px; font-size: 13px; color: #999 }
.mode-card :deep(.el-card__header) { padding-bottom: 12px }
.mode-list { display: flex; flex-direction: column; gap: 8px }
.mode-item { display: flex; align-items: center; gap: 8px; padding: 12px; border-radius: 8px; cursor: pointer; border: 2px solid transparent; transition: all 0.2s }
.mode-item.active { border-color: #E91E63; background: #fff5f7 }
.mode-icon { font-size: 20px }
.mode-label { font-size: 14px; color: #333 }
.task-list { display: flex; flex-direction: column; gap: 8px }
.task-item { display: flex; align-items: center; justify-content: space-between }
.task-label { font-size: 14px }
.task-reward { font-size: 12px; color: #E91E63 }
.wiki-list { display: flex; flex-direction: column; gap: 8px }
.wiki-item { display: flex; align-items: center; gap: 8px; padding: 8px; cursor: pointer; border-radius: 6px; transition: background 0.2s }
.wiki-item:hover { background: #faf8fb }
.wiki-icon { font-size: 18px }
.wiki-title { font-size: 14px; color: #333 }
</style>
