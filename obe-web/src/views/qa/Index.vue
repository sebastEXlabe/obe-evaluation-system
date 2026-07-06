<template>
  <div class="qa-container">
    <!-- 流程说明 -->
    <el-alert type="info" :closable="false" show-icon style="margin-bottom:0">
      <template #title>
        <span v-if="isStudent">💡 <b>提问流程</b>：点击「向老师提问」→ 填写问题和附件 → 老师收到后回复 → 你确认「已解决」</span>
        <span v-else>💡 <b>答疑流程</b>：查看「待回答」→ 点击「回答」→ 学生确认后可标记「已解决」</span>
      </template>
    </el-alert>

    <!-- 顶部操作栏 -->
    <div class="qa-toolbar">
      <div class="toolbar-left">
        <el-radio-group v-model="viewMode" size="small" @change="loadQuestions">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="my" v-if="isStudent">我的提问</el-radio-button>
          <el-radio-button value="pending" v-if="isTeacher">待回答 ({{ pendingCount }})</el-radio-button>
        </el-radio-group>
        <el-select v-model="filterGroupId" @change="loadQuestions" placeholder="按小组筛选" clearable size="small" style="width:180px;margin-left:12px" filterable>
          <el-option v-for="g in groups" :key="g.id" :label="g.groupName" :value="g.id" />
        </el-select>
      </div>
      <div>
        <el-button type="primary" @click="openAskDialog" v-if="isStudent">✍️ 向老师提问</el-button>
        <el-button @click="loadQuestions">🔄 刷新</el-button>
      </div>
    </div>

    <!-- 统计卡片（教师端） -->
    <el-row :gutter="12" class="stats-row" v-if="isTeacher">
      <el-col :span="6" v-for="s in statsCards" :key="s.label">
        <div class="mini-stat" :style="{borderTop:'3px solid '+s.color}">
          <div class="mini-value" :style="{color:s.color}">{{ s.value }}</div>
          <div class="mini-label">{{ s.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 问题列表 -->
    <el-card v-loading="loading">
      <div v-if="questions.length === 0 && !loading" class="empty-state">
        <el-empty :description="isStudent ? '还没有提问，点击上方按钮向老师提问' : '暂无问题'">
          <el-button type="primary" v-if="isStudent" @click="openAskDialog">发布第一个问题</el-button>
        </el-empty>
      </div>

      <div class="question-list" v-else>
        <div class="question-item" v-for="q in questions" :key="q.id" @click="openDetail(q)">
          <div class="q-main">
            <div class="q-header">
              <el-tag size="small" :type="statusType(q.status)">{{ statusLabel(q.status) }}</el-tag>
              <span class="q-title">{{ q.title }}</span>
              <span v-if="q.attachmentCount > 0" class="q-badge">📎{{ q.attachmentCount }}</span>
            </div>
            <div class="q-preview">{{ truncate(q.question, 100) }}</div>
            <div class="q-meta">
              <span>👤 {{ q.askUserName || '学生' }}</span>
              <span v-if="q.answerUserName">→ 👨‍🏫 {{ q.answerUserName }}</span>
              <span>{{ formatTime(q.askedAt) }}</span>
            </div>
          </div>
          <div v-if="isTeacher && q.status === 'PENDING'">
            <el-button type="primary" size="small" @click.stop="openAnswerDialog(q)">✏️ 回答</el-button>
          </div>
        </div>

        <el-pagination v-if="totalQuestions > pageSize" layout="prev,pager,next" :total="totalQuestions"
          :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadQuestions" small class="qa-pagination" />
      </div>
    </el-card>

    <!-- 提问对话框 -->
    <el-dialog v-model="askVisible" title="向老师提问" width="600px" :close-on-click-modal="false">
      <el-form :model="askForm" label-width="80px">
        <el-form-item label="问题标题" required>
          <el-input v-model="askForm.title" placeholder="简明扼要描述你的问题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="所属小组">
          <el-select v-model="askForm.groupId" placeholder="选择小组（可选）" clearable style="width:100%" filterable>
            <el-option v-for="g in myGroups" :key="g.id" :label="g.groupName" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题详情" required>
          <el-input v-model="askForm.question" type="textarea" :rows="5" placeholder="详细描述你的问题..." maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload :http-request="handleUpload" :auto-upload="false" :on-change="onFileChange"
            :file-list="tempFiles" :on-remove="onFileRemove" :before-upload="()=>false" multiple>
            <el-button size="small" plain>📎 选择文件</el-button>
            <template #tip><span class="upload-tip">支持图片、文档、PDF，≤10MB</span></template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="askVisible=false">取消</el-button>
        <el-button type="primary" @click="submitQuestion" :loading="submitting">发布问题</el-button>
      </template>
    </el-dialog>

    <!-- 问题详情对话框 -->
    <el-dialog v-model="detailVisible" :title="detailQ?.title" width="700px">
      <div class="detail-body" v-if="detailQ">
        <div class="detail-block q-block">
          <div class="block-label">❓ 学生提问</div>
          <div class="block-meta">
            <span>👤 {{ detailQ.askUserName || '学生' }}</span>
            <span>{{ formatTime(detailQ.askedAt) }}</span>
            <el-tag size="small" :type="statusType(detailQ.status)">{{ statusLabel(detailQ.status) }}</el-tag>
          </div>
          <div class="block-content">{{ detailQ.question }}</div>
          <div v-if="detailQ.attachments && detailQ.attachments.length > 0" class="att-section">
            <div class="att-label">📎 附件：</div>
            <div class="att-item" v-for="att in detailQ.attachments" :key="att.path">
              <span>📄 {{ att.name }}</span>
              <span class="att-size">{{ formatSize(att.size) }}</span>
              <el-button size="small" text type="primary" @click="downloadAtt(detailQ.id, att)">⬇ 下载</el-button>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="detail-block a-block" v-if="detailQ.answer">
          <div class="block-label">✅ 教师回答</div>
          <div class="block-meta">
            <span>👨‍🏫 {{ detailQ.answerUserName || '教师' }}</span>
            <span>{{ formatTime(detailQ.answeredAt) }}</span>
          </div>
          <div class="block-content answer-content">{{ detailQ.answer }}</div>
        </div>

        <div v-if="!detailQ.answer && isTeacher" style="margin-top:16px">
          <el-input v-model="answerText" type="textarea" :rows="4" placeholder="输入回答..." />
          <el-button type="primary" @click="submitAnswer" :loading="answering" style="margin-top:10px">📤 提交回答</el-button>
        </div>

        <div v-if="detailQ.status === 'ANSWERED' && isStudent" style="margin-top:12px">
          <el-button type="success" @click="resolveQuestion(detailQ.id)" size="small">✅ 标记为已解决</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 教师快速回答对话框 -->
    <el-dialog v-model="answerVisible" title="快速回答" width="550px">
      <div v-if="answerTarget" class="q-context">
        <div class="ctx-label">{{ answerTarget.askUserName }} 的提问：</div>
        <div class="ctx-content">{{ answerTarget.question }}</div>
      </div>
      <el-input v-model="answerText" type="textarea" :rows="6" placeholder="输入你的回答..." style="margin-top:12px" />
      <template #footer>
        <el-button @click="answerVisible=false">取消</el-button>
        <el-button type="primary" @click="submitAnswer" :loading="answering">提交回答</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/index.js'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const isTeacher = computed(() => ['ADMIN','TEACHER'].includes(auth.user?.roleCode))
const isStudent = computed(() => !isTeacher.value)

const loading = ref(false), submitting = ref(false), answering = ref(false)
const viewMode = ref(isTeacher.value ? 'pending' : 'all')
const filterGroupId = ref(null)
const currentPage = ref(1), pageSize = ref(10), totalQuestions = ref(0)
const questions = ref([]), groups = ref([]), myGroups = ref([]), pendingCount = ref(0)

const askVisible = ref(false)
const askForm = reactive({ title: '', question: '', groupId: null })
const tempFiles = ref([]), tempFileObjs = ref([])

const detailVisible = ref(false), detailQ = ref(null)
const answerVisible = ref(false), answerTarget = ref(null), answerText = ref('')

const statsCards = computed(() => {
  const arr = questions.value
  return [
    { label:'待回答', value:arr.filter(q=>q.status==='PENDING').length, color:'#E6A23C' },
    { label:'已回答', value:arr.filter(q=>q.status==='ANSWERED').length, color:'#409EFF' },
    { label:'已解决', value:arr.filter(q=>q.status==='RESOLVED').length, color:'#67C23A' },
    { label:'总计', value:arr.length, color:'#909399' },
  ]
})

const statusType = s => ({ PENDING:'warning', ANSWERED:'', RESOLVED:'success' }[s] || 'info')
const statusLabel = s => ({ PENDING:'待回答', ANSWERED:'已回答', RESOLVED:'已解决' }[s] || s)

async function loadQuestions() {
  loading.value = true
  try {
    if (viewMode.value === 'my') {
      const { data } = await http.get('/qa/questions/my')
      questions.value = data || []; totalQuestions.value = questions.value.length
    } else if (viewMode.value === 'pending' && isTeacher.value) {
      const params = filterGroupId.value ? { groupId: filterGroupId.value } : {}
      const { data } = await http.get('/qa/questions/pending', { params })
      questions.value = (data || []).map(q=>({...q, status:q.status||'PENDING', attachmentCount:q.attachmentCount||0}))
      totalQuestions.value = questions.value.length
    } else {
      const params = { page: currentPage.value, size: pageSize.value }
      if (filterGroupId.value) params.groupId = filterGroupId.value
      const { data } = await http.get('/qa/questions', { params })
      questions.value = data?.records || []; totalQuestions.value = data?.total || 0
    }

    if (isTeacher.value) {
      try {
        const p = filterGroupId.value ? '?groupId='+filterGroupId.value : ''
        const { data } = await http.get('/qa/questions/pending'+p)
        pendingCount.value = (data||[]).length
      } catch (e) { console.error(e) }
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

async function loadGroups() {
  try {
    const { data } = await http.get('/groups')
    const allGroups = data || []
    if (isTeacher.value) {
      // 教师：只看自己负责的小组（管理员看全部）
      groups.value = allGroups
    } else {
      // 学生：只看自己所在的小组
      groups.value = allGroups.filter(g => {
        // 简化为显示所有小组，后端会校验实际权限
        return true
      })
    }
    myGroups.value = allGroups
  } catch (e) { console.error(e) }
}

function openAskDialog() {
  askForm.title = ''; askForm.question = ''
  // 学生自动填充所在小组
  if (isStudent.value && myGroups.value.length === 1) {
    askForm.groupId = myGroups.value[0].id
  } else {
    askForm.groupId = myGroups.value.length > 0 ? myGroups.value[0].id : null
  }
  tempFiles.value = []; tempFileObjs.value = []
  askVisible.value = true
}

function onFileChange(file) { tempFileObjs.value.push(file.raw) }
function onFileRemove(file) {
  const idx = tempFileObjs.value.findIndex(f => f.name === file.name)
  if (idx >= 0) tempFileObjs.value.splice(idx, 1)
}
function handleUpload() {}

async function submitQuestion() {
  if (!askForm.title.trim()) return ElMessage.warning('请输入问题标题')
  if (!askForm.question.trim()) return ElMessage.warning('请输入问题详情')
  submitting.value = true
  try {
    const { data } = await http.post('/qa/questions', { ...askForm })
    const qid = data.id
    if (tempFileObjs.value.length > 0) {
      for (const file of tempFileObjs.value) {
        const fd = new FormData(); fd.append('file', file)
        await http.post('/qa/questions/'+qid+'/upload', fd)
      }
    }
    ElMessage.success('问题已发布，等待老师回答')
    askVisible.value = false; loadQuestions()
  } catch { ElMessage.error('发布失败') }
  finally { submitting.value = false }
}

async function openDetail(q) {
  try {
    const { data } = await http.get('/qa/questions/' + q.id)
    detailQ.value = data || q
  } catch { detailQ.value = q }
  answerText.value = ''; detailVisible.value = true
}

function openAnswerDialog(q) { answerTarget.value = q; answerText.value = ''; answerVisible.value = true }

async function submitAnswer() {
  if (!answerText.value.trim()) return ElMessage.warning('请输入回答内容')
  answering.value = true
  const qid = detailQ.value?.id || answerTarget.value?.id
  try {
    await http.post('/qa/questions/'+qid+'/answer', { answer: answerText.value })
    ElMessage.success('回答已提交')
    answerVisible.value = false; detailVisible.value = false; loadQuestions()
  } catch { ElMessage.error('提交失败') }
  finally { answering.value = false }
}

async function resolveQuestion(id) {
  try { await http.put('/qa/questions/'+id+'/resolve'); ElMessage.success('已标记为已解决'); detailVisible.value = false; loadQuestions() }
  catch { ElMessage.error('操作失败') }
}

function formatTime(t) { if(!t)return''; const d=new Date(t); return `${d.getMonth()+1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2,'0')}` }
function formatSize(b) { return b<1048576?(b/1024).toFixed(0)+'KB':(b/1048576).toFixed(1)+'MB' }

async function downloadAtt(qid, att) {
  const token = localStorage.getItem('token')
  try {
    const res = await fetch('/api/qa/questions/'+qid+'/attachments/'+att.path, {
      headers: { Authorization: 'Bearer '+token }
    })
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a'); a.href = url; a.download = att.name
    a.click(); URL.revokeObjectURL(url)
  } catch { ElMessage.error('下载失败') }
}
function truncate(s,n) { return s&&s.length>n?s.substring(0,n)+'...':s||'' }

onMounted(() => { loadGroups(); loadQuestions() })
</script>

<style scoped>
.qa-container { display:flex; flex-direction:column; gap:12px; padding:8px; max-width:1200px; }
.qa-toolbar { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px; }
.toolbar-left { display:flex; align-items:center; gap:8px; }
.stats-row { margin-bottom:0; }
.mini-stat { background:#fff; padding:16px; border-radius:8px; text-align:center; cursor:pointer; box-shadow:0 1px 3px rgba(0,0,0,.08); }
.mini-value { font-size:28px; font-weight:700; }
.mini-label { font-size:12px; color:#909399; margin-top:4px; }
.empty-state { padding:40px 0; }
.question-item { display:flex; justify-content:space-between; align-items:center; padding:14px 16px; border-bottom:1px solid #f0f0f0; cursor:pointer; transition:background .15s; }
.question-item:hover { background:#f5f7fa; }
.q-main { flex:1; min-width:0; }
.q-header { display:flex; align-items:center; gap:8px; margin-bottom:4px; }
.q-title { font-weight:600; font-size:15px; }
.q-badge { font-size:12px; background:#f0f0f0; padding:1px 6px; border-radius:10px; }
.q-preview { font-size:13px; color:#909399; margin-bottom:6px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.q-meta { display:flex; gap:16px; font-size:12px; color:#c0c4cc; }
.qa-pagination { margin-top:16px; display:flex; justify-content:center; }
.detail-body { max-height:60vh; overflow-y:auto; }
.detail-block { margin-bottom:16px; }
.block-label { font-weight:700; font-size:15px; margin-bottom:6px; }
.block-meta { display:flex; gap:12px; font-size:12px; color:#909399; margin-bottom:8px; align-items:center; }
.block-content { font-size:14px; line-height:1.7; white-space:pre-wrap; background:#f5f7fa; padding:12px; border-radius:8px; }
.answer-content { background:#f0f9eb; border-left:4px solid #67C23A; }
.att-section { margin-top:8px; }
.att-label { font-size:13px; font-weight:500; margin-bottom:4px; }
.att-item { display:flex; justify-content:space-between; padding:4px 8px; background:#f5f7fa; border-radius:4px; margin-bottom:4px; font-size:13px; }
.att-size { color:#909399; font-size:12px; }
.q-context { background:#f5f7fa; padding:12px; border-radius:8px; }
.ctx-label { font-size:13px; color:#909399; margin-bottom:4px; }
.ctx-content { font-size:14px; }
.upload-tip { font-size:12px; color:#909399; margin-left:8px; }
</style>
