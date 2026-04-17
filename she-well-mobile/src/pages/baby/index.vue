<template>
  <view class="baby-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">👶</text>
        <view class="header-text">
          <text class="header-title">宝宝成长</text>
          <text class="header-sub">记录每一个珍贵时刻</text>
        </view>
      </view>
      <view class="add-baby-btn" @click="showAddDialog = true">+ 添加</view>
    </view>

    <!-- 宝宝选择卡片 -->
    <view class="baby-carousel" v-if="babies.length > 0">
      <scroll-view class="baby-scroll" scroll-x>
        <view class="baby-chips">
          <view
            v-for="b in babies"
            :key="b.id"
            class="baby-chip"
            :class="{ active: selectedBaby?.id === b.id }"
            @click="selectBaby(b)"
          >
            <text class="chip-avatar">{{ b.avatar }}</text>
            <text class="chip-name">{{ b.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 当前宝宝详情 -->
    <view class="current-baby-card" v-if="selectedBaby">
      <view class="baby-avatar-large">{{ selectedBaby.avatar }}</view>
      <view class="baby-detail">
        <view class="baby-name-row">
          <text class="baby-name">{{ selectedBaby.name }}</text>
          <text class="baby-gender-tag" :class="'gender-' + selectedBaby.gender">
            {{ { '女': '👧', '男': '👦' }[selectedBaby.gender] || '👶' }}
          </text>
        </view>
        <view class="baby-age-display">{{ babyAge(selectedBaby.birthDate) }}</view>
        <view class="baby-birthday">生日：{{ selectedBaby.birthDate }}</view>
      </view>
      <view class="baby-actions">
        <text class="action-btn" @click.stop="addMilestone">📝</text>
        <text class="action-btn" @click.stop="addGrowth">📊</text>
      </view>
    </view>

    <!-- 无宝宝提示 -->
    <view class="no-baby-card" v-else>
      <text class="no-baby-icon">👶</text>
      <text class="no-baby-text">还没有宝宝信息</text>
      <text class="no-baby-sub">点击上方「添加」按钮记录宝宝信息</text>
    </view>

    <!-- 发育记录 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">📈 发育记录</text>
        <text class="add-btn" @click="addGrowth">+ 记录</text>
      </view>

      <view v-if="growthRecords.length === 0" class="empty-state">
        <text class="empty-icon">📊</text>
        <text class="empty-text">暂无发育记录</text>
      </view>

      <view v-else class="growth-chart">
        <view class="growth-grid">
          <view class="growth-col">
            <text class="growth-label">日期</text>
            <view v-for="g in growthRecords.slice(-5)" :key="g.id" class="growth-cell date-cell">
              {{ g.recordDate?.slice(0, 7) || g.date }}
            </view>
          </view>
          <view class="growth-col">
            <text class="growth-label">体重/kg</text>
            <view v-for="g in growthRecords.slice(-5)" :key="g.id" class="growth-cell">
              {{ g.weight || '—' }}
            </view>
          </view>
          <view class="growth-col">
            <text class="growth-label">身高/cm</text>
            <view v-for="g in growthRecords.slice(-5)" :key="g.id" class="growth-cell">
              {{ g.height || '—' }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 里程碑 -->
    <view class="section-card">
      <view class="section-header">
        <text class="section-title">🌟 成长里程碑</text>
        <text class="add-btn" @click="addMilestone">+ 添加</text>
      </view>

      <view v-if="milestones.length === 0" class="empty-state">
        <text class="empty-icon">🌟</text>
        <text class="empty-text">暂无里程碑</text>
      </view>

      <view v-else class="milestone-list">
        <view v-for="m in milestones" :key="m.id" class="milestone-item">
          <text class="ms-icon">{{ m.icon || '⭐' }}</text>
          <view class="ms-info">
            <text class="ms-title">{{ m.title }}</text>
            <text class="ms-date">{{ m.milestoneDate || m.recordDate }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- AI 发育建议 -->
    <view class="section-card ai-card">
      <view class="section-header">
        <text class="section-title">🤖 AI 发育建议</text>
      </view>

      <view v-if="aiAdvice" class="ai-content">{{ aiAdvice }}</view>
      <view v-else-if="aiLoading" class="ai-loading">正在生成...</view>
      <view v-else class="empty-state">
        <text class="empty-icon">🤖</text>
        <text class="empty-text">根据宝宝月龄获取科学发育建议</text>
      </view>

      <view class="ai-btn" @click="getBabyAdvice" :class="{ disabled: aiLoading || !selectedBaby }">
        {{ aiLoading ? '加载中...' : '获取 AI 发育建议' }}
      </view>
    </view>

    <view class="bottom-placeholder"></view>

    <!-- 添加宝宝弹窗 -->
    <view class="dialog-overlay" v-if="showAddDialog" @click="showAddDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showAddDialog = false">取消</text>
          <text class="sheet-title">添加宝宝</text>
          <text class="sheet-confirm" @click="submitBaby">保存</text>
        </view>
        <view class="sheet-form">
          <input class="sheet-input" v-model="babyForm.name" placeholder="宝宝姓名" />
          <input class="sheet-input" v-model="babyForm.birthDate" type="date" placeholder="出生日期" />
          <view class="gender-picker">
            <text class="picker-label">性别</text>
            <view class="gender-options">
              <view
                v-for="g in ['男', '女']"
                :key="g"
                class="gender-option"
                :class="{ selected: babyForm.gender === g }"
                @click="babyForm.gender = g"
              >
                <text>{{ g === '女' ? '👧' : '👦' }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 添加里程碑弹窗 -->
    <view class="dialog-overlay" v-if="showMilestoneDialog" @click="showMilestoneDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showMilestoneDialog = false">取消</text>
          <text class="sheet-title">添加里程碑</text>
          <text class="sheet-confirm" @click="submitMilestone">保存</text>
        </view>
        <view class="sheet-form">
          <input class="sheet-input" v-model="milestoneForm.title" placeholder="里程碑名称（如：第一次微笑）" />
          <input class="sheet-input" v-model="milestoneForm.date" type="date" placeholder="日期" />
          <view class="icon-picker">
            <text class="picker-label">选择图标</text>
            <view class="icon-grid">
              <text
                v-for="icon in iconOptions"
                :key="icon"
                class="icon-opt"
                :class="{ selected: milestoneForm.icon === icon }"
                @click="milestoneForm.icon = icon"
              >{{ icon }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 添加发育弹窗 -->
    <view class="dialog-overlay" v-if="showGrowthDialog" @click="showGrowthDialog = false">
      <view class="dialog-sheet" @click.stop>
        <view class="sheet-header">
          <text class="sheet-cancel" @click="showGrowthDialog = false">取消</text>
          <text class="sheet-title">记录发育</text>
          <text class="sheet-confirm" @click="submitGrowth">保存</text>
        </view>
        <view class="sheet-form">
          <input class="sheet-input" v-model="growthForm.date" type="date" placeholder="日期" />
          <input class="sheet-input" v-model="growthForm.weight" type="digit" placeholder="体重（kg，如 5.6）" />
          <input class="sheet-input" v-model="growthForm.height" type="digit" placeholder="身高（cm，如 60）" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { baby, ai } from '@/api/index.js'

const babies = ref([])
const selectedBaby = ref(null)
const growthRecords = ref([])
const milestones = ref([])
const aiAdvice = ref('')
const aiLoading = ref(false)
const showAddDialog = ref(false)
const showMilestoneDialog = ref(false)
const showGrowthDialog = ref(false)
const babyForm = ref({ name: '', birthDate: '', gender: '' })
const milestoneForm = ref({ title: '', date: '', icon: '😊' })
const growthForm = ref({ date: '', weight: '', height: '' })
const iconOptions = ['😊', '🗣️', '🖐️', '🏃', '👋', '🎵', '🎨', '⭐', '🌙', '🚼']

async function loadData() {
  try {
    const res = await baby.list()
    babies.value = (res?.data || res || []).map(b => ({
      id: b.id,
      name: b.name,
      birthDate: b.birthDate,
      avatar: b.gender === '女' ? '👧' : b.gender === '男' ? '👦' : '👶',
      gender: b.gender || '',
    }))
    if (babies.value.length) selectedBaby.value = babies.value[0]
  } catch (e) { console.error('loadData error', e) }
}

function babyAge(birthDate) {
  if (!birthDate) return '—'
  const birth = new Date(birthDate)
  const now = new Date()
  const totalMonths = (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
  const days = Math.floor((now - birth) / 86400000)
  if (days < 30) return `${days}天`
  if (totalMonths < 12) return `${totalMonths}个月`
  const years = Math.floor(totalMonths / 12)
  const months = totalMonths % 12
  return months > 0 ? `${years}岁${months}个月` : `${years}岁`
}

function selectBaby(b) { selectedBaby.value = b }

async function submitBaby() {
  if (!babyForm.value.name || !babyForm.value.birthDate) { uni.showToast({ title: '请填写姓名和生日', icon: 'none' }); return }
  try {
    await baby.create({ name: babyForm.value.name, birthDate: babyForm.value.birthDate, gender: babyForm.value.gender })
    uni.showToast({ title: '添加成功', icon: 'success' })
    showAddDialog.value = false
    babyForm.value = { name: '', birthDate: '', gender: '' }
    await loadData()
  } catch { uni.showToast({ title: '添加失败', icon: 'none' }) }
}

async function submitMilestone() {
  if (!milestoneForm.value.title || !milestoneForm.value.date) { uni.showToast({ title: '请填写完整', icon: 'none' }); return }
  uni.showToast({ title: '保存成功', icon: 'success' })
  showMilestoneDialog.value = false
  milestones.value.unshift({ id: Date.now(), title: milestoneForm.value.title, milestoneDate: milestoneForm.value.date, icon: milestoneForm.value.icon })
  milestoneForm.value = { title: '', date: '', icon: '😊' }
}

async function submitGrowth() {
  if (!growthForm.value.date) { uni.showToast({ title: '请选择日期', icon: 'none' }); return }
  uni.showToast({ title: '记录成功', icon: 'success' })
  showGrowthDialog.value = false
  growthRecords.value.unshift({ id: Date.now(), recordDate: growthForm.value.date, weight: growthForm.value.weight, height: growthForm.value.height })
  growthForm.value = { date: '', weight: '', height: '' }
}

async function getBabyAdvice() {
  if (!selectedBaby.value || aiLoading.value) return
  aiLoading.value = true
  try {
    const res = await ai.healthAdvice({ type: 'baby', babyId: selectedBaby.value.id })
    aiAdvice.value = res?.data?.advice || res?.advice || '暂无建议'
  } catch {
    aiAdvice.value = '获取建议失败，请稍后再试。'
  } finally {
    aiLoading.value = false
  }
}

function addMilestone() { showMilestoneDialog.value = true }
function addGrowth() { showGrowthDialog.value = true }

onMounted(loadData)
</script>

<style lang="scss" scoped>
@import "../../uni.scss";

.baby-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header {
  position: relative;
  padding: 80rpx 32rpx 24rpx;
  background: $she-gradient-nursing;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 16rpx;
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
}

.add-baby-btn {
  position: absolute;
  top: 80rpx;
  right: 32rpx;
  padding: 8rpx 24rpx;
  background: rgba(255,255,255,0.25);
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #fff;
}

// 宝宝选择
.baby-carousel {
  margin: 24rpx 0 0;
  padding: 0 32rpx;
}

.baby-scroll { width: 100%; }

.baby-chips {
  display: flex;
  gap: 16rpx;
}

.baby-chip {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 16rpx 24rpx;
  background: $she-white;
  border-radius: 20rpx;
  box-shadow: $she-shadow-sm;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.active {
    border-color: $she-nursing;
    background: rgba($she-nursing, 0.06);
  }
}

.chip-avatar { font-size: 48rpx; }
.chip-name { font-size: 24rpx; color: $she-sub; }

// 当前宝宝卡片
.current-baby-card {
  margin: 24rpx 32rpx;
  background: $she-white;
  border-radius: 24rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  box-shadow: $she-shadow-md;
}

.baby-avatar-large { font-size: 80rpx; }

.baby-detail { flex: 1; }

.baby-name-row { display: flex; align-items: center; gap: 12rpx; }

.baby-name {
  font-size: 34rpx;
  font-weight: 700;
  color: $she-text;
}

.baby-gender-tag {
  font-size: 28rpx;
}

.gender-tag {
  display: inline-block;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
}

.baby-age-display {
  font-size: 28rpx;
  color: $she-nursing;
  font-weight: 600;
  margin-top: 4rpx;
}

.baby-birthday {
  font-size: 24rpx;
  color: $she-muted;
  margin-top: 2rpx;
}

.baby-actions { display: flex; gap: 12rpx; }

.action-btn {
  font-size: 36rpx;
  padding: 8rpx;
}

// 无宝宝
.no-baby-card {
  margin: 24rpx 32rpx;
  background: $she-white;
  border-radius: 24rpx;
  padding: 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  box-shadow: $she-shadow-md;
}

.no-baby-icon { font-size: 72rpx; opacity: 0.5; }
.no-baby-text { font-size: 30rpx; font-weight: 600; color: $she-title; }
.no-baby-sub { font-size: 26rpx; color: $she-muted; text-align: center; }

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
.add-btn { font-size: 26rpx; color: $she-nursing; }

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

// 发育记录
.growth-chart { overflow-x: auto; }

.growth-grid {
  display: flex;
  background: $she-bg;
  border-radius: 16rpx;
  overflow: hidden;
}

.growth-col {
  flex: 1;
  min-width: 150rpx;
}

.growth-label {
  display: block;
  text-align: center;
  padding: 12rpx;
  background: rgba($she-nursing, 0.1);
  font-size: 22rpx;
  color: $she-nursing;
  font-weight: 600;
}

.growth-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16rpx 8rpx;
  font-size: 26rpx;
  color: $she-text;
  border-bottom: 1rpx solid rgba($she-nursing, 0.06);
  text-align: center;
}

.date-cell {
  font-size: 24rpx;
  color: $she-muted;
}

// 里程碑
.milestone-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.milestone-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background: $she-bg;
  border-radius: 16rpx;
}

.ms-icon { font-size: 40rpx; }

.ms-info { display: flex; flex-direction: column; gap: 4rpx; }

.ms-title { font-size: 28rpx; font-weight: 500; color: $she-text; }
.ms-date { font-size: 24rpx; color: $she-muted; }

// AI卡片
.ai-card { background: $she-white; }

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
  color: $she-nursing;
}

.ai-btn {
  width: 100%;
  text-align: center;
  padding: 20rpx;
  background: $she-gradient-nursing;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;

  &.disabled { opacity: 0.6; }
}

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
.sheet-confirm { color: $she-nursing; text-align: right; }

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

.picker-label {
  font-size: 26rpx;
  color: $she-sub;
  margin-bottom: 8rpx;
}

.gender-picker {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.gender-options { display: flex; gap: 16rpx; }

.gender-option {
  padding: 12rpx 32rpx;
  border-radius: 16rpx;
  background: $she-bg;
  border: 2rpx solid transparent;
  font-size: 36rpx;
  transition: all 0.2s ease;

  &.selected {
    border-color: $she-nursing;
    background: rgba($she-nursing, 0.1);
  }
}

.icon-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.icon-opt {
  font-size: 40rpx;
  padding: 8rpx 12rpx;
  border-radius: 12rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    border-color: $she-nursing;
    background: rgba($she-nursing, 0.1);
  }
}
</style>
