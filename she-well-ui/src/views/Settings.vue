<template>
  <div class="settings-page">
    <!-- 欢迎横幅 -->
    <div class="settings-banner">
      <div class="banner-content">
        <h2 class="banner-title">⚙️ 设置中心</h2>
        <p class="banner-sub">管理您的个人信息和偏好设置</p>
      </div>
      <div class="banner-decoration">✨</div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧设置导航 -->
      <el-col :span="6">
        <div class="card nav-card">
          <div class="nav-list">
            <div
              v-for="item in navItems"
              :key="item.key"
              class="nav-item"
              :class="{ active: activeNav === item.key }"
              @click="activeNav = item.key"
            >
              <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
              <span class="nav-label">{{ item.label }}</span>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧设置内容 -->
      <el-col :span="18">
        <!-- 个人信息 -->
        <div v-show="activeNav === 'profile'" class="card settings-card">
          <div class="card-header">
            <span class="card-title">👤 个人信息</span>
          </div>
          <el-form
            :model="profile"
            label-width="100px"
            class="settings-form"
          >
            <el-form-item label="头像">
              <div class="avatar-upload">
                <div class="avatar-preview">{{ profile.nickname?.charAt(0)?.toUpperCase() || '👤' }}</div>
                <el-button size="small" type="primary" plain>更换头像</el-button>
              </div>
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profile.nickname" placeholder="请输入昵称" maxlength="20" />
            </el-form-item>
            <el-form-item label="生日">
              <el-date-picker
                v-model="profile.birthday"
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="身高">
              <el-input v-model="profile.height" type="number" placeholder="cm">
                <template #append>cm</template>
              </el-input>
            </el-form-item>
            <el-form-item label="体重">
              <el-input v-model="profile.weight" type="number" placeholder="kg">
                <template #append>kg</template>
              </el-input>
            </el-form-item>
            <el-form-item label="血型">
              <el-select v-model="profile.bloodType" placeholder="请选择血型" style="width: 100%">
                <el-option v-for="b in bloodTypes" :key="b" :label="b" :value="b" />
              </el-select>
            </el-form-item>
            <el-form-item label="月经周期">
              <el-input-number
                v-model="profile.cycleLength"
                :min="21"
                :max="35"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="经期天数">
              <el-input-number
                v-model="profile.periodLength"
                :min="3"
                :max="10"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">
                <el-icon><Check /></el-icon> 保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 通知设置 -->
        <div v-show="activeNav === 'notification'" class="card settings-card">
          <div class="card-header">
            <span class="card-title">🔔 通知设置</span>
          </div>
          <el-form label-width="140px" class="settings-form">
            <el-form-item label="推送通知">
              <el-switch v-model="settings.pushEnabled" />
              <span class="setting-hint">接收系统通知和更新提醒</span>
            </el-form-item>
            <el-form-item label="经期提醒">
              <el-switch v-model="settings.reminderEnabled" />
              <span class="setting-hint">提前提醒即将到来的经期</span>
            </el-form-item>
            <el-form-item v-if="settings.reminderEnabled" label="提醒时间">
              <el-time-picker
                v-model="settings.reminderTime"
                format="HH:mm"
                value-format="HH:mm"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="排卵日提醒">
              <el-switch v-model="settings.ovulReminder" />
              <span class="setting-hint">在排卵日前后发送提醒</span>
            </el-form-item>
            <el-form-item label="备孕模式">
              <el-switch v-model="settings.tryingMode" />
              <span class="setting-hint">开启后可获得更多备孕建议</span>
            </el-form-item>
            <el-form-item label="怀孕模式">
              <el-switch v-model="settings.pregnantMode" />
              <span class="setting-hint">开启后可获得孕期相关指导</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings">
                <el-icon><Check /></el-icon> 保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 隐私设置 -->
        <div v-show="activeNav === 'privacy'" class="card settings-card">
          <div class="card-header">
            <span class="card-title">🔒 隐私设置</span>
          </div>
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="个人资料">
              <el-radio-group v-model="settings.profileVisibility">
                <el-radio label="public">公开</el-radio>
                <el-radio label="friends">仅好友</el-radio>
                <el-radio label="private">仅自己</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="健康数据">
              <el-radio-group v-model="settings.healthDataVisibility">
                <el-radio label="public">公开</el-radio>
                <el-radio label="friends">仅好友</el-radio>
                <el-radio label="private">仅自己</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="允许私信">
              <el-switch v-model="settings.allowDm" />
            </el-form-item>
            <el-form-item label="显示在线状态">
              <el-switch v-model="settings.showOnline" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings">
                <el-icon><Check /></el-icon> 保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 外观设置 -->
        <div v-show="activeNav === 'appearance'" class="card settings-card">
          <div class="card-header">
            <span class="card-title">🎨 外观设置</span>
          </div>
          <el-form label-width="100px" class="settings-form">
            <el-form-item label="主题">
              <el-radio-group v-model="settings.theme">
                <el-radio label="light">
                  <span class="theme-option">
                    <el-icon><Sunny /></el-icon> 浅色
                  </span>
                </el-radio>
                <el-radio label="dark">
                  <span class="theme-option">
                    <el-icon><Moon /></el-icon> 深色
                  </span>
                </el-radio>
                <el-radio label="auto">
                  <span class="theme-option">
                    <el-icon><Monitor /></el-icon> 跟随系统
                  </span>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主题色">
              <div class="color-options">
                <div
                  v-for="color in themeColors"
                  :key="color.value"
                  class="color-item"
                  :class="{ active: settings.primaryColor === color.value }"
                  :style="{ background: color.value }"
                  @click="settings.primaryColor = color.value"
                >
                  <el-icon v-if="settings.primaryColor === color.value"><Check /></el-icon>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="字体大小">
              <el-slider
                v-model="settings.fontSize"
                :min="12"
                :max="18"
                :step="1"
                show-stops
              />
              <span class="setting-value">{{ settings.fontSize }}px</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings">
                <el-icon><Check /></el-icon> 保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 数据管理 -->
        <div v-show="activeNav === 'data'" class="card settings-card">
          <div class="card-header">
            <span class="card-title">💾 数据管理</span>
          </div>
          <div class="data-management">
            <div class="data-item">
              <div class="data-info">
                <div class="data-icon">📤</div>
                <div class="data-text">
                  <span class="data-title">导出数据</span>
                  <span class="data-desc">导出您的所有健康数据</span>
                </div>
              </div>
              <el-button type="primary" plain @click="exportData">
                <el-icon><Download /></el-icon> 导出
              </el-button>
            </div>
            <div class="data-item">
              <div class="data-info">
                <div class="data-icon">📥</div>
                <div class="data-text">
                  <span class="data-title">导入数据</span>
                  <span class="data-desc">从备份文件恢复数据</span>
                </div>
              </div>
              <el-button type="primary" plain @click="importData">
                <el-icon><Upload /></el-icon> 导入
              </el-button>
            </div>
            <div class="data-item danger">
              <div class="data-info">
                <div class="data-icon">🗑️</div>
                <div class="data-text">
                  <span class="data-title">清除缓存</span>
                  <span class="data-desc">清理本地存储的临时数据</span>
                </div>
              </div>
              <el-button type="danger" plain @click="clearCache">
                <el-icon><Delete /></el-icon> 清除
              </el-button>
            </div>
            <div class="data-item danger">
              <div class="data-info">
                <div class="data-icon">⚠️</div>
                <div class="data-text">
                  <span class="data-title">注销账号</span>
                  <span class="data-desc">永久删除您的账户和所有数据</span>
                </div>
              </div>
              <el-button type="danger" @click="showDeleteConfirm = true">
                <el-icon><Delete /></el-icon> 注销
              </el-button>
            </div>
          </div>
        </div>

        <!-- 关于 -->
        <div v-show="activeNav === 'about'" class="card settings-card">
          <div class="card-header">
            <span class="card-title">ℹ️ 关于我们</span>
          </div>
          <div class="about-content">
            <div class="about-logo">💊</div>
            <h3 class="about-name">她好 (SheWell)</h3>
            <p class="about-desc">专业女性健康管理平台</p>
            <div class="about-version">版本 1.0.0</div>
            <el-divider />
            <div class="about-links">
              <a class="about-link" @click="openLink('privacy')">隐私政策</a>
              <a class="about-link" @click="openLink('terms')">服务条款</a>
              <a class="about-link" @click="openLink('contact')">联系我们</a>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 注销确认 -->
    <el-dialog v-model="showDeleteConfirm" title="注销账号" width="400px" class="she-dialog">
      <el-alert type="error" :closable="false" class="delete-alert">
        <template #title>
          确定要注销您的账号吗？此操作不可恢复！
        </template>
      </el-alert>
      <p style="margin-top: 16px; color: #666;">
        注销后，您的所有个人数据将被永久删除，包括：
      </p>
      <ul style="color: #999; margin-top: 8px; padding-left: 20px;">
        <li>个人信息和健康记录</li>
        <li>社区帖子和评论</li>
        <li>设置和偏好</li>
      </ul>
      <template #footer>
        <el-button @click="showDeleteConfirm = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确认注销</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Bell,
  Lock,
  Brush,
  Folder,
  InfoFilled,
  Check,
  Sunny,
  Moon,
  Monitor,
  Download,
  Upload,
  Delete
} from '@element-plus/icons-vue'
import { getProfile, updateProfile, getSettings, updateSettings } from '@/api'

