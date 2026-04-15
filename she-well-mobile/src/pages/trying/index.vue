<template>
  <view class="trying-page">
    <view class="header">
      <view class="header-title">🤰 备孕中心</view>
    </view>

    <!-- 排卵追踪卡片 -->
    <view class="ovu-card">
      <view class="ovu-main">
        <view class="ovu-label">🎯 排卵日</view>
        <view class="ovu-date">{{ ovulationDate }}</view>
        <view class="ovu-status" :class="ovuStatusClass">{{ ovuStatusText }}</view>
      </view>
      <view class="ovu-countdown" v-if="daysToOvulation > 0">
        <view class="countdown-num">{{ daysToOvulation }}</view>
        <view class="countdown-label">天后</view>
      </view>
      <view class="ovu-countdown" v-else-if="daysToOvulation === 0">
        <view class="countdown-num" style="color:#fff">今</view>
        <view class="countdown-label">天</view>
      </view>
      <view class="ovu-countdown" v-else>
        <view class="countdown-num" style="color:#fff">−</view>
        <view class="countdown-label">{{ Math.abs(daysToOvulation) }}天前</view>
      </view>
    </view>

    <!-- LH试纸记录 -->
    <view class="section">
      <view class="section-title">🧪 排卵试纸记录</view>
      <view class="strip-timeline">
        <view v-for="s in stripRecords" :key="s.id" class="strip-item">
          <view class="strip-date">{{ s.recordDate?.slice(5) || s.date }}</view>
          <view class="strip-result" :class="'strip-' + s.result">{{ s.resultText }}</view>
        </view>
        <view v-if="!stripRecords.length" class="empty-tip">暂无试纸记录</view>
      </view>
      <view class="add-strip-btn" @click="addStripRecord">+ 记录试纸</view>
    </view>

    <!-- 同房记录 -->
    <view class="section">
      <view class="section-title">💕 同房记录</view>
      <view class="ic-list">
        <view v-for="r in icRecords" :key="r.id" class="ic-item">
          <view class="ic-date">{{ r.recordDate || r.date }}</view>
          <view class="ic-time">{{ r.notes || '—' }}</view>
          <view class="ic-delete" @click.stop="deleteIc(r.id)">删除</view>
        </view>
        <view v-if="!icRecords.length" class="empty-tip">暂无同房记录，点击下方添加</view>
      </view>
      <view class="add-ic-btn" @click="showIcDialog = true">+ 记录同房</view>
    </view>

    <!-- 基础体温 -->
    <view class="section">
      <view class="section-title">🌡️ 基础体温曲线</view>
      <view class="bbt-chart-placeholder">
        <view class="chart-placeholder">近{{ bbtRecords.length }}天体温趋势</view>
        <view class="bbt-list">
          <view v-for="b in bbtRecords" :key="b.recordDate" class="bbt-item">
            <text class="bbt-date">{{ b.recordDate?.slice(5) || b.date }}</text>
            <text class="bbt-temp">{{ b.temperature }}°C</text>
          </view>
        </view>
        <view v-if="!bbtRecords.length" class="empty-tip">暂无体温数据</view>
      </view>
      <view class="add-bbt-btn" @click="showBbtDialog = true">+ 记录体温</view>
    </view>

    <!-- AI 备孕建议 -->
    <view class="section ai-section">
      <view class="section-title">🤖 AI 备孕建议</view>
      <view class="ai-advice" v-if="aiAdvice">{{ aiAdvice }}</view>
      <view class="ai-loading" v-else-if="aiLoading">正在生成建议...</view>
      <view class="empty-tip" v-else>点击按钮获取个性化备孕建议</view>
      <view class="ai-btn" @click="getAdvice" :class="{disabled: aiLoading}">
        {{ aiLoading ? '生成中...' : '获取 AI 建议' }}
      </view>
    </view>

    <!-- 添加同房弹窗 -->
    <view class="dialog-overlay" v-if="showIcDialog" @click="showIcDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">记录同房</view>
        <input class="dialog-input" v-model="icForm.date" type="date" placeholder="选择日期" />
        <input class="dialog-input" v-model="icForm.notes" placeholder="备注（可选，如晚上/上午）" />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showIcDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitIc">确定</view>
        </view>
      </view>
    </view>

    <!-- 添加体温弹窗 -->
    <view class="dialog-overlay" v-if="showBbtDialog" @click="showBbtDialog = false">
      <view class="dialog-box" @click.stop>
        <view class="dialog-title">记录体温</view>
        <input class="dialog-input" v-model="bbtForm.date" type="date" placeholder="选择日期" />
        <input class="dialog-input" v-model="bbtForm.temperature" type="digit" placeholder="体温（如 36.5）" />
        <view class="dialog-btns">
          <view class="dialog-cancel" @click="showBbtDialog = false">取消</view>
          <view class="dialog-confirm" @click="submitBbt">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { bbt, ovulation, intercourse, ai } from '@/api/index.js'

