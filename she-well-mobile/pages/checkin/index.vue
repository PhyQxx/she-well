<template>
  <view class="checkin-page">
    <view class="card">
      <view class="card-header">
        <text class="card-title">🎯 我的习惯</text>
        <button class="add-btn" @click="showAddHabit = true">+ 添加习惯</button>
      </view>
      <view v-if="habits.length === 0" class="empty">暂无习惯，点击添加</view>
      <view v-for="h in habits" :key="h.id" class="habit-item">
        <view class="habit-left">
          <text class="habit-icon">{{ h.icon || '✨' }}</text>
          <text class="habit-name">{{ h.name }}</text>
        </view>
        <button class="checkin-today-btn" @click="openCheckin(h)">打卡</button>
      </view>
    </view>

    <view class="card">
      <view class="card-header">
        <text class="card-title">📅 今日打卡</text>
        <text class="card-date">{{ today }}</text>
      </view>
      <view v-if="todayRecords.length === 0" class="empty">今日还未打卡~</view>
      <view v-for="c in todayRecords" :key="c.id" class="checkin-record">
        <text class="cr-habit">{{ c.habitName || '习惯' }}</text>
        <text class="cr-value">{{ c.value || c.symptom || '已完成' }}</text>
      </view>
    </view>

    <!-- 打卡弹窗 -->
    <view class="dialog-mask" v-if="showCheckin" @click="showCheckin = false">
      <view class="dialog" @click.stop>
        <text class="dialog-title">打卡 - {{ currentHabit.name }}</text>
        <view class="dialog-body">
          <input class="checkin-input" v-model="checkinForm.value" :placeholder="currentHabit.targetType === 'numeric' ? '输入数值（如80kg）' : '记录内容（选填）'" />
          <view class="symptom-row">
            <text class="form-label">症状（选填）</text>
            <view class="symptom-tags">
              <view v-for="s in symptomTags" :key="s" class="symptom-tag" :class="{ active: checkinForm.symptom?.includes(s) }" @click="toggleSymptom(s)">{{ s }}</view>
            </view>
          </view>
          <view class="mood-row">
            <text class="form-label">心情</text>
            <view class="mood-tags">
              <text v-for="m in moodTags" :key="m" class="mood-tag" :class="{ active: checkinForm.mood === m }" @click="checkinForm.mood = m">{{ m }}</text>
            </view>
          </view>
        </view>
        <view class="dialog-footer">
          <button @click="showCheckin = false">取消</button>
          <button class="confirm-btn" @click="doCheckin">确认打卡</button>
        </view>
      </view>
    </view>

    <!-- 添加习惯弹窗 -->
    <view class="dialog-mask" v-if="showAddHabit" @click="showAddHabit = false">
      <view class="dialog" @click.stop>
        <text class="dialog-title">添加习惯</text>
        <view class="dialog-body">
          <view class="form-row"><text class="form-label">习惯名称</text><input v-model="habitForm.name" placeholder="如：喝水、运动、早睡" /></view>
          <view class="form-row"><text class="form-label">目标类型</text>
            <radio-group @change="e => habitForm.targetType = e.detail.value">
              <label><radio value="numeric" /> 数值目标（如8杯水）</label>
              <label style="margin-left:32rpx"><radio value="binary" /> 完成即打卡</label>
            </radio-group>
          </view>
          <view class="form-row" v-if="habitForm.targetType === 'numeric'">
            <text class="form-label">目标值</text><input v-model="habitForm.targetValue" placeholder="如：8杯" /></view>
        </view>
        <view class="dialog-footer">
          <button @click="showAddHabit = false">取消</button>
          <button class="confirm-btn" @click="addHabit">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHabits, addHabit as apiAddHabit, getTodayCheckins, addCheckin } from '@/api/index.js'
import dayjs from 'dayjs'

const habits = ref([])
const todayRecords = ref([])
const today = dayjs().format('M月D日')
const showCheckin = ref(false)
const showAddHabit = ref(false)
const currentHabit = ref({})
const symptomTags = ['头痛', '腹痛', '腹胀', '乳房胀痛', '恶心', '疲劳', '情绪波动', '失眠']
const moodTags = ['😊 愉快', '😐 一般', '😔 低落', '😤 烦躁', '🥰 幸福']
const checkinForm = ref({ value: '', symptom: '', mood: '' })
const habitForm = ref({ name: '', targetType: 'binary', targetValue: '' })

