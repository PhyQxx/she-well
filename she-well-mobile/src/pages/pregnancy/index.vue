<template>
  <view class="pregnancy-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">🤰</text>
        <view class="header-text">
          <text class="header-title">怀孕日记</text>
          <text class="header-sub">{{ pregWeek }}周 {{ pregDay }}天</text>
        </view>
      </view>
    </view>

    <!-- 孕周进度卡片 -->
    <view class="progress-card">
      <view class="progress-left">
        <view class="week-ring">
          <view class="week-num">{{ pregWeek }}</view>
          <view class="week-label">孕周</view>
        </view>
        <view class="week-info">
          <view class="edd-row">
            <text class="info-label">预产期</text>
            <text class="info-value">{{ edd || '未设置' }}</text>
          </view>
          <view class="days-row">
            <text v-if="daysToEdd > 0" class="days-badge upcoming">距预产期还有 {{ daysToEdd }} 天</text>
            <text v-else-if="daysToEdd === 0" class="days-badge today">🎉 今天就是预产期！</text>
            <text v-else class="days-badge overdue">已超过预产期 {{ Math.abs(daysToEdd) }} 天</text>
          </view>
        </view>
      </view>
      <view class="trimester-tabs">
        <view
          v-for="t in trimesters"
          :key="t.key"
          class="trimester-tab"
          :class="{ active: currentTrimester === t.key }"
          @click="currentTrimester = t.key"
        >
          <text>{{ t.label }}</text>
        </view>
      </view>
    </view>

    <!-- 产检记录 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">🏥 产检记录</text>
        <text class="add-btn" @click="showCheckupDialog = true">+ 添加</text>
      </view>

      <view v-if="checkupHistory.length === 0" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无产检记录</text>
      </view>

      <view v-else class="checkup-list">
        <view v-for="c in checkupHistory" :key="c.id" class="checkup-item">
          <view class="checkup-date-col">
            <text class="checkup-date">{{ c.checkupDate?.slice(5) || '' }}</text>
          </view>
          <view class="checkup-info-col">
            <text class="checkup-name">{{ c.checkupItem || '常规产检' }}</text>
            <text v-if="c.hospital" class="checkup-hospital">{{ c.hospital }}</text>
          </view>
          <view class="checkup-result-badge" :class="'result-' + (c.result === '异常' ? 'abnormal' : 'normal')">
            {{ c.result || '正常' }}
          </view>
        </view>
      </view>
    </view>

    <!-- 孕期日记 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">📝 孕期日记</text>
        <text class="add-btn" @click="showDiaryDialog = true">✏️ 写日记</text>
      </view>

      <view v-if="diaries.length === 0" class="empty-state">
        <text class="empty-icon">📖</text>
        <text class="empty-text">开始记录你的孕期故事吧~</text>
      </view>

      <view v-else class="diary-list">
        <view v-for="d in diaries" :key="d.id" class="diary-item">
          <view class="diary-header">
            <text class="diary-date">{{ d.diaryDate || d.recordDate }}</text>
          </view>
          <text class="diary-content">{{ d.content || d.notes }}</text>
        </view>
      </view>
    </view>

    <!-- 宝宝发育 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">👶 宝宝发育</text>
      </view>

      <view class="baby-stats">
        <view class="baby-stat">
          <text class="stat-icon">📏</text>
          <text class="stat-val">{{ babyLength || '--' }}</text>
          <text class="stat-unit">cm</text>
          <text class="stat-label">身长</text>
        </view>
        <view class="baby-stat">
          <text class="stat-icon">⚖️</text>
          <text class="stat-val">{{ babyWeight || '--' }}</text>
          <text class="stat-unit">g</text>
          <text class="stat-label">体重</text>
        </view>
      </view>

      <view class="baby-desc" v-if="babyDesc">{{ babyDesc }}</view>
      <view class="baby-placeholder" v-else>点击按钮获取本周宝宝发育信息</view>

      <view class="ai-fetch-btn" @click="getBabyAdvice" :class="{ loading: aiLoading }">
        <text v-if="aiLoading">🤖 加载中...</text>
        <text v-else>🤖 获取本周宝宝发育指导</text>
      </view>
    </view>

    <!-- 日记弹窗 -->
    <view class="dialog-overlay" v-if="showDiaryDialog" @click="showDiaryDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showDiaryDialog = false">取消</text>
          <text class="sheet-title">写日记</text>
          <text class="sheet-confirm" @click="submitDiary">保存</text>
        </view>
        <textarea
          class="sheet-textarea"
          v-model="diaryForm.content"
          placeholder="今天发生了什么..."
          maxlength="500"
        />
      </view>
    </view>

    <!-- 产检弹窗 -->
    <view class="dialog-overlay" v-if="showCheckupDialog" @click="showCheckupDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showCheckupDialog = false">取消</text>
          <text class="sheet-title">添加产检</text>
          <text class="sheet-confirm" @click="submitCheckup">保存</text>
        </view>
        <view class="sheet-form">
          <input class="sheet-input" v-model="checkupForm.checkupDate" type="date" placeholder="产检日期" />
          <input class="sheet-input" v-model="checkupForm.checkupItem" placeholder="产检项目（如：唐筛）" />
          <input class="sheet-input" v-model="checkupForm.hospital" placeholder="医院（可选）" />
          <input class="sheet-input" v-model="checkupForm.result" placeholder="检查结果（可选）" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { pregnancy, ai } from '@/api/index.js'

