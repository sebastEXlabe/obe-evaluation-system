<template>
  <div>
    <div class="toolbar">
      <el-select v-model="courseId" @change="loadGroups" placeholder="选择课程" style="width:220px" clearable>
        <el-option v-for="c in courses" :key="c.id" :label="c.courseName + ' (' + c.semester + ')'" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="openCreateDialog">+ 创建小组</el-button>
      <el-button @click="loadGroups">刷新</el-button>
    </div>

    <el-row :gutter="16" v-loading="loading">
      <el-col v-for="g in groups" :key="g.id" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom:16px">
        <el-card shadow="hover" class="group-card">
          <template #header>
            <div class="card-header">
              <span class="group-name">{{ g.groupName }}</span>
              <el-tag :type="g.status === 1 ? 'success' : 'info'" size="small">{{ g.status === 1 ? '活跃' : '已归档' }}</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="card-item"><span class="label">课程：</span>{{ g.courseId ? (courses.find(c=>c.id===g.courseId)?.courseName || '课程'+g.courseId) : '-' }}</div>
            <div class="card-item">
              <span class="label">邀请码：</span>
              <el-tag type="warning" style="cursor:pointer" @click="copyCode(g.inviteCode)">{{ g.inviteCode || '-' }}</el-tag>
            </div>
            <div class="card-item"><span class="label">成员数：</span>{{ g.memberCount || 0 }} / {{ g.maxMembers || 8 }}</div>
            <div class="card-item"><span class="label">创建人：</span>{{ g.teacherId ? '教师ID:'+g.teacherId : '-' }}</div>
          </div>
          <div class="card-footer">
            <el-button size="small" type="primary" link @click="openMemberDialog(g)">查看成员</el-button>
            <el-button size="small" type="warning" link @click="editGroup(g)">编辑</el-button>
            <el-button size="small" type="danger" link @click="deleteGroup(g)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && groups.length === 0" description="暂无小组数据" />

    <!-- 创建/编辑小组对话框 -->
    <el-dialog v-model="groupVisible" :title="groupForm.id ? '编辑小组' : '创建小组'" width="450px">
      <el-form :model="groupForm" label-width="80px">
        <el-form-item label="小组名称"><el-input v-model="groupForm.groupName" placeholder="请输入小组名称" /></el-form-item>
        <el-form-item label="所属课程">
          <el-select v-model="groupForm.courseId" placeholder="请选择课程" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName + ' (' + c.semester + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大人数"><el-input-number v-model="groupForm.maxMembers" :min="2" :max="50" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="groupVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGroup">{{ groupForm.id ? '更新' : '创建' }}</el-button>
      </template>
    </el-dialog>

    <!-- 成员管理对话框 -->
    <el-dialog v-model="memberVisible" :title="'成员管理 - ' + (currentGroup?.groupName || '')" width="650px">
      <div style="margin-bottom:12px;display:flex;gap:8px">
        <el-select v-model="newMemberUserId" filterable remote :remote-method="searchUsers" :loading="userSearching" placeholder="搜索用户" style="flex:1" clearable>
          <el-option v-for="u in searchUserList" :key="u.id" :label="u.realName + ' (' + u.username + ')'" :value="u.id" />
        </el-select>
        <el-select v-model="newMemberRole" placeholder="角色" style="width:120px">
          <el-option label="组长" value="LEADER" />
          <el-option label="成员" value="MEMBER" />
        </el-select>
        <el-button type="primary" @click="addMember" :disabled="!newMemberUserId">添加成员</el-button>
      </div>
      <el-table :data="members" border stripe style="width:100%" v-loading="loading">
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色">
          <template #default="{ row }">
            <el-tooltip :content="getRoleDesc(row.roleCode || row.role)" placement="top">
              <el-tag :type="roleTagType(row.roleCode || row.role)" size="small">{{ roleLabel(row.roleCode || row.role) }}</el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button size="small" type="danger" link @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-divider />
      <el-button size="small" text @click="showHistory = !showHistory">📋 岗位轮换历史 {{ showHistory ? '▲' : '▼' }}</el-button>
      <el-table v-if="showHistory" :data="positionHistories" size="small" stripe style="margin-top:8px" max-height="200">
        <el-table-column prop="userName" label="成员" width="80" />
        <el-table-column label="变更" width="120">
          <template #default="{row}">{{ row.oldRole||'加入' }} → {{ row.newRole }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="120" show-overflow-tooltip />
        <el-table-column prop="changedByName" label="操作人" width="80" />
        <el-table-column label="时间" width="140">
          <template #default="{row}">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const courses = ref([])
const courseId = ref(null)
const groups = ref([])
const loading = ref(false)

const groupVisible = ref(false)
const groupForm = reactive({ id: null, groupName: '', courseId: null, maxMembers: 8 })

const memberVisible = ref(false)
const currentGroup = ref(null)
const members = ref([])
const showHistory = ref(false)
const positionHistories = ref([])
const searchUserList = ref([])
const userSearching = ref(false)
const newMemberUserId = ref(null)
const newMemberRole = ref('MEMBER')

