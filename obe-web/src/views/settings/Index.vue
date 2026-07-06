<template>
  <div>
    <el-row :gutter="16">
      <!-- 个人资料卡片 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span style="font-weight:bold">个人资料</span></template>
          <el-form :model="profileForm" label-width="80px" :disabled="profileLoading">
            <el-form-item label="用户名"><el-input v-model="profileForm.username" disabled /></el-form-item>
            <el-form-item label="真实姓名"><el-input v-model="profileForm.realName" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="profileForm.phone" /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="profileLoading">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 修改密码 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span style="font-weight:bold">修改密码</span></template>
          <el-form :model="pwdForm" label-width="100px" :disabled="pwdLoading">
            <el-form-item label="旧密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item label="确认密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="pwdLoading">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- Git 身份 -->
        <el-card shadow="hover" style="margin-top:16px">
          <template #header><span style="font-weight:bold">Git 身份绑定</span></template>
          <el-form :model="gitForm" label-width="100px" :disabled="gitLoading">
            <el-form-item label="Git 用户名"><el-input v-model="gitForm.gitUsername" /></el-form-item>
            <el-form-item label="Git 邮箱"><el-input v-model="gitForm.gitEmail" /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveGitIdentity" :loading="gitLoading">绑定</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 教师/管理员：小组概览 & 学生达成度查询 -->
    <template v-if="isTeacherOrAdmin">
      <el-card shadow="hover" style="margin-top:16px">
        <template #header><span style="font-weight:bold">小组概览</span></template>
        <el-table :data="groupOverview" border stripe>
          <el-table-column prop="name" label="小组名称" />
          <el-table-column prop="courseName" label="课程" />
          <el-table-column prop="memberCount" label="成员数" />
          <el-table-column prop="overallAchievement" label="整体达成度">
            <template #default="{ row }">{{ row.overallAchievement ? (row.overallAchievement * 100).toFixed(1) + '%' : '-' }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 学生达成度查询 -->
      <el-card shadow="hover" style="margin-top:16px">
        <template #header>
          <div class="card-header-row">
            <span style="font-weight:bold">学生达成度查询</span>
            <div>
              <el-select v-model="studentSearchGroupId" placeholder="选择小组" style="width:180px" clearable>
                <el-option v-for="g in groups" :key="g.id" :label="g.groupName" :value="g.id" />
              </el-select>
              <el-button type="primary" @click="searchStudentAchievements" style="margin-left:8px">查询</el-button>
            </div>
          </div>
        </template>
        <el-table :data="studentAchievements" border stripe>
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column label="达成度">
            <template #default="{ row }">{{ row.achievement ? (row.achievement * 100).toFixed(1) + '%' : '-' }}</template>
          </el-table-column>
          <el-table-column label="小组得分" prop="groupScore" />
          <el-table-column label="贡献系数">
            <template #default="{ row }">{{ row.contributionRatio ? (row.contributionRatio * 100).toFixed(0) + '%' : '-' }}</template>
          </el-table-column>
          <el-table-column prop="bonus" label="加分" />
          <el-table-column prop="finalScore" label="最终成绩" />
        </el-table>
      </el-card>
    </template>

    <!-- 管理员：用户管理 -->
    <template v-if="user.roleCode === 'ADMIN'">
      <el-card shadow="hover" style="margin-top:16px">
        <template #header>
          <div class="card-header-row">
            <span style="font-weight:bold">用户管理</span>
            <div style="display:flex;gap:8px">
              <el-input v-model="userKeyword" placeholder="搜索用户..." style="width:200px" clearable size="small" />
              <el-button type="primary" @click="openUserCreateDialog" size="small">+ 创建用户</el-button>
            </div>
          </div>
        </template>
        <el-table :data="filteredUserList" border stripe v-loading="tableLoading">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="realName" label="姓名" />
          <el-table-column label="角色">
            <template #default="{ row }">
              <el-tag :type="row.roleCode === 'ADMIN' ? 'danger' : row.roleCode === 'TEACHER' ? 'warning' : 'info'" size="small">
                {{ row.roleCode === 'ADMIN' ? '管理员' : row.roleCode === 'TEACHER' ? '教师' : '学生' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-switch v-model="row.enabled" :active-value="true" :inactive-value="false" @change="toggleUser(row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" type="warning" link @click="openRoleDialog(row)">改角色</el-button>
              <el-button size="small" type="danger" link @click="deleteUser(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <!-- 学生：我的小组 & 成绩摘要 -->
    <template v-if="user.roleCode === 'STUDENT'">
      <el-row :gutter="16" style="margin-top:16px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span style="font-weight:bold">我的小组</span></template>
            <el-empty v-if="!myGroups || myGroups.length === 0" description="暂未加入小组" :image-size="80" />
            <div v-else v-for="g in myGroups" :key="g.id" class="my-group-item">
              <span class="group-name-text">{{ g.groupName }}</span>
              <el-tag :type="g.myRole === 'LEADER' ? 'warning' : 'info'" size="small">{{ g.myRole === 'LEADER' ? '组长' : '成员' }}</el-tag>
              <span style="color:#909399;font-size:12px">{{ g.courseName || '' }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span style="font-weight:bold">成绩摘要</span></template>
            <el-empty v-if="!myScoreSummary" description="暂无成绩" :image-size="80" />
            <div v-else class="score-summary">
              <div class="score-row"><span>整体达成度</span><strong>{{ (myScoreSummary.overallAchievement * 100).toFixed(1) }}%</strong></div>
              <div class="score-row"><span>最终成绩</span><strong>{{ myScoreSummary.finalScore }}</strong></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 修改角色对话框 -->
    <el-dialog v-model="roleVisible" title="修改用户角色" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户"><span style="font-weight:500">{{ roleEditUser?.realName || roleEditUser?.username || '' }}</span></el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="roleEditNewRole" style="width:100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="roleVisible = false">取消</el-button><el-button type="primary" @click="changeUserRole">确认修改</el-button></template>
    </el-dialog>

    <!-- 创建用户对话框 -->
    <el-dialog v-model="userCreateVisible" title="创建用户" width="450px">
      <el-form :model="userCreateForm" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="userCreateForm.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="userCreateForm.password" type="password" show-password /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="userCreateForm.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userCreateForm.roleCode">
            <el-option label="学生" value="STUDENT" /><el-option label="教师" value="TEACHER" /><el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="userCreateVisible = false">取消</el-button><el-button type="primary" @click="createUser">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const isTeacherOrAdmin = computed(() => user.roleCode === 'ADMIN' || user.roleCode === 'TEACHER')

// 个人资料
const profileForm = reactive({ username: '', realName: '', email: '', phone: '' })
const profileLoading = ref(false)

// 密码
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdLoading = ref(false)

// Git 身份
const gitForm = reactive({ gitUsername: '', gitEmail: '' })
const gitLoading = ref(false)

// 小组概览（教师/管理员）
const groups = ref([])
const groupOverview = ref([])

// 学生达成度查询
const studentSearchGroupId = ref(null)
const studentAchievements = ref([])

// 用户管理（管理员）
const tableLoading = ref(false)
const userList = ref([])
const userKeyword = ref('')
const filteredUserList = computed(() => {
  if (!userKeyword.value) return userList.value
  const kw = userKeyword.value.toLowerCase()
  return userList.value.filter(u =>
    (u.username||'').toLowerCase().includes(kw) || (u.realName||'').toLowerCase().includes(kw)
  )
})
const userCreateVisible = ref(false)
const userCreateForm = reactive({ username: '', password: '', realName: '', roleCode: 'STUDENT' })
const roleVisible = ref(false)
const roleEditUser = ref(null)
const roleEditNewRole = ref('STUDENT')

// 学生数据
const myGroups = ref([])
const myScoreSummary = ref(null)

onMounted(() => {
  loadProfile()
  loadGitIdentity()
  if (isTeacherOrAdmin.value) {
    loadGroups()
    loadGroupOverview()
  }
  if (user.roleCode === 'ADMIN') loadUserList()
  if (user.roleCode === 'STUDENT') {
    loadMyGroups()
    loadMyScoreSummary()
  }
})

async function loadProfile() {
  try {
    const { data } = await http.get('/settings/profile')
    if (data) Object.assign(profileForm, data)
  } catch (e) { console.error(e) }
}

async function loadGitIdentity() {
  try {
    const { data } = await http.get('/settings/git-identity')
    if (data) Object.assign(gitForm, data)
  } catch (e) { console.error(e) }
}

async function loadGroups() {
  try { const { data } = await http.get('/groups'); groups.value = data || [] } catch (e) { console.error(e) }
}

async function loadGroupOverview() {
  try { const { data } = await http.get('/settings/group-overview'); groupOverview.value = data || [] } catch (e) { console.error(e) }
}

async function loadUserList() {
  tableLoading.value = true
  try { const { data } = await http.get('/users'); userList.value = data || [] } catch (e) { console.error(e) } finally { tableLoading.value = false }
}

async function loadMyGroups() {
  try { const { data } = await http.get('/group/my-groups'); myGroups.value = data || [] } catch (e) { console.error(e) }
}

async function loadMyScoreSummary() {
  try { const { data } = await http.get('/student/scores'); myScoreSummary.value = data } catch (e) { console.error(e) }
}

// 保存个人资料
async function saveProfile() {
  profileLoading.value = true
  try {
    await http.put('/settings/profile', { ...profileForm })
    ElMessage.success('资料已更新')
    // Update localStorage
    const stored = JSON.parse(localStorage.getItem('user') || '{}')
    stored.realName = profileForm.realName
    localStorage.setItem('user', JSON.stringify(stored))
  } catch (e) { console.error(e) } finally { profileLoading.value = false }
}

// 修改密码
async function changePassword() {
  if (!pwdForm.oldPassword) return ElMessage.warning('请输入旧密码')
  if (!pwdForm.newPassword) return ElMessage.warning('请输入新密码')
  if (pwdForm.newPassword !== pwdForm.confirmPassword) return ElMessage.warning('两次密码输入不一致')
  pwdLoading.value = true
  try {
    await http.put('/settings/password', {
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码已修改')
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch (e) { console.error(e) } finally { pwdLoading.value = false }
}

// Git 绑定
async function saveGitIdentity() {
  gitLoading.value = true
  try {
    await http.put('/settings/git-identity', { ...gitForm })
    ElMessage.success('Git 身份已绑定')
  } catch (e) { console.error(e) } finally { gitLoading.value = false }
}

// 学生达成度查询
async function searchStudentAchievements() {
  if (!studentSearchGroupId.value) return ElMessage.warning('请选择小组')
  try {
    const { data } = await http.get(`/settings/student-achievements?groupId=${studentSearchGroupId.value}`)
    studentAchievements.value = data || []
  } catch (e) { console.error(e) }
}

// 用户管理
function openUserCreateDialog() {
  Object.assign(userCreateForm, { username: '', password: '', realName: '', roleCode: 'STUDENT' })
  userCreateVisible.value = true
}
async function createUser() {
  if (!userCreateForm.username || !userCreateForm.password) return ElMessage.warning('请填写用户名和密码')
  try {
    await http.post('/users', { ...userCreateForm })
    ElMessage.success('用户已创建')
    userCreateVisible.value = false
    loadUserList()
  } catch (e) { console.error(e) }
}
async function toggleUser(row) {
  try {
    await http.put(`/users/${row.id}/status`, { enabled: row.enabled })
    ElMessage.success(row.enabled ? '已启用' : '已禁用')
  } catch { loadUserList() }
}
async function openRoleDialog(row) {
  roleEditUser.value = row
  roleEditNewRole.value = row.roleCode || 'STUDENT'
  roleVisible.value = true
}
async function changeUserRole() {
  if (!roleEditUser.value) return
  try {
    await http.put(`/auth/users/${roleEditUser.value.id}/role`, { roleCode: roleEditNewRole.value })
    ElMessage.success('角色已更新')
    roleVisible.value = false
    loadUserList()
  } catch (e) { console.error(e) }
}
async function deleteUser(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户 "${row.realName || row.username}"？`, '确认删除', { type: 'warning' })
    await http.delete(`/users/${row.id}`)
    ElMessage.success('用户已删除')
    loadUserList()
  } catch (e) { console.error(e) }
}
</script>

<style scoped>
.card-header-row { display: flex; justify-content: space-between; align-items: center; }
.overview-item { text-align: center; }
.my-group-item { display: flex; align-items: center; gap: 8px; padding: 8px 0; border-bottom: 1px solid #ebeef5; }
.my-group-item:last-child { border-bottom: none; }
.group-name-text { font-weight: 500; }
.score-summary { display: flex; flex-direction: column; gap: 12px; }
.score-row { display: flex; justify-content: space-between; font-size: 14px; }
.score-row strong { font-size: 16px; }
</style>
