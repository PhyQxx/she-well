<template>
  <div class="notification-page">
    <div class="page-header">
      <h2>🔔 消息通知</h2>
      <el-button text size="small" @click="markAllRead" :disabled="unreadCount === 0">
        全部标为已读
      </el-button>
    </div>

    <!-- 通知统计 -->
    <el-row :gutter="12" style="margin-bottom:20px">
      <el-col :span="8">
        <div class="stat-card pink">
          <div class="stat-num">{{ unreadCount }}</div>
          <div class="stat-label">未读消息</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card purple">
          <div class="stat-num">{{ todayCount }}</div>
          <div class="stat-label">今日新消息</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card blue">
          <div class="stat-num">{{ totalCount }}</div>
          <div class="stat-label">总消息数</div>
        </div>
      </el-col>
    </el-row>

    <!-- 通知列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="系统通知" name="system">
            <el-badge :value="systemUnread" :hidden="systemUnread === 0">
              <span>系统通知</span>
            </el-badge>
          </el-tab-pane>
          <el-tab-pane label="打卡提醒" name="checkin">
            <el-badge :value="checkinUnread" :hidden="checkinUnread === 0">
              <span>打卡提醒</span>
            </el-badge>
          </el-tab-pane>
          <el-tab-pane label="社区消息" name="community">
            <el-badge :value="communityUnread" :hidden="communityUnread === 0">
              <span>社区消息</span>
            </el-badge>
          </el-tab-pane>
          <el-tab-pane label="专家回复" name="expert">
            <el-badge :value="expertUnread" :hidden="expertUnread === 0">
              <span>专家回复</span>
            </el-badge>
          </el-tab-pane>
        </el-tabs>
      </template>

      <div class="notify-list">
        <div
          v-for="n in filteredNotifications"
          :key="n.id"
          class="notify-item"
          :class="{ unread: !n.read }"
          @click="openNotification(n)"
        >
          <div class="notify-icon">{{ n.icon }}</div>
          <div class="notify-content">
            <div class="notify-title">{{ n.title }}</div>
            <div class="notify-body">{{ n.body }}</div>
            <div class="notify-time">{{ n.time }}</div>
          </div>
          <div class="notify-actions">
            <el-badge :value="''" :hidden="n.read" color="#E91E63" />
            <el-button text size="small" @click.stop="deleteNotification(n)">删除</el-button>
          </div>
        </div>
        <el-empty v-if="!filteredNotifications.length" description="暂无消息" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, getUnreadCount, markAllRead as markAllReadApi, markRead } from '@/api'

const activeTab = ref('all')
const notifications = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const [listRes, countRes] = await Promise.all([
      getNotifications(),
      getUnreadCount(),
    ])
    const list = listRes?.data || listRes || []
    notifications.value = list.map(n => ({
      id: n.id,
      type: n.type || 'system',
      icon: getIconByType(n.type),
      title: n.title || n.content?.slice(0, 30) || '通知',
      body: n.content || n.message || '',
      time: n.createTime || n.createTime || '',
      read: !!n.readTime,
    }))
  } catch (err) {
    notifications.value = []
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

function getIconByType(type) {
  const map = { system: '🔔', checkin: '✅', community: '💬', expert: '👩‍⚕️', ai: '🤖', period: '🩸', activity: '🎁' }
  return map[type] || '🔔'
}

const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  return notifications.value.filter(n => n.type === activeTab.value)
})

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
const todayCount = computed(() => {
  const today = new Date().toISOString().slice(0, 10)
  return notifications.value.filter(n => n.time && n.time.startsWith(today)).length
})
const totalCount = computed(() => notifications.value.length)
const systemUnread = computed(() => notifications.value.filter(n => n.type === 'system' && !n.read).length)
const checkinUnread = computed(() => notifications.value.filter(n => n.type === 'checkin' && !n.read).length)
const communityUnread = computed(() => notifications.value.filter(n => n.type === 'community' && !n.read).length)
const expertUnread = computed(() => notifications.value.filter(n => n.type === 'expert' && !n.read).length)

async function markAllRead() {
  try {
    await markAllReadApi()
    notifications.value.forEach(n => n.read = true)
    ElMessage.success('已全部标为已读')
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

async function openNotification(n) {
  if (!n.read) {
    try { await markRead(n.id) } catch (_) {}
    n.read = true
  }
}

function deleteNotification(n) {
  notifications.value = notifications.value.filter(i => i.id !== n.id)
}

onMounted(loadData)
</script>

<style scoped>
.notification-page { padding: 24px }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px }
.page-header h2 { margin: 0; font-size: 24px }
.stat-card { border-radius: 12px; padding: 20px; color: white; text-align: center }
.stat-card.pink { background: linear-gradient(135deg, #E91E63, #F48FB1) }
.stat-card.purple { background: linear-gradient(135deg, #9C27B0, #CE93D8) }
.stat-card.blue { background: linear-gradient(135deg, #2196F3, #90CAF9) }
.stat-num { font-size: 36px; font-weight: bold }
.stat-label { font-size: 14px; opacity: 0.9 }
.notify-list { display: flex; flex-direction: column; }
.notify-item { display: flex; gap: 12px; padding: 16px 0; border-bottom: 1px solid #f5f5f5; cursor: pointer; transition: background 0.2s }
.notify-item:last-child { border-bottom: none }
.notify-item:hover { background: #faf8fb }
.notify-item.unread { background: #fff5f7 }
.notify-icon { font-size: 28px; flex-shrink: 0; width: 40px; text-align: center }
.notify-content { flex: 1; min-width: 0 }
.notify-title { font-size: 15px; font-weight: 500; color: #333; margin-bottom: 4px }
.notify-body { font-size: 13px; color: #666; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap }
.notify-time { font-size: 12px; color: #bbb }
.notify-actions { display: flex; flex-direction: column; align-items: center; gap: 8px; flex-shrink: 0 }
</style>
