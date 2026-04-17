<template>
  <div class="dashboard-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">📊 数据概览</h1>
      <p class="page-subtitle">实时了解平台运营状况</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card pink">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <div class="stat-num">{{ stats.users?.total || 0 }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-icon">💬</div>
          <div class="stat-content">
            <div class="stat-num">{{ stats.content?.posts?.total || 0 }}</div>
            <div class="stat-label">社区帖子</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-icon">📖</div>
          <div class="stat-content">
            <div class="stat-num">{{ stats.content?.articles?.total || 0 }}</div>
            <div class="stat-label">知识文章</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-num">{{ stats.checkin?.today || 0 }}</div>
            <div class="stat-label">今日打卡</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="14">
        <div class="card">
          <div class="card-header">
            <span class="card-title">📈 用户增长趋势（近7天）</span>
          </div>
          <div ref="userChartRef" style="height: 280px"></div>
          <div v-if="!hasTrendData" class="empty-chart">
            <span>暂无趋势数据</span>
          </div>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="card">
          <div class="card-header">
            <span class="card-title">📊 内容数据统计</span>
          </div>
          <el-table :data="contentStats" stripe class="stats-table">
            <el-table-column prop="type" label="类型">
              <template #default="{ row }">
                <span class="type-cell">
                  <span class="type-icon">{{ row.icon }}</span>
                  {{ row.type }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="total" label="总数" align="center" />
            <el-table-column prop="today" label="今日新增" align="center">
              <template #default="{ row }">
                <span class="today-badge">+{{ row.today }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>

    <!-- 待处理事项 -->
    <div class="card pending-card">
      <div class="card-header">
        <span class="card-title">🔔 待处理事项</span>
        <span class="pending-total">{{ pendingTotal }} 项待处理</span>
      </div>
      <div class="pending-grid">
        <div class="pending-item" @click="$router.push('/community')">
          <div class="pending-icon">💬</div>
          <div class="pending-content">
            <div class="pending-num">{{ stats.pending?.posts || 0 }}</div>
            <div class="pending-label">帖子待审核</div>
          </div>
          <div class="pending-arrow">&gt;</div>
        </div>
        <div class="pending-item" @click="$router.push('/question')">
          <div class="pending-icon">❓</div>
          <div class="pending-content">
            <div class="pending-num">{{ stats.pending?.questions || 0 }}</div>
            <div class="pending-label">问答待回复</div>
          </div>
          <div class="pending-arrow">&gt;</div>
        </div>
        <div class="pending-item" @click="$router.push('/expert')">
          <div class="pending-icon">👩‍⚕️</div>
          <div class="pending-content">
            <div class="pending-num">{{ stats.pending?.experts || 0 }}</div>
            <div class="pending-label">专家待审核</div>
          </div>
          <div class="pending-arrow">&gt;</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getTrendStats } from '@/api'

const stats = ref({})
const contentStats = ref([])
const userChartRef = ref(null)
const hasTrendData = ref(false)

const pendingTotal = computed(() => {
  return (stats.value.pending?.posts || 0) +
    (stats.value.pending?.questions || 0) +
    (stats.value.pending?.experts || 0)
})

async function loadDashboard() {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.value = res.data
      const d = res.data
      contentStats.value = [
        { type: '社区帖子', icon: '💬', total: d.content?.posts?.total || 0, today: d.content?.posts?.today || 0 },
        { type: '知识文章', icon: '📖', total: d.content?.articles?.total || 0, today: d.content?.articles?.today || 0 },
      ]
    }
  } catch {}
}

async function loadTrend() {
  try {
    const res = await getTrendStats(7)
    if (res.data?.userTrend && res.data.userTrend.length > 0) {
      hasTrendData.value = true
      await nextTick()
      if (!userChartRef.value) return
      const chart = echarts.init(userChartRef.value)
      const dates = res.data.userTrend.map(i => i.date?.slice(5) || '')
      const counts = res.data.userTrend.map(i => i.count || 0)
      chart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#eee', textStyle: { color: '#333' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLine: { lineStyle: { color: '#eee' } }, axisLabel: { color: '#666' } },
        yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f5f5f5' } } },
        series: [{
          name: '新增用户',
          data: counts,
          type: 'bar',
          color: '#E91E63',
          barWidth: '50%',
          itemStyle: { borderRadius: [4, 4, 0, 0] },
          emphasis: { itemStyle: { color: '#C2185B' } }
        }]
      })
    }
  } catch {}
}

onMounted(() => {
  loadDashboard()
  loadTrend()
})
</script>

<style lang="scss" scoped>
@import '@/styles/global.scss';

.dashboard-page {
  padding: $admin-space-6;
  animation: fadeIn 0.4s ease;
}

.page-header {
  margin-bottom: $admin-space-6;
}

.page-title {
  font-size: $admin-font-size-2xl;
  font-weight: 700;
  color: $admin-text;
  margin: 0 0 $admin-space-1;
}

.page-subtitle {
  font-size: $admin-font-size-sm;
  color: $admin-muted;
  margin: 0;
}

.stat-row {
  margin-bottom: $admin-space-5;
}

.stat-card {
  border-radius: $admin-radius-xl;
  padding: $admin-space-5;
  display: flex;
  align-items: center;
  gap: $admin-space-4;
  color: white;
  transition: transform $admin-transition-normal;

  &:hover {
    transform: translateY(-2px);
  }

  &.pink { background: linear-gradient(135deg, #E91E63, #F48FB1); }
  &.purple { background: linear-gradient(135deg, #9C27B0, #CE93D8); }
  &.blue { background: linear-gradient(135deg, #2196F3, #90CAF9); }
  &.green { background: linear-gradient(135deg, #4CAF50, #A5D6A7); }
}

.stat-icon {
  font-size: 36px;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  opacity: 0.85;
}

.chart-row {
  margin-bottom: $admin-space-5;
}

.card {
  background: $admin-white;
  border-radius: $admin-radius-xl;
  padding: $admin-space-5;
  box-shadow: $admin-shadow-sm;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $admin-space-4;
}

.card-title {
  font-size: $admin-font-size-md;
  font-weight: 600;
  color: $admin-text;
}

.empty-chart {
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $admin-muted;
  font-size: 14px;
}

.stats-table {
  border-radius: $admin-radius-md;
  overflow: hidden;
}

.type-cell {
  display: flex;
  align-items: center;
  gap: $admin-space-2;
}

.type-icon {
  font-size: 18px;
}

.today-badge {
  display: inline-block;
  padding: 2px 8px;
  background: rgba($admin-primary, 0.1);
  color: $admin-primary;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.pending-card {
  margin-bottom: $admin-space-5;
}

.pending-total {
  font-size: $admin-font-size-sm;
  color: $admin-muted;
}

.pending-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $admin-space-4;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: $admin-space-3;
  padding: $admin-space-4;
  background: $admin-bg;
  border-radius: $admin-radius-lg;
  cursor: pointer;
  transition: all $admin-transition-fast;

  &:hover {
    background: rgba($admin-primary, 0.06);
    transform: translateX(4px);
  }
}

.pending-icon {
  font-size: 28px;
}

.pending-num {
  font-size: 24px;
  font-weight: 700;
  color: $admin-primary;
}

.pending-label {
  font-size: 12px;
  color: $admin-muted;
}

.pending-arrow {
  margin-left: auto;
  font-size: 18px;
  color: $admin-border;
}
</style>
