<template>
  <view class="checkin-page">
    <!-- 头部 -->
    <view class="page-header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">✅</text>
        <view class="header-text">
          <text class="header-title">健康打卡</text>
          <text class="header-date">{{ today }}</text>
        </view>
      </view>
    </view>

    <!-- 打卡类型 -->
    <view class="type-tabs">
      <view
        v-for="t in checkinTypes"
        :key="t.key"
        class="type-tab"
        :class="{ active: currentType === t.key }"
        @click="currentType = t.key"
      >
        <text class="type-icon">{{ t.icon }}</text>
        <text class="type-label">{{ t.label }}</text>
      </view>
    </view>

    <!-- 打卡表单 -->
    <view class="form-card">
      <!-- 经期打卡 -->
      <view v-if="currentType === 'period'" class="form-section">
        <view class="form-section-title">🩸 经期症状</view>
        <view class="tag-grid">
          <view
            v-for="tag in periodTags"
            :key="tag"
            class="tag-item"
            :class="{ selected: selectedPeriodTags.includes(tag) }"
            @click="togglePeriodTag(tag)"
          >
            <text>{{ tag }}</text>
          </view>
        </view>

        <view class="form-section-title" style="margin-top: 32rpx">💧 经量</view>
        <view class="amount-row">
          <view
            v-for="a in ['轻', '中', '重']"
            :key="a"
            class="amount-item"
            :class="{ selected: selectedAmount === a }"
            @click="selectedAmount = a"
          >
            <text>{{ a }}</text>
          </view>
        </view>

        <view class="form-section-title" style="margin-top: 32rpx">📝 日记</view>
        <textarea
          class="diary-input"
          v-model="diary"
          placeholder="今天感觉怎么样..."
          maxlength="200"
        />
      </view>

      <!-- 体温打卡 -->
      <view v-if="currentType === 'bbt'" class="form-section">
        <view class="form-section-title">🌡️ 基础体温</view>
        <view class="temp-display">{{ currentTemp }}°C</view>
        <slider
          class="temp-slider"
          min="35.5"
          max="37.5"
          step="0.05"
          :value="currentTemp"
          show-value
          @change="e => currentTemp = e.detail.value"
          active-color="#E91E63"
          block-size="24"
        />
        <view class="temp-tips">排卵后体温会升高 0.3~0.5°C</view>

        <view class="temp-chart">
          <view class="chart-title">近7天体温趋势</view>
          <view class="chart-placeholder">
            <text class="placeholder-icon">📈</text>
            <text class="placeholder-text">暂无数据</text>
          </view>
        </view>
      </view>

      <!-- 排卵试纸 -->
      <view v-if="currentType === 'strip'" class="form-section">
        <view class="form-section-title">🧪 排卵试纸结果</view>
        <view class="strip-grid">
          <view
            v-for="r in stripOptions"
            :key="r.value"
            class="strip-item"
            :class="{ selected: selectedStrip === r.value }"
            @click="selectedStrip = r.value"
          >
            <text class="strip-value">{{ r.value }}</text>
            <text class="strip-desc">{{ r.desc }}</text>
          </view>
        </view>

        <view class="form-section-title" style="margin-top: 32rpx">📷 拍照上传（可选）</view>
        <view class="upload-area" @click="uploadStripImg">
          <text v-if="!stripImg" class="upload-hint">+ 点击上传试纸照片</text>
          <image v-else :src="stripImg" mode="aspectFit" class="upload-preview" />
        </view>
      </view>

      <!-- 同房记录 -->
      <view v-if="currentType === 'intercourse'" class="form-section">
        <view class="form-section-title">💕 同房记录</view>
        <view class="input-row">
          <text class="input-label">日期</text>
          <picker mode="date" :value="icDate" @change="e => icDate = e.detail.value">
            <view class="picker-value">{{ icDate || '选择日期' }}</view>
          </picker>
        </view>
        <view class="input-row">
          <text class="input-label">时间</text>
          <picker mode="time" :value="icTime" @change="e => icTime = e.detail.value">
            <view class="picker-value">{{ icTime || '选择时间' }}</view>
          </picker>
        </view>
        <view class="input-row">
          <text class="input-label">备注</text>
          <input class="text-input" v-model="icNote" placeholder="可选备注" />
        </view>
      </view>

      <!-- 白带观测 -->
      <view v-if="currentType === 'cervical'" class="form-section">
        <view class="form-section-title">💧 白带观测</view>
        <view class="tag-grid">
          <view
            v-for="t in cervicalTypes"
            :key="t"
            class="tag-item"
            :class="{ selected: selectedCervical === t }"
            @click="selectedCervical = t"
          >
            <text>{{ t }}</text>
          </view>
        </view>
      </view>

      <!-- 生活打卡 -->
      <view v-if="currentType === 'lifestyle'" class="form-section">
        <view class="form-section-title">😊 今日心情</view>
        <view class="emoji-row">
          <view
            v-for="e in emotions"
            :key="e.label"
            class="emoji-item"
            :class="{ selected: selectedEmotion === e.label }"
            @click="selectedEmotion = e.label"
          >
            <text class="emoji-icon">{{ e.icon }}</text>
            <text class="emoji-label">{{ e.label }}</text>
          </view>
        </view>

        <view class="form-section-title" style="margin-top: 32rpx">😴 睡眠时长</view>
        <view class="sleep-row">
          <view
            v-for="s in sleepOptions"
            :key="s"
            class="sleep-item"
            :class="{ selected: selectedSleep === s }"
            @click="selectedSleep = s"
          >
            <text>{{ s }}</text>
          </view>
        </view>

        <view class="form-section-title" style="margin-top: 32rpx">🏃 运动</view>
        <view class="tag-grid">
          <view
            v-for="t in exerciseTypes"
            :key="t"
            class="tag-item"
            :class="{ selected: selectedExercises.includes(t) }"
            @click="toggleExercise(t)"
          >
            <text>{{ t }}</text>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <button class="submit-btn" @click="submitCheckin">
        <text>提交打卡</text>
      </button>
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
const sleepOptions = ['<6h', '6-7h', '7-8h', '>8h']
const stripOptions = [
  { value: '阴性(-)', desc: '未检测到' },
  { value: '弱阳性(±)', desc: '接近阈值' },
  { value: '阳性(+)', desc: '明显检测到' },
  { value: '强阳性(++)', desc: '强信号' },
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

function toggleExercise(e) {
  const idx = selectedExercises.value.indexOf(e)
  if (idx > -1) selectedExercises.value.splice(idx, 1)
  else selectedExercises.value.push(e)
}

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

<style lang="scss" scoped>
@import "../../uni.scss";

.checkin-page {
  min-height: 100vh;
  background: $she-bg;
  padding-bottom: 48rpx;
}

// 头部
.page-header { position: relative; }

.header-bg {
  height: 240rpx;
  background: $she-gradient-primary;
  border-radius: 0 0 48rpx 48rpx;
}

.header-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 80rpx 32rpx 0;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.header-icon {
  font-size: 64rpx;
}

.header-text {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
}

.header-date {
  font-size: 26rpx;
  color: rgba(255,255,255,0.85);
  margin-top: 4rpx;
}

// 类型 Tab
.type-tabs {
  display: flex;
  overflow-x: auto;
  padding: 24rpx 16rpx;
  gap: 12rpx;
  white-space: nowrap;
  -webkit-overflow-scrolling: touch;
}

.type-tab {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 16rpx 24rpx;
  background: $she-white;
  border-radius: 20rpx;
  border: 2rpx solid transparent;
  box-shadow: $she-shadow-sm;
  transition: all 0.2s ease;

  &.active {
    border-color: $she-primary;
    background: rgba($she-primary, 0.06);
  }

  &:active {
    transform: scale(0.97);
  }
}

.type-icon { font-size: 40rpx; }
.type-label { font-size: 22rpx; color: $she-sub; }

// 表单卡片
.form-card {
  margin: 0 32rpx;
  background: $she-white;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: $she-shadow-md;
}

.form-section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $she-title;
  margin-bottom: 16rpx;
}