const trimesters = [
  { key: 'first', label: '孕早期' },
  { key: 'second', label: '孕中期' },
  { key: 'third', label: '孕晚期' },
]

const pregWeek = ref(0)
const pregDay = ref(0)
const edd = ref('')
const daysToEdd = ref(0)
const currentTrimester = ref('first')
const checkupHistory = ref([])
const diaries = ref([])
const babyLength = ref('')
const babyWeight = ref('')
const babyDesc = ref('')
const aiLoading = ref(false)
const showDiaryDialog = ref(false)
const showCheckupDialog = ref(false)
const diaryForm = ref({ content: '' })
const checkupForm = ref({ checkupDate: '', checkupItem: '', hospital: '', result: '' })

async function loadData() {
  try {
    const [recordRes, diaryRes] = await Promise.all([
      pregnancy.getRecord(),
      pregnancy.diaryList(),
    ])
    const record = recordRes?.data || recordRes
    if (record) {
      const lmp = record.lastMenstrualPeriod || record.lmpDate
      if (lmp) {
        const today = new Date()
        const lmpDate = new Date(lmp)
        const diffDays = Math.floor((today - lmpDate) / 86400000)
        pregWeek.value = Math.floor(diffDays / 7)
        pregDay.value = diffDays % 7
        const eddDate = new Date(lmpDate.getTime() + 279 * 86400000)
        edd.value = eddDate.toISOString().slice(0, 10)
        daysToEdd.value = Math.ceil((eddDate - today) / 86400000)
      } else {
        pregWeek.value = record.pregWeek || 0
        pregDay.value = record.pregDay || 0
        edd.value = record.expectedDueDate || record.edd || ''
        if (edd.value) daysToEdd.value = Math.ceil((new Date(edd.value) - new Date()) / 86400000)
      }
      if (pregWeek.value <= 12) currentTrimester.value = 'first'
      else if (pregWeek.value <= 28) currentTrimester.value = 'second'
      else currentTrimester.value = 'third'
    }
    diaries.value = (diaryRes?.data || diaryRes || []).map(d => ({
      id: d.id,
      diaryDate: d.diaryDate || d.recordDate,
      content: d.content || d.notes,
    }))
  } catch (e) { console.error('loadData error', e) }
}

async function getBabyAdvice() {
  if (aiLoading.value) return
  aiLoading.value = true
  try {
    const res = await ai.healthAdvice({ type: 'pregnancy', week: pregWeek.value })
    const text = res?.data?.advice || res?.advice || ''
    const sizeMatch = text.match(/身长[:：]?\s*(\d+\.?\d*)\s*cm/i)
    const weightMatch = text.match(/体重[:：]?\s*(\d+\.?\d*)\s*[g克]/i)
    if (sizeMatch) babyLength.value = sizeMatch[1]
    if (weightMatch) babyWeight.value = weightMatch[1].replace('g', '')
    babyDesc.value = text
  } catch {
    babyDesc.value = '获取宝宝发育信息失败，请稍后再试。'
  } finally {
    aiLoading.value = false
  }
}

async function submitDiary() {
  if (!diaryForm.value.content.trim()) { uni.showToast({ title: '请输入内容', icon: 'none' }); return }
  try {
    await pregnancy.createDiary({ content: diaryForm.value.content })
    uni.showToast({ title: '保存成功', icon: 'success' })
    showDiaryDialog.value = false
    diaryForm.value.content = ''
    const res = await pregnancy.diaryList()
    diaries.value = (res?.data || res || []).map(d => ({ id: d.id, diaryDate: d.diaryDate || d.recordDate, content: d.content || d.notes }))
  } catch { uni.showToast({ title: '保存失败', icon: 'none' }) }
}

async function submitCheckup() {
  if (!checkupForm.value.checkupDate) { uni.showToast({ title: '请选择日期', icon: 'none' }); return }
  try {
    await pregnancy.createDiary({
      content: `【产检】${checkupForm.value.checkupItem || '常规检查'} | ${checkupForm.value.hospital || ''} | 结果：${checkupForm.value.result || '正常'}`,
      diaryDate: checkupForm.value.checkupDate
    })
    uni.showToast({ title: '添加成功', icon: 'success' })
    showCheckupDialog.value = false
    checkupForm.value = { checkupDate: '', checkupItem: '', hospital: '', result: '' }
    checkupHistory.value.unshift({ id: Date.now(), ...checkupForm.value })
  } catch { uni.showToast({ title: '添加失败', icon: 'none' }) }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.pregnancy-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 260rpx;
  background: $she-gradient-pregnant;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0; left: 0; right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.header-icon { font-size: 64rpx; }

.header-text { display: flex; flex-direction: column; }

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
}

