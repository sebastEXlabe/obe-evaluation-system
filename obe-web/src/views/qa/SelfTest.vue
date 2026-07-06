<template>
  <div class="quiz-container">
    <!-- ========== 教师端 ========== -->
    <template v-if="isTeacher">
      <!-- Tab切换 -->
      <el-tabs v-model="teacherTab">
        <el-tab-pane label="🤖 AI出题" name="generate" />
        <el-tab-pane label="📋 我的测验" name="quizzes" />
      </el-tabs>

      <!-- AI出题 -->
      <div v-if="teacherTab==='generate'" class="panel">
        <div class="gen-bar">
          <span>题目数量：</span><el-input-number v-model="genCount" :min="1" :max="20" size="small" />
          <el-input v-model="genTopic" placeholder="主题（可选）" size="small" style="width:200px" />
          <el-button type="primary" @click="aiGenerate" :loading="genLoading">🤖 AI生成题目</el-button>
          <el-select v-model="quizCourseId" placeholder="关联课程" clearable size="small" style="width:150px">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </div>
        <div v-if="genQuestions.length" class="edit-list">
          <div class="edit-header">
            <span>共 {{ genQuestions.length }} 题，可逐题编辑后保存</span>
            <div>
              <el-input v-model="quizTitle" placeholder="测验标题" size="small" style="width:200px" />
              <el-button type="success" @click="saveQuiz" :loading="saveLoading" size="small">💾 保存测验</el-button>
            </div>
          </div>
          <div class="q-card" v-for="(q,i) in genQuestions" :key="i">
            <div class="q-card-header">
              <b>第{{ i+1 }}题</b>
              <el-button size="small" text type="danger" @click="genQuestions.splice(i,1)">删除</el-button>
            </div>
            <el-input v-model="q.question" size="small" placeholder="题目" />
            <div class="opt-row">
              <el-input v-model="q.options[0]" size="small" placeholder="A" class="opt" />
              <el-input v-model="q.options[1]" size="small" placeholder="B" class="opt" />
              <el-input v-model="q.options[2]" size="small" placeholder="C" class="opt" />
              <el-input v-model="q.options[3]" size="small" placeholder="D" class="opt" />
            </div>
            <el-select v-model="q.answer" size="small" style="width:100px" placeholder="答案">
              <el-option label="A" value="A" /><el-option label="B" value="B" /><el-option label="C" value="C" /><el-option label="D" value="D" />
            </el-select>
          </div>
        </div>
      </div>

      <!-- 我的测验列表 -->
      <div v-if="teacherTab==='quizzes'" class="panel">
        <el-button type="primary" @click="teacherTab='generate'" size="small">+ AI出题</el-button>
        <el-table :data="quizzes" stripe size="small" style="margin-top:12px" v-loading="quizLoading">
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column label="题目数" width="80">
            <template #default="{row}">{{ questionCount(row) }}题</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{row}">
              <el-tag size="small" :type="row.status==='PUBLISHED'?'success':row.status==='CLOSED'?'info':'warning'">
                {{ row.status==='DRAFT'?'草稿':row.status==='PUBLISHED'?'已发布':'已关闭' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="140">
            <template #default="{row}">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{row}">
              <el-button v-if="row.status==='DRAFT'" size="small" type="success" @click="publishQuiz(row)">发布</el-button>
              <el-button v-if="row.status==='DRAFT'" size="small" @click="editQuiz(row)">编辑</el-button>
              <el-button size="small" type="danger" text @click="deleteQuiz(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <!-- ========== 学生端 ========== -->
    <template v-else>
      <h3>📝 测验列表</h3>
      <div v-if="studentQuizzes.length===0" class="empty">暂无测验</div>
      <div v-else class="quiz-cards">
        <div class="quiz-card" v-for="q in studentQuizzes" :key="q.id" @click="startStudentQuiz(q)">
          <div class="qc-title">{{ q.title }}</div>
          <div class="qc-meta">{{ questionCount(q) }}题 · {{ formatTime(q.publishedAt) }}</div>
        </div>
      </div>

      <!-- 答题界面 -->
      <el-dialog v-model="taking" :title="currentQuiz?.title" width="700px" :close-on-click-modal="false">
        <div v-if="currentQuiz" class="take-body">
          <div class="tq-item" v-for="(q,i) in quizQuestions" :key="i">
            <div class="tq-num">{{ i+1 }}. {{ q.question }}</div>
            <el-radio-group v-model="q.userAnswer" class="tq-opts">
              <el-radio v-for="(o,j) in q.options" :key="j" :value="'ABCD'[j]">{{ o }}</el-radio>
            </el-radio-group>
          </div>
        </div>
        <template #footer>
          <el-button @click="taking=false">取消</el-button>
          <el-button type="primary" @click="submitQuiz" :loading="submitting">提交答卷</el-button>
        </template>
      </el-dialog>

      <!-- 结果 -->
      <el-dialog v-model="resultVisible" title="答题结果" width="400px">
        <div class="result-box" v-if="result">
          <el-progress type="dashboard" :percentage="result.score" :color="result.score>=60?'#67C23A':'#F56C6C'" />
          <p style="text-align:center;margin-top:12px">正确 {{ result.correct }} / {{ result.total }} 题</p>
        </div>
        <template #footer><el-button type="primary" @click="resultVisible=false">关闭</el-button></template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const isTeacher = computed(() => ['ADMIN','TEACHER'].includes(auth.user?.roleCode))

// Teacher state
const teacherTab = ref('quizzes')
const genCount = ref(5), genTopic = ref(''), genLoading = ref(false), genQuestions = ref([])
const quizTitle = ref(''), quizCourseId = ref(null), saveLoading = ref(false)
const quizzes = ref([]), quizLoading = ref(false), courses = ref([])

// Student state
const studentQuizzes = ref([]), taking = ref(false), currentQuiz = ref(null)
const quizQuestions = ref([]), submitting = ref(false)
const result = ref(null), resultVisible = ref(false)

const questionCount = (q) => { try{return JSON.parse(q.questions||'[]').length}catch{return 0} }
const formatTime = t => t ? new Date(t).toLocaleString('zh-CN') : ''

async function aiGenerate() {
  genLoading.value = true; genQuestions.value = []
  try {
    const { data } = await http.post('/ai-chat/generate-questions', {
      count: genCount.value, topic: genTopic.value, courseId: quizCourseId.value
    })
    const raw = JSON.parse(data.questions || data.raw || '[]')
    genQuestions.value = raw.map(q => ({ ...q, options: q.options || ['','','',''] }))
    quizTitle.value = genTopic.value || 'AI生成测验'
  } catch (e) { ElMessage.error('出题失败') }
  genLoading.value = false
}

async function saveQuiz() {
  if (!quizTitle.value) return ElMessage.warning('请输入测验标题')
  saveLoading.value = true
  try {
    await http.post('/ai-chat/quizzes', {
      title: quizTitle.value, questions: JSON.stringify(genQuestions.value),
      courseId: quizCourseId.value, totalScore: genQuestions.value.length * 10
    })
    ElMessage.success('已保存为草稿')
    genQuestions.value = []; quizTitle.value = ''; teacherTab.value = 'quizzes'; loadQuizzes()
  } catch { ElMessage.error('保存失败') }
  saveLoading.value = false
}

async function loadQuizzes() {
  quizLoading.value = true
  try { const { data } = await http.get('/ai-chat/quizzes'); quizzes.value = data || [] } catch (e) { console.error(e) }
  quizLoading.value = false
}

async function publishQuiz(q) {
  try { await http.post('/ai-chat/quizzes/'+q.id+'/publish'); ElMessage.success('已发布'); loadQuizzes() }
  catch { ElMessage.error('发布失败') }
}

async function editQuiz(q) {
  try {
    genQuestions.value = JSON.parse(q.questions || '[]')
    quizTitle.value = q.title; quizCourseId.value = q.courseId
    teacherTab.value = 'generate'
    // Delete old draft
    await http.delete('/ai-chat/quizzes/'+q.id)
  } catch { ElMessage.error('加载失败') }
}

async function deleteQuiz(q) {
  try { await ElMessageBox.confirm('删除？'); await http.delete('/ai-chat/quizzes/'+q.id); loadQuizzes() } catch (e) { console.error(e) }
}

async function loadStudentQuizzes() {
  try { const { data } = await http.get('/ai-chat/student-quizzes'); studentQuizzes.value = data || [] } catch (e) { console.error(e) }
}

function startStudentQuiz(q) {
  currentQuiz.value = q
  try { quizQuestions.value = JSON.parse(q.questions||'[]').map(x=>({...x,userAnswer:''})) } catch { quizQuestions.value = [] }
  taking.value = true
}

async function submitQuiz() {
  submitting.value = true
  try {
    const { data } = await http.post('/ai-chat/quizzes/'+currentQuiz.value.id+'/submit', {
      answers: quizQuestions.value.map(q=>({question:q.question,options:q.options,answer:q.answer,userAnswer:q.userAnswer||''}))
    })
    result.value = data; taking.value = false; resultVisible.value = true
  } catch { ElMessage.error('提交失败') }
  submitting.value = false
}

onMounted(async () => {
  try { const { data } = await http.get('/courses'); courses.value = data || [] } catch (e) { console.error(e) }
  if (isTeacher.value) loadQuizzes()
  else loadStudentQuizzes()
})
</script>

<style scoped>
.quiz-container { max-width:1000px; margin:0 auto; padding:12px; }
.panel { padding:16px 0; }
.gen-bar { display:flex; gap:10px; align-items:center; flex-wrap:wrap; margin-bottom:16px; }
.edit-list { display:flex; flex-direction:column; gap:12px; }
.edit-header { display:flex; justify-content:space-between; align-items:center; gap:12px; }
.q-card { background:#f5f7fa; padding:12px; border-radius:8px; }
.q-card-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:8px; }
.opt-row { display:flex; gap:6px; margin:6px 0; }
.opt { flex:1; }
.quiz-cards { display:flex; flex-wrap:wrap; gap:12px; margin-top:12px; }
.quiz-card { width:200px; padding:16px; border:1px solid #ebeef5; border-radius:8px; cursor:pointer; transition:all .15s; }
.quiz-card:hover { border-color:#409EFF; box-shadow:0 2px 8px rgba(0,0,0,.06); }
.qc-title { font-weight:600; }
.qc-meta { font-size:12px; color:#909399; margin-top:4px; }
.empty { text-align:center; padding:60px; color:#909399; }
.take-body { max-height:60vh; overflow-y:auto; }
.tq-item { padding:12px 0; border-bottom:1px solid #f0f0f0; }
.tq-num { font-weight:600; margin-bottom:8px; }
.tq-opts { display:flex; flex-direction:column; gap:6px; }
.result-box { text-align:center; padding:20px; }
</style>
