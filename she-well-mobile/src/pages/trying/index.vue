<template>
  <view class="trying-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">🌈</text>
        <view class="header-text">
          <text class="header-title">备孕中心</text>
          <text class="header-sub">科学备孕，好孕来</text>
        </view>
      </view>
    </view>

    <!-- 排卵追踪卡片 -->
    <view class="ovu-card">
      <view class="ovu-left">
        <view class="ovu-label">🎯 排卵日</view>
        <view class="ovu-date">{{ ovulationDate || '—' }}</view>
        <view class="ovu-status-badge" :class="ovuStatusClass">{{ ovuStatusText }}</view>
      </view>
      <view class="ovu-right">
        <view v-if="daysToOvulation > 0" class="countdown-display">
          <text class="countdown-num">{{ daysToOvulation }}</text>
          <text class="countdown-label">天后</text>
        </view>
        <view v-else-if="daysToOvulation === 0" class="countdown-display today">
          <text class="countdown-num">今</text>
          <text class="countdown-label">天排卵</text>
        </view>
        <view v-else class="countdown-display passed">
          <text class="countdown-num">{{ Math.abs(daysToOvulation) }}</text>
          <text class="countdown-label">天前</text>
        </view>
      </view>
    </view>

    <!-- LH试纸记录 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">🧪 排卵试纸</text>
        <text class="add-btn" @click="addStripRecord">+ 记录</text>
      </view>

      <view v-if="stripRecords.length === 0" class="empty-state">
        <text class="empty-icon">🧪</text>
        <text class="empty-text">暂无试纸记录</text>
      </view>

      <scroll-view class="strip-scroll" scroll-x v-else>
        <view class="strip-row">
          <view v-for="s in stripRecords" :key="s.id" class="strip-chip" :class="'chip-' + s.result">
            <text class="chip-date">{{ s.recordDate?.slice(5) || '' }}</text>
            <text class="chip-result">{{ s.resultText }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 同房记录 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">💕 同房记录</text>
        <text class="add-btn" @click="showIcDialog = true">+ 添加</text>
      </view>

      <view v-if="icRecords.length === 0" class="empty-state">
        <text class="empty-icon">💕</text>
        <text class="empty-text">暂无同房记录，点击添加</text>
      </view>

      <view v-else class="ic-list">
        <view v-for="r in icRecords" :key="r.id" class="ic-item">
          <view class="ic-date-col">
            <text class="ic-date">{{ r.recordDate?.slice(5) || r.recordDate }}</text>
          </view>
          <text class="ic-note">{{ r.notes || '—' }}</text>
          <text class="ic-delete" @click.stop="deleteIc(r.id)">删除</text>
        </view>
      </view>
    </view>

    <!-- 基础体温 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">🌡️ 基础体温</text>
        <text class="add-btn" @click="showBbtDialog = true">+ 记录</text>
      </view>

      <view v-if="bbtRecords.length === 0" class="empty-state">
        <text class="empty-icon">🌡️</text>
        <text class="empty-text">暂无体温数据</text>
      </view>

      <view v-else class="bbt-chart">
        <view class="bbt-grid">
          <view v-for="b in bbtRecords.slice(-7)" :key="b.recordDate" class="bbt-dot-col">
            <text class="bbt-temp">{{ b.temperature }}°</text>
            <view class="bbt-bar" :style="{ height: ((b.temperature - 35.5) * 80) + 'rpx' }"></view>
            <text class="bbt-date">{{ b.recordDate?.slice(5) || '' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- AI 建议 -->
    <view class="section-card ai-card">
      <view class="section-header">
        <text class="section-title">🤖 AI 备孕建议</text>
      </view>

      <view v-if="aiAdvice" class="ai-content">{{ aiAdvice }}</view>
      <view v-else-if="aiLoading" class="ai-loading">正在生成建议...</view>
      <view v-else class="empty-state">
        <text class="empty-icon">🤖</text>
        <text class="empty-text">点击按钮获取个性化备孕建议</text>
      </view>

      <view class="ai-btn" @click="getAdvice" :class="{ disabled: aiLoading }">
        {{ aiLoading ? '生成中...' : '获取 AI 建议' }}
      </view>
    </view>

    <!-- 生育力评估入口 -->
    <view class="fertility-entry" @click="goFertility">
      <view class="fertility-left">
        <text class="fertility-icon">🌈</text>
        <view class="fertility-info">
          <text class="fertility-title">生育力评估</text>
          <text class="fertility-sub">了解你的生育能力，科学备孕</text>
        </view>
      </view>
      <text class="fertility-arrow">›</text>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 同房弹窗 -->
    <view class="dialog-overlay" v-if="showIcDialog" @click="showIcDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showIcDialog = false">取消</text>
          <text class="sheet-title">记录同房</text>
          <text class="sheet-confirm" @click="submitIc">保存</text>
        </view>
        <view class="sheet-form">
          <input class="sheet-input" v-model="icForm.date" type="date" placeholder="选择日期" />
          <input class="sheet-input" v-model="icForm.notes" placeholder="备注（可选，如晚上/上午）" />
        </view>
      </view>
    </view>

    <!-- 体温弹窗 -->
    <view class="dialog-overlay" v-if="showBbtDialog" @click="showBbtDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showBbtDialog = false">取消</text>
          <text class="sheet-title">记录体温</text>
          <text class="sheet-confirm" @click="submitBbt">保存</text>
        </view>
        <view class="sheet-form">
          <input class="sheet-input" v-model="bbtForm.date" type="date" placeholder="选择日期" />
          <input class="sheet-input" v-model="bbtForm.temperature" type="digit" placeholder="体温（如 36.5）" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { bbt, ovulation, intercourse, ai } from '@/api/index.js'

const ovulationDate = ref('')
const daysToOvulation = ref(0)
const ovuStatusClass = ref('')
const ovuStatusText = ref('计算中...')
const stripRecords = ref([])
const icRecords = ref([])
const bbtRecords = ref([])
const aiAdvice = ref('')
const aiLoading = ref(false)
const showIcDialog = ref(false)
const showBbtDialog = ref(false)
const icForm = ref({ date: '', notes: '' })
const bbtForm = ref({ date: '', temperature: '' })

async function loadData() {
  try {
    const [ovuList, icList, bbtList] = await Promise.all([
      ovulation.list(),
      intercourse.list(),
      bbt.trend(14),
    ])
    const today = new Date()
    const records = (ovuList?.data || ovuList || []).sort((a, b) => new Date(b.recordDate) - new Date(a.recordDate))
    if (records.length) {
      const next = records.find(r => new Date(r.recordDate) > today)
      const last = records[0]
      if (next) {
        const diff = Math.ceil((new Date(next.recordDate) - today) / 86400000)
        ovulationDate.value = next.recordDate
        daysToOvulation.value = diff
        ovuStatusText.value = `距排卵日 ${diff} 天`
        ovuStatusClass.value = diff <= 2 ? 'status-active' : ''
      } else {
        ovulationDate.value = last.recordDate
        const diff = Math.ceil((today - new Date(last.recordDate)) / 86400000)
        daysToOvulation.value = -diff
        ovuStatusText.value = `已排卵 ${diff} 天前`
        ovuStatusClass.value = 'status-passed'
      }
    }
    stripRecords.value = (ovuList?.data || ovuList || []).map(r => ({
      id: r.id,
      recordDate: r.recordDate,
      result: r.lhResult || 'negative',
      resultText: getStripText(r.lhResult),
    }))
    icRecords.value = (icList?.data || icList || []).map(r => ({
      id: r.id, recordDate: r.recordDate, notes: r.notes || '',
    }))
    bbtRecords.value = (bbtList?.data || bbtList || []).map(r => ({
      id: r.id, recordDate: r.recordDate, temperature: r.temperature,
    }))
  } catch (e) { console.error('loadData error', e) }
}

function getStripText(result) {
  const map = { negative: '阴性(-)', weak: '弱阳性(±)', positive: '阳性(+)', strong: '强阳性(++)' }
  return map[result] || result || '阴性(-)'
}

async function getAdvice() {
  if (aiLoading.value) return
  aiLoading.value = true
  try {
    const res = await ai.healthAdvice({ type: 'trying' })
    aiAdvice.value = res?.data?.advice || res?.advice || '暂无建议，请先完善您的备孕记录。'
  } catch {
    aiAdvice.value = '获取建议失败，请稍后再试。'
  } finally {
    aiLoading.value = false
  }
}

async function submitIc() {
  if (!icForm.value.date) { uni.showToast({ title: '请选择日期', icon: 'none' }); return }
  try {
    await intercourse.create({ recordDate: icForm.value.date, notes: icForm.value.notes })
    uni.showToast({ title: '记录成功', icon: 'success' })
    showIcDialog.value = false
    icForm.value = { date: '', notes: '' }
    const res = await intercourse.list()
    icRecords.value = (res?.data || res || []).map(r => ({ id: r.id, recordDate: r.recordDate, notes: r.notes || '' }))
  } catch { uni.showToast({ title: '记录失败', icon: 'none' }) }
}

async function submitBbt() {
  if (!bbtForm.value.date || !bbtForm.value.temperature) { uni.showToast({ title: '请填写完整', icon: 'none' }); return }
  try {
    await bbt.create({ recordDate: bbtForm.value.date, temperature: parseFloat(bbtForm.value.temperature) })
    uni.showToast({ title: '记录成功', icon: 'success' })
    showBbtDialog.value = false
    bbtForm.value = { date: '', temperature: '' }
    const res = await bbt.trend(14)
    bbtRecords.value = (res?.data || res || []).map(r => ({ id: r.id, recordDate: r.recordDate, temperature: r.temperature }))
  } catch { uni.showToast({ title: '记录失败', icon: 'none' }) }
}

async function deleteIc(id) {
  try {
    await intercourse.delete(id)
    icRecords.value = icRecords.value.filter(r => r.id !== id)
    uni.showToast({ title: '已删除', icon: 'success' })
  } catch { uni.showToast({ title: '删除失败', icon: 'none' }) }
}

function addStripRecord() { uni.showToast({ title: '试纸记录功能开发中', icon: 'none' }) }
function goFertility() { uni.navigateTo({ url: '/pages/fertility/index' }) }

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.trying-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 260rpx;
  background: $she-gradient-trying;
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

// 排卵卡片
.ovu-card {
  background: $she-white;
  margin: -80rpx 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: $she-shadow-md;
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ovu-left { display: flex; flex-direction: column; gap: 6rpx; }

.ovu-label { font-size: 24rpx; color: $she-muted; }
.ovu-date { font-size: 40rpx; font-weight: 700; color: $she-trying; }

.ovu-status-badge {
  display: inline-block;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  background: rgba($she-trying, 0.1);
  color: $she-trying;

  &.status-active { background: #FFD700; color: #333; }
  &.status-passed { background: $she-bg; color: $she-muted; }
}

.ovu-right { display: flex; align-items: center; }

.countdown-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 24rpx;
  background: rgba($she-trying, 0.08);
  border-radius: 16rpx;

  &.today { background: $she-trying; .countdown-num, .countdown-label { color: #fff; } }
  &.passed { opacity: 0.6; }
}

.countdown-num {
  font-size: 52rpx;
  font-weight: 700;
  color: $she-trying;
  line-height: 1;
}

.countdown-label {
  font-size: 22rpx;
  color: $she-muted;
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
  margin-bottom: 16rpx;
}

.section-title { font-size: 30rpx; font-weight: 600; color: $she-title; }

.add-btn { font-size: 26rpx; color: $she-trying; }

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32rpx 0;
  gap: 10rpx;
}

.empty-icon { font-size: 48rpx; opacity: 0.5; }
.empty-text { font-size: 26rpx; color: $she-muted; }

// 试纸滚动
.strip-scroll { width: 100%; }

.strip-row {
  display: flex;
  gap: 12rpx;
  padding-bottom: 8rpx;
}

.strip-chip {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 16rpx 20rpx;
  border-radius: 16rpx;
  background: $she-bg;

  &.chip-negative { background: #f5f5f5; .chip-result { color: $she-muted; } }
  &.chip-weak { background: #FFF3E0; .chip-result { color: $she-warning; } }
  &.chip-positive { background: #FCE4EC; .chip-result { color: $she-primary; } }
  &.chip-strong { background: $she-primary; .chip-result { color: #fff; } }
}

.chip-date { font-size: 22rpx; color: $she-muted; }
.chip-result { font-size: 26rpx; font-weight: 600; }

// 同房列表
.ic-list { display: flex; flex-direction: column; gap: 12rpx; }

.ic-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background: $she-bg;
  border-radius: 12rpx;
}

.ic-date-col { min-width: 100rpx; }
.ic-date { font-size: 26rpx; font-weight: 600; color: $she-trying; }
.ic-note { flex: 1; font-size: 26rpx; color: $she-sub; }
.ic-delete { font-size: 24rpx; color: $she-danger; }

// 体温图
.bbt-chart { padding: 8rpx 0; }

.bbt-grid {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 200rpx;
}

.bbt-dot-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.bbt-temp {
  font-size: 22rpx;
  font-weight: 600;
  color: $she-trying;
}

.bbt-bar {
  width: 40rpx;
  background: rgba($she-trying, 0.2);
  border-radius: 8rpx 8rpx 0 0;
  transition: height 0.3s ease;
}

.bbt-date { font-size: 20rpx; color: $she-muted; }

// AI卡片
.ai-card {
  background: $she-white;
}

.ai-content {
  padding: 20rpx;
  background: $she-bg;
  border-radius: 16rpx;
  font-size: 26rpx;
  color: $she-text;
  line-height: 1.7;
  margin-bottom: 16rpx;
}

.ai-loading {
  text-align: center;
  padding: 20rpx;
  font-size: 26rpx;
  color: $she-trying;
}

.ai-btn {
  width: 100%;
  text-align: center;
  padding: 20rpx;
  background: $she-gradient-trying;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;

  &.disabled { opacity: 0.6; }
}

// 生育力入口
.fertility-entry {
  margin: 0 32rpx 24rpx;
  background: $she-white;
  border-radius: 24rpx;
  padding: 28rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: $she-shadow-md;
}

.fertility-left { display: flex; align-items: center; gap: 20rpx; }
.fertility-icon { font-size: 48rpx; }

.fertility-info { display: flex; flex-direction: column; gap: 4rpx; }

.fertility-title { font-size: 30rpx; font-weight: 600; color: $she-text; }
.fertility-sub { font-size: 24rpx; color: $she-muted; }
.fertility-arrow { font-size: 40rpx; color: $she-border; }

.bottom-placeholder { height: 48rpx; }

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
  padding-bottom: env(safe-area-inset-bottom);
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

.sheet-cancel, .sheet-confirm { font-size: 30rpx; min-width: 100rpx; }
.sheet-cancel { color: $she-muted; }
.sheet-title { font-size: 32rpx; font-weight: 600; color: $she-text; }
.sheet-confirm { color: $she-trying; text-align: right; }

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
