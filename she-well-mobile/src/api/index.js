// SheWell 移动端 API 层
// 基础路径：通过 manifest.json 中配置的域名，或本地调试时使用本地地址
const BASE_URL = 'http://localhost:8080'

// 请求封装
function request(url, options = {}) {
  const token = uni.getStorageSync('sw_token')
  const header = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
    ...options.header
  }
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 0 || res.data.code === 200) {
            resolve(res.data.data)
          } else {
            uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
            reject(res.data)
          }
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('sw_token')
          uni.reLaunch({ url: '/pages/login/login' })
          reject({ msg: '未登录' })
        } else {
          uni.showToast({ title: '网络错误', icon: 'none' })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// ========== 认证 ==========
export const auth = {
  sendCode: (phone) => request('/api/auth/send-code', { method: 'POST', data: { phone } }),
  login: (phone, code) => request('/api/auth/login', { method: 'POST', data: { phone, code } }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
}

// ========== 用户 ==========
export const user = {
  getProfile: () => request('/api/user/profile'),
  updateProfile: (data) => request('/api/user/profile', { method: 'PUT', data }),
  getSettings: () => request('/api/settings'),
  updateSettings: (data) => request('/api/settings', { method: 'PUT', data }),
  switchMode: (mode) => request('/api/settings/mode', { method: 'PUT', data: { mode } }),
}

// ========== 经期 ==========
export const period = {
  list: (params) => request('/api/period/list', { data: params }),
  create: (data) => request('/api/period', { method: 'POST', data }),
  update: (id, data) => request('/api/period/' + id, { method: 'PUT', data }),
  delete: (id) => request('/api/period/' + id, { method: 'DELETE' }),
  prediction: () => request('/api/period/prediction'),
  stats: () => request('/api/stats/period'),
}

// ========== 打卡 ==========
export const checkin = {
  list: (params) => request('/api/checkin/list', { data: params }),
  create: (data) => request('/api/checkin', { method: 'POST', data }),
  todayStatus: () => request('/api/checkin/today'),
  delete: (id) => request('/api/checkin/' + id, { method: 'DELETE' }),
}

// ========== 基础体温 ==========
export const bbt = {
  list: (params) => request('/api/bbt/list', { data: params }),
  create: (data) => request('/api/bbt', { method: 'POST', data }),
  latest: () => request('/api/bbt/latest'),
  trend: (days) => request('/api/bbt/trend', { data: { days } }),
  delete: (id) => request('/api/bbt/' + id, { method: 'DELETE' }),
}

// ========== 排卵试纸 ==========
export const ovulation = {
  list: () => request('/api/ovulation/list'),
  create: (data) => request('/api/ovulation', { method: 'POST', data }),
  delete: (id) => request('/api/ovulation/' + id, { method: 'DELETE' }),
}

// ========== 同房记录 ==========
export const intercourse = {
  list: () => request('/api/intercourse/list'),
  create: (data) => request('/api/intercourse', { method: 'POST', data }),
  delete: (id) => request('/api/intercourse/' + id, { method: 'DELETE' }),
}

// ========== 怀孕 ==========
export const pregnancy = {
  getRecord: () => request('/api/pregnancy'),
  create: (data) => request('/api/pregnancy', { method: 'POST', data }),
  update: (id, data) => request('/api/pregnancy/' + id, { method: 'PUT', data }),
  diaryList: () => request('/api/pregnancy/diary-list'),
  createDiary: (data) => request('/api/pregnancy/diary', { method: 'POST', data }),
}

// ========== 宝宝 ==========
export const baby = {
  list: () => request('/api/baby/list'),
  create: (data) => request('/api/baby', { method: 'POST', data }),
  update: (id, data) => request('/api/baby/' + id, { method: 'PUT', data }),
  delete: (id) => request('/api/baby/' + id, { method: 'DELETE' }),
}

// ========== 健康报告 ==========
export const healthReport = {
  generate: (type) => request('/api/health-report/generate?type=' + type, { method: 'POST' }),
  getLatest: () => request('/api/health-report/latest'),
  history: () => request('/api/health-report/history'),
}

// ========== 知识库 ==========
export const knowledge = {
  categories: () => request('/api/knowledge/categories'),
  articles: (params) => request('/api/knowledge/articles', { data: params }),
  article: (id) => request('/api/knowledge/article/' + id),
}

// ========== 社区 ==========
export const community = {
  topics: () => request('/api/community/topics'),
  posts: (params) => request('/api/community/posts', { data: params }),
  createPost: (data) => request('/api/community/post', { method: 'POST', data }),
  like: (postId) => request('/api/community/like/' + postId, { method: 'POST' }),
  collect: (postId) => request('/api/community/collect/' + postId, { method: 'POST' }),
}

// ========== AI ==========
export const ai = {
  chat: (message, context) => request('/api/ai/chat', { method: 'POST', data: { message, context } }),
  analyzePeriod: (data) => request('/api/ai/analyze-period', { method: 'POST', data }),
  analyzeStrip: (imageBase64) => request('/api/ai/analyze-strip', { method: 'POST', data: { image: imageBase64 } }),
  healthAdvice: (data) => request('/api/ai/health-advice', { method: 'POST', data }),
}

// ========== 成就 ==========
export const achievement = {
  list: () => request('/api/achievement/list'),
  my: () => request('/api/achievement/my'),
  check: () => request('/api/achievement/check', { method: 'POST' }),
}

// ========== 通知 ==========
export const notification = {
  list: () => request('/api/notification/list'),
  unreadCount: () => request('/api/notification/unread-count'),
  markRead: (id) => request('/api/notification/read/' + id, { method: 'PUT' }),
  markAllRead: () => request('/api/notification/read-all', { method: 'PUT' }),
}

// ========== 发现页 ==========
export const discovery = {
  banners: () => request('/api/discovery/banners'),
  activities: () => request('/api/discovery/activities'),
  recommended: (params) => request('/api/discovery/recommended', { data: params }),
}

// ========== 文件上传 ==========
export const file = {
  upload: (filePath, dir) => {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('sw_token')
      uni.uploadFile({
        url: BASE_URL + '/api/file/upload/' + (dir || 'post'),
        filePath,
        name: 'file',
        header: { 'Authorization': 'Bearer ' + token },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 0 || data.code === 200) resolve(data.data)
            else { uni.showToast({ title: '上传失败', icon: 'none' }); reject(data) }
          } catch (e) { reject(e) }
        },
        fail: (err) => { uni.showToast({ title: '上传失败', icon: 'none' }); reject(err) }
      })
    })
  },
}