// 标签网格
.tag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.tag-item {
  padding: 12rpx 24rpx;
  background: $she-bg;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: $she-sub;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: rgba($she-primary, 0.1);
    color: $she-primary;
    border-color: $she-primary;
  }

  &:active {
    transform: scale(0.97);
  }
}

// 经量选择
.amount-row {
  display: flex;
  gap: 16rpx;
}

.amount-item {
  flex: 1;
  text-align: center;
  padding: 16rpx;
  background: $she-bg;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: $she-sub;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: $she-primary;
    color: #fff;
  }

  &:active {
    transform: scale(0.97);
  }
}

// 日记输入
.diary-input {
  width: 100%;
  height: 160rpx;
  background: $she-bg;
  border: 2rpx solid $she-border;
  border-radius: 16rpx;
  padding: 16rpx;
  font-size: 28rpx;
  color: $she-text;
  box-sizing: border-box;
  &::placeholder { color: $she-muted; }
  &:focus { border-color: $she-primary; }
}

// 体温
.temp-display {
  text-align: center;
  font-size: 80rpx;
  font-weight: 700;
  color: $she-primary;
  margin-bottom: 16rpx;
}

.temp-slider {
  margin: 0 8rpx;
}

.temp-tips {
  text-align: center;
  font-size: 24rpx;
  color: $she-muted;
  margin-top: 8rpx;
}

