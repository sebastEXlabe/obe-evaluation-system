<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header><h2 style="text-align:center;margin:0">OBE-CDIO 教学评价管理系统</h2></template>
      <el-form :model="form" label-width="0" @keyup.enter="doLogin">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" clearable /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" show-password clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doLogin" :loading="loading" style="width:100%">登 录</el-button>
        </el-form-item>
        <el-button link type="primary" @click="regVisible = true" style="width:100%">没有账号？立即注册</el-button>
      </el-form>
    </el-card>

    <el-dialog v-model="regVisible" title="注册新账号" width="400px">
      <el-form :model="regForm" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="regForm.username" placeholder="至少3位" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="regForm.password" type="password" placeholder="至少6位" show-password /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="regForm.realName" placeholder="真实姓名" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="regVisible = false">取消</el-button>
        <el-button type="primary" @click="doRegister" :loading="loading">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../api/index.js'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const regVisible = ref(false)
const regForm = reactive({ username: '', password: '', realName: '' })

async function doLogin() {
  if (!form.username || !form.password) return ElMessage.warning('请输入用户名和密码')
  loading.value = true
  try {
    const res = await http.post('/auth/login', { username: form.username, password: form.password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify({ id: res.data.id, username: res.data.username, realName: res.data.realName, roleCode: res.data.roleCode }))
    router.push('/')
  } catch { ElMessage.error('登录失败，请检查用户名和密码') } finally { loading.value = false }
}

async function doRegister() {
  if (!regForm.username || !regForm.password) return ElMessage.warning('请填写用户名和密码')
  loading.value = true
  try {
    await http.post('/auth/register', regForm)
    ElMessage.success('注册成功，请登录')
    regVisible.value = false
    Object.assign(regForm, { username: '', password: '', realName: '' })
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.login-page { display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 400px; }
</style>
