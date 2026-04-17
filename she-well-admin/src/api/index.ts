import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('sw_admin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('sw_admin_token')
      window.location.href = '/login'
    } else {
      ElMessage.error(err.response?.data?.message || '请求失败')
    }
    return Promise.reject(err)
  }
)

export default api

// ===== Banner =====
export const getBannerList = (params?: any) => api.get('/banner/list', { params })
export const createBanner = (data: any) => api.post('/banner', data)
export const updateBanner = (id: number, data: any) => api.put(`/banner/${id}`, data)
export const deleteBanner = (id: number) => api.delete(`/banner/${id}`)

// ===== Article =====
export const getArticleList = (params?: any) => api.get('/knowledge/articles', { params })
export const createArticle = (data: any) => api.post('/knowledge/article', data)
export const updateArticle = (id: number, data: any) => api.put(`/knowledge/article/${id}`, data)
export const deleteArticle = (id: number) => api.delete(`/knowledge/article/${id}`)

// ===== User =====
export const getUserList = (params?: any) => api.get('/admin/user/list', { params })
export const updateUserStatus = (id: number, status: number) => api.put(`/admin/user/${id}/status`, { status })
export const resetUserPassword = (id: number, password?: string) => api.put(`/admin/user/${id}/reset-password`, password ? { password } : {})

// ===== Expert =====
export const getExpertList = (params?: any) => api.get('/expert/all', { params })
export const approveExpert = (id: number) => api.put(`/expert/${id}/approve`)
export const rejectExpert = (id: number, reason: string) => api.put(`/expert/${id}/reject`, { reason })

// ===== Question =====
export const getQuestionList = (params?: any) => api.get('/expert/questions', { params })
export const replyQuestion = (id: number, content: string) => api.put(`/expert/question/${id}/reply`, { content })
export const deleteQuestion = (id: number) => api.delete(`/expert/question/${id}`)

// ===== Activity =====
export const getActivityList = (params?: any) => api.get('/activity/list', { params })
export const createActivity = (data: any) => api.post('/activity', data)
export const updateActivity = (id: number, data: any) => api.put(`/activity/${id}`, data)
export const deleteActivity = (id: number) => api.delete(`/activity/${id}`)

// ===== Community =====
export const getPostList = (params?: any) => api.get('/community/posts', { params })
export const approvePost = (id: number) => api.put(`/community/post/${id}/approve`)
export const deletePost = (id: number) => api.delete(`/community/post/${id}`)
export const banPost = (id: number) => api.put(`/community/post/${id}/ban`)
export const getCommentList = (params?: any) => api.get('/community/comments', { params })
export const deleteComment = (id: number) => api.delete(`/community/comment/${id}`)

// ===== Dashboard Stats =====
export const getDashboardStats = () => api.get('/admin/stats/dashboard')
export const getTrendStats = (days?: number) => api.get('/admin/stats/trend', { params: { days: days || 7 } })

// ===== System Config =====
export const getConfig = () => api.get('/admin/config')
export const saveConfig = (data: Record<string, string>) => api.put('/admin/config', data)
