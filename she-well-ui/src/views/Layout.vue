<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background: #fff; border-right: 1px solid #eee">
      <div class="logo">🌸 SheWell</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/home"><el-icon><House /></el-icon> 首页</el-menu-item>
        <el-menu-item index="/period"><el-icon><Calendar /></el-icon> 经期管理</el-menu-item>
        <el-menu-item index="/pregnancy"><el-icon><Sugar /></el-icon> 备孕/怀孕</el-menu-item>
        <el-menu-item index="/community"><el-icon><ChatDotRound /></el-icon> 社区</el-menu-item>
        <el-menu-item index="/knowledge"><el-icon><Reading /></el-icon> 知识库</el-menu-item>
        <el-menu-item index="/health"><el-icon><DataLine /></el-icon> 健康报告</el-menu-item>
        <el-menu-item index="/fertility"><el-icon><TrendCharts /></el-icon> 生育力评估</el-menu-item>
        <el-menu-item index="/reminder"><el-icon><Bell /></el-icon> 健康提醒</el-menu-item>
        <el-menu-item index="/settings"><el-icon><Setting /></el-icon> 设置</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: white; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between">
        <span style="font-size: 18px; font-weight: 600; color: #333">{{ pageTitle }}</span>
        <div style="display: flex; align-items: center; gap: 12px">
          <el-badge :value="3" :max="99"><el-icon :size="20"><Bell /></el-icon></el-badge>
          <span>{{ auth.nickname }}</span>
          <el-button size="small" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main style="background: #f5f5f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const pageTitle = computed(() => {
  const map: Record<string,string> = {
    '/home': '首页', '/period': '经期管理', '/pregnancy': '备孕/怀孕',
    '/community': '社区', '/knowledge': '知识库', '/health': '健康报告',
    '/fertility': '生育力评估', '/reminder': '健康提醒', '/settings': '设置'
  }
  return map[route.path] || 'SheWell'
})

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo { padding: 20px; text-align: center; font-size: 20px; color: #e91e63; font-weight: bold; border-bottom: 1px solid #f5f5f5; }
.el-menu { border: none; }
</style>
