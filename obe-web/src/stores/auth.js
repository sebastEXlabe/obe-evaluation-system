import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '../api/index.js'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value.roleCode === 'ADMIN')
  const isTeacher = computed(() => user.value.roleCode === 'TEACHER')
  const isStudent = computed(() => user.value.roleCode === 'STUDENT')

  async function login(username, password) {
    const res = await http.post('/auth/login', { username, password })
    token.value = res.data.token
    user.value = { id: res.data.id, username: res.data.username, realName: res.data.realName, roleCode: res.data.roleCode }
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function logout() {
    token.value = ''; user.value = {}
    localStorage.clear()
  }

  function updateProfile(fields) {
    Object.assign(user.value, fields)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return { user, token, isLoggedIn, isAdmin, isTeacher, isStudent, login, logout, updateProfile }
})