const ovulationDate = ref('—')
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
    // 排卵
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
    // 试纸（从排卵记录中提取）
    stripRecords.value = (ovuList?.data || ovuList || []).map(r => ({
      id: r.id,
      recordDate: r.recordDate,
      result: r.lhResult || 'negative',
      resultText: getStripText(r.lhResult),
    }))
    // 同房
    icRecords.value = (icList?.data || icList || []).map(r => ({
      id: r.id,
      recordDate: r.recordDate,
      notes: r.notes || '',
    }))
    // 体温
    bbtRecords.value = (bbtList?.data || bbtList || []).map(r => ({
      id: r.id,
      recordDate: r.recordDate,
      temperature: r.temperature,
    }))
  } catch (e) {
    console.error('loadData error', e)
  }
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
  } catch (e) {
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
  } catch (e) { uni.showToast({ title: '记录失败', icon: 'none' }) }
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
  } catch (e) { uni.showToast({ title: '记录失败', icon: 'none' }) }
}

async function deleteIc(id) {
  try {
    await intercourse.delete(id)
    icRecords.value = icRecords.value.filter(r => r.id !== id)
    uni.showToast({ title: '已删除', icon: 'success' })
  } catch (e) { uni.showToast({ title: '删除失败', icon: 'none' }) }
}

function addStripRecord() {
  uni.showToast({ title: '试纸记录功能开发中', icon: 'none' })
}

onMounted(loadData)
</script>

<style scoped>
.trying-page { min-height: 100vh; background: #faf8fb; padding-bottom: 32rpx }
.header { background: linear-gradient(135deg, #9C27B0, #CE93D8); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.ovu-card { background: linear-gradient(135deg, #9C27B0, #CE93D8); margin: 24rpx 32rpx; border-radius: 24rpx; padding: 32rpx; color: #fff; display: flex; justify-content: space-between; align-items: center }
.ovu-label { font-size: 26rpx; opacity: 0.9 }
.ovu-date { font-size: 48rpx; font-weight: bold; margin: 8rpx 0 }
.ovu-status { font-size: 24rpx; background: rgba(255,255,255,0.3); padding: 6rpx 16rpx; border-radius: 20rpx; display: inline-block }
.status-active { background: #FFD700; color: #333 }
.status-passed { background: rgba(0,0,0,0.2); color: #fff }
.countdown-num { font-size: 56rpx; font-weight: bold }
.countdown-label { font-size: 24rpx; opacity: 0.9 }
.section { background: #fff; margin: 0 32rpx 24rpx; border-radius: 20rpx; padding: 24rpx }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx }
.strip-timeline { display: flex; gap: 16rpx; overflow-x: auto; padding-bottom: 8rpx }
.strip-item { display: flex; flex-direction: column; align-items: center; gap: 4rpx; min-width: 80rpx }
.strip-date { font-size: 22rpx; color: #999 }
.strip-result { font-size: 22rpx; padding: 6rpx 12rpx; border-radius: 8rpx; font-weight: 500 }
.strip-negative { background: #f5f5f5; color: #999 }
.strip-weak { background: #fff3e0; color: #ff9800 }
.strip-positive { background: #fce4ec; color: #E91E63 }
.strip-strong { background: #E91E63; color: #fff }
.ic-list { display: flex; flex-direction: column; gap: 12rpx; margin-bottom: 16rpx }
.ic-item { display: flex; justify-content: space-between; align-items: center; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.ic-date { font-size: 28rpx; color: #333 }
.ic-time { font-size: 26rpx; color: #999; flex: 1; margin-left: 16rpx }
.ic-delete { font-size: 24rpx; color: #E91E63 }
.bbt-chart-placeholder { background: #faf8fb; border-radius: 12rpx; padding: 24rpx }
.chart-placeholder { text-align: center; color: #ccc; font-size: 24rpx; margin-bottom: 16rpx }
.bbt-list { display: flex; gap: 8rpx; justify-content: space-around; overflow-x: auto }
.bbt-item { display: flex; flex-direction: column; align-items: center; gap: 4rpx; min-width: 60rpx }
.bbt-date { font-size: 22rpx; color: #999 }
.bbt-temp { font-size: 26rpx; font-weight: 600; color: #9C27B0 }
.empty-tip { text-align: center; color: #ccc; font-size: 26rpx; padding: 24rpx }
.add-strip-btn, .add-ic-btn, .add-bbt-btn { text-align: center; color: #9C27B0; font-size: 28rpx; padding: 16rpx; border: 2rpx dashed #CE93D8; border-radius: 12rpx; margin-top: 8rpx }
.ai-section .ai-advice { background: #faf8fb; border-radius: 12rpx; padding: 20rpx; font-size: 26rpx; color: #555; line-height: 1.6; margin-bottom: 16rpx }
.ai-loading { text-align: center; color: #9C27B0; font-size: 26rpx; padding: 20rpx }
.ai-btn { text-align: center; background: linear-gradient(135deg, #9C27B0, #CE93D8); color: #fff; padding: 20rpx; border-radius: 12rpx; font-size: 28rpx }
.ai-btn.disabled { opacity: 0.6 }
/* dialog */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center }
.dialog-box { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 560rpx }
.dialog-title { font-size: 32rpx; font-weight: bold; text-align: center; margin-bottom: 24rpx }
.dialog-input { border: 1rpx solid #eee; border-radius: 10rpx; padding: 16rpx; font-size: 28rpx; margin-bottom: 16rpx; width: 100%; box-sizing: border-box }
.dialog-btns { display: flex; gap: 20rpx; margin-top: 8rpx }
.dialog-cancel { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 10rpx; color: #999; font-size: 28rpx }
.dialog-confirm { flex: 1; text-align: center; padding: 16rpx; background: linear-gradient(135deg, #9C27B0, #CE93D8); border-radius: 10rpx; color: #fff; font-size: 28rpx }
</style>
