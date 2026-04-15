import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('sw_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器
api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('sw_token')
      router.push('/login')
    } else {
      ElMessage.error(err.response?.data?.message || '请求失败')
    }
    return Promise.reject(err)
  }
)

// ===== Auth =====
export const login = (data: { phone: string; code: string }) => api.post('/auth/login', data)
export const sendCode = (phone: string) => api.post('/auth/sendCode', { phone })

// ===== User =====
export const getProfile = () => api.get('/user/profile')
export const updateProfile = (data: any) => api.put('/user/profile', data)
export const getSettings = () => api.get('/user/settings')
export const updateSettings = (data: any) => api.put('/user/settings', data)

// ===== Period =====
export const getPeriodRecords = () => api.get('/period/record/list')
export const addPeriodRecord = (data: any) => api.post('/period/record', data)
export const updatePeriodRecord = (id: number, data: any) => api.put(`/period/record/${id}`, data)
export const deletePeriodRecord = (id: number) => api.delete(`/period/record/${id}`)
export const getPrediction = () => api.get('/period/prediction')
export const calculatePrediction = () => api.post('/period/prediction/calculate')

// ===== Pregnancy =====
export const getPregnancyRecord = () => api.get('/pregnancy/record')
export const addPregnancyRecord = (data: any) => api.post('/pregnancy/record', data)
export const babyBorn = (data: any) => api.post('/pregnancy/baby-born', data)
export const getBabies = () => api.get('/baby/list')
export const addBaby = (data: any) => api.post('/baby', data)

// ===== Checkin =====
export const getHabits = () => api.get('/checkin/habit/list')
export const addHabit = (data: any) => api.post('/checkin/habit', data)
export const updateHabit = (id: number, data: any) => api.put(`/checkin/habit/${id}`, data)
export const deleteHabit = (id: number) => api.delete(`/checkin/habit/${id}`)
export const getTodayCheckins = () => api.get('/checkin/record/today')
export const addCheckin = (data: any) => api.post('/checkin/record', data)

// ===== Community =====
export const getTopics = () => api.get('/community/topics')
export const getPosts = (params: any) => api.get('/community/posts', { params })
export const getPostDetail = (id: number) => api.get(`/community/post/${id}`)
export const createPost = (data: any) => api.post('/community/post', data)
export const deletePost = (id: number) => api.delete(`/community/post/${id}`)
export const likePost = (id: number) => api.post(`/community/post/${id}/like`)
export const unlikePost = (id: number) => api.delete(`/community/post/${id}/like`)
export const collectPost = (id: number) => api.post(`/community/post/${id}/collect`)
export const getComments = (postId: number) => api.get('/community/comments', { params: { postId } })
export const addComment = (data: any) => api.post('/community/comment', data)
export const deleteComment = (id: number) => api.delete(`/community/comment/${id}`)

// ===== Knowledge =====
export const getCategories = () => api.get('/knowledge/categories')
export const getArticles = (params: any) => api.get('/knowledge/articles', { params })
export const getArticleDetail = (id: number) => api.get(`/knowledge/article/${id}`)
export const getHotArticles = (limit = 10) => api.get('/discovery/hot-articles', { params: { limit } })

// ===== Discovery =====
export const getBanners = () => api.get('/discovery/banners')
export const getHotArticles2 = (limit = 10) => api.get('/discovery/hot-articles', { params: { limit } })
export const getRecommendArticles = (limit = 5) => api.get('/discovery/recommend-articles', { params: { limit } })

// ===== Stats =====
export const getStatsOverview = () => api.get('/stats/overview')
export const getMonthlyStats = (params: any) => api.get('/stats/period/monthly', { params })

// ===== Health Report =====
export const getHealthReports = (params?: any) => api.get('/health-report/list', { params })
export const getHealthReportDetail = (id: number) => api.get(`/health-report/${id}`)
export const generateHealthReport = (period = 'monthly') => api.post('/health-report/generate', null, { params: { period } })

// ===== AI =====
export const aiConsult = (data: { question: string; context?: string }) => api.post('/ai/consult', data)
export const getHealthAdvice = () => api.get('/ai/advice')
export const getCycleAnalysis = (months = 3) => api.get('/ai/cycle-analysis', { params: { months } })
export const recognizeStrip = (file: File) => {
  const form = new FormData()
  form.append('file', file)
  return api.post('/ai/recognize-strip', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}
export const getAiModels = () => api.get('/ai/models')

// ===== Notification =====
export const getNotifications = (params?: any) => api.get('/notification/list', { params })
export const getUnreadCount = () => api.get('/notification/unread-count')
export const markAllRead = () => api.put('/notification/read-all')
export const markRead = (id: number) => api.put(`/notification/read/${id}`)

// ===== Expert =====
export const getExperts = (params?: any) => api.get('/expert/list', { params })
export const getExpertDetail = (id: number) => api.get(`/expert/${id}`)
export const getQuestions = (params?: any) => api.get('/expert/questions', { params })
export const askExpert = (data: any) => api.post('/expert/question', data)

// ===== Activity =====
export const getActivities = (params?: any) => api.get('/activity/list', { params })
export const getActivityDetail = (id: number) => api.get(`/activity/${id}`)

// ===== Baby =====
export const getBabyList = () => api.get('/baby/list')
export const getBaby = (id: number) => api.get(`/baby/${id}`)
export const updateBaby = (id: number, data: any) => api.put(`/baby/${id}`, data)
export const deleteBaby = (id: number) => api.delete(`/baby/${id}`)

// ===== File =====
export const uploadAvatar = (file: File) => {
  const form = new FormData()
  form.append('file', file)
  return api.post('/file/upload/avatar', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}
export const uploadPostImage = (file: File) => {
  const form = new FormData()
  form.append('file', file)
  return api.post('/file/upload/post-image', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export default api
