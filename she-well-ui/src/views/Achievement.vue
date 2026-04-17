<template>
  <div class="achievement-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">🏆</span>
          成就系统
        </h1>
        <p class="page-desc">完成健康任务，解锁专属成就</p>
      </div>
      <div class="achievement-summary">
        <div class="summary-badge">
          <span class="badge-num">{{ unlockedCount }}</span>
          <span class="badge-sep">/</span>
          <span class="badge-total">{{ totalCount }}</span>
        </div>
        <div class="summary-label">已解锁成就</div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <div class="stat-card pink">
          <div class="stat-icon">✨</div>
          <div class="stat-body">
            <div class="stat-num">{{ unlockedCount }}</div>
            <div class="stat-label">已解锁</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-icon">⭐</div>
          <div class="stat-body">
            <div class="stat-num">{{ totalPoints }}</div>
            <div class="stat-label">成就积分</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-icon">📂</div>
          <div class="stat-body">
            <div class="stat-num">{{ categoryCount }}</div>
            <div class="stat-label">成就类别</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-icon">🎯</div>
          <div class="stat-body">
            <div class="stat-num">{{ totalCount }}</div>
            <div class="stat-label">全部成就</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 分类筛选 -->
    <el-card class="filter-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">🎯</span>
            <span>成就分类</span>
          </div>
        </div>
      </template>
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane v-for="cat in categories" :key="cat" :label="cat" :name="cat" />
      </el-tabs>
    </el-card>

    <!-- 已解锁成就 -->
    <el-card class="list-card unlocked-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">✨</span>
            <span>已解锁</span>
            <el-tag type="success" size="small">{{ unlockedCount }}</el-tag>
          </div>
        </div>
      </template>
      
      <div class="achievement-grid" v-if="filteredUnlocked.length">
        <div 
          v-for="a in filteredUnlocked" 
          :key="a.id" 
          class="achievement-badge unlocked"
        >
          <div class="badge-top">
            <div class="badge-icon-wrap">
              <span class="badge-icon">{{ a.icon }}</span>
            </div>
            <div class="badge-glow"></div>
          </div>
          <div class="badge-info">
            <h4 class="badge-name">{{ a.name }}</h4>
            <p class="badge-desc">{{ a.description }}</p>
            <div class="badge-footer">
              <el-tag size="small" type="success" plain>{{ a.category }}</el-tag>
              <span class="badge-reward">+{{ a.rewardPoints }}积分</span>
            </div>
          </div>
          <div class="badge-earned-time" v-if="a.earnedTime">
            {{ formatTime(a.earnedTime) }}
          </div>
        </div>
      </div>
      
      <el-empty v-else description="暂无已解锁成就，继续努力！" />
    </el-card>

    <!-- 锁定成就 -->
    <el-card class="list-card locked-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">🔒</span>
            <span>进行中</span>
            <el-tag type="warning" size="small">{{ lockedCount }}</el-tag>
          </div>
        </div>
      </template>
      
      <div class="achievement-grid" v-if="filteredLocked.length">
        <div 
          v-for="a in filteredLocked" 
          :key="a.id" 
          class="achievement-badge locked"
        >
          <div class="badge-top">
            <div class="badge-icon-wrap locked">
              <span class="badge-icon">{{ a.icon || '🔒' }}</span>
            </div>
          </div>
          <div class="badge-info">
            <h4 class="badge-name">{{ a.name }}</h4>
            <p class="badge-desc">{{ a.description }}</p>
            <div class="progress-section">
              <el-progress
                :percentage="getProgress(a)"
                :stroke-width="8"
                :show-text="false"
                color="#E91E63"
              />
              <span class="progress-text">{{ getProgressText(a) }}</span>
            </div>
            <div class="badge-footer">
              <el-tag size="small" type="info" plain>{{ a.category }}</el-tag>
              <span class="badge-reward">+{{ a.rewardPoints }}积分</span>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="暂无进行中成就" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAchievementList, getMyAchievements } from '@/api'

const achievements = ref([])
const myAchievements = ref([])
const activeCategory = ref('all')
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const [allRes, myRes] = await Promise.all([
      getAchievementList(),
      getMyAchievements(),
    ])
    achievements.value = ((allRes?.data || [])).map(a => ({
      id: a.id,
      code: a.code,
      name: a.name,
      description: a.description || '',
      icon: a.icon || '🏆',
      category: a.category || '其他',
      conditionType: a.conditionType,
      conditionValue: a.conditionValue,
      rewardPoints: a.rewardPoints || 10,
      displayOrder: a.displayOrder || 0,
      progress: a.currentProgress || 0,
      target: a.targetValue || 100,
    }))
    const myData = myRes?.data || []
    const earnedIds = new Set(myData.map(ua => ua.achievementId))
    achievements.value.forEach(a => {
      const ua = myData.find(u => u.achievementId === a.id)
      a.earned = earnedIds.has(a.id)
      a.earnedTime = ua?.earnedTime || null
    })
  } catch (err) {
    ElMessage.error('加载成就数据失败')
  } finally {
    loading.value = false
  }
}

const categories = computed(() => {
  return [...new Set(achievements.value.map(a => a.category).filter(Boolean))]
})

