import axios from 'axios'
import { ElMessage } from 'element-plus'
import { createDemoAdapter } from './demo'

const http = axios.create({
  baseURL: '/api',
  timeout: 12000
})

if (import.meta.env.VITE_DEMO_MODE === 'true') {
  http.defaults.adapter = createDemoAdapter()
}

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('guagua_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && typeof payload.code === 'number') {
      if (payload.code === 200) {
        return payload.data
      }
      if (payload.code === 401) {
        localStorage.removeItem('guagua_token')
        localStorage.removeItem('guagua_user')
        window.dispatchEvent(new CustomEvent('guagua-auth-expired'))
      }
      ElMessage.error(payload.message || '请求失败')
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload
  },
  (error) => {
    const message = error.response?.data?.message || (error.code === 'ECONNABORTED' ? '请求超时，请稍后重试' : '网络连接失败')
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default http
