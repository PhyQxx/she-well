<template>
  <div class="discovery-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <span class="title-icon">🔍</span>
          发现
        </h1>
        <p class="page-desc">探索健康内容，开启美好生活</p>
      </div>
      <div class="header-search">
        <el-input 
          v-model="keyword" 
          placeholder="搜索感兴趣的内容..." 
          size="large"
          class="search-input"
          clearable
          @keyup.enter="search"
        >
          <template #prefix>
            <span class="search-icon">🔍</span>
          </template>
        </el-input>
      </div>
    </div>

    <el-row :gutter="24" class="main-content">
      <!-- 左侧内容区 -->
      <el-col :span="16">
        <!-- Banner 轮播 -->
        <el-card class="banner-card" shadow="hover" body-style="padding: 0;">
          <el-carousel trigger="click" height="260px" interval="5000" indicator-position="outside">
            <el-carousel-item v-for="b in banners" :key="b.id">
              <div class="banner-item" :style="{ background: b.bg }" @click="bannerClick(b)">
                <div class="banner-content">
                  <div class="banner-badge">{{ b.badge || '热门' }}</div>
                  <h3 class="banner-title">{{ b.title }}</h3>
                  <p class="banner-sub">{{ b.sub }}</p>
                  <div class="banner-cta">点击查看 →</div>
                </div>
                <div class="banner-decoration">
                  <div class="deco-ring"></div>
                  <div class="deco-dot"></div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-card>

        <!-- 热门活动 -->
        <el-card class="activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="title-icon">🎁</span>
                <span>热门活动</span>
              </div>
              <el-link type="primary" :underline="false" @click="$router.push('/activity')">
                查看全部 →
              </el-link>
            </div>
          </template>
          <el-row :gutter="16">
            <el-col :span="8" v-for="a in activities" :key="a.id">
              <div class="activity-item" :style="{ background: a.bg }" @click="activityClick(a)">
                <div class="activity-top">
                  <span class="activity-date">{{ a.date }}</span>
                  <span class="activity-participants">{{ a.participants }}人参与</span>
                </div>
                <div class="activity-name">{{ a.name }}</div>
                <div class="activity-cta">立即参加</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 为你推荐 -->
        <el-card class="article-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="title-icon">📖</span>
                <span>为你推荐</span>
              </div>
              <el-link type="primary" :underline="false">
                更多内容 →
              </el-link>
            </div>
          </template>
          <div class="article-list">
            <div 
              v-for="a in articles" 
              :key="a.id" 
              class="article-item"
              @click="viewArticle(a)"
            >
              <div class="article-cover" :style="{ background: a.coverBg }">
                <span class="article-icon">{{ a.icon }}</span>
              </div>
              <div class="article-body">
                <h4 class="article-title">{{ a.title }}</h4>
                <div class="article-meta">
                  <el-tag size="small" type="danger" plain>{{ a.category }}</el-tag>
                  <span class="meta-item">
                    <span class="meta-icon">👁</span>
                    {{ a.views }}
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">❤️</span>
                    {{ a.likes }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :span="8">
        <!-- 模式切换 -->
        <el-card class="mode-card" shadow="hover">
          <template #header>
            <div class="card-header no-border">
              <div class="header-title">
                <span class="title-icon">⚡</span>
                <span>当前模式</span>
              </div>
            </div>
          </template>
          <div class="mode-list">
            <div
              v-for="m in modes" 
              :key="m.key"
              class="mode-item"
              :class="{ active: currentMode === m.key }"
              @click="switchMode(m.key)"
            >
              <div class="mode-icon-wrap">
                <span class="mode-icon">{{ m.icon }}</span>
              </div>
              <div class="mode-info">
                <span class="mode-label">{{ m.label }}</span>
                <span class="mode-desc">{{ m.desc }}</span>
              </div>
              <div class="mode-check" v-if="currentMode === m.key">✓</div>
            </div>
          </div>
        </el-card>

        <!-- 今日任务 -->
        <el-card class="task-card" shadow="hover">
          <template #header>
            <div class="card-header no-border">
              <div class="header-title">
                <span class="title-icon">📋</span>
                <span>今日任务</span>
              </div>
              <el-tag type="warning" size="small">进行中</el-tag>
            </div>
          </template>
          <div class="task-list">
            <div v-for="task in todayTasks" :key="task.id" class="task-item">
              <el-checkbox v-model="task.done" :label="task.label" />
              <span class="task-reward">+{{ task.reward }}</span>
            </div>
          </div>
          <div class="task-footer">
            <span class="task-progress">已完成 {{ completedTaskCount }}/{{ todayTasks.length }}</span>
            <el-progress 
              :percentage="taskProgress" 
              :stroke-width="6" 
              :show-text="false"
              color="#E91E63"
            />
          </div>
        </el-card>

        <!-- 孕育百科 -->
        <el-card class="wiki-card" shadow="hover">
          <template #header>
            <div class="card-header no-border">
              <div class="header-title">
                <span class="title-icon">📚</span>
                <span>孕育百科</span>
              </div>
              <el-link type="primary" :underline="false" size="small">更多</el-link>
            </div>
          </template>
          <div class="wiki-list">
            <div 
              v-for="w in wikiList" 
              :key="w.id" 
              class="wiki-item"
              @click="viewArticle(w)"
            >
              <div class="wiki-icon">{{ w.icon }}</div>
              <div class="wiki-info">
                <span class="wiki-title">{{ w.title }}</span>
                <span class="wiki-category">{{ w.category }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
      badge: b.badge || '热门',
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
  { key: 'period', label: '经期模式', icon: '📅', desc: '记录经期，了解周期' },
  { key: 'trying', label: '备孕模式', icon: '🤰', desc: '科学备孕，提高成功率' },
  { key: 'pregnancy', label: '怀孕模式', icon: '🤱', desc: '孕期记录，贴心陪伴' },
  { key: 'nursing', label: '妈妈模式', icon: '🍼', desc: '产后恢复，宝宝成长' },
]
const currentMode = ref('period')

const todayTasks = ref([
  { id: 1, label: '记录基础体温', done: false, reward: 5 },
  { id: 2, label: '记录饮食情况', done: false, reward: 3 },
  { id: 3, label: '完成今日运动', done: false, reward: 5 },
  { id: 4, label: '阅读健康资讯', done: false, reward: 2 },
])

const wikiList = ref([
  { id: 1, icon: '🌡', title: '基础体温测量法', category: '备孕知识' },
  { id: 2, icon: '💊', title: '叶酸补充指南', category: '孕期营养' },
  { id: 3, icon: '🏃', title: '孕期运动建议', category: '孕期保健' },
  { id: 4, icon: '🍼', title: '辅食添加时间表', category: '宝宝喂养' },
])

const completedTaskCount = computed(() => todayTasks.value.filter(t => t.done).length)
const taskProgress = computed(() => (completedTaskCount.value / todayTasks.value.length) * 100)

function search() {
  if (!keyword.value) return
  ElMessage.info(`搜索：${keyword.value}`)
}

function bannerClick(b) { 
  if (b.link) window.location.hash = b.link 
}

function activityClick(a) { 
  ElMessage.info(`活动：${a.name}`) 
}

function viewArticle(a) { 
  if (a.id) window.location.hash = `/knowledge/${a.id}` 
}

async function switchMode(key) {
  currentMode.value = key
  try {
    const apiModule = (await import('@/api')).default
    await apiModule.put('/settings/mode', { mode: key })
  } catch {}
}

onMounted(loadData)
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.discovery-page {
  padding: $she-space-6;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $she-space-6;
  padding: $she-space-4 $she-space-6;
  background: linear-gradient(135deg, #FCE4EC 0%, #F8BBD9 100%);
  border-radius: $she-radius-xl;
}

.page-title {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  font-size: $she-font-size-2xl;
  font-weight: $she-font-weight-bold;
  color: $she-title;
  margin: 0;
  
  .title-icon {
    font-size: 28px;
  }
}

.page-desc {
  color: $she-sub;
  font-size: $she-font-size-sm;
  margin-top: $she-space-1;
}

.search-input {
  width: 320px;
  
  :deep(.el-input__wrapper) {
    border-radius: $she-radius-full;
    padding-left: $she-space-4;
  }
}

.search-icon {
  font-size: 16px;
}

.main-content {
  min-height: 500px;
}

// Banner Card
.banner-card {
  border-radius: $she-radius-xl;
  overflow: hidden;
  margin-bottom: $she-space-6;
  border: none;
}

.banner-item {
  height: 260px;
  display: flex;
  align-items: center;
  padding: $she-space-10;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.banner-content {
  position: relative;
  z-index: 1;
  max-width: 60%;
}

.banner-badge {
  display: inline-block;
  padding: $she-space-1 $she-space-3;
  background: rgba(255,255,255,0.2);
  border-radius: $she-radius-full;
  font-size: $she-font-size-xs;
  color: white;
  margin-bottom: $she-space-3;
}

.banner-title {
  font-size: 32px;
  font-weight: $she-font-weight-bold;
  color: white;
  margin-bottom: $she-space-2;
}

.banner-sub {
  font-size: $she-font-size-lg;
  color: rgba(255,255,255,0.9);
  margin-bottom: $she-space-4;
}

.banner-cta {
  color: white;
  font-size: $she-font-size-sm;
  opacity: 0.8;
}

.banner-decoration {
  position: absolute;
  right: $she-space-10;
  top: 50%;
  transform: translateY(-50%);
}

.deco-ring {
  width: 200px;
  height: 200px;
  border: 40px solid rgba(255,255,255,0.1);
  border-radius: 50%;
}

.deco-dot {
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.15);
  border-radius: 50%;
  position: absolute;
  top: 60px;
  right: -40px;
}

// Activity Card
.activity-card {
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-6;
  border: none;
  
  :deep(.el-card__header) {
    padding-bottom: $she-space-4;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  &.no-border {
    padding-bottom: 0;
  }
}

.header-title {
  display: flex;
  align-items: center;
  gap: $she-space-2;
  font-size: $she-font-size-lg;
  font-weight: $she-font-weight-semibold;
  
  .title-icon {
    font-size: 20px;
  }
}

.activity-item {
  border-radius: $she-radius-lg;
  padding: $she-space-5;
  color: white;
  cursor: pointer;
  transition: transform $she-transition-normal, box-shadow $she-transition-normal;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: $she-shadow-lg;
  }
}

.activity-top {
  display: flex;
  justify-content: space-between;
  font-size: $she-font-size-xs;
  opacity: 0.85;
}

.activity-name {
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-semibold;
}

.activity-cta {
  font-size: $she-font-size-xs;
  opacity: 0.75;
}

// Article Card
.article-card {
  border-radius: $she-radius-xl;
  border: none;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
}

.article-item {
  display: flex;
  gap: $she-space-4;
  padding: $she-space-3;
  border-radius: $she-radius-lg;
  cursor: pointer;
  transition: background $she-transition-fast;
  
  &:hover {
    background: rgba($she-primary, 0.04);
  }
}

.article-cover {
  width: 100px;
  height: 80px;
  border-radius: $she-radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.article-icon {
  font-size: 40px;
}

.article-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: $she-space-2;
}

.article-title {
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-medium;
  color: $she-title;
  margin: 0;
  line-height: $she-line-height-tight;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: $she-font-size-xs;
    color: $she-muted;
  }
  
  .meta-icon {
    font-size: 12px;
  }
}

// Mode Card
.mode-card {
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-4;
  border: none;
}

.mode-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-2;
}

