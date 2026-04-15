<template>
  <div class="achievement-page">
    <div class="page-header">
      <h2>🏆 成就系统</h2>
      <div class="achievement-summary">
        <span class="summary-num">{{ unlockedCount }}</span>
        <span class="summary-label"> / {{ totalCount }} 已解锁</span>
      </div>
    </div>

    <!-- 已解锁成就 -->
    <el-card shadow="hover" style="margin-bottom:16px">
      <template #header>
        <span style="font-weight:600">✨ 已解锁 ({{ unlockedCount }})</span>
      </template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="a in unlockedAchievements" :key="a.id">
          <div class="achievement-badge unlocked">
            <div class="badge-icon">{{ a.icon }}</div>
            <div class="badge-name">{{ a.name }}</div>
            <div class="badge-desc">{{ a.desc }}</div>
            <div class="badge-date">解锁于 {{ a.unlockedAt }}</div>
            <el-tag size="small" type="success" style="margin-top:4px">已解锁</el-tag>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 锁定成就 -->
    <el-card shadow="hover">
      <template #header>
        <span style="font-weight:600">🔒 进行中 ({{ lockedCount }})</span>
      </template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="a in lockedAchievements" :key="a.id">
          <div class="achievement-badge locked">
            <div class="badge-icon">🔒</div>
            <div class="badge-name">{{ a.name }}</div>
            <div class="badge-desc">{{ a.desc }}</div>
            <el-progress :percentage="a.progress" :stroke-width="8" :show-text="true" style="margin-top:8px" />
            <div class="badge-progress-text">{{ a.progressText }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const unlockedAchievements = ref([
  { id: 1, icon: '🌸', name: '初次记录', desc: '完成首次经期记录', unlockedAt: '2026-01-15' },
  { id: 2, icon: '📅', name: '坚持7天', desc: '连续打卡7天', unlockedAt: '2026-01-22' },
  { id: 3, icon: '🔥', name: '坚持30天', desc: '连续打卡30天', unlockedAt: '2026-02-15' },
  { id: 4, icon: '🌡️', name: '体温达人', desc: '连续记录14天体温', unlockedAt: '2026-02-20' },
  { id: 5, icon: '💪', name: '健康之星', desc: '完成50次健康打卡', unlockedAt: '2026-03-10' },
  { id: 6, icon: '📖', name: '好学之人', desc: '阅读10篇健康知识', unlockedAt: '2026-03-15' },
  { id: 7, icon: '💬', name: '社交达人', desc: '发布5篇帖子', unlockedAt: '2026-03-20' },
  { id: 8, icon: '🤝', name: '互帮互助', desc: '回复10条姐妹问答', unlockedAt: '2026-04-01' },
])

const lockedAchievements = ref([
  { id: 9, icon: '🏆', name: '妇科达人', desc: '完成100次健康打卡', progress: 50, progressText: '50/100次' },
  { id: 10, icon: '⭐', name: '五星好评', desc: '获得50个赞', progress: 40, progressText: '20/50个' },
  { id: 11, icon: '🎯', name: '精准预测', desc: '经期预测连续准确3个月', progress: 33, progressText: '1/3个月' },
  { id: 12, icon: '👶', name: '新手妈妈', desc: '添加宝宝信息', progress: 0, progressText: '0%' },
  { id: 13, icon: '🌈', name: '孕育全程', desc: '完成备孕→怀孕→生产全程', progress: 10, progressText: '10%' },
  { id: 14, icon: '📚', name: '知识渊博', desc: '阅读100篇健康知识', progress: 28, progressText: '28/100篇' },
  { id: 15, icon: '💎', name: '至尊徽章', desc: '解锁全部成就', progress: 53, progressText: '8/15个' },
])

const unlockedCount = computed(() => unlockedAchievements.value.length)
const lockedCount = computed(() => lockedAchievements.value.length)
const totalCount = computed(() => unlockedCount.value + lockedCount.value)
</script>

<style scoped>
.achievement-page { padding: 24px }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px }
.page-header h2 { margin: 0; font-size: 24px }
.achievement-summary { font-size: 16px }
.summary-num { font-size: 28px; font-weight: bold; color: #E91E63 }
.summary-label { color: #999 }
.achievement-badge {
  border-radius: 12px; padding: 20px 16px; text-align: center;
  border: 2px solid transparent; transition: all 0.2s;
}
.achievement-badge.unlocked { background: linear-gradient(135deg, #fff5f7, #fff); border-color: #fce4ec }
.achievement-badge.locked { background: #faf8fb; opacity: 0.7 }
.badge-icon { font-size: 48px; margin-bottom: 8px }
.badge-name { font-size: 15px; font-weight: 600; color: #333; margin-bottom: 4px }
.badge-desc { font-size: 12px; color: #999; margin-bottom: 4px }
.badge-date { font-size: 11px; color: #bbb }
.badge-progress-text { font-size: 11px; color: #999; margin-top: 2px }
</style>
