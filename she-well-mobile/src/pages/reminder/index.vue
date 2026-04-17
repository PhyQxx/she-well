<template>
  <view class="reminder-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <view class="header-left">
          <text class="header-icon">⏰</text>
          <view class="header-text">
            <text class="header-title">健康提醒</text>
            <text class="header-sub">养成健康好习惯</text>
          </view>
        </view>
        <view class="header-add" @click="showAddDialog = true">
          <text class="add-icon">+</text>
          <text class="add-text">添加</text>
        </view>
      </view>
    </view>

    <!-- 统计 -->
    <view class="stats-card">
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
      <view v-if="!reminderList.length" class="she-empty">
        <text class="empty-icon">⏰</text>
        <text class="empty-text">暂无提醒</text>
        <text class="empty-sub">点击上方「添加」设置健康提醒</text>
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

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 添加弹窗 -->
    <view class="dialog-overlay" v-if="showAddDialog" @click="showAddDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showAddDialog = false">取消</text>
          <text class="sheet-title">添加提醒</text>
          <text class="sheet-confirm" @click="submitReminder">保存</text>
        </view>
        <view class="sheet-body">
          <!-- 提醒类型 -->
          <view class="type-grid">
            <view v-for="t in typeOptions" :key="t.type" class="type-item" :class="{ selected: addForm.type === t.type }" @click="addForm.type = t.type; addForm.title = t.name">
              <view class="type-icon">{{ t.icon }}</view>
              <view class="type-name">{{ t.name }}</view>
            </view>
          </view>
          <input class="sheet-input" v-model="addForm.title" placeholder="提醒标题（选填）" />
          <input class="sheet-input" v-model="addForm.remindTime" type="time" placeholder="提醒时间" />
          <input class="sheet-input" v-model="addForm.content" placeholder="备注内容（选填）" />
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

<style lang="scss" scoped>
@import "../../uni.scss";

.reminder-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 220rpx;
  background: $she-gradient-nursing;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0; left: 0; right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.header-icon { font-size: 48rpx; }

.header-text { display: flex; flex-direction: column; }

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
}

.header-sub {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
  margin-top: 4rpx;
}

.header-add {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: rgba(255,255,255,0.25);
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
}

.add-icon { font-size: 28rpx; color: #fff; }
.add-text { font-size: 26rpx; color: #fff; }

// 统计
.stats-card {
  display: flex;
  background: $she-white;
  margin: -60rpx 32rpx 24rpx;
  border-radius: 20rpx;
  padding: 24rpx 0;
  box-shadow: $she-shadow-md;
  position: relative;
  z-index: 1;
}

.stat-item { flex: 1; text-align: center; }
.stat-num { font-size: 40rpx; font-weight: 700; color: $she-nursing; }
.stat-label { font-size: 22rpx; color: $she-muted; margin-top: 4rpx; }
.stat-divider { width: 1rpx; background: $she-border; margin-top: 8rpx; }

// 提醒列表
.reminder-list { margin: 0 32rpx; }

.reminder-card {
  background: $she-white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  box-shadow: $she-shadow-sm;
  transition: opacity 0.2s;

  &.disabled { opacity: 0.5; }
  &:active { transform: scale(0.99); }
}

.reminder-icon { font-size: 48rpx; }
.reminder-info { flex: 1; }
.reminder-title { font-size: 30rpx; font-weight: 600; color: $she-title; }
.reminder-time { font-size: 26rpx; color: $she-nursing; margin-top: 4rpx; }

.reminder-type-tag {
  display: inline-block;
  font-size: 22rpx;
  padding: 2rpx 10rpx;
  border-radius: 8rpx;
  margin-top: 4rpx;
}

.type-water { background: rgba(#2196F3, 0.1); color: #2196F3; }
.type-exercise { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
.type-sleep { background: rgba($she-trying, 0.1); color: $she-trying; }
.type-medicine { background: rgba($she-nursing, 0.1); color: $she-nursing; }
.type-period { background: rgba($she-primary, 0.1); color: $she-primary; }
.type-ovulation { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
.type-custom { background: $she-bg; color: $she-sub; }

.reminder-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.delete-btn { font-size: 28rpx; padding: 4rpx; }

// 快捷添加
.quick-add {
  margin: 24rpx 32rpx 0;
  background: $she-white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: $she-shadow-sm;
}

.quick-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $she-title;
  margin-bottom: 16rpx;
}

.quick-list { display: flex; gap: 16rpx; }

.quick-item {
  flex: 1;
  background: $she-bg;
  border-radius: 16rpx;
  padding: 20rpx 0;
  text-align: center;
  border: 2rpx solid transparent;
  transition: all 0.2s;

  &:active {
    border-color: $she-nursing;
    background: rgba($she-nursing, 0.06);
  }
}

.quick-icon { font-size: 36rpx; }
.quick-name { font-size: 24rpx; color: $she-sub; margin-top: 4rpx; }

.bottom-placeholder { height: 48rpx; }

// 弹窗
.dialog-sheet { background: $she-white; border-radius: 32rpx 32rpx 0 0; padding-bottom: env(safe-area-inset-bottom); }

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid $she-border;
}

.sheet-cancel { font-size: 30rpx; color: $she-muted; min-width: 100rpx; }
.sheet-title { font-size: 32rpx; font-weight: 600; color: $she-text; }
.sheet-confirm { font-size: 30rpx; color: $she-nursing; min-width: 100rpx; text-align: right; }

.sheet-body { padding: 24rpx; }

.type-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.type-item {
  width: calc(33.33% - 8rpx);
  background: $she-bg;
  border-radius: 16rpx;
  padding: 20rpx 0;
  text-align: center;
  border: 2rpx solid transparent;
  transition: all 0.2s;

  &.selected {
    border-color: $she-nursing;
    background: rgba($she-nursing, 0.08);
  }
}

.type-icon { font-size: 36rpx; }
.type-name { font-size: 22rpx; color: $she-sub; margin-top: 4rpx; }

.sheet-input {
  border: 1rpx solid $she-border;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: $she-text;
  margin-bottom: 16rpx;
  width: 100%;
  box-sizing: border-box;
}
</style>