const categoryCount = computed(() => categories.value.length)

const unlockedList = computed(() => achievements.value.filter(a => a.earned))
const lockedList = computed(() => achievements.value.filter(a => !a.earned))

const filteredUnlocked = computed(() => {
  if (activeCategory.value === 'all') return unlockedList.value
  return unlockedList.value.filter(a => a.category === activeCategory.value)
})

const filteredLocked = computed(() => {
  if (activeCategory.value === 'all') return lockedList.value
  return lockedList.value.filter(a => a.category === activeCategory.value)
})

const unlockedCount = computed(() => unlockedList.value.length)
const lockedCount = computed(() => lockedList.value.length)
const totalCount = computed(() => achievements.value.length)
const totalPoints = computed(() => unlockedList.value.reduce((sum, a) => sum + (a.rewardPoints || 0), 0))

function getProgress(a) {
  if (!a.target || a.target === 0) return 0
  return Math.min(100, Math.round((a.progress / a.target) * 100))
}

function getProgressText(a) {
  return `${a.progress || 0} / ${a.target || 100}`
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

function handleCategoryChange() {}
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.achievement-page {
  padding: $she-space-6;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $she-space-5 $she-space-6;
  background: linear-gradient(135deg, #FCE4EC 0%, #F8BBD9 100%);
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-6;
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

.achievement-summary {
  text-align: center;
}

.summary-badge {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: $she-space-1;
}

.badge-num {
  font-size: 36px;
  font-weight: $she-font-weight-bold;
  color: $she-primary;
}

.badge-sep {
  font-size: 24px;
  color: $she-muted;
}

.badge-total {
  font-size: 20px;
  color: $she-sub;
}

.summary-label {
  font-size: $she-font-size-sm;
  color: $she-sub;
}

// Stats Row
.stats-row {
  margin-bottom: $she-space-6;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: $she-space-4;
  padding: $she-space-5;
  border-radius: $she-radius-xl;
  color: white;
  
  .stat-icon {
    font-size: 28px;
    width: 56px;
    height: 56px;
    background: rgba(255,255,255,0.2);
    border-radius: $she-radius-lg;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  &.pink {
    background: linear-gradient(135deg, $she-primary, $she-primary-light);
  }
  
  &.purple {
    background: linear-gradient(135deg, #9C27B0, #CE93D8);
  }
  
  &.blue {
    background: linear-gradient(135deg, #2196F3, #90CAF9);
  }
  
  &.green {
    background: linear-gradient(135deg, #4CAF50, #A5D6A7);
  }
}

.stat-body {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: $she-font-size-2xl;
  font-weight: $she-font-weight-bold;
}

.stat-label {
  font-size: $she-font-size-sm;
  opacity: 0.9;
}

// Filter Card
.filter-card {
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-6;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

// List Card
.list-card {
  border-radius: $she-radius-xl;
  margin-bottom: $she-space-6;
  border: none;
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $she-space-4;
}

.achievement-badge {
  border-radius: $she-radius-xl;
  padding: $she-space-5;
  text-align: center;
  position: relative;
  transition: transform $she-transition-normal, box-shadow $she-transition-normal;
  
  &:hover {
    transform: translateY(-4px);
  }
  
  &.unlocked {
    background: linear-gradient(135deg, #FFF5F7, #FFF);
    border: 2px solid rgba($she-primary, 0.2);
    
    &:hover {
      box-shadow: $she-shadow-lg;
    }
  }
  
  &.locked {
    background: var(--el-fill-color-light);
    opacity: 0.8;
    
    &:hover {
      opacity: 1;
    }
  }
}

.badge-top {
  position: relative;
  margin-bottom: $she-space-4;
}

.badge-icon-wrap {
  width: 80px;
  height: 80px;
  margin: 0 auto;
  background: linear-gradient(135deg, $she-primary-bg, #fff);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  
  &.locked {
    background: var(--el-fill-color);
  }
  
  .badge-icon {
    font-size: 40px;
  }
}

.badge-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba($she-primary, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  z-index: 0;
}

.badge-info {
  // empty
}

.badge-name {
  font-size: $she-font-size-md;
  font-weight: $she-font-weight-semibold;
  color: $she-title;
  margin: 0 0 $she-space-2;
}

.badge-desc {
  font-size: $she-font-size-xs;
  color: $she-muted;
  margin-bottom: $she-space-3;
  line-height: 1.4;
  min-height: 36px;
}

.badge-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.badge-reward {
  font-size: $she-font-size-xs;
  color: $she-primary;
  font-weight: $she-font-weight-medium;
}

.progress-section {
  margin-bottom: $she-space-3;
}

.progress-text {
  display: block;
  text-align: center;
  font-size: $she-font-size-xs;
  color: $she-muted;
  margin-top: $she-space-1;
}

.badge-earned-time {
  position: absolute;
  top: $she-space-3;
  right: $she-space-3;
  font-size: $she-font-size-xs;
  color: $she-muted;
}

.unlocked-card {
  :deep(.el-card__header) {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.locked-card {
  :deep(.el-card__header) {
    border-bottom: none;
    padding-bottom: 0;
  }
}
</style>
