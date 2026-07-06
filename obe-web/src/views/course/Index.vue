<template>
  <div class="course-layout">
    <!-- 顶部面包屑级联 -->
    <div class="header-bar">
      <div class="cascade">
        <span class="cascade-label">课程：</span>
        <el-select v-model="courseId" @change="loadAll" placeholder="选择课程" size="large" style="width:240px">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id">
            <span>{{ c.courseName }}</span><span class="opt-extra">{{ c.semester }}</span>
          </el-option>
        </el-select>
        <el-button @click="openCourseDialog" size="large">+ 新建</el-button>
      </div>
      <div class="header-stats" v-if="courseId">
        <span class="stat-item">目标数：<b>{{ objectives.length }}</b></span>
        <span class="stat-item">指标数：<b>{{ totalIndicators }}</b></span>
        <span class="stat-item" :class="{warn:Math.abs(weightSum-1)>=0.01}">权重合计：<b>{{ (weightSum*100).toFixed(0) }}%</b></span>
      </div>
    </div>

    <el-empty v-if="!courseId" description="选择课程或创建新课" style="padding:80px 0" />

    <!-- 步骤式主体 -->
    <template v-else>
      <!-- 步骤条 -->
      <el-steps :active="activeStep" align-center finish-status="success" style="margin:20px 0">
        <el-step title="课程目标" description="定义成果导向目标" @click="activeStep=0" />
        <el-step title="指标点分解" description="每个目标的具体指标" @click="activeStep=1" />
        <el-step title="评价方式" description="考核方式与数据源" @click="activeStep=2" />
        <el-step title="毕业要求映射" description="CO→PO/GR 矩阵" @click="activeStep=3" />
      </el-steps>

      <!-- Step 0: 课程目标 -->
      <div v-show="activeStep===0" class="step-panel" v-loading="loading">
        <div class="step-toolbar">
          <span class="step-title">📋 课程目标列表</span>
          <el-button type="primary" size="small" @click="openObjDialog()">+ 添加目标</el-button>
        </div>
        <div class="weight-bar">
          <div class="bar-track">
            <div class="bar-fill" :style="{width:Math.min(weightSum*100,100)+'%',background:weightSum>1.01?'#F56C6C':weightSum<0.99?'#E6A23C':'#67C23A'}"></div>
          </div>
          <span class="bar-label" :style="{color:Math.abs(weightSum-1)<0.01?'#67C23A':'#F56C6C'}">{{ (weightSum*100).toFixed(0) }}%</span>
          <span class="bar-hint">（所有目标权重之和应为100%）</span>
        </div>

        <el-table :data="objectives" stripe row-key="id" style="width:100%">
          <el-table-column prop="objectiveNo" label="编号" width="120" />
          <el-table-column prop="title" label="课程目标" min-width="200">
            <template #default="{row}"><b>{{ row.title }}</b></template>
          </el-table-column>
          <el-table-column label="维度" width="120">
            <template #default="{row}"><el-tag :type="dimType(row.dimension)" size="small">{{ dimLabel(row.dimension) }}</el-tag></template>
          </el-table-column>
          <el-table-column label="权重" width="100">
            <template #default="{row}">
              <el-tag size="small" :type="row.weight>0.3?'warning':''">{{ ((row.weight||0)*100).toFixed(0) }}%</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{row}">
              <el-button size="small" text @click="editObj(row)">编辑</el-button>
              <el-button size="small" text type="danger" @click="delObj(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 1: 指标点 -->
      <div v-show="activeStep===1" class="step-panel">
        <div class="step-toolbar">
          <span class="step-title">🎯 指标点分解</span>
          <span style="font-size:13px;color:#909399">选择目标 → 查看/添加下属指标点</span>
        </div>
        <el-row :gutter="16">
          <el-col :span="8">
            <div class="obj-mini-list">
              <div class="obj-mini" v-for="o in objectives" :key="o.id"
                :class="{active:selectedObjId===o.id}" @click="selectObj(o)">
                <div class="mini-tag"><el-tag :type="dimType(o.dimension)" size="small">{{ dimLabel(o.dimension) }}</el-tag></div>
                <div class="mini-title">{{ o.title }}</div>
                <div class="mini-weight">{{ ((o.weight||0)*100).toFixed(0) }}%</div>
              </div>
            </div>
          </el-col>
          <el-col :span="16">
            <div v-if="!selectedObjId" class="empty-hint">← 选择一个课程目标</div>
            <div v-else>
              <div class="step-toolbar">
                <span>指标点 · {{ selectedObj?.title }}</span>
                <el-button type="success" size="small" @click="openIndDialog()">+ 指标点</el-button>
              </div>
              <el-table :data="currentIndicators" stripe size="small">
                <el-table-column prop="indicatorNo" label="编号" width="100" />
                <el-table-column prop="title" label="指标点名称" min-width="180" />
                <el-table-column prop="description" label="描述" min-width="120" show-overflow-tooltip />
                <el-table-column label="操作" width="120">
                  <template #default="{row}">
                    <el-button size="small" text @click="editInd(row)">编辑</el-button>
                    <el-button size="small" text type="danger" @click="delInd(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Step 2: 评价方式 -->
      <div v-show="activeStep===2" class="step-panel">
        <div class="step-toolbar">
          <span class="step-title">📊 评价方式配置</span>
          <span style="font-size:13px;color:#909399">为指标点配置评价方式：评分来源、权重、满分</span>
        </div>
        <el-row :gutter="16">
          <el-col :span="8">
            <div class="obj-mini-list">
              <div class="obj-mini" v-for="o in objectives" :key="o.id"
                :class="{active:selectedObjId===o.id}" @click="selectObj(o)">
                <div class="mini-tag"><el-tag :type="dimType(o.dimension)" size="small">{{ dimLabel(o.dimension) }}</el-tag></div>
                <div class="mini-title">{{ o.title }}</div>
              </div>
            </div>
          </el-col>
          <el-col :span="16">
            <div v-if="!selectedObjId" class="empty-hint">← 选择一个课程目标</div>
            <div v-else v-for="ind in currentIndicators" :key="ind.id" class="ind-method-block">
              <div class="ind-label">
                <el-tag type="success" size="small">{{ ind.indicatorNo }}</el-tag>
                <span>{{ ind.title }}</span>
                <el-button size="small" type="warning" text @click="openMethodDialog(ind)">+ 评价方式</el-button>
              </div>
              <el-table :data="getMethods(ind.id)" stripe size="small" v-if="getMethods(ind.id).length">
                <el-table-column prop="methodName" label="名称" width="140" />
                <el-table-column prop="dataSource" label="数据源" width="90">
                  <template #default="{row}"><el-tag size="small" type="info">{{ row.dataSource }}</el-tag></template>
                </el-table-column>
                <el-table-column label="权重" width="70">
                  <template #default="{row}">{{ ((row.weight||0)*100).toFixed(0) }}%</template>
                </el-table-column>
                <el-table-column prop="fullScore" label="满分" width="70" />
                <el-table-column prop="evalType" label="类型" width="80" />
                <el-table-column label="操作" width="80">
                  <template #default="{row}"><el-button size="small" text @click="editMethod(row)">编辑</el-button></template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Step 3: 毕业要求映射矩阵 -->
      <div v-show="activeStep===3" class="step-panel">
        <div class="step-toolbar">
          <span class="step-title">🔗 毕业要求映射矩阵（CO → GR）</span>
          <el-tag size="small" type="info">H=高支撑 M=中支撑 L=低支撑</el-tag>
        </div>
        <div class="matrix-wrapper" v-if="gradReqs.length && objectives.length">
          <table class="matrix-table">
            <thead>
              <tr>
                <th class="corner">课程目标 ↓ / 毕业要求 →</th>
                <th v-for="gr in gradReqs" :key="gr.id" class="gr-col">
                  <div class="gr-no">{{ gr.reqNo }}</div>
                  <div class="gr-title">{{ gr.title.length>10?gr.title.substring(0,10)+'...':gr.title }}</div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="obj in objectives" :key="obj.id">
                <td class="co-label">
                  <div class="co-no">{{ obj.objectiveNo }}</div>
                  <div class="co-name">{{ obj.title.length>10?obj.title.substring(0,10)+'...':obj.title }}</div>
                </td>
                <td v-for="gr in gradReqs" :key="gr.id" class="cell"
                  :class="{mapped:getMapLevel(obj.id,gr.id)}"
                  @click="toggleMatrixCell(obj.id,gr)">
                  <span class="cell-value">{{ getMapLevel(obj.id,gr.id) }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <el-empty v-else description="请先添加课程目标和毕业要求" />
      </div>

      <!-- 底部导航 -->
      <div class="step-nav">
        <el-button v-if="activeStep>0" @click="activeStep--">← 上一步</el-button>
        <span style="flex:1"></span>
        <el-button v-if="activeStep<3" type="primary" @click="activeStep++">下一步 →</el-button>
      </div>
    </template>

    <!-- Dialogs -->
    <el-dialog v-model="objVisible" :title="objForm.id?'编辑':'新建'" width="500px">
      <el-form :model="objForm" label-width="80px">
        <el-form-item label="编号"><el-input v-model="objForm.objectiveNo" /></el-form-item>
        <el-form-item label="标题"><el-input v-model="objForm.title" /></el-form-item>
        <el-form-item label="维度"><el-select v-model="objForm.dimension"><el-option label="知识" value="KNOWLEDGE"/><el-option label="能力" value="ABILITY"/><el-option label="素养" value="QUALITY"/></el-select></el-form-item>
        <el-form-item label="权重"><el-input-number v-model="objForm.weight" :min="0" :max="1" :step="0.05"/></el-form-item>
        <el-form-item label="描述"><el-input v-model="objForm.description" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="objVisible=false">取消</el-button><el-button type="primary" @click="saveObj">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="indVisible" :title="indForm.id?'编辑':'新建'" width="480px">
      <el-form :model="indForm" label-width="80px">
        <el-form-item label="编号"><el-input v-model="indForm.indicatorNo"/></el-form-item>
        <el-form-item label="标题"><el-input v-model="indForm.title"/></el-form-item>
        <el-form-item label="描述"><el-input v-model="indForm.description" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="indVisible=false">取消</el-button><el-button type="primary" @click="saveInd">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="methodVisible" :title="methodForm.id?'编辑':'新建'" width="500px">
      <el-form :model="methodForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="methodForm.methodName"/></el-form-item>
        <el-form-item label="权重"><el-input-number v-model="methodForm.weight" :min="0" :max="1" :step="0.1"/></el-form-item>
        <el-form-item label="数据源"><el-select v-model="methodForm.dataSource"><el-option label="教师评分" value="MANUAL"/><el-option label="Git" value="GIT"/><el-option label="日志" value="JOURNAL"/><el-option label="自测" value="TEST"/><el-option label="MaxKB" value="MAXKB"/></el-select></el-form-item>
        <el-form-item label="满分"><el-input-number v-model="methodForm.fullScore" :min="1" :max="100"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="methodForm.evalType"><el-option label="过程性" value="FORMATIVE"/><el-option label="终结性" value="SUMMATIVE"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="methodVisible=false">取消</el-button><el-button type="primary" @click="saveMethod">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="courseVisible" title="新建课程" width="400px">
      <el-form :model="courseForm" label-width="100px">
        <el-form-item label="课程名称"><el-input v-model="courseForm.courseName"/></el-form-item>
        <el-form-item label="学期"><el-input v-model="courseForm.semester"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="courseVisible=false">取消</el-button><el-button type="primary" @click="createCourse" :loading="courseCreating">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted } from 'vue'
