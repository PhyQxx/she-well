import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('../views/Home.vue'), meta: { title: '首页' } },
      { path: 'period', name: 'Period', component: () => import('../views/Period.vue'), meta: { title: '经期管理' } },
      { path: 'pregnancy', name: 'Pregnancy', component: () => import('../views/Pregnancy.vue'), meta: { title: '怀孕模式' } },
      { path: 'health', name: 'Health', component: () => import('../views/Health.vue'), meta: { title: '健康数据' } },
      { path: 'knowledge', name: 'Knowledge', component: () => import('../views/Knowledge.vue'), meta: { title: '知识库' } },
      { path: 'community', name: 'Community', component: () => import('../views/Community.vue'), meta: { title: '姐妹社区' } },
      { path: 'settings', name: 'Settings', component: () => import('../views/Settings.vue'), meta: { title: '设置' } },
      { path: 'discovery', name: 'Discovery', component: () => import('../views/Discovery.vue'), meta: { title: '发现' } },
      { path: 'achievement', name: 'Achievement', component: () => import('../views/Achievement.vue'), meta: { title: '成就' } },
      { path: 'notification', name: 'Notification', component: () => import('../views/Notification.vue'), meta: { title: '通知' } },
      { path: 'fertility', name: 'Fertility', component: () => import('../views/Fertility.vue'), meta: { title: '生育力评估' } },
      { path: 'reminder', name: 'Reminder', component: () => import('../views/Reminder.vue'), meta: { title: '健康提醒' } },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { title: '登录' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) document.title = to.meta.title as string
  const token = localStorage.getItem('sw_token')
  if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
