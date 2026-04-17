<template>
  <div class="admin-layout">
    <!-- 左侧边栏 -->
    <el-aside class="sidebar" :width="isCollapsed ? '64px' : '220px'">
      <div class="sidebar-header">
        <div class="logo-area">
          <span class="logo-icon">🌸</span>
          <span class="logo-text" v-if="!isCollapsed">SheWell</span>
        </div>
        <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
          <span class="collapse-icon">{{ isCollapsed ? '»' : '«' }}</span>
        </div>
      </div>

      <el-menu
        :default-active="$route.path"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#263445"
        text-color="#bfcbd9"
        active-text-color="#E91E63"
        :router="true"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <span class="menu-icon">📊</span>
          <span class="menu-text">数据看板</span>
        </el-menu-item>

        <el-menu-item index="/banner">
          <span class="menu-icon">🎯</span>
          <span class="menu-text">Banner管理</span>
        </el-menu-item>

        <el-menu-item index="/article">
          <span class="menu-icon">📖</span>
          <span class="menu-text">文章管理</span>
        </el-menu-item>

        <el-menu-item index="/user">
          <span class="menu-icon">👥</span>
          <span class="menu-text">用户管理</span>
        </el-menu-item>

        <el-menu-item index="/expert">
          <span class="menu-icon">👩‍⚕️</span>
          <span class="menu-text">专家管理</span>
        </el-menu-item>

        <el-menu-item index="/question">
          <span class="menu-icon">❓</span>
          <span class="menu-text">问答管理</span>
        </el-menu-item>

        <el-menu-item index="/activity">
          <span class="menu-icon">🎁</span>
          <span class="menu-text">活动管理</span>
        </el-menu-item>

        <el-menu-item index="/community">
          <span class="menu-icon">💬</span>
          <span class="menu-text">社区管理</span>
        </el-menu-item>

        <el-menu-item index="/config">
          <span class="menu-icon">⚙️</span>
          <span class="menu-text">系统配置</span>
        </el-menu-item>

        <el-menu-item @click="logout" class="logout-item">
          <span class="menu-icon">🚪</span>
          <span class="menu-text">退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title || $route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="admin-user">
              <span class="user-avatar">👤</span>
              <span class="user-name">{{ adminName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isCollapsed = ref(false)
const adminName = ref('管理员')

function handleCommand(command) {
  if (command === 'logout') logout()
}

function logout() {
  localStorage.removeItem('sw_admin_token')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
@import '@/styles/global.scss';

.admin-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

// 侧边栏
.sidebar {
  background: #263445;
  display: flex;
  flex-direction: column;
  transition: width $admin-transition-normal;
  overflow: hidden;
  flex-shrink: 0;
}

.sidebar-header {
  height: $admin-header-height;
  background: #1a2a3a;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $admin-space-4;
  flex-shrink: 0;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: $admin-space-2;
  overflow: hidden;
}

.logo-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.collapse-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.1);
  border-radius: 6px;
  cursor: pointer;
  flex-shrink: 0;
  transition: background $admin-transition-fast;

  &:hover {
    background: rgba(255,255,255,0.2);
  }
}

.collapse-icon {
  font-size: 14px;
  color: #bfcbd9;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: $admin-space-2 0;

  .el-menu-item {
    height: 52px;
    display: flex;
    align-items: center;
    padding: 0 $admin-space-4 !important;
    border-radius: 0;

    &:hover {
      background: rgba(255,255,255,0.06) !important;
    }

    &.is-active {
      background: rgba(233, 30, 99, 0.15) !important;
      border-right: 3px solid $admin-primary;
    }
  }

  .logout-item {
    margin-top: auto;
    border-top: 1px solid rgba(255,255,255,0.06);
  }
}

.menu-icon {
  font-size: 18px;
  flex-shrink: 0;
  margin-right: $admin-space-3;
}

.menu-text {
  font-size: 14px;
  white-space: nowrap;
}

// 主容器
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

// 顶部栏
.header {
  height: $admin-header-height;
  background: $admin-white;
  border-bottom: 1px solid $admin-border;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $admin-space-6;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.admin-user {
  display: flex;
  align-items: center;
  gap: $admin-space-2;
  cursor: pointer;
  padding: $admin-space-2 $admin-space-3;
  border-radius: $admin-radius-md;
  transition: background $admin-transition-fast;

  &:hover {
    background: $admin-bg;
  }
}

.user-avatar {
  font-size: 22px;
}

.user-name {
  font-size: 14px;
  color: $admin-text;
}

// 主内容
.main-content {
  background: $admin-bg;
  padding: $admin-space-6;
  overflow-y: auto;
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