import http from '../../api/index.js'

const courses = ref([]); const courseId = ref(null)
const objectives = ref([]); const allIndicators = ref([]); const allMethods = ref([])
const loading = ref(false); const activeStep = ref(0)
const selectedObjId = ref(null); const gradReqs = ref([]); const mappings = ref([])

// Computed
const selectedObj = computed(() => objectives.value.find(o => o.id === selectedObjId.value))
const currentIndicators = computed(() => allIndicators.value.filter(i => i.objectiveId === selectedObjId.value))
const totalIndicators = computed(() => allIndicators.value.length)
const weightSum = computed(() => objectives.value.reduce((s,o) => s+(o.weight||0), 0))

const getMethods = (indId) => allMethods.value.filter(m => m.indicatorId === indId)

function getMapLevel(objId, gr) {
  const m = mappings.value.find(x => x.objectiveId === objId && x.requirementId === gr.id)
  return m ? (m.supportLevel === 'HIGH' ? 'H' : m.supportLevel === 'MEDIUM' ? 'M' : 'L') : ''
}

const dimType = d => ({KNOWLEDGE:'success',ABILITY:'warning',QUALITY:'danger'}[d]||'info')
const dimLabel = d => ({KNOWLEDGE:'知识',ABILITY:'能力',QUALITY:'素养'}[d]||d)

