<template>
  <view class="reminder-page">
    <view class="header">
      <view class="header-title">⏰ 健康提醒</view>
      <view class="header-add" @click="showAddDialog = true">+ 添加</view>
    </view>

    <!-- 统计 -->
    <view class="stats-row">
      <view class="stat-item">
        <view class="stat-num">{{ reminderList.filter(r => r.enabled).length }}</view>
        <view class="stat-label">已开启</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <view class="stat-num">{{ reminderList.length }}</view>
        <view class="stat-label">共设置</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <view class="stat-num">{{ typeCount('water') }}</view>
        <view class="stat-label">喝水</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <view class="stat-num">{{ typeCount('exercise') }}</view>
        <view class="stat-label">运动</view>
      </view>
    </view>

    <!-- 提醒列表 -->
    <view class="reminder-list">
      <view v-if="!reminderList.length" class="empty-state">
        <view class="empty-icon">⏰</view>
        <view class="empty-text">暂无提醒</view>
        <view class="empty-sub">点击上方「添加」设置健康提醒</view>
      </view>

      <view v-for="r in reminderList" :key="r.id" class="reminder-card" :class="{ disabled: !r.enabled }">
        <view class="reminder-icon">{{ typeIcon(r.type) }}</view>
        <view class="reminder-info">
          <view class="reminder-title">{{ r.title || typeName(r.type) }}</view>
          <view class="reminder-time">🕐 {{ r.remindTime || '08:00' }}</view>
          <view class="reminder-type-tag" :class="'type-' + r.type">{{ typeName(r.type) }}</view>
        </view>
        <view class="reminder-actions">
          <switch :checked="r.enabled" @change="toggleReminder(r)" color="#E91E63" />
          <view class="delete-btn" @click="deleteReminder(r)">🗑</view>
        </view>
      </view>
    </view>

    <!-- 快捷添加 -->
    <view class="quick-add" v-if="reminderList.length < 10">
      <view class="quick-title">💡 快速添加</view>
      <view class="quick-list">
        <view v-for="q in quickTypes" :key="q.type" class="quick-item" @click="quickAdd(q)">
          <view class="quick-icon">{{ q.icon }}</view>
          <view class="quick-name">{{ q.name }}</view>
        </view>
      </view>
    </view>

    <!-- 添加弹窗 -->
    <view class="dialog-overlay" v-if="showAddDialog" @click="showAddDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">添加提醒</view>

        <!-- 提醒类型 -->
        <view class="type-grid">
          <view v-for="t in typeOptions" :key="t.type" class="type-item" :class="{ selected: addForm.type === t.type }" @click="addForm.type = t.type; addForm.title = t.name">
            <view class="type-icon">{{ t.icon }}</view>
            <view class="type-name">{{ t.name }}</view>
          </view>
        </view>

        <input class="dialog-input" v-model="addForm.title" placeholder="提醒标题（选填）" />
        <input class="dialog-input" v-model="addForm.remindTime" type="time" placeholder="提醒时间" />
        <input class="dialog-input" v-model="addForm.content" placeholder="备注内容（选填）" />

        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showAddDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitReminder">保存</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { reminder } from '@/api/index.js'

const reminderList = ref([])
const showAddDialog = ref(false)
const addForm = ref({ type: 'water', title: '', remindTime: '09:00', content: '' })

const typeOptions = [
  { type: 'water', icon: '💧', name: '喝水提醒' },
  { type: 'exercise', icon: '🏃', name: '运动提醒' },
  { type: 'sleep', icon: '😴', name: '早睡提醒' },
  { type: 'medicine', icon: '💊', name: '吃药提醒' },
  { type: 'period', icon: '🌸', name: '经期提醒' },
  { type: 'ovulation', icon: '🧪', name: '排卵提醒' },
  { type: 'custom', icon: '✨', name: '自定义' },
]

const quickTypes = [
  { type: 'water', icon: '💧', name: '喝水' },
  { type: 'exercise', icon: '🏃', name: '运动' },
  { type: 'sleep', icon: '😴', name: '早睡' },
  { type: 'medicine', icon: '💊', name: '吃药' },
]

function typeIcon(type) {
  return { water: '💧', exercise: '🏃', sleep: '😴', medicine: '💊', period: '🌸', ovulation: '🧪', checkup: '📋', custom: '✨' }[type] || '⏰'
}

function typeName(type) {
  return { water: '喝水提醒', exercise: '运动提醒', sleep: '早睡提醒', medicine: '吃药提醒', period: '经期提醒', ovulation: '排卵提醒', checkup: '产检提醒', custom: '自定义' }[type] || '提醒'
}

function typeCount(type) {
  return reminderList.value.filter(r => r.type === type && r.enabled).length
}

async function loadData() {
  try {
    const res = await reminder.list()
    reminderList.value = (res?.data || res || []).map(r => ({
      id: r.id,
      type: r.type || 'custom',
      title: r.title || '',
      content: r.content || '',
      remindTime: r.remindTime || '08:00',
      enabled: r.enabled !== false,
    }))
  } catch (e) {
    console.error('load reminders error', e)
  }
}

async function toggleReminder(r) {
  const newEnabled = !r.enabled
  try {
    await reminder.toggle(r.id, newEnabled)
    r.enabled = newEnabled
  } catch (e) {
    uni.showToast({ title: '切换失败', icon: 'none' })
    // revert
    r.enabled = !newEnabled
  }
}

