<template>
  <div>
    <div class="toolbar">
      <el-select v-model="groupId" @change="onGroupChange" placeholder="选择小组" style="width:220px" clearable>
        <el-option v-for="g in groups" :key="g.id" :label="g.groupName" :value="g.id" />
      </el-select>
      <el-button @click="refreshAll">刷新</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 里程碑 Tab -->
      <el-tab-pane label="里程碑" name="milestones">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openMilestoneDialog()">+ 添加里程碑</el-button>
        </div>
        <el-table :data="milestones" border stripe v-loading="loading">
          <el-table-column prop="title" label="名称" />
          <el-table-column label="CDIO阶段">
            <template #default="{ row }">
              <el-tag :type="cdioTagType(row.cdioPhase)" size="small">{{ cdioLabel(row.cdioPhase) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="截止日期" width="120" />
          <el-table-column label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'COMPLETED' ? 'success' : row.status === 'IN_PROGRESS' ? 'warning' : 'info'" size="small">
                {{ row.status === 'COMPLETED' ? '已完成' : row.status === 'IN_PROGRESS' ? '进行中' : '未开始' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openMilestoneDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" link @click="deleteMilestone(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 任务看板 Tab -->
      <el-tab-pane label="任务" name="tasks">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openTaskDialog()">+ 添加任务</el-button>
        </div>
        <div class="kanban">
          <div v-for="col in taskColumns" :key="col.status" class="kanban-col">
            <div class="kanban-col-header" :style="{ borderTop: '3px solid ' + col.color }">
              <span>{{ col.label }}</span>
              <el-tag size="small">{{ getTaskCount(col.status) }}</el-tag>
            </div>
            <div class="kanban-list">
              <el-card v-for="element in taskColMap[col.status]" :key="element.id" shadow="hover" class="task-card">
                <div class="task-title">{{ element.title }}</div>
                <div class="task-info">
                  <el-tag v-if="element.priority" :type="element.priority === 'HIGH' ? 'danger' : element.priority === 'MEDIUM' ? 'warning' : 'info'" size="small">{{ element.priority === 'HIGH' ? '高' : element.priority === 'MEDIUM' ? '中' : '低' }}</el-tag>
                  <span v-if="element.assigneeName" class="task-assignee">{{ element.assigneeName }}</span>
                </div>
                <div class="task-actions">
                  <el-dropdown v-if="col.status !== 'TODO'" @command="(cmd) => moveTask(element, cmd)" style="margin-right:4px">
                    <el-button size="small" type="warning" link>&lt;</el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-if="col.status !== 'TODO'" command="TODO">移到待办</el-dropdown-item>
                        <el-dropdown-item v-if="col.status !== 'DOING'" command="DOING">移到进行中</el-dropdown-item>
                        <el-dropdown-item v-if="col.status !== 'DONE'" command="DONE">移到已完成</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  <el-dropdown v-if="col.status !== 'DONE'" @command="(cmd) => moveTask(element, cmd)">
                    <el-button size="small" type="success" link>&gt;</el-button>
                    <template #dropdown>
                      <el-dropdown-item v-if="col.status !== 'TODO'" command="TODO">移到待办</el-dropdown-item>
                      <el-dropdown-item v-if="col.status !== 'DOING'" command="DOING">移到进行中</el-dropdown-item>
                      <el-dropdown-item v-if="col.status !== 'DONE'" command="DONE">移到已完成</el-dropdown-item>
                    </template>
                  </el-dropdown>
                  <el-button size="small" type="primary" link @click="openTaskDialog(element)">编辑</el-button>
                  <el-button size="small" type="danger" link @click="deleteTask(element)">删除</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 日志 Tab -->
      <el-tab-pane label="日志" name="journals">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openJournalDialog()">+ 添加日志</el-button>
        </div>
        <el-table :data="journals" border stripe v-loading="loading">
          <el-table-column prop="realName" label="用户" width="100" />
          <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="时间" width="160" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openJournalDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" link @click="deleteJournal(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Git 提交 Tab -->
      <el-tab-pane label="Git 提交" name="gitCommits">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openGitCommitDialog()">+ 添加提交记录</el-button>
        </div>
        <el-table :data="gitCommits" border stripe v-loading="loading">
          <!-- Git提交用户列：优先API返回userName（后端已解析），查不到再本地查找 -->
<el-table-column label="用户" width="100"><template #default="{row}">{{ row.userName || userName(row.userId) }}</template></el-table-column>
          <el-table-column prop="message" label="提交信息" min-width="200" show-overflow-tooltip />
          <el-table-column prop="additions" label="+行数" width="80" />
          <el-table-column prop="deletions" label="-行数" width="80" />
          <el-table-column label="时间" width="160"><template #default="{row}">{{ formatTime(row.committedAt) }}</template></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openGitCommitDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" link @click="deleteGitCommit(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 贡献 Tab -->
      <el-tab-pane label="贡献" name="contributions">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openContributionDialog()">+ 添加贡献</el-button>
        </div>
        <el-table :data="contributions" border stripe v-loading="loading">
          <el-table-column prop="realName" label="用户" width="100" />
          <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
          <el-table-column prop="bonus" label="加分" width="80" />
          <el-table-column label="审核状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.approved ? 'success' : 'warning'" size="small">{{ row.approved ? '已通过' : '待审核' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openContributionDialog(row)">编辑</el-button>
              <el-button v-if="isTeacher && !row.approved" size="small" type="success" link @click="approveContribution(row)">通过</el-button>
              <el-button size="small" type="danger" link @click="deleteContribution(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Git 仓库 Tab -->
      <el-tab-pane label="Git 仓库" name="gitRepos">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="openGitRepoDialog()">+ 添加仓库</el-button>
        </div>
        <el-table :data="gitRepos" border stripe v-loading="loading">
          <el-table-column label="平台" width="100">
            <template #default="{ row }"><el-tag size="small">{{ row.platform || '-' }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="owner" label="拥有者" width="120" />
          <el-table-column prop="repo" label="仓库名" min-width="150" />
          <el-table-column prop="branch" label="分支" width="120" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openGitRepoDialog(row)">编辑</el-button>
              <el-button size="small" type="success" link @click="syncGitRepo(row)">同步</el-button>
              <el-button size="small" type="danger" link @click="deleteGitRepo(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 里程碑对话框 -->
    <el-dialog v-model="milestoneVisible" :title="milestoneForm.id ? '编辑里程碑' : '添加里程碑'" width="500px">
      <el-form :model="milestoneForm" label-width="90px">
        <el-form-item label="名称"><el-input v-model="milestoneForm.title" /></el-form-item>
        <el-form-item label="CDIO阶段">
          <el-select v-model="milestoneForm.cdioPhase">
            <el-option label="构思" value="CONCEIVE" /><el-option label="设计" value="DESIGN" />
            <el-option label="实施" value="IMPLEMENT" /><el-option label="运行" value="OPERATE" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="milestoneForm.dueDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="milestoneForm.status">
            <el-option label="未开始" value="NOT_STARTED" /><el-option label="进行中" value="IN_PROGRESS" /><el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="milestoneForm.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="milestoneVisible=false">取消</el-button><el-button type="primary" @click="saveMilestone">{{ milestoneForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>

    <!-- 任务对话框 -->
    <el-dialog v-model="taskVisible" :title="taskForm.id ? '编辑任务' : '添加任务'" width="500px">
      <el-form :model="taskForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="taskForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="taskForm.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="taskForm.status">
            <el-option label="待办" value="TODO" /><el-option label="进行中" value="DOING" /><el-option label="已完成" value="DONE" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="taskForm.priority">
            <el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" /><el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="taskForm.assigneeId" filterable placeholder="搜索成员" style="width:100%">
            <el-option v-for="m in groupMembers" :key="m.userId || m.id" :label="m.realName || m.username" :value="m.userId || m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="taskForm.dueDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="taskVisible=false">取消</el-button><el-button type="primary" @click="saveTask">{{ taskForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>

    <!-- 日志对话框 -->
    <el-dialog v-model="journalVisible" :title="journalForm.id ? '编辑日志' : '添加日志'" width="500px">
      <el-form :model="journalForm" label-width="60px">
        <el-form-item label="内容"><el-input v-model="journalForm.content" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="journalVisible=false">取消</el-button><el-button type="primary" @click="saveJournal">{{ journalForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>

    <!-- Git提交对话框 -->
    <el-dialog v-model="gitCommitVisible" :title="gitCommitForm.id ? '编辑提交' : '添加提交'" width="500px">
      <el-form :model="gitCommitForm" label-width="80px">
        <el-form-item label="提交信息"><el-input v-model="gitCommitForm.message" /></el-form-item>
        <el-form-item label="新增行数"><el-input-number v-model="gitCommitForm.addedLines" :min="0" /></el-form-item>
        <el-form-item label="删除行数"><el-input-number v-model="gitCommitForm.removedLines" :min="0" /></el-form-item>
        <el-form-item label="提交时间"><el-date-picker v-model="gitCommitForm.commitTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="gitCommitVisible=false">取消</el-button><el-button type="primary" @click="saveGitCommit">{{ gitCommitForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>

    <!-- 贡献对话框 -->
    <el-dialog v-model="contributionVisible" :title="contributionForm.id ? '编辑贡献' : '添加贡献'" width="500px">
      <el-form :model="contributionForm" label-width="60px">
        <el-form-item label="描述"><el-input v-model="contributionForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="加分"><el-input-number v-model="contributionForm.bonus" :min="0" :max="100" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="contributionVisible=false">取消</el-button><el-button type="primary" @click="saveContribution">{{ contributionForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>

    <!-- Git仓库对话框 -->
    <el-dialog v-model="gitRepoVisible" :title="gitRepoForm.id ? '编辑仓库' : '添加仓库'" width="500px">
      <el-form :model="gitRepoForm" label-width="80px">
        <el-form-item label="平台">
          <el-select v-model="gitRepoForm.platform">
            <el-option label="GitHub" value="github" /><el-option label="GitLab" value="gitlab" /><el-option label="Gitee" value="gitee" />
          </el-select>
        </el-form-item>
        <el-form-item label="拥有者"><el-input v-model="gitRepoForm.owner" /></el-form-item>
        <el-form-item label="仓库名"><el-input v-model="gitRepoForm.repo" /></el-form-item>
        <el-form-item label="分支"><el-input v-model="gitRepoForm.branch" placeholder="main" /></el-form-item>
        <el-form-item label="Token"><el-input v-model="gitRepoForm.token" type="password" show-password placeholder="访问令牌（可选）" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="gitRepoVisible=false">取消</el-button><el-button type="primary" @click="saveGitRepo">{{ gitRepoForm.id ? '更新' : '创建' }}</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const isTeacher = computed(() => user.roleCode === 'ADMIN' || user.roleCode === 'TEACHER')

const loading = ref(false)
const groups = ref([])
const groupId = ref(null)
const activeTab = ref('milestones')
const groupMembers = ref([])

// 里程碑
const milestones = ref([])
const milestoneVisible = ref(false)
const milestoneForm = reactive({ id: null, title: '', cdioPhase: 'CONCEIVE', dueDate: null, status: 'NOT_STARTED', description: '' })

// 任务看板
const tasks = ref([])
const taskVisible = ref(false)
const taskForm = reactive({ id: null, title: '', description: '', status: 'TODO', priority: 'MEDIUM', assigneeId: null, dueDate: null })
const taskColumns = [
  { status: 'TODO', label: '待办', color: '#409eff' },
  { status: 'DOING', label: '进行中', color: '#e6a23c' },
  { status: 'DONE', label: '已完成', color: '#67c23a' }
]
const taskColMap = computed(() => {
  const map = { TODO: [], DOING: [], DONE: [] }
  tasks.value.forEach(t => { if (map[t.status]) map[t.status].push(t) })
  return map
})

// 日志
const journals = ref([])
const journalVisible = ref(false)
const journalForm = reactive({ id: null, content: '' })

// Git提交
const gitCommits = ref([])
const gitCommitVisible = ref(false)
const gitCommitForm = reactive({ id: null, message: '', addedLines: 0, removedLines: 0, commitTime: null })

// 贡献
const contributions = ref([])
const contributionVisible = ref(false)
const contributionForm = reactive({ id: null, description: '', bonus: 0 })

// Git仓库
const gitRepos = ref([])
const gitRepoVisible = ref(false)
const gitRepoForm = reactive({ id: null, platform: 'github', owner: '', repo: '', branch: 'main', token: '' })

onMounted(() => { loadGroups() })

async function loadGroups() {
  try { const { data } = await http.get('/groups'); groups.value = data || [] } catch (e) { console.error(e) }
}

function onGroupChange() {
  groupMembers.value = []
  refreshAll()
  if (groupId.value) loadGroupMembers()
}

async function loadGroupMembers() {
  try { const { data } = await http.get(`/groups/${groupId.value}/members`); groupMembers.value = data || [] } catch (e) { console.error(e) }
}

function refreshAll() { onTabChange(activeTab.value) }

function onTabChange(tab) {
  if (!groupId.value) return
  switch (tab) {
    case 'milestones': loadMilestones(); break
    case 'tasks': loadTasks(); break
    case 'journals': loadJournals(); break
    case 'gitCommits': loadGitCommits(); break
    case 'contributions': loadContributions(); break
    case 'gitRepos': loadGitRepos(); break
  }
}

async function loadMilestones() { loading.value = true; try { const { data } = await http.get(`/project/milestones?groupId=${groupId.value}`); milestones.value = Array.isArray(data)?data:(data?.records||[]) } catch (e) { console.error(e) } finally { loading.value = false } }
async function loadTasks() { loading.value = true; try { const { data } = await http.get(`/project/tasks?groupId=${groupId.value}`); tasks.value = Array.isArray(data)?data:(data?.records||[]) } catch (e) { console.error(e) } finally { loading.value = false } }
async function loadJournals() { loading.value = true; try { const { data } = await http.get(`/project/journals?groupId=${groupId.value}`); journals.value = Array.isArray(data)?data:(data?.records||[]) } catch (e) { console.error(e) } finally { loading.value = false } }
async function loadGitCommits() { loading.value = true; try { const { data } = await http.get(`/project/git-commits?groupId=${groupId.value}`); gitCommits.value = Array.isArray(data)?data:(data?.records||[]) } catch (e) { console.error(e) } finally { loading.value = false } }
async function loadContributions() { loading.value = true; try { const { data } = await http.get(`/project/contributions?groupId=${groupId.value}`); contributions.value = Array.isArray(data)?data:(data?.records||[]) } catch (e) { console.error(e) } finally { loading.value = false } }
async function loadGitRepos() { loading.value = true; try { const { data } = await http.get(`/project/git-repos?groupId=${groupId.value}`); gitRepos.value = data || [] } catch (e) { console.error(e) } finally { loading.value = false } }

function cdioLabel(v) { return { CONCEIVE: '构思', DESIGN: '设计', IMPLEMENT: '实施', OPERATE: '运行' }[v] || v }
function cdioTagType(v) { return { CONCEIVE: 'primary', DESIGN: 'success', IMPLEMENT: 'warning', OPERATE: 'danger' }[v] || 'info' }
function getTaskCount(status) { return tasks.value.filter(t => t.status === status).length }
function userName(uid) { const u = groupMembers.value.find(m => m.userId === uid); return u ? (u.realName || u.username) : (uid || '') }
function formatTime(t) { if (!t) return ''; const d = new Date(t); return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', {hour:'2-digit',minute:'2-digit'}) }

// 里程碑 CRUD
function openMilestoneDialog(row) {
  if (row) Object.assign(milestoneForm, { ...row })
  else Object.assign(milestoneForm, { id: null, title: '', cdioPhase: 'CONCEIVE', dueDate: null, status: 'NOT_STARTED', description: '' })
  milestoneVisible.value = true
}
async function saveMilestone() {
  const { id } = milestoneForm
  try {
    if (id) await http.put(`/project/milestones/${id}`, { ...milestoneForm, groupId: groupId.value })
    else await http.post('/project/milestones', { ...milestoneForm, groupId: groupId.value })
    milestoneVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadMilestones()
  } catch { ElMessage.error('操作失败') }
}
async function deleteMilestone(row) {
  try { await ElMessageBox.confirm('确定删除该里程碑？'); await http.delete(`/project/milestones/${row.id}`); ElMessage.success('已删除'); loadMilestones() } catch (e) { console.error(e) }
}

// 任务 CRUD + 拖拽
function openTaskDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(taskForm, { ...row })
  else Object.assign(taskForm, { id: null, title: '', description: '', status: 'TODO', priority: 'MEDIUM', assigneeId: null, dueDate: null })
  taskVisible.value = true
}
async function saveTask() {
  const { id } = taskForm
  try {
    if (id) await http.put(`/project/tasks/${id}`, { ...taskForm, groupId: groupId.value })
    else await http.post('/project/tasks', { ...taskForm, groupId: groupId.value })
    taskVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadTasks()
  } catch { ElMessage.error('操作失败') }
}
async function deleteTask(row) {
  try { await ElMessageBox.confirm('确定删除该任务？'); await http.delete(`/project/tasks/${row.id}`); ElMessage.success('已删除'); loadTasks() } catch (e) { console.error(e) }
}
async function moveTask(task, newStatus) {
  try {
    await http.put(`/project/tasks/${task.id}/status`, { status: newStatus })
    ElMessage.success('任务已移动')
    loadTasks()
  } catch { ElMessage.error('操作失败') }
}

// 日志 CRUD
function openJournalDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(journalForm, { ...row })
  else Object.assign(journalForm, { id: null, content: '' })
  journalVisible.value = true
}
async function saveJournal() {
  const { id } = journalForm
  try {
    if (id) await http.put(`/project/journals/${id}`, { ...journalForm, groupId: groupId.value })
    else await http.post('/project/journals', { ...journalForm, groupId: groupId.value })
    journalVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadJournals()
  } catch { ElMessage.error('操作失败') }
}
async function deleteJournal(row) {
  try { await ElMessageBox.confirm('确定删除该日志？'); await http.delete(`/project/journals/${row.id}`); ElMessage.success('已删除'); loadJournals() } catch (e) { console.error(e) }
}

// Git提交 CRUD
function openGitCommitDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(gitCommitForm, { id: row.id, message: row.message || '', addedLines: row.additions || 0, removedLines: row.deletions || 0, commitTime: row.committedAt || null })
  else Object.assign(gitCommitForm, { id: null, message: '', addedLines: 0, removedLines: 0, commitTime: null })
  gitCommitVisible.value = true
}
async function saveGitCommit() {
  const { id } = gitCommitForm
  try {
    if (id) await http.put(`/project/git-commits/${id}`, { ...gitCommitForm, groupId: groupId.value })
    else await http.post('/project/git-commits', { ...gitCommitForm, groupId: groupId.value })
    gitCommitVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadGitCommits()
  } catch { ElMessage.error('操作失败') }
}
async function deleteGitCommit(row) {
  try { await ElMessageBox.confirm('确定删除该提交记录？'); await http.delete(`/project/git-commits/${row.id}`); ElMessage.success('已删除'); loadGitCommits() } catch (e) { console.error(e) }
}

// 贡献 CRUD + 审核
function openContributionDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(contributionForm, { ...row })
  else Object.assign(contributionForm, { id: null, description: '', bonus: 0 })
  contributionVisible.value = true
}
async function saveContribution() {
  const { id } = contributionForm
  try {
    if (id) await http.put(`/project/contributions/${id}`, { ...contributionForm, groupId: groupId.value })
    else await http.post('/project/contributions', { ...contributionForm, groupId: groupId.value })
    contributionVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadContributions()
  } catch { ElMessage.error('操作失败') }
}
async function deleteContribution(row) {
  try { await ElMessageBox.confirm('确定删除该贡献记录？'); await http.delete(`/project/contributions/${row.id}`); ElMessage.success('已删除'); loadContributions() } catch (e) { console.error(e) }
}
async function approveContribution(row) {
  try {
    await http.put(`/project/contributions/${row.id}/approve`)
    ElMessage.success('已通过审核'); loadContributions()
  } catch { ElMessage.error('操作失败') }
}

// Git仓库 CRUD + 同步
function openGitRepoDialog(row) {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  if (row) Object.assign(gitRepoForm, { ...row })
  else Object.assign(gitRepoForm, { id: null, platform: 'github', owner: '', repo: '', branch: 'main', token: '' })
  gitRepoVisible.value = true
}
async function saveGitRepo() {
  const { id } = gitRepoForm
  try {
    if (id) await http.put(`/project/git-repos/${id}`, { ...gitRepoForm, groupId: groupId.value })
    else await http.post('/project/git-repos', { ...gitRepoForm, groupId: groupId.value })
    gitRepoVisible.value = false; ElMessage.success(id ? '已更新' : '已创建'); loadGitRepos()
  } catch { ElMessage.error('操作失败') }
}
async function deleteGitRepo(row) {
  try { await ElMessageBox.confirm('确定删除该仓库配置？'); await http.delete(`/project/git-repos/${row.id}`); ElMessage.success('已删除'); loadGitRepos() } catch (e) { console.error(e) }
}
async function syncGitRepo(row) {
  if (!row || !row.id) return ElMessage.warning('仓库信息不完整')
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/project/git-repos/' + row.id + '/sync', {
      method: 'POST',
      headers: { 'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json' }
    })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      const d = data.data
      ElMessage.success(d.totalNewCommits > 0 ? '同步完成：' + d.totalNewCommits + '条新提交' : '已是最新')
      loadGitCommits()
    } else {
      ElMessage.error(data.message || '同步失败')
    }
  } catch (e) {
    ElMessage.error('同步请求失败')
  }
}
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.kanban { display: flex; gap: 12px; }
.kanban-col { flex: 1; background: #f5f7fa; border-radius: 8px; padding: 12px; min-height: 300px; }
.kanban-col-header { font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; padding-bottom: 8px; border-bottom: 1px solid #e4e7ed; }
.kanban-list { min-height: 200px; }
.task-card { margin-bottom: 8px; cursor: pointer; }
.task-title { font-weight: 500; margin-bottom: 6px; }
.task-info { display: flex; gap: 6px; align-items: center; margin-bottom: 6px; }
.task-assignee { font-size: 12px; color: #909399; }
.task-actions { display: flex; justify-content: flex-end; gap: 4px; }
</style>
