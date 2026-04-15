<template>
  <view class="pregnancy-page">
    <view class="header">
      <view class="header-title">🤱 怀孕日记</view>
    </view>

    <!-- 孕周进度 -->
    <view class="week-card">
      <view class="week-ring">
        <view class="week-num">{{ pregWeek }}</view>
        <view class="week-label">孕周</view>
      </view>
      <view class="week-info">
        <view class="week-detail">第 {{ pregWeek }} 周第 {{ pregDay }} 天</view>
        <view class="edd">预产期：{{ edd || '未设置' }}</view>
        <view class="days-left" v-if="daysToEdd > 0">距预产期还有 {{ daysToEdd }} 天</view>
        <view class="days-left" v-else-if="daysToEdd === 0">今天就是预产期！</view>
        <view class="days-left" v-else>已超过预产期 {{ Math.abs(daysToEdd) }} 天</view>
      </view>
    </view>

    <!-- 产检记录 -->
    <view class="section">
      <view class="section-title">📅 产检记录</view>
      <view class="checkup-list">
        <view v-for="c in checkupHistory" :key="c.id" class="checkup-item">
          <view class="checkup-date">{{ c.checkupDate }}</view>
          <view class="checkup-info">
            <view class="checkup-name">{{ c.checkupItem || '常规产检' }}</view>
            <view class="checkup-hospital" v-if="c.hospital">{{ c.hospital }}</view>
          </view>
          <view class="checkup-result" :class="c.result || 'normal'">{{ c.result || '正常' }}</view>
        </view>
        <view v-if="!checkupHistory.length" class="empty-tip">暂无产检记录</view>
      </view>
      <view class="add-btn" @click="showCheckupDialog = true">+ 添加产检记录</view>
    </view>

    <!-- 孕期日记 -->
    <view class="section">
      <view class="section-title">📝 孕期日记</view>
      <view class="diary-list">
        <view v-for="d in diaries" :key="d.id" class="diary-item">
          <view class="diary-date">{{ d.diaryDate || d.recordDate }}</view>
          <view class="diary-content">{{ d.content || d.notes }}</view>
        </view>
        <view v-if="!diaries.length" class="empty-tip">暂无日记</view>
      </view>
      <view class="write-diary-btn" @click="showDiaryDialog = true">✏️ 写日记</view>
    </view>

    <!-- 宝宝发育 -->
    <view class="section">
      <view class="section-title">👶 宝宝发育</view>
      <view class="baby-info">
        <view class="baby-size">身长约 {{ babyLength }} cm</view>
        <view class="baby-weight">体重约 {{ babyWeight }} g</view>
      </view>
      <view class="baby-desc">{{ babyDesc || '正在获取宝宝发育信息...' }}</view>
      <view class="ai-btn" @click="getBabyAdvice" :class="{disabled: aiLoading}">
        {{ aiLoading ? '加载中...' : '🤖 获取本周宝宝发育指导' }}
      </view>
    </view>

    <!-- 添加日记弹窗 -->
    <view class="dialog-overlay" v-if="showDiaryDialog" @click="showDiaryDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">写日记</view>
        <textarea class="dialog-textarea" v-model="diaryForm.content" placeholder="今天发生了什么..." />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showDiaryDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitDiary">保存</view>
        </view>
      </view>
    </view>

    <!-- 添加产检弹窗 -->
    <view class="dialog-overlay" v-if="showCheckupDialog" @click="showCheckupDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">添加产检</view>
        <input class="dialog-input" v-model="checkupForm.checkupDate" type="date" placeholder="产检日期" />
        <input class="dialog-input" v-model="checkupForm.checkupItem" placeholder="产检项目（如：唐筛）" />
        <input class="dialog-input" v-model="checkupForm.hospital" placeholder="医院（可选）" />
        <input class="dialog-input" v-model="checkupForm.result" placeholder="检查结果（可选）" />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showCheckupDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitCheckup">保存</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { pregnancy, ai } from '@/api/index.js'

const pregWeek = ref(0)
const pregDay = ref(0)
const edd = ref('')
const daysToEdd = ref(0)
const checkupHistory = ref([])
const diaries = ref([])
const babyLength = ref('—')
const babyWeight = ref('—')
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
      // 根据末次月经计算孕周
      const lmp = record.lastMenstrualPeriod || record.lmpDate
      if (lmp) {
        const today = new Date()
        const lmpDate = new Date(lmp)
        const diffDays = Math.floor((today - lmpDate) / 86400000)
        pregWeek.value = Math.floor(diffDays / 7)
        pregDay.value = diffDays % 7
        // 预产期 = LMP + 280天
        const eddDate = new Date(lmpDate.getTime() + 279 * 86400000)
        edd.value = eddDate.toISOString().slice(0, 10)
        daysToEdd.value = Math.ceil((eddDate - today) / 86400000)
      } else {
        pregWeek.value = record.pregWeek || 0
        pregDay.value = record.pregDay || 0
        edd.value = record.expectedDueDate || record.edd || ''
        if (edd.value) {
          const daysLeft = Math.ceil((new Date(edd.value) - new Date()) / 86400000)
          daysToEdd.value = daysLeft
        }
      }
    }
    diaries.value = (diaryRes?.data || diaryRes || []).map(d => ({
      id: d.id,
      diaryDate: d.diaryDate || d.recordDate,
      content: d.content || d.notes,
    }))
  } catch (e) {
    console.error('loadData error', e)
  }
}

