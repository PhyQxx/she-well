<template>
  <view class="checkin-page">
    <view class="header">
      <view class="header-title">✅ 健康打卡</view>
      <view class="header-date">{{ today }}</view>
    </view>

    <!-- 打卡类型选择 -->
    <view class="type-tabs">
      <view
        v-for="t in checkinTypes" :key="t.key"
        class="type-tab"
        :class="{ active: currentType === t.key }"
        @click="currentType = t.key"
      >
        <text class="type-icon">{{ t.icon }}</text>
        <text class="type-label">{{ t.label }}</text>
      </view>
    </view>

    <!-- 打卡表单 -->
    <view class="checkin-form">
      <!-- 经期相关打卡 -->
      <view v-if="currentType === 'period'" class="form-section">
        <view class="form-title">🩸 经期症状</view>
        <view class="tag-list">
          <view
            v-for="tag in periodTags" :key="tag"
            class="tag"
            :class="{ selected: selectedPeriodTags.includes(tag) }"
            @click="togglePeriodTag(tag)"
          >{{ tag }}</view>
        </view>
        <view class="form-title" style="margin-top:24rpx">💧 经量</view>
        <view class="amount-btns">
          <view v-for="a in ['轻', '中', '重']" :key="a" class="amount-btn" :class="{ selected: selectedAmount === a }" @click="selectedAmount = a">{{ a }}</view>
        </view>
        <view class="form-title" style="margin-top:24rpx">📝 日记</view>
        <textarea class="diary-input" v-model="diary" placeholder="今天感觉怎么样..." maxlength="200" />
      </view>

      <!-- 体温打卡 -->
      <view v-if="currentType === 'bbt'" class="form-section">
        <view class="form-title">🌡️ 基础体温</view>
        <view class="temp-display">{{ currentTemp }}°C</view>
        <slider class="temp-slider" min="35.5" max="37.5" step="0.05" :value="currentTemp" show-value @change="e => currentTemp = e.detail.value" activeColor="#E91E63" />
        <view class="temp-tips">排卵后体温会升高0.3-0.5°C</view>
        <view class="temp-chart-placeholder">
          <view class="chart-placeholder">近7天体温趋势图</view>
        </view>
      </view>

      <!-- 排卵试纸 -->
      <view v-if="currentType === 'strip'" class="form-section">
        <view class="form-title">🧪 排卵试纸结果</view>
        <view class="strip-btns">
          <view v-for="r in ['阴性(-)', '弱阳性(±)', '阳性(+)', '强阳性(++)']" :key="r"
            class="strip-btn" :class="{ selected: selectedStrip === r }"
            @click="selectedStrip = r">{{ r }}</view>
        </view>
        <view class="form-title" style="margin-top:24rpx">📷 拍照上传（可选）</view>
        <view class="upload-area" @click="uploadStripImg">
          <text v-if="!stripImg">+ 上传试纸照片</text>
          <image v-else :src="stripImg" mode="aspectFit" style="width:200rpx;height:200rpx" />
        </view>
      </view>

      <!-- 同房记录 -->
      <view v-if="currentType === 'intercourse'" class="form-section">
        <view class="form-title">💕 同房记录</view>
        <view class="form-row">
          <view class="form-label">日期</view>
          <picker mode="date" :value="icDate" @change="e => icDate = e.detail.value">
            <view class="picker-value">{{ icDate || '选择日期' }}</view>
          </picker>
        </view>
        <view class="form-row">
          <view class="form-label">时间</view>
          <picker mode="time" :value="icTime" @change="e => icTime = e.detail.value">
            <view class="picker-value">{{ icTime || '选择时间' }}</view>
          </picker>
        </view>
        <view class="form-row">
          <view class="form-label">备注</view>
          <input class="form-input" v-model="icNote" placeholder="可选备注" />
        </view>
      </view>

      <!-- 白带观测 -->
      <view v-if="currentType === 'cervical'" class="form-section">
        <view class="form-title">💧 白带观测</view>
        <view class="tag-list">
          <view v-for="t in cervicalTypes" :key="t" class="tag" :class="{ selected: selectedCervical === t }" @click="selectedCervical = t">{{ t }}</view>
        </view>
      </view>

      <!-- 心情/睡眠/运动 -->
      <view v-if="currentType === 'lifestyle'" class="form-section">
        <view class="form-title">😊 今日心情</view>
        <view class="emoji-list">
          <text v-for="e in emotions" :key="e.label" class="emoji-item" :class="{ selected: selectedEmotion === e.label }" @click="selectedEmotion = e.label">{{ e.icon }}</text>
        </view>
        <view class="form-title" style="margin-top:24rpx">😴 睡眠时长</view>
        <view class="sleep-btns">
          <view v-for="s in ['<6h', '6-7h', '7-8h', '>8h']" :key="s" class="sleep-btn" :class="{ selected: selectedSleep === s }" @click="selectedSleep = s">{{ s }}</view>
        </view>
        <view class="form-title" style="margin-top:24rpx">🏃 运动</view>
        <view class="tag-list">
          <view v-for="t in exerciseTypes" :key="t" class="tag" :class="{ selected: selectedExercises.includes(t) }" @click="toggleExercise(t)">{{ t }}</view>
        </view>
      </view>

      <button class="submit-btn" @click="submitCheckin">提交打卡</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { checkin, bbt, ovulation, intercourse } from '@/api/index.js'

