import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/Login.vue') },
    {
      path: '/',
      component: () => import('@/layout/Layout.vue'),
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
        { path: 'banner', name: 'Banner', component: () => import('@/views/Banner.vue') },
        { path: 'article', name: 'Article', component: () => import('@/views/Article.vue') },
        { path: 'user', name: 'User', component: () => import('@/views/User.vue') },
        { path: 'expert', name: 'Expert', component: () => import('@/views/Expert.vue') },
        { path: 'question', name: 'Question', component: () => import('@/views/Question.vue') },
        { path: 'activity', name: 'Activity', component: () => import('@/views/Activity.vue') },
        { path: 'community', name: 'Community', component: () => import('@/views/Community.vue') },
        { path: 'config', name: 'Config', component: () => import('@/views/Config.vue') },
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('sw_admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