// Dialogs
const objVisible = ref(false), indVisible = ref(false), methodVisible = ref(false)
const courseVisible = ref(false), courseCreating = ref(false)
const courseForm = reactive({ courseName:'', semester:'' })
const objForm = reactive({ id:null, objectiveNo:'', title:'', dimension:'KNOWLEDGE', weight:0.25, description:'', courseId:null })
const indForm = reactive({ id:null, indicatorNo:'', title:'', description:'', objectiveId:null })
const methodForm = reactive({ id:null, methodName:'', weight:0.5, dataSource:'MANUAL', evalType:'SUMMATIVE', fullScore:100, indicatorId:null })

onMounted(async () => {
  try { const {data} = await http.get('/courses'); courses.value = data||[] } catch {}
  loadAll()
})
async function loadAll() {
  if (!courseId.value) { objectives.value = []; return }
  loading.value = true
  try {
    const treeRes = await http.get('/course/tree?courseId='+courseId.value)
    const tree = treeRes.data || []
    const objs = tree.map(o => ({ id:parseInt(o.objective.id), ...o.objective }))
    objectives.value = objs
    const inds = []; const methods = []
    tree.forEach(o => (o.indicators||[]).forEach(ind => {
      inds.push({ id:parseInt(ind.indicator.id), ...ind.indicator })
      ;(ind.methods||[]).forEach(m => methods.push({ ...m, id:parseInt(m.id), indicatorId:parseInt(ind.indicator.id) }))
    }))
    allIndicators.value = inds; allMethods.value = methods
    // GR mappings
    const [grRes, mapRes] = await Promise.all([http.get('/graduation-requirements'), http.get('/graduation-requirements/mappings')])
    gradReqs.value = grRes.data || []; mappings.value = mapRes.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}
function selectObj(o) { selectedObjId.value = selectedObjId.value===o.id?null:o.id }

// CRUD
function openObjDialog() { Object.assign(objForm, { id:null, objectiveNo:'', title:'', dimension:'KNOWLEDGE', weight:0.25, description:'', courseId:courseId.value }); objVisible.value=true }
function editObj(o) { Object.assign(objForm, { id:o.id, objectiveNo:o.objectiveNo, title:o.title, dimension:o.dimension, weight:o.weight||0.25, description:o.description||'', courseId:o.courseId }); objVisible.value=true }
function delObj(o) { ElMessageBox.confirm('删除目标「'+o.title+'」及所有下级数据？').then(async()=>{ await http.delete('/course/objectives/'+o.id); ElMessage.success('已删除'); loadAll() }).catch(()=>{}) }
async function saveObj() {
  try { if(objForm.id) await http.put('/course/objectives',{...objForm}); else await http.post('/course/objectives',{...objForm}); objVisible.value=false; ElMessage.success('已保存'); loadAll() } catch { ElMessage.error('失败') }
}
function openIndDialog() { indForm.objectiveId = selectedObjId.value; Object.assign(indForm, { id:null, indicatorNo:'', title:'', description:'' }); indVisible.value=true }
function editInd(i) { Object.assign(indForm, { id:i.id, indicatorNo:i.indicatorNo, title:i.title, description:i.description||'', objectiveId:i.objectiveId }); indVisible.value=true }
function delInd(i) { ElMessageBox.confirm('删除指标点「'+i.title+'」？').then(async()=>{ await http.delete('/course/indicators/'+i.id); ElMessage.success('已删除'); loadAll() }).catch(()=>{}) }
async function saveInd() {
  try { if(indForm.id) await http.put('/course/indicators',{...indForm}); else await http.post('/course/indicators',{...indForm}); indVisible.value=false; ElMessage.success('已保存'); loadAll() } catch { ElMessage.error('失败') }
}
function openMethodDialog(ind) { Object.assign(methodForm, { id:null, methodName:'', weight:0.5, dataSource:'MANUAL', evalType:'SUMMATIVE', fullScore:100, indicatorId:ind.id }); methodVisible.value=true }
function editMethod(m) { Object.assign(methodForm, { id:m.id, methodName:m.methodName, weight:m.weight||0.5, dataSource:m.dataSource, evalType:m.evalType||'SUMMATIVE', fullScore:m.fullScore||100, indicatorId:m.indicatorId }); methodVisible.value=true }
async function saveMethod() {
  try { if(methodForm.id) await http.put('/course/methods',{...methodForm}); else await http.post('/course/methods',{...methodForm}); methodVisible.value=false; ElMessage.success('已保存'); loadAll() } catch { ElMessage.error('失败') }
}
// Course
function openCourseDialog() { courseForm.courseName=''; courseForm.semester=''; courseVisible.value=true }
async function createCourse() {
  if(!courseForm.courseName) return ElMessage.warning('请输入课程名称')
  courseCreating.value=true
  try { const {data} = await http.post('/courses',{...courseForm}); courses.value.push(data); courseId.value=data.id; courseVisible.value=false; ElMessage.success('已创建'); loadAll() } catch { ElMessage.error('失败') } finally { courseCreating.value=false }
}
// Matrix toggle
async function toggleMatrixCell(objId, gr) {
  const existing = mappings.value.find(x => x.objectiveId===objId && x.requirementId===gr.id)
  if (existing) {
    // Cycle: H→M→L→delete
    if (existing.supportLevel==='HIGH') existing.supportLevel='MEDIUM'
    else if (existing.supportLevel==='MEDIUM') existing.supportLevel='LOW'
    else { await http.delete('/graduation-requirements/mappings/'+existing.id); loadAll(); return }
    await http.put('/graduation-requirements/mappings/'+existing.id, { weight:existing.weight, supportLevel:existing.supportLevel })
  } else {
    const res = await http.post('/graduation-requirements/mappings', { objectiveId:objId, requirementId:gr.id, weight:0.25, supportLevel:'HIGH' })
    if (res.data) mappings.value.push(res.data)
  }
  loadAll()
}
</script>

<style scoped>
.course-layout { max-width:1100px; margin:0 auto; }
.header-bar { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; margin-bottom:8px; }
.cascade { display:flex; align-items:center; gap:10px; }
.cascade-label { font-weight:600; }
.opt-extra { float:right; color:#909399; font-size:12px; }
.header-stats { display:flex; gap:20px; font-size:14px; }
.stat-item b { color:#409EFF; }
.stat-item.warn b { color:#F56C6C; }

.step-panel { min-height:400px; padding:8px 0; }
.step-toolbar { display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; }
.step-title { font-weight:600; font-size:15px; }

.weight-bar { display:flex; align-items:center; gap:10px; margin-bottom:16px; }
.bar-track { flex:1; height:10px; background:#f0f0f0; border-radius:5px; overflow:hidden; }
.bar-fill { height:100%; transition:width .4s; border-radius:5px; }
.bar-label { font-weight:700; font-size:16px; white-space:nowrap; }
.bar-hint { font-size:12px; color:#909399; white-space:nowrap; }

.obj-mini-list { display:flex; flex-direction:column; gap:4px; }
.obj-mini { padding:10px 12px; border-radius:8px; cursor:pointer; border:1px solid #ebeef5; transition:all .15s; display:flex; align-items:center; gap:8px; }
.obj-mini:hover { border-color:#409EFF; }
.obj-mini.active { border-color:#409EFF; background:#ecf5ff; }
.mini-title { flex:1; font-size:13px; font-weight:500; }
.mini-weight { font-size:12px; color:#909399; }

.empty-hint { text-align:center; padding:60px 20px; color:#909399; font-size:14px; }

.ind-method-block { margin-bottom:16px; }
.ind-label { display:flex; align-items:center; gap:8px; margin-bottom:6px; font-weight:500; }

.matrix-wrapper { overflow-x:auto; }
.matrix-table { border-collapse:collapse; width:100%; font-size:12px; }
.matrix-table th, .matrix-table td { border:1px solid #ebeef5; padding:6px 8px; text-align:center; vertical-align:middle; }
.matrix-table .corner { background:#f5f7fa; font-weight:600; min-width:100px; }
.gr-col { min-width:70px; background:#fafbfc; }
.gr-no { font-weight:600; color:#409EFF; font-size:11px; }
.gr-title { font-size:10px; color:#909399; margin-top:2px; }
.co-label { background:#fafbfc; text-align:left; }
.co-no { font-weight:600; font-size:11px; }
.co-name { font-size:10px; color:#909399; }
.cell { cursor:pointer; transition:all .15s; }
.cell:hover { background:#ecf5ff; }
.cell.mapped { background:#d9ecff; font-weight:700; color:#409EFF; }
.cell-value { font-size:14px; }

.step-nav { display:flex; gap:12px; margin-top:24px; padding-top:16px; border-top:1px solid #ebeef5; }
</style>
