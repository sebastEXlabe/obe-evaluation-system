<!--
  系统主布局组件
  职责：根据用户角色(ADMIN/TEACHER/STUDENT)显示不同的侧边栏菜单
  左侧为可折叠菜单栏，右侧为面包屑导航+内容区
  认证信息通过 Pinia auth store 获取，保证响应式更新
-->
<template>
  <el-container style="height:100vh">
    <!-- 左侧可折叠导航栏 -->
    <el-aside :width="sidebarCollapsed ? '64px' : '220px'" class="app-side" :class="{ collapsed: sidebarCollapsed }">
      <div class="side-header">
        <template v-if="!sidebarCollapsed">
          <div class="side-title">{{ roleLabels[role] }}</div>
          <div class="side-name">{{ user.realName || user.username }}</div>
        </template>
        <el-button class="collapse-btn" text @click="sidebarCollapsed = !sidebarCollapsed">
          <span v-if="sidebarCollapsed" style="font-size:18px;color:#bfcbd9">&#9776;</span>
          <span v-else style="font-size:18px;color:#bfcbd9">&#10094;&#10094;</span>
        </el-button>
      </div>
      <!-- 根据角色动态渲染菜单项，点击即路由跳转 -->
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF" :collapse="sidebarCollapsed">
        <el-menu-item v-for="m in visibleMenus" :key="m.path" :index="m.path">
          <Icon :name="m.icon" /><span>{{ m.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部：面包屑 + 退出 -->
      <el-header class="app-header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <el-button text @click="doLogout">退出登录</el-button>
      </el-header>
      <!-- 主内容区，由router-view渲染 -->
      <el-main class="app-main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 系统布局 — 根据角色（ADMIN/TEACHER/STUDENT）显示不同菜单
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
// 从Pinia store取用户信息，实现响应式刷新
const user = computed(() => auth.user)
const role = computed(() => auth.user.roleCode || 'STUDENT')
const sidebarCollapsed = ref(false)

// 三种角色的侧边栏标题文案
const roleLabels = { ADMIN: '🔧 管理端', TEACHER: '👨‍🏫 教师端', STUDENT: '🎓 学生端' }

// 根据OBE-CDIO系统的角色划分，每个角色看到不同的功能模块
// 管理员：全部功能；教师：教学管理相关；学生：学习与查看相关
const allMenus = {
  ADMIN: [
    { path: '/dashboard', title: '教师工作台', icon: 'monitor' },
    { path: '/course', title: '课程目标', icon: 'aim' },
    { path: '/group', title: '小组管理', icon: 'user' },
    { path: '/qa', title: '答疑解惑', icon: 'chat' },
    { path: '/ai-chat', title: 'AI助手', icon: 'aim' },
    { path: '/self-test', title: 'AI自测', icon: 'edit-pen' },
    { path: '/knowledge', title: '知识库', icon: 'doc' },
    { path: '/project', title: '项目追踪', icon: 'list' },
    { path: '/evaluation', title: '多元评价', icon: 'trophy' },
    { path: '/analysis', title: '达成度分析', icon: 'chart' },
    { path: '/settings', title: '个人中心', icon: 'person' },
    { path: '/docs', title: '使用文档', icon: 'doc' },
  ],
  TEACHER: [
    { path: '/dashboard', title: '教师工作台', icon: 'monitor' },
    { path: '/course', title: '课程目标', icon: 'aim' },
    { path: '/group', title: '小组管理', icon: 'user' },
    { path: '/qa', title: '答疑解惑', icon: 'chat' },
    { path: '/ai-chat', title: 'AI助手', icon: 'aim' },
    { path: '/self-test', title: 'AI自测', icon: 'edit-pen' },
    { path: '/knowledge', title: '知识库', icon: 'doc' },
    { path: '/project', title: '项目追踪', icon: 'list' },
    { path: '/evaluation', title: '评价打分', icon: 'trophy' },
    { path: '/analysis', title: '达成度分析', icon: 'chart' },
    { path: '/settings', title: '个人中心', icon: 'person' },
    { path: '/docs', title: '使用文档', icon: 'doc' },
  ],
  STUDENT: [
    { path: '/student', title: '我的小组', icon: 'user' },
    { path: '/project', title: '项目追踪', icon: 'list' },
    { path: '/qa', title: '答疑解惑', icon: 'chat' },
    { path: '/ai-chat', title: 'AI助手', icon: 'aim' },
    { path: '/self-test', title: 'AI自测', icon: 'edit-pen' },
    { path: '/student/scores', title: '我的成绩', icon: 'trophy' },
    { path: '/analysis', title: '达成度', icon: 'chart' },
    { path: '/settings', title: '个人中心', icon: 'person' },
    { path: '/docs', title: '使用文档', icon: 'doc' },
  ],
}

const visibleMenus = computed(() => allMenus[role.value] || allMenus.STUDENT)

function doLogout() {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.app-side { background: #304156; overflow-y: auto; }
.side-header { padding: 12px 16px; color: #fff; text-align: center; border-bottom: 1px solid #4a5a6a; position: relative; }
.collapse-btn { position: absolute; top: 50%; right: 4px; transform: translateY(-50%); padding: 4px; min-height: auto; }
.collapsed .collapse-btn { position: static; transform: none; margin: 0 auto; display: block; }
.side-title { font-size: 14px; }
.side-name { font-size: 12px; color: #909399; margin-top: 4px; }
.app-header { display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #dcdfe6; background: #fff; }
.app-main { background: #f0f2f5; min-height: calc(100vh - 60px); }
</style>
