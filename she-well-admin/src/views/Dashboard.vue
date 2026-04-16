<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6">
        <div class="stat-card pink">
          <div class="stat-num">{{ stats.users?.total || 0 }}</div>
          <div class="stat-label">注册用户</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-num">{{ stats.content?.posts?.total || 0 }}</div>
          <div class="stat-label">社区帖子</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-num">{{ stats.content?.articles?.total || 0 }}</div>
          <div class="stat-label">知识文章</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-num">{{ stats.checkin?.today || 0 }}</div>
          <div class="stat-label">今日打卡</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>📈 用户增长趋势（近7天）</template>
          <div ref="userChartRef" style="height: 250px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>📊 内容数据统计</template>
          <el-table :data="contentStats" stripe>
            <el-table-column prop="type" label="类型" />
            <el-table-column prop="total" label="总数" />
            <el-table-column prop="today" label="今日新增" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>🔔 待处理事项</template>
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="pending-item" @click="$router.push('/community')">
                <div class="pending-num">{{ stats.pending?.posts || 0 }}</div>
                <div class="pending-label">帖子待审核</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getTrendStats } from '@/api'

const stats = ref({})
const contentStats = ref([])
const userChartRef = ref(null)

async function loadDashboard() {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.value = res.data
      const d = res.data
      contentStats.value = [
        { type: '社区帖子', total: d.content?.posts?.total || 0, today: d.content?.posts?.today || 0 },
        { type: '知识文章', total: d.content?.articles?.total || 0, today: d.content?.articles?.today || 0 },
      ]
    }
  } catch {}
}

async function loadTrend() {
  try {
    const res = await getTrendStats(7)
    if (res.data?.userTrend) {
      await nextTick()
      if (!userChartRef.value) return
      const chart = echarts.init(userChartRef.value)
      const dates = res.data.userTrend.map(i => i.date?.slice(5) || '')
      const counts = res.data.userTrend.map(i => i.count || 0)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['新增用户'] },
        xAxis: { type: 'category', data: dates },
        yAxis: { type: 'value' },
        series: [{ name: '新增用户', data: counts, type: 'bar', color: '#E91E63' }]
      })
    }
  } catch {}
}

onMounted(() => {
  loadDashboard()
  loadTrend()
})
</script>

<style scoped>
.stat-card {
  border-radius: 12px; padding: 24px; color: white;
  display: flex; flex-direction: column; gap: 8px;
}
.stat-card.pink { background: linear-gradient(135deg, #E91E63, #F48FB1) }
.stat-card.purple { background: linear-gradient(135deg, #9C27B0, #CE93D8) }
.stat-card.blue { background: linear-gradient(135deg, #2196F3, #90CAF9) }
.stat-card.green { background: linear-gradient(135deg, #4CAF50, #A5D6A7) }
.stat-num { font-size: 36px; font-weight: bold }
.stat-label { font-size: 14px; opacity: 0.9 }
.pending-item {
  background: #fff; border-radius: 8px; padding: 20px; text-align: center;
  cursor: pointer; border: 1px solid #f0f0f0; transition: all 0.2s;
}
.pending-item:hover { border-color: #E91E63; box-shadow: 0 2px 8px rgba(233,30,99,0.1) }
.pending-num { font-size: 32px; font-weight: bold; color: #E91E63 }
.pending-label { font-size: 13px; color: #666; margin-top: 4px }
</style>