const activeNav = ref('profile')
const profile = ref<any>({})
const settings = ref<any>({})
const showDeleteConfirm = ref(false)

const bloodTypes = ['A', 'B', 'O', 'AB', '未知']

const themeColors = [
  { label: '玫红', value: '#E91E63' },
  { label: '紫色', value: '#9C27B0' },
  { label: '蓝色', value: '#2196F3' },
  { label: '绿色', value: '#4CAF50' },
  { label: '橙色', value: '#FF9800' }
]

const navItems = [
  { key: 'profile', label: '个人信息', icon: User },
  { key: 'notification', label: '通知设置', icon: Bell },
  { key: 'privacy', label: '隐私设置', icon: Lock },
  { key: 'appearance', label: '外观设置', icon: Brush },
  { key: 'data', label: '数据管理', icon: Folder },
  { key: 'about', label: '关于我们', icon: InfoFilled }
]

function fmt(d: any) {
  if (!d) return ''
  if (typeof d === 'string') return d
  return new Date(d).toISOString().slice(0, 10)
}

async function saveProfile() {
  try {
    await updateProfile({
      ...profile.value,
      birthday: fmt(profile.value.birthday)
    })
    ElMessage.success('个人信息已保存')
  } catch {
    ElMessage.error('保存失败')
  }
}

