<template>
  <div class="achievement-page">
    <div class="page-header">
      <h2>🏆 成就系统</h2>
      <div class="achievement-summary">
        <span class="summary-num">{{ unlockedCount }}</span>
        <span class="summary-label"> / {{ totalCount }} 已解锁</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="6">
        <div class="stat-card pink">
          <div class="stat-num">{{ unlockedCount }}</div>
          <div class="stat-label">已解锁成就</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-num">{{ totalPoints }}</div>
          <div class="stat-label">成就积分</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-num">{{ categoryStats.length }}</div>
          <div class="stat-label">成就类别</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-num">{{ totalCount }}</div>
          <div class="stat-label">全部成就</div>
        </div>
      </el-col>
    </el-row>

    <!-- 分类筛选 -->
    <el-card shadow="hover" style="margin-bottom:16px">
      <template #header>
        <span style="font-weight:600">🎯 成就分类</span>
      </template>
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane v-for="cat in categories" :key="cat" :label="cat" :name="cat" />
      </el-tabs>
    </el-card>

    <!-- 已解锁成就 -->
    <el-card shadow="hover" style="margin-bottom:16px">
      <template #header>
        <span style="font-weight:600">✨ 已解锁 ({{ unlockedCount }})</span>
      </template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="a in filteredUnlocked" :key="a.id">
          <div class="achievement-badge unlocked">
            <div class="badge-icon">{{ a.icon }}</div>
            <div class="badge-name">{{ a.name }}</div>
            <div class="badge-desc">{{ a.description }}</div>
            <div class="badge-reward">+{{ a.rewardPoints }}积分</div>
            <el-tag v-if="a.category" size="small" type="success" style="margin-top:4px">{{ a.category }}</el-tag>
          </div>
        </el-col>
      </el-row>
      <el-empty v-if="!filteredUnlocked.length" description="暂无已解锁成就" />
    </el-card>

    <!-- 锁定成就 -->
    <el-card shadow="hover">
      <template #header>
        <span style="font-weight:600">🔒 进行中 ({{ lockedCount }})</span>
      </template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="a in filteredLocked" :key="a.id">
          <div class="achievement-badge locked">
            <div class="badge-icon">{{ a.icon || '🔒' }}</div>
            <div class="badge-name">{{ a.name }}</div>
            <div class="badge-desc">{{ a.description }}</div>
            <el-progress
              :percentage="getProgress(a)"
              :stroke-width="8"
              :show-text="true"
              style="margin-top:8px"
            />
            <div class="badge-progress-text">{{ getProgressText(a) }}</div>
            <div class="badge-reward">+{{ a.rewardPoints }}积分</div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-if="!filteredLocked.length" description="暂无进行中成就" />
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
    achievements.value = (allRes?.data || []).map(a => ({
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
    const earnedIds = new Set((myRes?.data || []).map(ua => ua.achievementId))
    achievements.value.forEach(a => {
      const ua = (myRes?.data || []).find(u => u.achievementId === a.id)
      a.earned = earnedIds.has(a.id)
      a.earnedTime = ua?.earnedTime || null
    })
  } catch (err) {
    ElMessage.error(err.message || '加载成就数据失败')
  } finally {
    loading.value = false
  }
}

const categories = computed(() => {
  return [...new Set(achievements.value.map(a => a.category).filter(Boolean))]
})

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
const categoryStats = computed(() => categories.value)

function getProgress(a) {
  if (!a.target || a.target === 0) return 0
  return Math.min(100, Math.round((a.progress / a.target) * 100))
}

function getProgressText(a) {
  return `${a.progress || 0} / ${a.target || 100}`
}

function handleCategoryChange() {}
</script>

<style scoped>
.achievement-page { padding: 24px }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px }
.page-header h2 { margin: 0; font-size: 24px }
.achievement-summary { font-size: 16px }
.summary-num { font-size: 28px; font-weight: bold; color: #E91E63 }
.summary-label { color: #999 }
.stat-card { border-radius: 12px; padding: 20px; color: white; text-align: center }
.stat-card.pink { background: linear-gradient(135deg, #E91E63, #F48FB1) }
.stat-card.purple { background: linear-gradient(135deg, #9C27B0, #CE93D8) }
.stat-card.blue { background: linear-gradient(135deg, #2196F3, #90CAF9) }
.stat-card.green { background: linear-gradient(135deg, #4CAF50, #A5D6A7) }
.stat-num { font-size: 36px; font-weight: bold }
.stat-label { font-size: 14px; opacity: 0.9 }
.achievement-badge {
  border-radius: 12px; padding: 20px 16px; text-align: center;
  border: 2px solid transparent; transition: all 0.2s;
}
.achievement-badge.unlocked { background: linear-gradient(135deg, #fff5f7, #fff); border-color: #fce4ec }
.achievement-badge.locked { background: #faf8fb; opacity: 0.7 }
.badge-icon { font-size: 48px; margin-bottom: 8px }
.badge-name { font-size: 15px; font-weight: 600; color: #333; margin-bottom: 4px }
.badge-desc { font-size: 12px; color: #999; margin-bottom: 4px }
.badge-reward { font-size: 11px; color: #E91E63; margin-top: 4px }
.badge-progress-text { font-size: 11px; color: #999; margin-top: 2px }
</style>