.mode-item {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  padding: $she-space-3;
  border-radius: $she-radius-lg;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all $she-transition-fast;
  
  &:hover {
    background: rgba($she-primary, 0.04);
  }
  
  &.active {
    border-color: $she-primary;
    background: rgba($she-primary, 0.06);
  }
}

.mode-icon-wrap {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, $she-primary-bg, #fff);
  border-radius: $she-radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mode-icon {
  font-size: 22px;
}

.mode-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.mode-label {
  font-size: $she-font-size-sm;
  font-weight: $she-font-weight-medium;
  color: $she-title;
}

.mode-desc {
  font-size: $she-font-size-xs;
  color: $she-muted;
}

.mode-check {
  width: 22px;
  height: 22px;
  background: $she-primary;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

// Task Card
.task-card {
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-4;
  border: none;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-3;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-reward {
  font-size: $she-font-size-xs;
  color: $she-primary;
  font-weight: $she-font-weight-medium;
}

.task-footer {
  margin-top: $she-space-4;
  padding-top: $she-space-3;
  border-top: 1px solid $she-border;
}

.task-progress {
  font-size: $she-font-size-xs;
  color: $she-muted;
  display: block;
  margin-bottom: $she-space-2;
}

// Wiki Card
.wiki-card {
  border-radius: $she-radius-xl;
  border: none;
}

.wiki-list {
  display: flex;
  flex-direction: column;
  gap: $she-space-2;
}

.wiki-item {
  display: flex;
  align-items: center;
  gap: $she-space-3;
  padding: $she-space-3;
  border-radius: $she-radius-lg;
  cursor: pointer;
  transition: background $she-transition-fast;
  
  &:hover {
    background: rgba($she-primary, 0.04);
  }
}

.wiki-icon {
  font-size: 24px;
}

.wiki-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.wiki-title {
  font-size: $she-font-size-sm;
  color: $she-title;
}

.wiki-category {
  font-size: $she-font-size-xs;
  color: $she-muted;
}
</style>