.header-sub {
  font-size: 26rpx;
  color: rgba(255,255,255,0.85);
  margin-top: 4rpx;
}

// 进度卡片
.progress-card {
  background: $she-white;
  margin: -80rpx 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: $she-shadow-md;
  position: relative;
  z-index: 1;
}

.progress-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 20rpx;
}

.week-ring {
  width: 140rpx;
  height: 140rpx;
  border-radius: 70rpx;
  background: $she-gradient-pregnant;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.week-num {
  font-size: 48rpx;
  font-weight: 700;
}

.week-label {
  font-size: 22rpx;
  opacity: 0.85;
}

.week-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.edd-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.info-label {
  font-size: 24rpx;
  color: $she-muted;
}

.info-value {
  font-size: 28rpx;
  font-weight: 600;
  color: $she-text;
}

.days-badge {
  display: inline-block;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;

  &.upcoming { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
  &.today { background: $she-gradient-primary; color: #fff; }
  &.overdue { background: rgba($she-danger, 0.1); color: $she-danger; }
}

// 孕周tabs
.trimester-tabs {
  display: flex;
  background: $she-bg;
  border-radius: 16rpx;
  padding: 6rpx;
}

.trimester-tab {
  flex: 1;
  text-align: center;
  padding: 14rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: $she-muted;
  transition: all 0.2s ease;

  &.active {
    background: $she-pregnant;
    color: #fff;
  }
}

// Section卡片
.section-card {
  background: $she-white;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $she-shadow-md;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $she-title;
}

.add-btn {
  font-size: 26rpx;
  color: $she-pregnant;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0;
  gap: 12rpx;
}

.empty-icon { font-size: 56rpx; opacity: 0.5; }
.empty-text { font-size: 26rpx; color: $she-muted; }

// 产检列表
.checkup-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.checkup-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background: $she-bg;
  border-radius: 16rpx;
  transition: all 0.2s ease;

  &:active {
    background: rgba($she-pregnant, 0.06);
  }
}

.checkup-date-col {
  min-width: 100rpx;
}

.checkup-date {
  font-size: 26rpx;
  font-weight: 600;
  color: $she-pregnant;
}

.checkup-info-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.checkup-name {
  font-size: 28rpx;
  color: $she-text;
}

.checkup-hospital {
  font-size: 24rpx;
  color: $she-muted;
}

.checkup-result-badge {
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;

  &.result-normal { background: rgba($she-pregnant, 0.1); color: $she-pregnant; }
  &.result-abnormal { background: rgba($she-warning, 0.1); color: $she-warning; }
}

// 日记列表
.diary-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.diary-item {
  padding: 20rpx;
  background: $she-bg;
  border-radius: 16rpx;
}

.diary-header {
  margin-bottom: 8rpx;
}

.diary-date {
  font-size: 24rpx;
  color: $she-muted;
}

.diary-content {
  font-size: 28rpx;
  color: $she-text;
  line-height: 1.6;
}

// 宝宝统计
.baby-stats {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.baby-stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx;
  background: rgba($she-pregnant, 0.06);
  border-radius: 20rpx;
  gap: 4rpx;
}

.stat-icon { font-size: 36rpx; }

.stat-val {
  font-size: 40rpx;
  font-weight: 700;
  color: $she-pregnant;
  line-height: 1.1;
}

.stat-unit {
  font-size: 22rpx;
  color: $she-muted;
}

.stat-label {
  font-size: 24rpx;
  color: $she-sub;
  margin-top: 4rpx;
}

.baby-desc {
  font-size: 26rpx;
  color: $she-text;
  line-height: 1.7;
  padding: 16rpx;
  background: $she-bg;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
}

.baby-placeholder {
  text-align: center;
  font-size: 26rpx;
  color: $she-muted;
  padding: 16rpx 0;
}

.ai-fetch-btn {
  width: 100%;
  text-align: center;
  padding: 20rpx;
  background: $she-gradient-pregnant;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
  transition: all 0.2s ease;

  &:active { transform: scale(0.98); }
  &.loading { opacity: 0.6; }
}

// 弹窗
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.dialog-sheet {
  width: 100%;
  background: $she-white;
  border-radius: 32rpx 32rpx 0 0;
  padding: 0 0 env(safe-area-inset-bottom);
  animation: slideUp 0.3s ease forwards;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid $she-border;
}

.sheet-cancel, .sheet-confirm {
  font-size: 30rpx;
  min-width: 100rpx;
}

.sheet-cancel { color: $she-muted; }
.sheet-title { font-size: 32rpx; font-weight: 600; color: $she-text; }
.sheet-confirm { color: $she-pregnant; text-align: right; }

.sheet-textarea {
  width: 100%;
  min-height: 300rpx;
  padding: 24rpx;
  font-size: 30rpx;
  color: $she-text;
  line-height: 1.6;
  box-sizing: border-box;
}

.sheet-form {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.sheet-input {
  border: 1rpx solid $she-border;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: $she-text;
}
</style>
