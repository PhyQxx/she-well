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
import { ref } from 'vue'

const keyword = ref('')

const banners = ref([
  { id: 1, title: '🌸 经期护理全攻略', sub: '科学应对经期不适', bg: 'linear-gradient(135deg, #E91E63, #F48FB1)', link: '/knowledge/1' },
  { id: 2, title: '🤰 备孕指南', sub: '提高受孕率的科学方法', bg: 'linear-gradient(135deg, #9C27B0, #CE93D8)', link: '/knowledge/2' },
  { id: 3, title: '💪 21天打卡挑战', sub: '坚持打卡赢好礼', bg: 'linear-gradient(135deg, #4CAF50, #A5D6A7)', link: '/activity/1' },
])

const activities = ref([
  { id: 1, name: '21天打卡挑战', date: '4.1-4.21', participants: 1234, bg: 'linear-gradient(135deg, #E91E63, #F48FB1)' },
  { id: 2, name: '女性健康知识赛', date: '5.1-5.7', participants: 0, bg: 'linear-gradient(135deg, #9C27B0, #CE93D8)' },
  { id: 3, name: '专家问答活动', date: '长期', participants: 567, bg: 'linear-gradient(135deg, #2196F3, #90CAF9)' },
])

const articles = ref([
  { id: 1, title: '经期疼痛缓解的10个科学方法', icon: '🩸', category: '经期护理', views: 2341, likes: 186, coverBg: 'linear-gradient(135deg, #E91E63, #F48FB1)' },
  { id: 2, title: '排卵试纸使用全攻略', icon: '🧪', category: '备孕指南', views: 1892, likes: 142, coverBg: 'linear-gradient(135deg, #9C27B0, #CE93D8)' },
  { id: 3, title: '孕早期营养补充指南', icon: '🍎', category: '孕期保健', views: 3210, likes: 256, coverBg: 'linear-gradient(135deg, #4CAF50, #A5D6A7)' },
  { id: 4, title: '产后恢复的7个关键点', icon: '💐', category: '产后恢复', views: 1567, likes: 198, coverBg: 'linear-gradient(135deg, #2196F3, #90CAF9)' },
])

const modes = [
  { key: 'period', label: '经期模式', icon: '📅' },
  { key: 'trying', label: '备孕模式', icon: '🤰' },
  { key: 'pregnancy', label: '怀孕模式', icon: '🤱' },
  { key: 'nursing', label: '妈妈模式', icon: '🍼' },
]
const currentMode = ref('period')

const todayTasks = ref([
  { id: 1, label: '记录基础体温', reward: 5, done: false },
  { id: 2, label: '完成健康打卡', reward: 10, done: false },
  { id: 3, label: '阅读1篇健康知识', reward: 5, done: false },
])

const wikiList = ref([
  { id: 1, title: '基础体温测量方法', icon: '🌡️' },
  { id: 2, title: '排卵试纸怎么看', icon: '🧪' },
  { id: 3, title: '孕早期注意事项', icon: '🤰' },
  { id: 4, title: '产后第一天护理', icon: '💐' },
  { id: 5, title: '婴儿抚触手法', icon: '👶' },
])

function search() {}
function bannerClick(b) {}
function activityClick(a) {}
function viewArticle(a) {}
function switchMode(key) { currentMode.value = key }
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