async function deleteReminder(r) {
  uni.showModal({
    title: '确认删除',
    content: `确定删除「${r.title || typeName(r.type)}」提醒？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await reminder.delete(r.id)
          reminderList.value = reminderList.value.filter(x => x.id !== r.id)
          uni.showToast({ title: '已删除', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

async function quickAdd(q) {
  try {
    const res = await reminder.create({
      type: q.type,
      title: q.name,
      remindTime: q.type === 'water' ? '09:00' : q.type === 'exercise' ? '07:00' : q.type === 'sleep' ? '22:00' : '08:00',
      enabled: true,
    })
    reminderList.value.push({
      id: res?.data?.id || Date.now(),
      type: q.type,
      title: q.name,
      content: '',
      remindTime: q.type === 'water' ? '09:00' : q.type === 'exercise' ? '07:00' : q.type === 'sleep' ? '22:00' : '08:00',
      enabled: true,
    })
    uni.showToast({ title: '已添加', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '添加失败', icon: 'none' })
  }
}

async function submitReminder() {
  if (!addForm.value.remindTime) { uni.showToast({ title: '请选择时间', icon: 'none' }); return }
  try {
    const res = await reminder.create({
      type: addForm.value.type,
      title: addForm.value.title || typeName(addForm.value.type),
      content: addForm.value.content,
      remindTime: addForm.value.remindTime,
      enabled: true,
    })
    reminderList.value.push({
      id: res?.data?.id || Date.now(),
      type: addForm.value.type,
      title: addForm.value.title || typeName(addForm.value.type),
      content: addForm.value.content,
      remindTime: addForm.value.remindTime,
      enabled: true,
    })
    showAddDialog.value = false
    addForm.value = { type: 'water', title: '', remindTime: '09:00', content: '' }
    uni.showToast({ title: '已添加', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '添加失败', icon: 'none' })
  }
}

onMounted(loadData)
</script>

<style scoped>
.reminder-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #FF9800, #FFB74D); padding: 32rpx; color: #fff; display: flex; justify-content: space-between; align-items: center }
.header-title { font-size: 36rpx; font-weight: bold }
.header-add { font-size: 28rpx; background: rgba(255,255,255,0.25); padding: 8rpx 20rpx; border-radius: 20rpx }
.stats-row { display: flex; background: #fff; margin: 24rpx 32rpx; border-radius: 16rpx; padding: 24rpx 0; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06) }
.stat-item { flex: 1; text-align: center }
.stat-num { font-size: 36rpx; font-weight: bold; color: #FF9800 }
.stat-label { font-size: 22rpx; color: #999; margin-top: 4rpx }
.stat-divider { width: 1rpx; background: #f0f0f0 }
.reminder-list { margin: 0 32rpx }
.reminder-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; display: flex; align-items: center; gap: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06); transition: opacity 0.2s }
.reminder-card.disabled { opacity: 0.5 }
.reminder-icon { font-size: 48rpx }
.reminder-info { flex: 1 }
.reminder-title { font-size: 30rpx; font-weight: 600; color: #333 }
.reminder-time { font-size: 26rpx; color: #FF9800; margin-top: 4rpx }
.reminder-type-tag { display: inline-block; font-size: 22rpx; padding: 2rpx 10rpx; border-radius: 8rpx; margin-top: 4rpx }
.type-water { background: #e3f2fd; color: #2196F3 }
.type-exercise { background: #e8f5e9; color: #4CAF50 }
.type-sleep { background: #f3e5f5; color: #9C27B0 }
.type-medicine { background: #fff3e0; color: #FF9800 }
.type-period { background: #fce4ec; color: #E91E63 }
.type-ovulation { background: #f0f9eb; color: #4CAF50 }
.type-custom { background: #faf8fb; color: #666 }
.reminder-actions { display: flex; flex-direction: column; align-items: center; gap: 8rpx }
.delete-btn { font-size: 28rpx; padding: 4rpx }
.empty-state { text-align: center; padding: 80rpx 0 }
.empty-icon { font-size: 80rpx; margin-bottom: 16rpx }
.empty-text { font-size: 32rpx; font-weight: 600; color: #999 }
.empty-sub { font-size: 26rpx; color: #ccc; margin-top: 8rpx }
.quick-add { margin: 24rpx 32rpx; background: #fff; border-radius: 16rpx; padding: 24rpx }
.quick-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx }
.quick-list { display: flex; gap: 16rpx }
.quick-item { flex: 1; background: #faf8fb; border-radius: 12rpx; padding: 20rpx 0; text-align: center; border: 2rpx solid transparent }
.quick-item:active { border-color: #FF9800 }
.quick-icon { font-size: 36rpx }
.quick-name { font-size: 24rpx; color: #666; margin-top: 4rpx }
/* dialog */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center }
.dialog-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 600rpx; max-height: 80vh; overflow-y: auto }
.dialog-title { font-size: 32rpx; font-weight: bold; text-align: center; margin-bottom: 24rpx }
.type-grid { display: flex; flex-wrap: wrap; gap: 12rpx; margin-bottom: 16rpx }
.type-item { width: calc(33.33% - 8rpx); background: #faf8fb; border-radius: 12rpx; padding: 16rpx 0; text-align: center; border: 2rpx solid transparent }
.type-item.selected { border-color: #FF9800; background: #fff3e0 }
.type-icon { font-size: 36rpx }
.type-name { font-size: 22rpx; color: #666; margin-top: 4rpx }
.dialog-input { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; box-sizing: border-box }
.dialog-btns { display: flex; gap: 20rpx; margin-top: 8rpx }
.dialog-cancel { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 10rpx; color: #999; font-size: 28rpx }
.dialog-confirm { flex: 1; text-align: center; padding: 16rpx; background: linear-gradient(135deg, #FF9800, #FFB74D); border-radius: 10rpx; color: #fff; font-size: 28rpx }
</style>
