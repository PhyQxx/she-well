<template>
  <div class="home-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">早上好，{{ auth.nickname || '用户' }} 👋</h2>
        <p class="welcome-sub">今天也要好好关注自己的健康哦~</p>
      </div>
      <div class="welcome-date">{{ today }}</div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：核心数据 -->
      <el-col :span="16">
        <!-- 今日概览 -->
        <div class="card overview-card">
          <div class="card-header">
            <span class="card-title">📅 今日概览</span>
            <el-tag type="primary" size="small">{{ currentMode }}</el-tag>
          </div>
          <div class="overview-grid">
            <div class="stat-card period">
              <div class="stat-icon">🩸</div>
              <div class="stat-content">
                <div class="stat-num">{{ stats.cycleDay || '--' }}</div>
                <div class="stat-label">周期第几天</div>
              </div>
            </div>
            <div class="stat-card pregnant">
              <div class="stat-icon">📆</div>
              <div class="stat-content">
                <div class="stat-num">{{ stats.nextPeriod || '--' }}</div>
                <div class="stat-label">距下次经期</div>
              </div>
            </div>
            <div class="stat-card trying">
              <div class="stat-icon">🎯</div>
              <div class="stat-content">
                <div class="stat-num">{{ stats.ovulation || '--' }}</div>
                <div class="stat-label">距排卵日</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 近期经期记录 -->
        <div class="card records-card">
          <div class="card-header">
            <span class="card-title">📊 近期经期</span>
            <el-button type="primary" link @click="$router.push('/period')">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div v-if="periodRecords.length === 0" class="empty-state">
            <div class="empty-icon">📅</div>
            <div class="empty-text">暂无经期记录，开始记录吧~</div>
          </div>
          <el-table v-else :data="periodRecords" stripe class="records-table">
            <el-table-column prop="startDate" label="开始日期" />
            <el-table-column prop="endDate" label="结束日期" />
            <el-table-column prop="duration" label="持续天数" />
            <el-table-column prop="flowLevel" label="流量等级">
              <template #default="{ row }">
                <el-tag size="small" :type="flowTagType(row.flowLevel)">
                  {{ flowLabels[row.flowLevel] || '-' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：习惯 & 文章 -->
      <el-col :span="8">
        <!-- 打卡习惯 -->
        <div class="card habits-card">
          <div class="card-header">
            <span class="card-title">🎯 打卡习惯</span>
            <el-button type="primary" link @click="$router.push('/checkin')">
              全部
            </el-button>
          </div>
          <div class="habits-list">
            <div
              v-for="h in habits"
              :key="h.id"
              class="habit-item"
            >
              <div class="habit-info">
                <span class="habit-icon">{{ h.icon || '✨' }}</span>
                <span class="habit-name">{{ h.name }}</span>
              </div>
              <el-tag size="small" type="success" effect="plain">
                已打卡
              </el-tag>
            </div>
          </div>
          <el-empty
            v-if="habits.length === 0"
            description="暂无习惯"
            :image-size="60"
          />
          <el-button
            class="add-habit-btn"
            type="primary"
            plain
            @click="showAddHabit"
          >
            <el-icon><Plus /></el-icon> 添加习惯
          </el-button>
        </div>

        <!-- 健康知识 -->
        <div class="card articles-card">
          <div class="card-header">
            <span class="card-title">📰 健康知识</span>
            <el-button type="primary" link @click="$router.push('/knowledge')">
              更多
            </el-button>
          </div>
          <div class="articles-list">
            <div
              v-for="a in articles"
              :key="a.id"
              class="article-item"
              @click="$router.push('/knowledge')"
            >
              <div class="article-info">
                <div class="article-title">{{ a.title }}</div>
                <div class="article-meta">
                  <span>👁️ {{ a.readCount || 0 }}</span>
                  <span>❤️ {{ a.likeCount || 0 }}</span>
                </div>
              </div>
              <el-image
                v-if="a.coverImage"
                :src="a.coverImage"
                class="article-cover"
                fit="cover"
              />
            </div>
          </div>
          <el-empty
            v-if="articles.length === 0"
            description="暂无推荐文章"
            :image-size="60"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <div class="quick-actions-header">
        <span class="card-title">⚡ 快捷操作</span>
      </div>
      <div class="quick-actions-grid">
        <div
          v-for="action in quickActions"
          :key="action.key"
          class="quick-action-item"
          @click="handleAction(action)"
        >
          <div class="action-icon">{{ action.icon }}</div>
          <div class="action-label">{{ action.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ArrowRight, Plus } from '@element-plus/icons-vue'
import { getPeriodRecords, getHabits, getRecommendArticles } from '@/api'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()

const stats = ref({ cycleDay: 0, nextPeriod: 0, ovulation: 0 })
const periodRecords = ref<any[]>([])
const habits = ref<any[]>([])
const articles = ref<any[]>([])
const loading = ref(false)

const flowLabels = ['', '极少', '少', '中', '多']
const flowTagType = (level: number) => {
  const types = ['', 'info', 'success', 'warning', 'danger']
  return types[level] || 'info'
}

const today = dayjs().format('YYYY年MM月DD日 ddd')
const currentMode = computed(() => {
  // 可从用户设置获取当前模式
  return '经期管理'
})

const quickActions = [
  { key: 'period', icon: '🩸', label: '记录经期', path: '/period' },
  { key: 'bbt', icon: '🌡️', label: '记录体温', path: '/checkin?type=bbt' },
  { key: 'strip', icon: '🧪', label: '排卵试纸', path: '/checkin?type=strip' },
  { key: 'intercourse', icon: '💕', label: '同房记录', path: '/checkin?type=intercourse' },
  { key: 'ai', icon: '🤖', label: 'AI健康助手', path: '/ai' },
  { key: 'report', icon: '📊', label: '健康报告', path: '/health' },
]

async function loadData() {
  loading.value = true
  try {
    const [records, habitRes, articleRes] = await Promise.all([
      getPeriodRecords(),
      getHabits(),
      getRecommendArticles(5),
    ])
    periodRecords.value = (records?.data || records || []).slice(0, 5)
    habits.value = (habitRes?.data || habitRes || []).slice(0, 3)
    articles.value = articleRes?.data || articleRes || []
  } catch (e) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

function handleAction(action: any) {
  router.push(action.path)
}

function showAddHabit() {
  // TODO: 打开添加习惯弹窗
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.home-page {
  @include page-container;
  max-width: 1400px;
  margin: 0 auto;
}

// --------------------------------------------
// 欢迎横幅
// --------------------------------------------
.welcome-banner {
  @include gradient-card($she-primary, $she-primary-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $she-space-6;
  color: #fff;

  .welcome-title {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    margin-bottom: $she-space-1;
  }

  .welcome-sub {
    font-size: $she-font-size-md;
    opacity: 0.9;
  }

  .welcome-date {
    font-size: $she-font-size-lg;
    opacity: 0.85;
    text-align: right;
  }
}

// --------------------------------------------
// 概览卡片
// --------------------------------------------
.overview-card {
  margin-bottom: $she-space-6;

  .overview-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $she-space-4;
    margin-top: $she-space-4;
  }

  .stat-card {
    display: flex;
    align-items: center;
    gap: $she-space-4;
    padding: $she-space-5;
    border-radius: $she-radius-xl;
    color: #fff;

    .stat-icon {
      font-size: 36px;
    }

    .stat-num {
      font-size: $she-font-size-2xl;
      font-weight: $she-font-weight-bold;
    }

    .stat-label {
      font-size: $she-font-size-sm;
      opacity: 0.9;
    }

    &.period {
      background: linear-gradient(135deg, $she-period, lighten($she-period, 10%));
    }
    &.pregnant {
      background: linear-gradient(135deg, $she-pregnant, lighten($she-pregnant, 10%));
    }
    &.trying {
      background: linear-gradient(135deg, $she-trying, lighten($she-trying, 10%));
    }
  }
}

// --------------------------------------------
// 记录卡片
// --------------------------------------------
.records-card {
  .records-table {
    margin-top: $she-space-4;
    border-radius: $she-radius-lg;
    overflow: hidden;
  }
}

// --------------------------------------------
// 习惯卡片
// --------------------------------------------
.habits-card {
  margin-bottom: $she-space-6;

  .habits-list {
    margin-top: $she-space-4;
  }

  .habit-item {
    @include flex-between;
    padding: $she-space-3 0;
    border-bottom: 1px solid $she-border;

    &:last-of-type {
      border-bottom: none;
    }
  }

  .habit-info {
    display: flex;
    align-items: center;
    gap: $she-space-3;

    .habit-icon {
      font-size: $she-font-size-xl;
    }

    .habit-name {
      font-size: $she-font-size-md;
      color: $she-text;
    }
  }

  .add-habit-btn {
    width: 100%;
    margin-top: $she-space-4;
    border-style: dashed;
  }
}

// --------------------------------------------
// 文章卡片
// --------------------------------------------
.articles-card {
  .articles-list {
    margin-top: $she-space-4;
  }

  .article-item {
    @include flex-between;
    padding: $she-space-3 0;
    border-bottom: 1px solid $she-border;
    cursor: pointer;
    transition: all $she-transition-fast;

    &:last-of-type {
      border-bottom: none;
    }

    &:hover {
      padding-left: $she-space-2;
    }
  }

  .article-title {
    @include text-ellipsis(2);
    font-size: $she-font-size-sm;
    color: $she-text;
    margin-bottom: $she-space-1;
    max-width: 180px;
  }

  .article-meta {
    display: flex;
    gap: $she-space-3;
    font-size: $she-font-size-xs;
    color: $she-muted;
  }

  .article-cover {
    width: 60px;
    height: 60px;
    border-radius: $she-radius-md;
    margin-left: $she-space-3;
  }
}

// --------------------------------------------
// 快捷操作
// --------------------------------------------
.quick-actions {
  @include soft-card;
  margin-top: $she-space-6;

  .quick-actions-header {
    margin-bottom: $she-space-4;
  }

  .quick-actions-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: $she-space-4;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(3, 1fr);
    }
  }

  .quick-action-item {
    @include flex-center;
    flex-direction: column;
    padding: $she-space-5;
    background: $she-bg;
    border-radius: $she-radius-xl;
    cursor: pointer;
    transition: all $she-transition-normal;

    &:hover {
      background: $she-primary-bg;
      transform: translateY(-2px);

      .action-icon {
        transform: scale(1.1);
      }
    }

    .action-icon {
      font-size: 36px;
      margin-bottom: $she-space-2;
      transition: transform $she-transition-normal;
    }

    .action-label {
      font-size: $she-font-size-sm;
      color: $she-sub;
      font-weight: $she-font-weight-medium;
    }
  }
}

// --------------------------------------------
// 响应式
// --------------------------------------------
@media (max-width: 1200px) {
  .el-col-16 {
    width: 100% !important;
  }
  .el-col-8 {
    width: 100% !important;
  }

  .overview-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }

  .quick-actions-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }
}
</style>