const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
const currentType = ref('period')
const checkinTypes = [
  { key: 'period', label: '经期', icon: '🩸' },
  { key: 'bbt', label: '体温', icon: '🌡️' },
  { key: 'strip', label: '试纸', icon: '🧪' },
  { key: 'intercourse', label: '同房', icon: '💕' },
  { key: 'cervical', label: '白带', icon: '💧' },
  { key: 'lifestyle', label: '生活', icon: '🌿' },
]

const periodTags = ['痛经', '头痛', '恶心', '腹胀', '腰痛', '情绪波动', '乳房胀痛', '乏力', '长痘', '失眠']
const cervicalTypes = ['透明蛋清', '白色乳液', '黄绿色', '血性', '灰白色']
const exerciseTypes = ['瑜伽', '散步', '跑步', '游泳', '健身', '无运动']
const emotions = [
  { icon: '😊', label: '愉悦' }, { icon: '😌', label: '平静' },
  { icon: '😔', label: '低落' }, { icon: '😣', label: '烦躁' },
  { icon: '😰', label: '焦虑' }, { icon: '😴', label: '疲惫' },
]

const selectedPeriodTags = ref([])
const selectedAmount = ref('中')
const diary = ref('')
const currentTemp = ref(36.5)
const selectedStrip = ref('')
const stripImg = ref('')
const icDate = ref('')
const icTime = ref('')
const icNote = ref('')
const selectedCervical = ref('')
const selectedEmotion = ref('')
const selectedSleep = ref('')
const selectedExercises = ref([])

function togglePeriodTag(tag) {
  const idx = selectedPeriodTags.value.indexOf(tag)
  if (idx > -1) selectedPeriodTags.value.splice(idx, 1)
  else selectedPeriodTags.value.push(tag)
}
function toggleExercise(e) { const idx = selectedExercises.value.indexOf(e); if (idx > -1) selectedExercises.value.splice(idx, 1); else selectedExercises.value.push(e) }
function uploadStripImg() {
  uni.chooseImage({
    count: 1,
    success: (res) => { stripImg.value = res.tempFilePaths[0] }
  })
}

async function submitCheckin() {
  const todayDate = new Date().toISOString().slice(0, 10)
  try {
    if (currentType.value === 'period') {
      await checkin.create({
        checkinDate: todayDate,
        symptom: selectedPeriodTags.value.join(','),
        mood: selectedEmotion.value,
        notes: diary.value,
      })
    } else if (currentType.value === 'bbt') {
      await bbt.create({
        temperature: currentTemp.value,
        measureDate: todayDate,
      })
    } else if (currentType.value === 'strip') {
      await ovulation.create({
        recordDate: todayDate,
        result: selectedStrip.value,
        imageUrl: stripImg.value,
      })
    } else if (currentType.value === 'intercourse') {
      await intercourse.create({
        intercourseDate: icDate.value || todayDate,
        note: icNote.value,
      })
    } else if (currentType.value === 'lifestyle') {
      await checkin.create({
        checkinDate: todayDate,
        emotion: selectedEmotion.value,
        sleepQuality: selectedSleep.value,
        exerciseType: selectedExercises.value.join(','),
      })
    } else {
      await checkin.create({ checkinDate: todayDate })
    }
    uni.showToast({ title: '打卡成功', icon: 'success' })
  } catch {
    uni.showToast({ title: '打卡失败，请重试', icon: 'none' })
  }
}