.temp-chart {
  margin-top: 24rpx;
}

.chart-title {
  font-size: 26rpx;
  color: $she-sub;
  margin-bottom: 16rpx;
}

.chart-placeholder {
  background: $she-bg;
  border-radius: 16rpx;
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.placeholder-icon { font-size: 48rpx; }
.placeholder-text { font-size: 24rpx; color: $she-muted; }

// 试纸
.strip-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12rpx;
}

.strip-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 20rpx;
  background: $she-bg;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: rgba($she-trying, 0.1);
    border-color: $she-trying;
  }
}

.strip-value {
  font-size: 28rpx;
  font-weight: 600;
  color: $she-text;
}

.strip-desc {
  font-size: 22rpx;
  color: $she-muted;
}

.upload-area {
  border: 2rpx dashed $she-border;
  border-radius: 16rpx;
  padding: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:active {
    border-color: $she-primary;
    background: rgba($she-primary, 0.04);
  }
}

.upload-hint {
  font-size: 26rpx;
  color: $she-muted;
}

.upload-preview {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
}

// 同房记录
.input-row {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid $she-border;
}

.input-label {
  font-size: 28rpx;
  color: $she-sub;
  width: 140rpx;
}

.picker-value {
  flex: 1;
  font-size: 28rpx;
  color: $she-text;
}

.text-input {
  flex: 1;
  font-size: 28rpx;
  color: $she-text;
}

// Emoji 心情
.emoji-row {
  display: flex;
  gap: 12rpx;
}

.emoji-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 16rpx 8rpx;
  background: $she-bg;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: rgba($she-primary, 0.1);
    border-color: $she-primary;
  }
}

.emoji-icon { font-size: 40rpx; }
.emoji-label { font-size: 20rpx; color: $she-sub; }

// 睡眠
.sleep-row {
  display: flex;
  gap: 12rpx;
}

.sleep-item {
  flex: 1;
  text-align: center;
  padding: 16rpx;
  background: $she-bg;
  border-radius: 16rpx;
  font-size: 26rpx;
  color: $she-sub;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    background: $she-pregnant;
    color: #fff;
  }
}

// 提交按钮
.submit-btn {
  width: 100%;
  height: 96rpx;
  background: $she-gradient-primary;
  color: #fff;
  border-radius: 48rpx;
  font-size: 34rpx;
  font-weight: 600;
  border: none;
  margin-top: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba($she-primary, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;

  &:active {
    transform: scale(0.98);
  }
}
</style>
