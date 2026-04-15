import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('sw_token') || '')
  const userId = ref<number | null>(null)
  const nickname = ref('')

  const isLoggedIn = computed(() => !!token.value)

  async function login(phone: string) {
    const res: any = await api.post('/auth/login', { phone, code: '123456' })
    if (res.success) {
      token.value = res.data.token
      userId.value = res.data.userId
      nickname.value = res.data.nickname
      localStorage.setItem('sw_token', res.data.token)
    }
    return res
  }

  function logout() {
    token.value = ''
    userId.value = null
    nickname.value = ''
    localStorage.removeItem('sw_token')
  }

  return { token, userId, nickname, isLoggedIn, login, logout }
})
