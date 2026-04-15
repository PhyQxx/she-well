import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: uni.getStorageSync('sw_token') || '',
    userId: null,
    nickname: '',
    profile: null
  }),
  actions: {
    setAuth(data) {
      this.token = data.token
      this.userId = data.userId
      this.nickname = data.nickname
      uni.setStorageSync('sw_token', data.token)
    },
    clearAuth() {
      this.token = ''
      this.userId = null
      this.nickname = ''
      this.profile = null
      uni.removeStorageSync('sw_token')
    },
    setProfile(profile) {
      this.profile = profile
    }
  },
  getters: {
    isLoggedIn: state => !!state.token
  }
})
