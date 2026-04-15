<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6">
        <div class="stat-card pink">
          <div class="stat-num">{{ stats.users || 0 }}</div>
          <div class="stat-label">注册用户</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-num">{{ stats.posts || 0 }}</div>
          <div class="stat-label">社区帖子</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-num">{{ stats.articles || 0 }}</div>
          <div class="stat-label">知识文章</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-num">{{ stats.questions || 0 }}</div>
          <div class="stat-label">专家问答</div>
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
            <el-table-column prop="pending" label="待审核" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>🔔 待处理事项</template>
          <el-row :gutter="16">
            <el-col :span="6">
              <div class="pending-item" @click="$router.push('/expert')">
                <div class="pending-num">{{ pending.expertApplies }}</div>
                <div class="pending-label">专家申请待审核</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="pending-item" @click="$router.push('/question')">
                <div class="pending-num">{{ pending.expertQuestions }}</div>
                <div class="pending-label">待回复问答</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="pending-item" @click="$router.push('/community')">
                <div class="pending-num">{{ pending.pendingPosts }}</div>
                <div class="pending-label">帖子待审核</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="pending-item" @click="$router.push('/report')">
                <div class="pending-num">{{ pending.userReports }}</div>
                <div class="pending-label">用户举报</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const stats = ref({ users: 0, posts: 0, articles: 0, questions: 0 })
const pending = ref({ expertApplies: 0, expertQuestions: 0, pendingPosts: 0, userReports: 0 })
const contentStats = ref([
  { type: '社区帖子', total: 0, today: 0, pending: 0 },
  { type: '社区评论', total: 0, today: 0, pending: 0 },
  { type: '知识文章', total: 0, today: 0, pending: 0 },
  { type: '专家问答', total: 0, today: 0, pending: 0 },
])
const userChartRef = ref(null)

function initChart() {
  if (!userChartRef.value) return
  const chart = echarts.init(userChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增用户'] },
    xAxis: { type: 'category', data: ['4/8', '4/9', '4/10', '4/11', '4/12', '4/13', '4/14'] },
    yAxis: { type: 'value' },
    series: [{ name: '新增用户', data: [12, 18, 15, 22, 19, 28, 25], type: 'bar', color: '#E91E63' }]
  })
}

onMounted(() => {
  initChart()
  // 演示数据
  stats.value = { users: 1248, posts: 3421, articles: 186, questions: 57 }
  pending.value = { expertApplies: 3, expertQuestions: 8, pendingPosts: 12, userReports: 2 }
  contentStats.value = [
    { type: '社区帖子', total: 3421, today: 28, pending: 12 },
    { type: '社区评论', total: 8932, today: 156, pending: 0 },
    { type: '知识文章', total: 186, today: 2, pending: 0 },
    { type: '专家问答', total: 57, today: 5, pending: 8 },
  ]
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
