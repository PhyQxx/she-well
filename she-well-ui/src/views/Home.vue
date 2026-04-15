<template>
  <div class="home">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><span>📅 今日概览</span></template>
          <el-row :gutter="16">
            <el-col :span="8"><div class="stat-card"><div class="stat-num">{{ stats.cycleDay || '--' }}</div><div class="stat-label">周期第几天</div></div></el-col>
            <el-col :span="8"><div class="stat-card"><div class="stat-num">{{ stats.nextPeriod || '--' }}</div><div class="stat-label">距下次经期</div></div></el-col>
            <el-col :span="8"><div class="stat-card"><div class="stat-num">{{ stats.ovulation || '--' }}</div><div class="stat-label">距排卵日</div></div></el-col>
          </el-row>
        </el-card>
        <el-card shadow="hover" style="margin-top: 16px">
          <template #header><span>📊 近期经期</span></template>
          <div v-if="periodRecords.length === 0" style="color: #999; text-align: center; padding: 20px">暂无经期记录</div>
          <el-table v-else :data="periodRecords" stripe>
            <el-table-column prop="startDate" label="开始日期" />
            <el-table-column prop="endDate" label="结束日期" />
            <el-table-column prop="duration" label="持续天数" />
            <el-table-column prop="flowLevel" label="流量等级">
              <template #default="{ row }">{{ ['','极少','少','中','多'][row.flowLevel] }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>🎯 打卡习惯</span></template>
          <div v-for="h in habits" :key="h.id" class="habit-item">
            <span>{{ h.name }}</span>
            <el-tag size="small" type="success">已打卡</el-tag>
          </div>
          <el-empty v-if="habits.length === 0" description="暂无习惯" :image-size="60" />
        </el-card>
        <el-card shadow="hover" style="margin-top: 16px">
          <template #header><span>📰 健康知识</span></template>
          <div v-for="a in articles" :key="a.id" class="article-item" @click="$router.push('/knowledge')">
            <div class="article-title">{{ a.title }}</div>
            <div class="article-meta">{{ a.readCount }} 阅读</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api'

const stats = ref({ cycleDay: 0, nextPeriod: 0, ovulation: 0 })
const periodRecords = ref<any[]>([])
const habits = ref<any[]>([])
const articles = ref<any[]>([])

onMounted(async () => {
  try {
    const [records, habitRes, articleRes] = await Promise.all([
      api.get('/period/record/list'),
      api.get('/checkin/habit/list'),
      api.get('/discovery/hot-articles?limit=5')
    ])
    periodRecords.value = (records.data || []).slice(0, 5)
    habits.value = (habitRes.data || []).slice(0, 3)
    articles.value = articleRes.data || []
  } catch {}
})
</script>

<style scoped>
.home { padding: 8px; }
.stat-card { background: linear-gradient(135deg, #fce4ec, #f8bbd0); border-radius: 12px; padding: 20px; text-align: center; }
.stat-num { font-size: 28px; font-weight: bold; color: #c2185b; }
.stat-label { color: #666; font-size: 12px; margin-top: 4px; }
.habit-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #f5f5f5; }
.article-item { padding: 8px 0; cursor: pointer; }
.article-title { font-size: 14px; color: #333; }
.article-meta { font-size: 12px; color: #999; margin-top: 2px; }
</style>
