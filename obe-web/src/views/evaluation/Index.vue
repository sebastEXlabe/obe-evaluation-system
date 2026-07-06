<template>
  <div>
    <div class="toolbar">
      <el-select v-model="groupId" @change="onGroupChange" placeholder="选择小组" style="width:220px" clearable filterable>
        <el-option v-for="g in groups" :key="g.id" :label="g.groupName" :value="g.id" />
      </el-select>
      <el-button @click="refreshAll">刷新</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 小组评价 -->
      <el-tab-pane label="小组评价" name="groupEval">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openGroupEvalDialog()">+ 添加评价</el-button>
        </div>
        <el-table :data="groupEvaluations" border stripe v-loading="loading">
          <el-table-column prop="dimension" label="评价维度" min-width="120" />
          <el-table-column prop="score" label="分数" width="100" />
          <el-table-column prop="comment" label="评语" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openGroupEvalDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" link @click="deleteGroupEval(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 角色评价 -->
      <el-tab-pane label="角色评价" name="roleEval">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openRoleEvalDialog()">+ 添加评价</el-button>
        </div>
        <el-table :data="roleEvaluations" border stripe v-loading="loading">
          <el-table-column prop="realName" label="用户" width="100" />
          <el-table-column label="角色" width="80">
            <template #default="{ row }">
              <el-tag :type="row.role === 'LEADER' ? 'warning' : 'info'" size="small">{{ row.role === 'LEADER' ? '组长' : '成员' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dimension" label="评价维度" min-width="100" />
          <el-table-column prop="score" label="分数" width="80" />
          <el-table-column prop="comment" label="评语" min-width="160" show-overflow-tooltip />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openRoleEvalDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" link @click="deleteRoleEval(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 个人成绩 -->
      <el-tab-pane label="个人成绩" name="personalScores">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="calcAllScores" :loading="loading" :disabled="!groupId">计算全员成绩</el-button>
        </div>
        <el-table :data="personalScores" border stripe v-loading="loading">
          <el-table-column prop="realName" label="用户" width="100" />
          <el-table-column prop="groupTotalScore" label="小组分" width="100" />
          <el-table-column label="贡献系数" width="150">
            <template #default="{ row }">
              <el-progress :percentage="Math.round((row.contributionRatio || 0) * 1000) / 10" :stroke-width="16" :format="() => ((row.contributionRatio || 0) * 100).toFixed(0) + '%'" />
            </template>
          </el-table-column>
          <el-table-column prop="bonusTotal" label="加分" width="80" />
          <el-table-column prop="finalScore" label="最终成绩" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 小组评价对话框 -->
    <el-dialog v-model="groupEvalVisible" :title="groupEvalForm.id ? '编辑评价' : '添加评价'" width="500px">
      <el-form :model="groupEvalForm" label-width="80px">
        <el-form-item label="评价维度"><el-input v-model="groupEvalForm.dimension" placeholder="如：项目完成度、代码质量等" /></el-form-item>
        <el-form-item label="分数"><el-input-number v-model="groupEvalForm.score" :min="0" :max="100" /></el-form-item>
        <el-form-item label="评语"><el-input v-model="groupEvalForm.comment" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="groupEvalVisible=false">取消</el-button><el-button type="primary" @click="saveGroupEval">{{ groupEvalForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>

    <!-- 角色评价对话框 -->
    <el-dialog v-model="roleEvalVisible" :title="roleEvalForm.id ? '编辑评价' : '添加评价'" width="500px">
      <el-form :model="roleEvalForm" label-width="80px">
        <el-form-item label="用户">
          <el-select v-if="!roleEvalForm.id" v-model="roleEvalForm.userId" filterable placeholder="搜索成员" style="width:100%">
            <el-option v-for="m in groupMembers" :key="m.userId || m.id" :label="m.realName || m.username" :value="m.userId || m.id" />
          </el-select>
          <el-input v-else :value="roleEvalForm.realName" disabled />
        </el-form-item>
        <el-form-item label="评价维度"><el-input v-model="roleEvalForm.dimension" /></el-form-item>
        <el-form-item label="分数"><el-input-number v-model="roleEvalForm.score" :min="0" :max="100" /></el-form-item>
        <el-form-item label="评语"><el-input v-model="roleEvalForm.comment" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="roleEvalVisible=false">取消</el-button><el-button type="primary" @click="saveRoleEval">{{ roleEvalForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const loading = ref(false)
const groups = ref([])
const groupId = ref(null)
const activeTab = ref('groupEval')
const groupMembers = ref([])

const groupEvaluations = ref([])
const groupEvalVisible = ref(false)
const groupEvalForm = reactive({ id: null, dimension: '', score: 0, comment: '' })

const roleEvaluations = ref([])
const roleEvalVisible = ref(false)
const roleEvalForm = reactive({ id: null, userId: null, realName: '', role: 'MEMBER', dimension: '', score: 0, comment: '' })

const personalScores = ref([])

onMounted(() => { loadGroups() })

async function loadGroups() {
  try { const { data } = await http.get('/groups'); groups.value = data || [] } catch (e) { console.error(e) }
}

async function onGroupChange() {
  groupMembers.value = []
  if (groupId.value) await loadGroupMembers()
  refreshAll()
}

async function loadGroupMembers() {
  try { const { data } = await http.get(`/groups/${groupId.value}/members`); groupMembers.value = data || [] } catch (e) { console.error(e) }
}

function refreshAll() { onTabChange(activeTab.value) }

function onTabChange(tab) {
  if (!groupId.value) return
  if (tab === 'groupEval') loadGroupEvaluations()
  else if (tab === 'roleEval') loadRoleEvaluations()
  else if (tab === 'personalScores') loadPersonalScores()
}

async function loadGroupEvaluations() {
  loading.value = true
  try { const { data } = await http.get(`/evaluation/group?groupId=${groupId.value}`); groupEvaluations.value = data || [] } catch (e) { console.error(e) } finally { loading.value = false }
}
async function loadRoleEvaluations() {
  loading.value = true
  try { const { data } = await http.get(`/evaluation/role?groupId=${groupId.value}`); roleEvaluations.value = data || [] } catch (e) { console.error(e) } finally { loading.value = false }
}
async function loadPersonalScores() {
  loading.value = true
  try {
    const { data } = await http.get(`/evaluation/personal-scores?groupId=${groupId.value}`)
    personalScores.value = (data || []).map(s => {
      const member = groupMembers.value.find(m => (m.userId || m.id) === (s.userId || s.id))
      return { ...s, realName: s.realName || member?.realName || member?.username || s.realName || `用户${s.userId || s.id}` }
    })
  } catch (e) { console.error(e) } finally { loading.value = false }
}

// 小组评价 CRUD
function openGroupEvalDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(groupEvalForm, { ...row })
  else Object.assign(groupEvalForm, { id: null, dimension: '', score: 0, comment: '' })
  groupEvalVisible.value = true
}
async function saveGroupEval() {
  const { id } = groupEvalForm
  try {
    if (id) await http.put(`/evaluation/group/${id}`, { ...groupEvalForm, groupId: groupId.value })
    else await http.post('/evaluation/group', { ...groupEvalForm, groupId: groupId.value })
    groupEvalVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadGroupEvaluations()
  } catch { ElMessage.error('操作失败') }
}
async function deleteGroupEval(row) {
  try { await ElMessageBox.confirm('确定删除该评价？'); await http.delete(`/evaluation/group/${row.id}`); ElMessage.success('已删除'); loadGroupEvaluations() } catch (e) { console.error(e) }
}

// 角色评价 CRUD
function openRoleEvalDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(roleEvalForm, { ...row })
  else Object.assign(roleEvalForm, { id: null, userId: null, realName: '', role: 'MEMBER', dimension: '', score: 0, comment: '' })
  roleEvalVisible.value = true
}
async function saveRoleEval() {
  const { id } = roleEvalForm
  if (!roleEvalForm.userId && !id) return ElMessage.warning('请选择用户')
  try {
    if (id) await http.put(`/evaluation/role/${id}`, { ...roleEvalForm, groupId: groupId.value })
    else await http.post('/evaluation/role', { ...roleEvalForm, groupId: groupId.value })
    roleEvalVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadRoleEvaluations()
  } catch { ElMessage.error('操作失败') }
}
async function deleteRoleEval(row) {
  try { await ElMessageBox.confirm('确定删除该评价？'); await http.delete(`/evaluation/role/${row.id}`); ElMessage.success('已删除'); loadRoleEvaluations() } catch (e) { console.error(e) }
}

// 计算全员成绩
async function calcAllScores() {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  try {
    await http.post(`/evaluation/calculate?groupId=${groupId.value}`)
    ElMessage.success('成绩计算完成')
    loadPersonalScores()
  } catch (e) { console.error(e) }
}
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
</style>
