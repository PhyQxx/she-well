<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside class="layout-sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">🌸</span>
        <span class="logo-text">SheWell</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>

      <!-- 用户信息 -->
      <div class="sidebar-user">
        <el-avatar :size="36" :icon="UserFilled" />
        <div class="user-info">
          <div class="user-name">{{ auth.nickname || '用户' }}</div>
          <div class="user-role">健康顾问</div>
        </div>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="layout-main">
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <h1 class="page-title">{{ pageTitle }}</h1>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 搜索 -->
          <el-input
            class="header-search"
            placeholder="搜索..."
            :prefix-icon="Search"
            width="200"
          />

          <!-- 通知 -->
          <el-badge :value="3" :max="99" class="header-badge">
            <el-button :icon="Bell" circle />
          </el-badge>

          <!-- 退出 -->
          <el-button :icon="SwitchButton" @click="logout" text>
            退出
          </el-button>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="layout-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  House, Calendar, Sugar, ChatDotRound, Reading,
  DataLine, TrendCharts, Bell, Setting, Search,
  UserFilled, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const menuItems = [
  { path: '/home', label: '首页', icon: House },
  { path: '/period', label: '经期管理', icon: Calendar },
  { path: '/pregnancy', label: '备孕/怀孕', icon: Sugar },
  { path: '/community', label: '社区', icon: ChatDotRound },
  { path: '/knowledge', label: '知识库', icon: Reading },
  { path: '/health', label: '健康报告', icon: DataLine },
  { path: '/fertility', label: '生育力评估', icon: TrendCharts },
  { path: '/reminder', label: '健康提醒', icon: Bell },
  { path: '/settings', label: '设置', icon: Setting },
]

const activeMenu = computed(() => route.path)

const pageTitleMap: Record<string, string> = {
  '/home': '首页',
  '/period': '经期管理',
  '/pregnancy': '备孕/怀孕',
  '/community': '社区',
  '/knowledge': '知识库',
  '/health': '健康报告',
  '/fertility': '生育力评估',
  '/reminder': '健康提醒',
  '/settings': '设置',
}

const pageTitle = computed(() => pageTitleMap[route.path] || 'SheWell')

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
@import '@/assets/styles/mixins.scss';

.layout-container {
  height: 100vh;
}

// --------------------------------------------
// 侧边栏
// --------------------------------------------
.layout-sidebar {
  width: 240px !important;
  background: $she-white;
  border-right: 1px solid $she-border;
  display: flex;
  flex-direction: column;
  transition: width $she-transition-normal;

  &:hover {
    // 可选：悬停时展开
  }
}

.sidebar-logo {
  padding: $she-space-6;
  @include flex-center;
  gap: $she-space-3;
  border-bottom: 1px solid $she-border;

  .logo-icon {
    font-size: 32px;
  }

  .logo-text {
    font-size: $she-font-size-xl;
    font-weight: $she-font-weight-bold;
    color: $she-primary;
    letter-spacing: 1px;
  }
}

.sidebar-menu {
  flex: 1;
  padding: $she-space-3;
  overflow-y: auto;

  .el-menu-item {
    height: 48px;
    line-height: 48px;
    margin-bottom: $she-space-1;
    border-radius: $she-radius-lg;
    color: $she-sub;

    .el-icon {
      font-size: 18px;
      margin-right: $she-space-3;
    }

    &:hover {
      background: $she-primary-bg;
      color: $she-primary;
    }

    &.is-active {
      background: $she-primary-bg;
      color: $she-primary;
      font-weight: $she-font-weight-semibold;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 24px;
        background: $she-primary;
        border-radius: 0 2px 2px 0;
      }
    }
  }
}

.sidebar-user {
  padding: $she-space-4 $she-space-6;
  border-top: 1px solid $she-border;
  display: flex;
  align-items: center;
  gap: $she-space-3;

  .user-info {
    flex: 1;
    min-width: 0;
  }

  .user-name {
    font-size: $she-font-size-sm;
    font-weight: $she-font-weight-medium;
    color: $she-text;
    @include text-ellipsis(1);
  }

  .user-role {
    font-size: $she-font-size-xs;
    color: $she-muted;
  }
}

// --------------------------------------------
// 顶部栏
// --------------------------------------------
.layout-header {
  background: $she-white;
  border-bottom: 1px solid $she-border;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $she-space-6;
  height: 72px !important;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: $she-space-1;

  .page-title {
    font-size: $she-font-size-xl;
    font-weight: $she-font-weight-semibold;
    color: $she-title;
    line-height: 1.2;
  }

  .el-breadcrumb {
    font-size: $she-font-size-xs;

    :deep(.el-breadcrumb__inner) {
      color: $she-muted;
      &.is-link:hover { color: $she-primary; }
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: $she-space-4;

  .header-search {
    width: 200px;

    :deep(.el-input__wrapper) {
      border-radius: $she-radius-full;
    }
  }

  .header-badge {
    :deep(.el-badge__content) {
      background: $she-primary;
    }
  }
}

// --------------------------------------------
// 主内容区
// --------------------------------------------
.layout-content {
  background: $she-bg;
  padding: $she-space-6;
  overflow-y: auto;
  @include custom-scrollbar;
}
</style>