onMounted(() => {
  const token = uni.getStorageSync('sw_token')
  if (!token) { uni.reLaunch({ url: '/pages/login/login' }); return }
})
</script>

<style scoped>
.checkin-page { min-height: 100vh; background: #faf8fb; padding-bottom: 48rpx }
.header { background: linear-gradient(135deg, #4CAF50, #A5D6A7); padding: 32rpx; color: #fff }
.header-title { font-size: 36rpx; font-weight: bold }
.header-date { font-size: 26rpx; opacity: 0.9; margin-top: 8rpx }
.type-tabs { display: flex; overflow-x: auto; padding: 24rpx 16rpx; gap: 12rpx; white-space: nowrap }
.type-tab { flex-shrink: 0; display: flex; flex-direction: column; align-items: center; gap: 4rpx; padding: 16rpx 24rpx; background: #fff; border-radius: 16rpx; border: 2rpx solid transparent }
.type-tab.active { border-color: #4CAF50; background: #f0f9eb }
.type-icon { font-size: 36rpx }
.type-label { font-size: 22rpx; color: #666 }
.checkin-form { margin: 0 32rpx; background: #fff; border-radius: 20rpx; padding: 24rpx }
.form-title { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx }
.tag-list { display: flex; flex-wrap: wrap; gap: 12rpx }
.tag { padding: 12rpx 24rpx; background: #f5f5f5; border-radius: 30rpx; font-size: 26rpx; color: #666; border: 2rpx solid transparent }
.tag.selected { background: #fce4ec; color: #E91E63; border-color: #E91E63 }
.amount-btns { display: flex; gap: 16rpx }
.amount-btn { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 12rpx; font-size: 28rpx; color: #666 }
.amount-btn.selected { background: #E91E63; color: #fff }
.diary-input { width: 100%; height: 160rpx; background: #faf8fb; border: 2rpx solid #f0f0f0; border-radius: 12rpx; padding: 16rpx; font-size: 28rpx; box-sizing: border-box }
.temp-display { text-align: center; font-size: 72rpx; font-weight: bold; color: #E91E63 }
.temp-slider { margin: 0 16rpx }
.temp-tips { text-align: center; font-size: 24rpx; color: #999; margin-top: 8rpx }
.temp-chart-placeholder { background: #faf8fb; border-radius: 12rpx; padding: 40rpx; margin-top: 16rpx; text-align: center }
.chart-placeholder { color: #ccc; font-size: 26rpx }
.strip-btns { display: flex; flex-wrap: wrap; gap: 12rpx }
.strip-btn { padding: 16rpx 24rpx; background: #f5f5f5; border-radius: 12rpx; font-size: 26rpx; color: #666; border: 2rpx solid transparent }
.strip-btn.selected { background: #9C27B0; color: #fff }
.upload-area { border: 2rpx dashed #ccc; border-radius: 12rpx; padding: 48rpx; text-align: center; color: #999; font-size: 26rpx }
.form-row { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5 }
.form-label { font-size: 28rpx; color: #666; width: 140rpx }
.picker-value, .form-input { flex: 1; font-size: 28rpx; color: #333 }
.emoji-list { display: flex; gap: 16rpx }
.emoji-item { font-size: 48rpx; padding: 8rpx; border-radius: 12rpx; border: 2rpx solid transparent }
.emoji-item.selected { background: #fff5f7; border-color: #E91E63 }
.sleep-btns { display: flex; gap: 12rpx }
.sleep-btn { flex: 1; text-align: center; padding: 16rpx; background: #f5f5f5; border-radius: 12rpx; font-size: 26rpx; color: #666 }
.sleep-btn.selected { background: #4CAF50; color: #fff }
.submit-btn { background: linear-gradient(135deg, #E91E63, #F48FB1); color: #fff; border-radius: 40rpx; margin-top: 32rpx; font-size: 32rpx; height: 88rpx; line-height: 88rpx; border: none }
</style>