async function saveSettings() {
  try {
    await updateSettings(settings.value)
    ElMessage.success('设置已保存')
  } catch {
    ElMessage.error('保存失败')
  }
}

async function exportData() {
  try {
    ElMessage.info('数据导出功能开发中')
  } catch {
    ElMessage.error('导出失败')
  }
}

function importData() {
  ElMessage.info('请选择备份文件')
}

async function clearCache() {
  try {
    await ElMessageBox.confirm('确定要清除缓存吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    localStorage.clear()
    ElMessage.success('缓存已清除')
  } catch {}
}

async function confirmDelete() {
  try {
    await api.delete('/user/account')
    ElMessage.success('账号已注销')
    showDeleteConfirm.value = false
    // 跳转到登录页
  } catch {
    ElMessage.error('注销失败')
  }
}

function openLink(type: string) {
  ElMessage.info(`打开${type}页面`)
}

onMounted(async () => {
  try {
    const [p, s] = await Promise.all([
      getProfile(),
      getSettings()
    ])
    profile.value = p?.data || {}
    settings.value = s?.data || {
      pushEnabled: true,
      reminderEnabled: true,
      reminderTime: '09:00',
      ovulReminder: true,
      tryingMode: false,
      pregnantMode: false,
      profileVisibility: 'private',
      healthDataVisibility: 'private',
      allowDm: true,
      showOnline: true,
      theme: 'light',
      primaryColor: '#E91E63',
      fontSize: 14
    }
  } catch {
    profile.value = {}
    settings.value = {}
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.settings-page {
  @include page-container;
  max-width: 1400px;
  margin: 0 auto;
}

// --------------------------------------------
// 欢迎横幅
// --------------------------------------------
.settings-banner {
  @include gradient-card($she-primary, $she-primary-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $she-space-6;
  color: #fff;

  .banner-title {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    margin-bottom: $she-space-1;
  }

  .banner-sub {
    font-size: $she-font-size-md;
    opacity: 0.9;
  }

  .banner-decoration {
    font-size: 64px;
    opacity: 0.4;
  }
}

// --------------------------------------------
// 设置导航
// --------------------------------------------
.nav-card {
  .nav-list {
    display: flex;
    flex-direction: column;
    gap: $she-space-1;
  }

  .nav-item {
    @include flex-between;
    padding: $she-space-3 $she-space-4;
    border-radius: $she-radius-lg;
    cursor: pointer;
    transition: all $she-transition-fast;
    color: $she-sub;

    &:hover {
      background: $she-primary-bg;
      color: $she-primary;
    }

    &.active {
      background: $she-primary-bg;
      color: $she-primary;
      font-weight: $she-font-weight-semibold;

      .nav-icon {
        color: $she-primary;
      }
    }
  }

  .nav-icon {
    font-size: $she-font-size-lg;
  }

  .nav-label {
    flex: 1;
    font-size: $she-font-size-sm;
    margin-left: $she-space-2;
  }
}

// --------------------------------------------
// 设置卡片
// --------------------------------------------
.settings-card {
  .settings-form {
    margin-top: $she-space-4;
    max-width: 600px;

    .setting-hint {
      margin-left: $she-space-3;
      font-size: $she-font-size-xs;
      color: $she-muted;
    }

    .setting-value {
      margin-left: $she-space-3;
      font-size: $she-font-size-sm;
      color: $she-primary;
      font-weight: $she-font-weight-medium;
    }
  }
}

// --------------------------------------------
// 头像上传
// --------------------------------------------
.avatar-upload {
  display: flex;
  align-items: center;
  gap: $she-space-4;

  .avatar-preview {
    @include flex-center;
    width: 64px;
    height: 64px;
    background: $she-gradient-primary;
    border-radius: $she-radius-full;
    color: #fff;
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
  }
}

// --------------------------------------------
// 主题选项
// --------------------------------------------
.theme-option {
  display: flex;
  align-items: center;
  gap: $she-space-1;
}

// --------------------------------------------
// 颜色选项
// --------------------------------------------
.color-options {
  display: flex;
  gap: $she-space-3;

  .color-item {
    @include flex-center;
    width: 40px;
    height: 40px;
    border-radius: $she-radius-full;
    cursor: pointer;
    color: #fff;
    transition: all $she-transition-fast;

    &:hover {
      transform: scale(1.1);
    }

    &.active {
      box-shadow: 0 0 0 3px rgba($she-primary, 0.3);
    }
  }
}

// --------------------------------------------
// 数据管理
// --------------------------------------------
.data-management {
  display: flex;
  flex-direction: column;
  gap: $she-space-4;
  margin-top: $she-space-4;
}

.data-item {
  @include flex-between;
  padding: $she-space-4;
  background: $she-bg;
  border-radius: $she-radius-xl;
  transition: all $she-transition-fast;

  &:hover {
    background: rgba($she-primary, 0.04);
  }

  &.danger {
    &:hover {
      background: rgba($she-danger, 0.04);
    }

    .data-icon {
      background: rgba($she-danger, 0.1);
    }
  }

  .data-info {
    display: flex;
    align-items: center;
    gap: $she-space-3;
  }

  .data-icon {
    @include flex-center;
    width: 48px;
    height: 48px;
    background: $she-primary-bg;
    border-radius: $she-radius-lg;
    font-size: 24px;
  }

  .data-text {
    display: flex;
    flex-direction: column;
    gap: $she-space-1;
  }

  .data-title {
    font-size: $she-font-size-md;
    font-weight: $she-font-weight-medium;
    color: $she-text;
  }

  .data-desc {
    font-size: $she-font-size-xs;
    color: $she-muted;
  }
}

// --------------------------------------------
// 关于我们
// --------------------------------------------
.about-content {
  @include flex-center;
  flex-direction: column;
  padding: $she-space-6;
  text-align: center;

  .about-logo {
    font-size: 80px;
    margin-bottom: $she-space-4;
  }

  .about-name {
    font-size: $she-font-size-2xl;
    font-weight: $she-font-weight-bold;
    color: $she-primary;
    margin-bottom: $she-space-2;
  }

  .about-desc {
    font-size: $she-font-size-md;
    color: $she-sub;
    margin-bottom: $she-space-2;
  }

  .about-version {
    font-size: $she-font-size-sm;
    color: $she-muted;
  }

  .about-links {
    display: flex;
    gap: $she-space-6;

    .about-link {
      font-size: $she-font-size-sm;
      color: $she-primary;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

// --------------------------------------------
// 删除确认
// --------------------------------------------
.delete-alert {
  margin-bottom: $she-space-2;
}

// --------------------------------------------
// 响应式
// --------------------------------------------
@media (max-width: 992px) {
  .el-col-6 {
    width: 100% !important;
  }

  .el-col-18 {
    width: 100% !important;
  }

  .nav-card {
    margin-bottom: $she-space-4;

    .nav-list {
      flex-direction: row;
      flex-wrap: wrap;
    }

    .nav-item {
      flex: 1;
      min-width: 100px;
      justify-content: center;
    }
  }
}
</style>
