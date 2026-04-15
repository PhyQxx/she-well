<template>
  <view class="period-page">
    <!-- 模式切换 -->
    <view class="mode-tabs">
      <view class="tab" :class="{ active: mode === 'period' }" @click="mode = 'period'">🌸 经期</view>
      <view class="tab" :class="{ active: mode === 'pregnancy' }" @click="mode = 'pregnancy'">🤰 备孕/怀孕</view>
    </view>

    <!-- 经期模式 -->
    <view v-if="mode === 'period'">
      <view class="card">
        <view class="card-header">
          <text class="card-title">📝 经期记录</text>
          <button class="add-btn" @click="showAddDialog = true">+ 记录</button>
        </view>
        <view class="tips" v-if="records.length === 0">暂无经期记录，点击上方按钮开始记录</view>
        <view v-for="r in records" :key="r.id" class="record-item">
          <view class="record-date">{{ r.startDate }} ~ {{ r.endDate }}</view>
          <view class="record-info">
            <text>持续 {{ r.duration }} 天</text>
            <text class="flow-tag">{{ ['','极少','少','中','多'][r.flowLevel] || '' }}</text>
          </view>
          <text class="del-btn" @click="delRecord(r.id)">删除</text>
        </view>
      </view>

      <view class="card">
        <view class="card-header">
          <text class="card-title">🔮 经期预测</text>
          <button class="refresh-btn" @click="calcPrediction">重新计算</button>
        </view>
        <view v-if="prediction" class="pred-grid">
          <view class="pred-item"><text class="pred-label">预测经期</text><text class="pred-val">{{ prediction.predictedStartDate }}</text></view>
          <view class="pred-item"><text class="pred-label">易孕期</text><text class="pred-val">{{ prediction.fertileStartDate }} ~ {{ prediction.fertileEndDate }}</text></view>
          <view class="pred-item"><text class="pred-label">排卵日</text><text class="pred-val pred-highlight">{{ prediction.ovulationDate || '--' }}</text></view>
        </view>
        <view v-else class="tips">点击「重新计算」获取预测</view>
      </view>
    </view>

    <!-- 怀孕模式 -->
    <view v-if="mode === 'pregnancy'">
      <view v-if="!pregnancyRecord" class="card">
        <view class="card-header"><text class="card-title">🤰 怀孕记录</text></view>
        <view class="tips">暂无怀孕记录</view>
        <view class="preg-form">
          <text class="form-label">末次月经日期</text>
          <picker mode="date" @change="onLastPeriodChange">
            <view class="picker-val">{{ pregForm.lastPeriodStart || '请选择' }}</view>
          </picker>
          <button class="start-btn" @click="startPregnancy">开始怀孕记录</button>
        </view>
      </view>

      <view v-else class="card">
        <view class="card-header"><text class="card-title">🤰 怀孕进行中</text></view>
        <view class="preg-stats">
          <view class="preg-stat"><text class="preg-num">{{ pregnancyRecord.currentWeek || 0 }}</text><text class="preg-label">孕周</text></view>
          <view class="preg-stat"><text class="preg-num">{{ pregnancyRecord.currentDay || 0 }}</text><text class="preg-label">孕天</text></view>
          <view class="preg-stat"><text class="preg-num">{{ pregnancyRecord.dueDate }}</text><text class="preg-label">预产期</text></view>
        </view>
        <button class="born-btn" @click="showBabyDialog = true">🤱 宝宝已出生</button>
      </view>
    </view>

    <!-- 记录经期弹窗 -->
    <view class="dialog-mask" v-if="showAddDialog" @click="showAddDialog = false">
      <view class="dialog" @click.stop>
        <text class="dialog-title">记录经期</text>
        <view class="dialog-body">
          <view class="form-row">
            <text class="form-label">开始日期</text>
            <picker mode="date" @change="e => form.startDate = e.detail.value">
              <view class="picker-val">{{ form.startDate || '请选择' }}</view>
            </picker>
          </view>
          <view class="form-row">
            <text class="form-label">结束日期</text>
            <picker mode="date" @change="e => form.endDate = e.detail.value">
              <view class="picker-val">{{ form.endDate || '请选择' }}</view>
            </picker>
          </view>
          <view class="form-row">
            <text class="form-label">流量</text>
            <radio-group @change="e => form.flowLevel = e.detail.value">
              <label v-for="(l, i) in ['极少','少','中','多']" :key="i"><radio :value="i+1" /> {{ l }}</label>
            </radio-group>
          </view>
        </view>
        <view class="dialog-footer">
          <button @click="showAddDialog = false">取消</button>
          <button class="confirm-btn" @click="addRecord">确定</button>
        </view>
      </view>
    </view>

    <!-- 宝宝出生弹窗 -->
    <view class="dialog-mask" v-if="showBabyDialog" @click="showBabyDialog = false">
      <view class="dialog" @click.stop>
        <text class="dialog-title">宝宝已出生</text>
        <view class="dialog-body">
          <view class="form-row"><text class="form-label">宝宝姓名</text><input v-model="babyForm.name" placeholder="请输入" /></view>
          <view class="form-row">
            <text class="form-label">出生日期</text>
            <picker mode="date" @change="e => babyForm.birthDate = e.detail.value">
              <view class="picker-val">{{ babyForm.birthDate || '请选择' }}</view>
            </picker>
          </view>
        </view>
        <view class="dialog-footer">
          <button @click="showBabyDialog = false">取消</button>
          <button class="confirm-btn" @click="confirmBabyBorn">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPeriodRecords, addPeriodRecord, deletePeriodRecord, getPrediction, calculatePrediction,
         getPregnancyRecord, addPregnancyRecord, babyBorn } from '@/api/index.js'

