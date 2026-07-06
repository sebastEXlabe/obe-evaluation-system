import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: '/api',
  timeout: 30000
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器：统一处理401跳转和错误返回
http.interceptors.response.use(
  res => {
    return res.data
  },
  err => {
    const status = err.response?.status || 0
    const msg = err.response?.data?.message || err.message || '请求失败'
    console.error('API Error:', status, msg)
    // 401未认证 → 清除登录态并跳转登录页
    if (status === 401) {
      localStorage.clear()
      window.location.hash = '#/login'
      window.location.reload()
    }
    return Promise.reject(err)
  }
)

export default http