onMounted(async () => {
  try { const { data } = await http.get('/courses'); courses.value = data || [] } catch (e) { console.error(e) }
  loadGroups()
})

async function loadGroups() {
  loading.value = true
  try {
    const params = courseId.value ? `?courseId=${courseId.value}` : ''
    const { data } = await http.get(`/groups${params}`)
    groups.value = data || []
    await enrichGroupMemberCounts()
  } catch (e) { console.error(e) } finally { loading.value = false }
}

async function enrichGroupMemberCounts() {
  const results = await Promise.allSettled(
    groups.value.map(g => http.get(`/groups/${g.id}/members`))
  )
  results.forEach((r, i) => {
    if (r.status === 'fulfilled') {
      const members = r.value?.data || r.value || []
      groups.value[i].memberCount = Array.isArray(members) ? members.length : 0
    }
  })
}

function openCreateDialog() {
  Object.assign(groupForm, { id: null, groupName: '', courseId: courseId.value, maxMembers: 8 })
  groupVisible.value = true
}

function editGroup(group) {
  Object.assign(groupForm, {
    id: group.id,
    groupName: group.groupName || '',
    courseId: group.courseId || courseId.value,
    maxMembers: group.maxMembers || 8
  })
  groupVisible.value = true
}

async function saveGroup() {
  if (!groupForm.groupName) return ElMessage.warning('请输入小组名称')
  if (!groupForm.courseId) return ElMessage.warning('请选择课程')
  const { id } = groupForm
  try {
    if (id) await http.put(`/groups/${id}`, { ...groupForm })
    else await http.post('/groups', { ...groupForm })
    groupVisible.value = false
    ElMessage.success(id ? '已更新' : '已创建')
    loadGroups()
  } catch { ElMessage.error('操作失败') }
}

async function deleteGroup(group) {
  try {
    await ElMessageBox.confirm('确定删除小组 "' + group.groupName + '"？此操作不可恢复。', '确认删除', { type: 'warning' })
    await http.delete(`/groups/${group.id}`)
    ElMessage.success('小组已删除')
    loadGroups()
  } catch { ElMessage.error('操作失败') }
}

async function openMemberDialog(group) {
  currentGroup.value = group
  memberVisible.value = true; showHistory.value = false
  newMemberUserId.value = null
  newMemberRole.value = 'MEMBER'
  await loadMembers()
  http.get('/groups/'+group.id+'/position-history').then(r => positionHistories.value = r.data || []).catch(()=>{})
}

async function loadMembers() {
  if (!currentGroup.value) return
  try {
    const { data } = await http.get('/groups/' + currentGroup.value.id + '/members')
    members.value = data || []
  } catch (e) { console.error(e) }
}

async function searchUsers(query) {
  if (!query) { searchUserList.value = []; return }
  userSearching.value = true
  try {
    const { data } = await http.get('/auth/users/search?keyword=' + encodeURIComponent(query))
    searchUserList.value = data || []
  } catch (e) { console.error(e) } finally { userSearching.value = false }
}

const roleLabels = { PM: '项目经理', DEVELOPER: '开发工程师', TESTER: '测试工程师', DOC_ADMIN: '文档管理员', LEADER: '组长', MEMBER: '成员' }
const roleDescs = { PM: '评价：计划制定·进度控制·团队协调', DEVELOPER: '评价：代码质量·技术实现·问题解决', TESTER: '评价：测试覆盖·Bug跟踪·质量报告', DOC_ADMIN: '评价：文档规范·完整性·及时性' }
const roleTagType = r => ({ PM: 'warning', DEVELOPER: 'primary', TESTER: 'success', DOC_ADMIN: 'danger' }[r] || 'info')
const roleLabel = r => roleLabels[r] || r
const getRoleDesc = r => roleDescs[r] || '通用评价'

async function addMember() {
  if (!newMemberUserId.value) return
  try {
    await http.post(`/groups/${currentGroup.value.id}/members`, {
      userId: newMemberUserId.value,
      role: newMemberRole.value
    })
    ElMessage.success('成员已添加')
    newMemberUserId.value = null
    loadMembers()
    loadGroups()
  } catch (e) { console.error(e) }
}

async function removeMember(row) {
  try {
    await ElMessageBox.confirm(`确定移除成员 "${row.realName || row.username}"？`, '确认移除', { type: 'warning' })
    await http.delete(`/groups/${currentGroup.value.id}/members/${row.id || row.userId}`)
    ElMessage.success('成员已移除')
    loadMembers()
    loadGroups()
  } catch (e) { console.error(e) }
}

async function copyCode(code) {
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success('邀请码已复制到剪贴板')
  } catch {
    ElMessage.info('邀请码: ' + code)
  }
}
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.group-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.group-name { font-weight: bold; font-size: 15px; }
.card-body { font-size: 13px; line-height: 2; }
.card-body .label { color: #909399; }
.card-footer { display: flex; justify-content: flex-end; gap: 8px; padding-top: 8px; border-top: 1px solid #ebeef5; }
</style>