const mode = ref('period')
const records = ref([])
const prediction = ref(null)
const pregnancyRecord = ref(null)
const showAddDialog = ref(false)
const showBabyDialog = ref(false)
const form = ref({ startDate: '', endDate: '', flowLevel: 3 })
const pregForm = ref({ lastPeriodStart: '' })
const babyForm = ref({ name: '', birthDate: '' })

function calcDuration() {
  if (form.value.startDate && form.value.endDate) {
    const s = new Date(form.value.startDate)
    const e = new Date(form.value.endDate)
    return Math.max(1, Math.ceil((e - s) / 86400000) + 1)
  }
  return 0
}

function addRecord() {
  if (!form.value.startDate) { uni.showToast({ title: '请选择开始日期', icon: 'none' }); return }
  addPeriodRecord({ ...form.value, duration: calcDuration() }).then(() => {
    uni.showToast({ title: '记录成功', icon: 'success' })
    showAddDialog.value = false
    loadData()
  }).catch(() => {})
}

function delRecord(id) {
  uni.showModal({ title: '确认删除', content: '确定要删除这条记录吗？' }).then(res => {
    if (res.confirm) deletePeriodRecord(id).then(() => { loadData() })
  })
}

function calcPrediction() {
  calculatePrediction().then(() => {
    uni.showToast({ title: '预测已更新', icon: 'success' })
    getPrediction().then(data => { prediction.value = data }).catch(() => {})
  }).catch(() => {})
}

function startPregnancy() {
  if (!pregForm.value.lastPeriodStart) { uni.showToast({ title: '请选择末次月经日期', icon: 'none' }); return }
  addPregnancyRecord({ lastPeriodStart: pregForm.value.lastPeriodStart }).then(() => {
    uni.showToast({ title: '怀孕记录已创建', icon: 'success' })
    loadData()
  }).catch(() => {})
}

function confirmBabyBorn() {
  if (!babyForm.value.birthDate) { uni.showToast({ title: '请选择出生日期', icon: 'none' }); return }
  babyBorn(babyForm.value).then(() => {
    uni.showToast({ title: '已记录宝宝出生 🎉', icon: 'success' })
    showBabyDialog.value = false
    mode.value = 'period'
    loadData()
  }).catch(() => {})
}

