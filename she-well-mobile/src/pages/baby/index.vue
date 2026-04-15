<template>
  <view class="baby-page">
    <view class="header">
      <view class="header-title">🍼 宝宝成长</view>
      <view class="add-baby-btn" @click="showAddDialog = true">+ 添加宝宝</view>
    </view>

    <!-- 宝宝卡片 -->
    <view class="baby-cards">
      <view v-for="baby in babies" :key="baby.id" class="baby-card" @click="selectBaby(baby)">
        <view class="baby-avatar">{{ baby.avatar || '👶' }}</view>
        <view class="baby-info">
          <view class="baby-name">{{ baby.name }}</view>
          <view class="baby-age">{{ babyAge(baby.birthDate) }}</view>
          <view class="baby-birth">生日：{{ baby.birthDate }}</view>
        </view>
        <view class="baby-actions">
          <view class="action-icon" @click.stop="addMilestone(baby)">📝</view>
          <view class="action-icon" @click.stop="addGrowth(baby)">📊</view>
        </view>
      </view>
      <view v-if="!babies.length" class="empty-tip">还没有宝宝信息，点击上方添加</view>
    </view>

    <!-- 发育记录 -->
    <view class="section">
      <view class="section-title">📈 发育记录</view>
      <view class="growth-table">
        <view class="growth-header">
          <text>日期</text><text>体重(kg)</text><text>身高(cm)</text>
        </view>
        <view v-for="g in growthRecords" :key="g.id" class="growth-row">
          <text>{{ g.recordDate?.slice(0, 7) || g.date }}</text>
          <text>{{ g.weight || '—' }}</text>
          <text>{{ g.height || '—' }}</text>
        </view>
        <view v-if="!growthRecords.length" class="empty-tip">暂无发育记录</view>
      </view>
      <view class="add-growth-btn" @click="addGrowth(null)">+ 记录发育</view>
    </view>

    <!-- 里程碑 -->
    <view class="section">
      <view class="section-title">🌟 成长里程碑</view>
      <view class="milestone-list">
        <view v-for="m in milestones" :key="m.id" class="milestone-item">
          <view class="ms-icon">{{ m.icon || '⭐' }}</view>
          <view class="ms-info">
            <view class="ms-title">{{ m.title }}</view>
            <view class="ms-date">{{ m.milestoneDate || m.recordDate }}</view>
          </view>
        </view>
        <view v-if="!milestones.length" class="empty-tip">暂无里程碑</view>
      </view>
      <view class="add-ms-btn" @click="addMilestone(null)">+ 添加里程碑</view>
    </view>

    <!-- AI 发育建议 -->
    <view class="section ai-section">
      <view class="section-title">🤖 AI 发育建议</view>
      <view class="ai-advice" v-if="aiAdvice">{{ aiAdvice }}</view>
      <view class="ai-loading" v-else-if="aiLoading">正在生成...</view>
      <view class="empty-tip" v-else>根据宝宝月龄获取科学发育建议</view>
      <view class="ai-btn" @click="getBabyAdvice" :class="{disabled: aiLoading || !selectedBaby}">
        {{ aiLoading ? '加载中...' : '获取 AI 发育建议' }}
      </view>
    </view>

    <!-- 添加宝宝弹窗 -->
    <view class="dialog-overlay" v-if="showAddDialog" @click="showAddDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">添加宝宝</view>
        <input class="dialog-input" v-model="babyForm.name" placeholder="宝宝姓名" />
        <input class="dialog-input" v-model="babyForm.birthDate" type="date" placeholder="出生日期" />
        <input class="dialog-input" v-model="babyForm.gender" placeholder="性别（可选：男/女）" />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showAddDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitBaby">保存</view>
        </view>
      </view>
    </view>

    <!-- 添加里程碑弹窗 -->
    <view class="dialog-overlay" v-if="showMilestoneDialog" @click="showMilestoneDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">添加里程碑</view>
        <input class="dialog-input" v-model="milestoneForm.title" placeholder="里程碑名称（如：第一次微笑）" />
        <input class="dialog-input" v-model="milestoneForm.date" type="date" placeholder="日期" />
        <view class="icon-picker">
          <text class="icon-label">选择图标：</text>
          <view class="icon-list">
            <text v-for="icon in iconOptions" :key="icon" class="icon-opt" :class="{selected: milestoneForm.icon === icon}" @click="milestoneForm.icon = icon">{{ icon }}</text>
          </view>
        </view>
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showMilestoneDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitMilestone">保存</view>
        </view>
      </view>
    </view>

    <!-- 添加发育弹窗 -->
    <view class="dialog-overlay" v-if="showGrowthDialog" @click="showGrowthDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">记录发育</view>
        <input class="dialog-input" v-model="growthForm.date" type="date" placeholder="日期" />
        <input class="dialog-input" v-model="growthForm.weight" type="digit" placeholder="体重（kg，如 5.6）" />
        <input class="dialog-input" v-model="growthForm.height" type="digit" placeholder="身高（cm，如 60）" />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showGrowthDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitGrowth">保存</view>
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
    }))
    if (babies.value.length) selectedBaby.value = babies.value[0]
  } catch (e) {
    console.error('loadData error', e)
  }
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
  } catch (e) { uni.showToast({ title: '添加失败', icon: 'none' }) }
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
  } catch (e) {
    aiAdvice.value = '获取建议失败，请稍后再试。'
  } finally {
    aiLoading.value = false
  }
}

