import { computed, reactive } from 'vue'
import { authApi, userApi } from '../api'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem('guagua_user') || 'null')
  } catch {
    return null
  }
}

export const authState = reactive({
  token: localStorage.getItem('guagua_token') || '',
  user: readUser()
})

export const isLoggedIn = computed(() => Boolean(authState.token))
export const isAdmin = computed(() => authState.user?.role === 'ADMIN')

function setSession(data) {
  authState.token = data.token
  authState.user = data.user
  localStorage.setItem('guagua_token', data.token)
  localStorage.setItem('guagua_user', JSON.stringify(data.user))
}

export async function login(credentials) {
  const data = await authApi.login(credentials)
  setSession(data)
  return data
}

export async function register(payload) {
  const data = await authApi.register(payload)
  setSession(data)
  return data
}

export async function refreshUser() {
  if (!authState.token) return null
  try {
    const user = await userApi.me()
    authState.user = user
    localStorage.setItem('guagua_user', JSON.stringify(user))
    return user
  } catch {
    logout()
    return null
  }
}

export function updateLocalUser(user) {
  authState.user = user
  localStorage.setItem('guagua_user', JSON.stringify(user))
}

export function logout() {
  authState.token = ''
  authState.user = null
  localStorage.removeItem('guagua_token')
  localStorage.removeItem('guagua_user')
}

window.addEventListener('guagua-auth-expired', () => {
  authState.token = ''
  authState.user = null
})

export function useAuth() {
  return { authState, isLoggedIn, isAdmin, login, register, logout, refreshUser }
}
