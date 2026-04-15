<template>
  <el-container style="height: 100vh">
    <!-- 左侧导航 -->
    <el-aside width="200px" style="background: #304156">
      <div class="admin-logo">🌸 SheWell 管理后台</div>
      <el-menu
        :default-active="$route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#E91E63"
        :router="true"
      >
        <el-menu-item index="/dashboard">
          <span>📊 数据看板</span>
        </el-menu-item>
        <el-menu-item index="/banner">
          <span>🎯 Banner管理</span>
        </el-menu-item>
        <el-menu-item index="/article">
          <span>📖 文章管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <span>👥 用户管理</span>
        </el-menu-item>
        <el-menu-item index="/expert">
          <span>👩‍⚕️ 专家管理</span>
        </el-menu-item>
        <el-menu-item index="/question">
          <span>❓ 问答管理</span>
        </el-menu-item>
        <el-menu-item index="/activity">
          <span>🎁 活动管理</span>
        </el-menu-item>
        <el-menu-item index="/community">
          <span>💬 社区管理</span>
        </el-menu-item>
        <el-menu-item index="/config">
          <span>⚙️ 系统配置</span>
        </el-menu-item>
        <el-menu-item @click="logout">
          <span>🚪 退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:space-between;background:#fff;border-bottom:1px solid #e6e6e6">
        <span style="font-size:16px;font-weight:600">{{ $route.meta.title || $route.name }}</span>
        <el-button text @click="logout">退出</el-button>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('sw_admin_token')
  router.push('/login')
}
</script>

<style scoped>
.admin-logo {
  height: 60px; line-height: 60px;
  text-align: center; color: #fff;
  font-size: 16px; font-weight: 600;
  background: #263445;
}
.el-aside { color: #fff }
</style>
