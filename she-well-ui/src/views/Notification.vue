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
import { ref, computed } from 'vue'

const activeTab = ref('all')
const notifications = ref([
  { id: 1, type: 'system', icon: '🔔', title: '欢迎使用SheWell', body: '感谢您的注册，祝您使用愉快！', time: '2026-04-14 10:00', read: false },
  { id: 2, type: 'checkin', icon: '✅', title: '打卡提醒', body: '今日健康打卡还未完成，记得记录哦~', time: '2026-04-14 09:00', read: false },
  { id: 3, type: 'community', icon: '💬', title: '收到新回复', body: '姐妹"花开"回复了你的帖子《经期护肤心得》：写得真好！', time: '2026-04-13 22:00', read: false },
  { id: 4, type: 'expert', icon: '👩‍⚕️', title: '专家回复通知', body: '王医生已回复您的问题"经期推迟一周是怎么回事"，点击查看', time: '2026-04-13 18:00', read: false },
  { id: 5, type: 'system', icon: '🎁', title: '活动提醒', body: '21天打卡挑战正在进行中，已坚持5天，继续加油！', time: '2026-04-13 08:00', read: true },
  { id: 6, type: 'checkin', icon: '🌡️', title: '体温记录提醒', body: '今日基础体温还未记录，早晨醒来后测量更准确哦', time: '2026-04-12 20:00', read: true },
  { id: 7, type: 'community', icon: '❤️', title: '帖子被点赞', body: '您的帖子《备孕期间的营养补充》收到了10个赞', time: '2026-04-12 15:00', read: true },
  { id: 8, type: 'expert', icon: '👩‍⚕️', title: '专家回复通知', body: '李医生已回复您的问题"孕早期饮食注意什么"，点击查看', time: '2026-04-11 14:00', read: true },
])

const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  return notifications.value.filter(n => n.type === activeTab.value)
})

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
const todayCount = computed(() => notifications.value.filter(n => n.time.startsWith('2026-04-14')).length)
const totalCount = computed(() => notifications.value.length)
const systemUnread = computed(() => notifications.value.filter(n => n.type === 'system' && !n.read).length)
const checkinUnread = computed(() => notifications.value.filter(n => n.type === 'checkin' && !n.read).length)
const communityUnread = computed(() => notifications.value.filter(n => n.type === 'community' && !n.read).length)
const expertUnread = computed(() => notifications.value.filter(n => n.type === 'expert' && !n.read).length)

function markAllRead() { notifications.value.forEach(n => n.read = true) }
function openNotification(n) { n.read = true }
function deleteNotification(n) { notifications.value = notifications.value.filter(i => i.id !== n.id) }
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