function addMilestone(baby) { showMilestoneDialog.value = true }
function addGrowth(baby) { showGrowthDialog.value = true }

onMounted(loadData)
</script>

<style scoped>
.baby-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #2196F3, #90CAF9); padding: 32rpx; color: #fff; display: flex; justify-content: space-between; align-items: center }
.header-title { font-size: 36rpx; font-weight: bold }
.add-baby-btn { font-size: 28rpx; background: rgba(255,255,255,0.25); padding: 8rpx 20rpx; border-radius: 20rpx }
.baby-cards { padding: 24rpx 32rpx }
.baby-card { background: #fff; border-radius: 20rpx; padding: 24rpx; display: flex; align-items: center; gap: 20rpx; margin-bottom: 16rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06) }
.baby-avatar { font-size: 64rpx }
.baby-info { flex: 1 }
.baby-name { font-size: 32rpx; font-weight: 600; color: #333 }
.baby-age { font-size: 26rpx; color: #2196F3; margin-top: 4rpx }
.baby-birth { font-size: 24rpx; color: #999; margin-top: 2rpx }
.baby-actions { display: flex; gap: 12rpx }
.action-icon { font-size: 36rpx; padding: 8rpx }
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.growth-table { background: #faf8fb; border-radius: 12rpx; overflow: hidden; margin-bottom: 16rpx }
.growth-header { display: flex; background: #eee; padding: 12rpx 16rpx; font-size: 24rpx; color: #666; font-weight: 600 }
.growth-row { display: flex; padding: 12rpx 16rpx; font-size: 26rpx; border-bottom: 1rpx solid #f0f0f0 }
.growth-row text { flex: 1; text-align: center }
.growth-row:last-child { border-bottom: none }
.milestone-list { display: flex; flex-direction: column; gap: 12rpx; margin-bottom: 16rpx }
.milestone-item { display: flex; gap: 16rpx; align-items: center; padding: 12rpx; background: #faf8fb; border-radius: 12rpx }
.ms-icon { font-size: 40rpx }
.ms-title { font-size: 28rpx; font-weight: 500; color: #333 }
.ms-date { font-size: 24rpx; color: #999 }
.ai-section .ai-advice { background: #faf8fb; border-radius: 12rpx; padding: 20rpx; font-size: 26rpx; color: #555; line-height: 1.6; margin-bottom: 16rpx }
.ai-loading { text-align: center; color: #2196F3; font-size: 26rpx; padding: 20rpx }
.ai-btn { text-align: center; background: linear-gradient(135deg, #2196F3, #90CAF9); color: #fff; padding: 20rpx; border-radius: 12rpx; font-size: 28rpx }
.ai-btn.disabled { opacity: 0.6 }
.add-growth-btn, .add-ms-btn { text-align: center; color: #2196F3; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #90CAF9; border-radius: 12rpx }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 24rpx }
/* dialog */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center }
.dialog-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 560rpx }
.dialog-title { font-size: 32rpx; font-weight: bold; text-align: center; margin-bottom: 24rpx }
.dialog-input { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; box-sizing: border-box }
.icon-picker { margin-bottom: 16rpx }
.icon-label { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx }
.icon-list { display: flex; flex-wrap: wrap; gap: 8rpx }
.icon-opt { font-size: 36rpx; padding: 6rpx 10rpx; border-radius: 8rpx; border: 2rpx solid transparent }
.icon-opt.selected { border-color: #2196F3; background: #e3f2fd }
.dialog-btns { display: flex; gap: 20rpx; margin-top: 8rpx }
.dialog-cancel { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 10rpx; color: #999; font-size: 28rpx }
.dialog-confirm { flex: 1; text-align: center; padding: 16rpx; background: linear-gradient(135deg, #2196F3, #90CAF9); border-radius: 10rpx; color: #fff; font-size: 28rpx }
</style>