async function getBabyAdvice() {
  if (aiLoading.value) return
  aiLoading.value = true
  try {
    const res = await ai.healthAdvice({ type: 'pregnancy', week: pregWeek.value })
    const text = res?.data?.advice || res?.advice || ''
    // 解析宝宝发育信息
    const sizeMatch = text.match(/身长[:：]?\s*(\d+\.?\d*)\s*cm/i)
    const weightMatch = text.match(/体重[:：]?\s*(\d+\.?\d*)\s*[g克]/i)
    if (sizeMatch) babyLength.value = sizeMatch[1]
    if (weightMatch) babyWeight.value = weightMatch[1].replace('g', '')
    babyDesc.value = text
  } catch (e) {
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
  } catch (e) { uni.showToast({ title: '保存失败', icon: 'none' }) }
}

async function submitCheckup() {
  if (!checkupForm.value.checkupDate) { uni.showToast({ title: '请选择日期', icon: 'none' }); return }
  try {
    // 通过日记接口记录产检
    await pregnancy.createDiary({ content: `【产检】${checkupForm.value.checkupItem || '常规检查'} | ${checkupForm.value.hospital || ''} | 结果：${checkupForm.value.result || '正常'}`, diaryDate: checkupForm.value.checkupDate })
    uni.showToast({ title: '添加成功', icon: 'success' })
    showCheckupDialog.value = false
    checkupForm.value = { checkupDate: '', checkupItem: '', hospital: '', result: '' }
    // 刷新记录（简化处理，实际可新建产检专用接口）
    checkupHistory.value.unshift({ id: Date.now(), checkupDate: checkupForm.value.checkupDate, checkupItem: checkupForm.value.checkupItem, hospital: checkupForm.value.hospital, result: checkupForm.value.result })
  } catch (e) { uni.showToast({ title: '添加失败', icon: 'none' }) }
}

onMounted(loadData)
</script>

<style scoped>
.pregnancy-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #4CAF50, #A5D6A7); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.week-card { background: linear-gradient(135deg, #4CAF50, #A5D6A7); margin: 24rpx 32rpx; border-radius: 24rpx; padding: 32rpx; color: #fff; display: flex; gap: 32rpx; align-items: center }
.week-ring { width: 160rpx; height: 160rpx; border-radius: 50%; background: rgba(255,255,255,0.25); display: flex; flex-direction: column; align-items: center; justify-content: center; flex-shrink: 0 }
.week-num { font-size: 52rpx; font-weight: bold }
.week-label { font-size: 24rpx; opacity: 0.9 }
.week-info { flex: 1 }
.week-detail { font-size: 30rpx; font-weight: 600 }
.edd { font-size: 26rpx; opacity: 0.9; margin-top: 8rpx }
.days-left { font-size: 26rpx; opacity: 0.9 }
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.checkup-list { display: flex; flex-direction: column; gap: 12rpx; margin-bottom: 16rpx }
.checkup-item { display: flex; align-items: center; gap: 12rpx; padding: 16rpx; background: #f0f9eb; border-radius: 12rpx }
.checkup-date { font-size: 26rpx; color: #4CAF50; font-weight: 600; min-width: 100rpx }
.checkup-info { flex: 1 }
.checkup-name { font-size: 28rpx; color: #333 }
.checkup-hospital { font-size: 24rpx; color: #999 }
.checkup-result { font-size: 24rpx; padding: 4rpx 12rpx; border-radius: 8rpx }
.checkup-result.normal { background: #e8f5e9; color: #4CAF50 }
.checkup-result.abnormal { background: #fff3e0; color: #ff9800 }
.diary-list { display: flex; flex-direction: column; gap: 16rpx; margin-bottom: 16rpx }
.diary-item { background: #faf8fb; border-radius: 12rpx; padding: 16rpx }
.diary-date { font-size: 24rpx; color: #999; margin-bottom: 8rpx }
.diary-content { font-size: 28rpx; color: #333; line-height: 1.6 }
.write-diary-btn { text-align: center; color: #4CAF50; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #A5D6A7; border-radius: 12rpx }
.baby-info { display: flex; gap: 24rpx; margin-bottom: 12rpx }
.baby-size, .baby-weight { flex: 1; background: #f0f9eb; border-radius: 12rpx; padding: 16rpx; text-align: center; font-size: 28rpx; color: #4CAF50; font-weight: 500 }
.baby-desc { font-size: 26rpx; color: #666; line-height: 1.8; margin-bottom: 16rpx }
.ai-btn { text-align: center; background: linear-gradient(135deg, #4CAF50, #A5D6A7); color: #fff; padding: 20rpx; border-radius: 12rpx; font-size: 28rpx }
.ai-btn.disabled { opacity: 0.6 }
.add-btn { text-align: center; color: #4CAF50; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #A5D6A7; border-radius: 12rpx; margin-top: 8rpx }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 24rpx }
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center }
.dialog-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 560rpx }
.dialog-title { font-size: 32rpx; font-weight: bold; text-align: center; margin-bottom: 24rpx }
.dialog-input { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; box-sizing: border-box }
.dialog-textarea { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; height: 200rpx; box-sizing: border-box; resize: none }
.dialog-btns { display: flex; gap: 20rpx; margin-top: 8rpx }
.dialog-cancel { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 10rpx; color: #999; font-size: 28rpx }
.dialog-confirm { flex: 1; text-align: center; padding: 16rpx; background: linear-gradient(135deg, #4CAF50, #A5D6A7); border-radius: 10rpx; color: #fff; font-size: 28rpx }
</style>