function loadData() {
  getPeriodRecords().then(data => { records.value = data || [] }).catch(() => {})
  getPrediction().then(data => { prediction.value = data }).catch(() => {})
  getPregnancyRecord().then(data => { pregnancyRecord.value = data }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.period-page { padding: 24rpx; background: #F5F5F5; min-height: 100vh; }
.mode-tabs { display: flex; background: white; border-radius: 16rpx; padding: 8rpx; margin-bottom: 24rpx; }
.tab { flex: 1; text-align: center; padding: 20rpx 0; border-radius: 12rpx; font-size: 28rpx; color: #666; }
.tab.active { background: #E91E63; color: white; }
.card { background: white; border-radius: 16rpx; padding: 28rpx; margin-bottom: 24rpx; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.card-title { font-size: 32rpx; font-weight: 600; }
.add-btn, .refresh-btn { background: #E91E63; color: white; border: none; border-radius: 32rpx; font-size: 24rpx; padding: 12rpx 24rpx; }
.tips { color: #CCC; font-size: 28rpx; text-align: center; padding: 20rpx 0; }
.record-item { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0; border-bottom: 1px solid #F5F5F5; }
.record-date { font-size: 28rpx; font-weight: 500; }
.record-info { display: flex; gap: 12rpx; align-items: center; }
.flow-tag { background: #FCE4EC; color: #E91E63; border-radius: 8rpx; padding: 4rpx 12rpx; font-size: 22rpx; }
.del-btn { color: #FF5252; font-size: 26rpx; }
.pred-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
.pred-item { background: #FFF5F7; border-radius: 12rpx; padding: 20rpx; flex: 1; min-width: 45%; text-align: center; }
.pred-label { display: block; font-size: 24rpx; color: #999; margin-bottom: 8rpx; }
.pred-val { display: block; font-size: 28rpx; font-weight: 600; color: #E91E63; }
.pred-highlight { color: #C2185B; font-size: 32rpx; }
.preg-stats { display: flex; gap: 24rpx; margin-bottom: 24rpx; }
.preg-stat { flex: 1; background: linear-gradient(135deg, #E8F5E9, #C8E6C9); border-radius: 16rpx; padding: 24rpx 0; text-align: center; }
.preg-num { display: block; font-size: 48rpx; font-weight: bold; color: #2E7D32; }
.preg-label { display: block; font-size: 24rpx; color: #666; margin-top: 4rpx; }
.born-btn { background: #4CAF50; color: white; border: none; border-radius: 48rpx; padding: 24rpx; font-size: 32rpx; }
.preg-form { margin-top: 20rpx; }
.form-label { display: block; font-size: 28rpx; color: #666; margin-bottom: 8rpx; margin-top: 16rpx; }
.picker-val { background: #F5F5F5; border-radius: 8rpx; padding: 20rpx 24rpx; font-size: 28rpx; }
.start-btn { background: #E91E63; color: white; border: none; border-radius: 48rpx; margin-top: 32rpx; padding: 24rpx; }
.dialog-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; }
.dialog { background: white; border-radius: 24rpx; width: 600rpx; padding: 40rpx; }
.dialog-title { display: block; text-align: center; font-size: 34rpx; font-weight: 600; margin-bottom: 32rpx; }
.dialog-body { margin-bottom: 32rpx; }
.form-row { margin-bottom: 24rpx; }
.form-row input { background: #F5F5F5; border-radius: 8rpx; padding: 20rpx 24rpx; font-size: 28rpx; margin-top: 8rpx; }
.dialog-footer { display: flex; gap: 24rpx; }
.dialog-footer button { flex: 1; border-radius: 48rpx; padding: 24rpx; font-size: 30rpx; }
.confirm-btn { background: #E91E63; color: white; border: none; }
</style>
