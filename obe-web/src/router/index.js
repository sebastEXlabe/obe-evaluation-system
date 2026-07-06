import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  {
    path: '/', component: () => import('../views/Layout.vue'),
    redirect: () => {
      const u = JSON.parse(localStorage.getItem('user') || '{}')
      if (u.roleCode === 'STUDENT') return '/student'
      return '/dashboard'
    },
    children: [
      { path: 'dashboard', name: 'Dashboard', meta: { title: '教师工作台', icon: 'Monitor', roles: ['ADMIN','TEACHER'] }, component: () => import('../views/teacher/Dashboard.vue') },
      { path: 'course', name: 'Course', meta: { title: '课程目标', roles: ['ADMIN','TEACHER'] }, component: () => import('../views/course/Index.vue') },
      { path: 'group', name: 'Group', meta: { title: '小组管理', roles: ['ADMIN','TEACHER'] }, component: () => import('../views/group/Index.vue') },
      { path: 'qa', name: 'QA', meta: { title: '答疑解惑' }, component: () => import('../views/qa/Index.vue') },
      { path: 'ai-chat', name: 'AIChat', meta: { title: 'AI助手' }, component: () => import('../views/qa/AiChat.vue') },
      { path: 'self-test', name: 'SelfTest', meta: { title: 'AI自测' }, component: () => import('../views/qa/SelfTest.vue') },
      { path: 'knowledge', name: 'Knowledge', meta: { title: '知识库管理', roles: ['ADMIN','TEACHER'] }, component: () => import('../views/course/KnowledgeManage.vue') },
      { path: 'project', name: 'Project', meta: { title: '项目追踪' }, component: () => import('../views/project/Index.vue') },
      { path: 'evaluation', name: 'Evaluation', meta: { title: '多元评价', roles: ['ADMIN','TEACHER'] }, component: () => import('../views/evaluation/Index.vue') },
      { path: 'analysis', name: 'Analysis', meta: { title: '达成度分析' }, component: () => import('../views/analysis/Index.vue') },
      { path: 'docs', name: 'Docs', meta: { title: '使用文档' }, component: () => import('../views/docs/Index.vue') },
      { path: 'student', name: 'Student', meta: { title: '我的小组' }, component: () => import('../views/student/Index.vue') },
      { path: 'student/scores', name: 'Scores', meta: { title: '我的成绩' }, component: () => import('../views/student/Scores.vue') },
      { path: 'settings', name: 'Settings', meta: { title: '个人中心' }, component: () => import('../views/settings/Index.vue') },
      { path: ':pathMatch(.*)*', name: 'NotFound', meta: { title: '404' }, component: { template: '<div style="text-align:center;padding:80px"><h1>404</h1><p>页面不存在</p><el-button type="primary" @click="$router.push(\'/\')">返回首页</el-button></div>' } }
    ]
  }
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) return next('/login')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.meta.roles && !to.meta.roles.includes(user.roleCode || 'STUDENT'))
    return next('/')
  next()
})

export default router