function toggleSymptom(s) {
  const list = checkinForm.value.symptom ? checkinForm.value.symptom.split(',') : []
  const idx = list.indexOf(s)
  if (idx > -1) list.splice(idx, 1)
  else list.push(s)
  checkinForm.value.symptom = list.join(',')
}

function openCheckin(h) {
  currentHabit.value = h
  checkinForm.value = { value: '', symptom: '', mood: '' }
  showCheckin.value = true
}

function doCheckin() {
  addCheckin({
    habitId: currentHabit.value.id,
    checkinDate: dayjs().format('YYYY-MM-DD'),
    value: checkinForm.value.value,
    symptom: checkinForm.value.symptom,
    mood: checkinForm.value.mood
  }).then(() => {
    uni.showToast({ title: '打卡成功 🎉', icon: 'success' })
    showCheckin.value = false
    loadData()
  }).catch(() => {})
}

function addHabit() {
  if (!habitForm.value.name) { uni.showToast({ title: '请输入习惯名称', icon: 'none' }); return }
  apiAddHabit(habitForm.value).then(() => {
    uni.showToast({ title: '习惯已添加', icon: 'success' })
    showAddHabit.value = false
    loadData()
  }).catch(() => {})
}

function loadData() {
  getHabits().then(data => { habits.value = data || [] }).catch(() => {})
  getTodayCheckins().then(data => { todayRecords.value = data || [] }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.checkin-page { padding: 24rpx; background: #F5F5F5; min-height: 100vh; }
.card { background: white; border-radius: 16rpx; padding: 28rpx; margin-bottom: 24rpx; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.card-title { font-size: 32rpx; font-weight: 600; }
.card-date { font-size: 26rpx; color: #999; }
.add-btn { background: #E91E63; color: white; border: none; border-radius: 32rpx; font-size: 24rpx; padding: 12rpx 24rpx; }
.empty { text-align: center; color: #CCC; padding: 20rpx; font-size: 28rpx; }
.habit-item { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0; border-bottom: 1px solid #F5F5F5; }
.habit-left { display: flex; align-items: center; gap: 16rpx; }
.habit-icon { font-size: 36rpx; }
.habit-name { font-size: 30rpx; color: #333; }
.checkin-today-btn { background: #F48FB1; color: white; border: none; border-radius: 32rpx; font-size: 24rpx; padding: 12rpx 28rpx; }
.checkin-record { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1px solid #F5F5F5; }
.cr-habit { font-size: 28rpx; color: #333; }
.cr-value { font-size: 28rpx; color: #E91E63; }
.dialog-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; }
.dialog { background: white; border-radius: 24rpx; width: 600rpx; padding: 40rpx; }
.dialog-title { display: block; text-align: center; font-size: 34rpx; font-weight: 600; margin-bottom: 32rpx; }
.dialog-body { margin-bottom: 32rpx; }
.dialog-footer { display: flex; gap: 24rpx; }
.dialog-footer button { flex: 1; border-radius: 48rpx; padding: 24rpx; font-size: 30rpx; }
.confirm-btn { background: #E91E63; color: white; border: none; }
.checkin-input { background: #F5F5F5; border-radius: 8rpx; padding: 20rpx 24rpx; font-size: 28rpx; margin-bottom: 20rpx; }
.form-row { margin-bottom: 20rpx; }
.form-label { display: block; font-size: 28rpx; color: #666; margin-bottom: 8rpx; }
.symptom-tags { display: flex; flex-wrap: wrap; gap: 12rpx; }
.symptom-tag { background: #F5F5F5; color: #666; border-radius: 32rpx; padding: 10rpx 20rpx; font-size: 24rpx; }
.symptom-tag.active { background: #FCE4EC; color: #E91E63; }
.mood-tags { display: flex; flex-wrap: wrap; gap: 12rpx; }
.mood-tag { background: #F5F5F5; color: #666; border-radius: 32rpx; padding: 10rpx 20rpx; font-size: 24rpx; }
.mood-tag.active { background: #F8BBD0; color: #C2185B; }
</style>
