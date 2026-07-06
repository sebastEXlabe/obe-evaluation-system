<template>
  <div class="kp-manage">
    <div class="kp-header">
      <h3>📚 知识库管理</h3>
      <el-button type="primary" @click="openAdd">+ 添加知识点</el-button>
    </div>
    <el-table :data="knowledgePoints" stripe v-loading="loading" style="width:100%">
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="chapter" label="章节" width="120" />
      <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="140">
        <template #default="{row}">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{row}">
          <el-button size="small" text @click="editKp(row)">编辑</el-button>
          <el-button size="small" text type="danger" @click="deleteKp(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id?'编辑':'添加'" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="章节"><el-input v-model="form.chapter" placeholder="如：第1章" /></el-form-item>
        <el-form-item label="内容">
          <div class="editor-wrapper">
            <div class="editor-toolbar">
              <el-button size="small" text @click="insertMD('**', '**')"><b>B</b></el-button>
              <el-button size="small" text @click="insertMD('*', '*')"><i>I</i></el-button>
              <el-button size="small" text @click="insertMD('## ', '')">H2</el-button>
              <el-button size="small" text @click="insertMD('- ', '')">•</el-button>
              <el-button size="small" text @click="insertMD('1. ', '')">1.</el-button>
              <el-button size="small" text @click="insertMD('\n```\n', '\n```')">&lt;/&gt;</el-button>
              <el-button size="small" text @click="showPreview=!showPreview">👁</el-button>
            </div>
            <el-input v-if="!showPreview" v-model="form.content" type="textarea" :rows="10" placeholder="支持 Markdown 格式：**粗体** *斜体* ## 标题 - 列表" class="editor-textarea" />
            <div v-else class="md-preview" v-html="renderMD(form.content)"></div>
          </div>
        </el-form-item>
        <el-form-item label="附件" v-if="form.id">
          <div style="display:flex;flex-wrap:wrap;gap:8px;align-items:center">
            <div v-for="att in formAttachments" :key="att.path" class="att-chip">
              <span>{{ att.name }}</span>
              <el-button size="small" text type="danger" @click="deleteAtt(att)">✕</el-button>
            </div>
            <el-upload :http-request="uploadAtt" :show-file-list="false" accept="image/*,.pdf,.txt,.doc,.docx">
              <el-button size="small" plain>📎 上传文件</el-button>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const knowledgePoints = ref([])
const loading = ref(false), saving = ref(false), dialogVisible = ref(false)
const form = reactive({ id:null, title:'', chapter:'', content:'' })
const showPreview = ref(false)
const formAttachments = ref([])

function editKp(row) {
  Object.assign(form, { id:row.id, title:row.title, chapter:row.chapter||'', content:row.content||'' })
  try { formAttachments.value = JSON.parse(row.attachments||'[]') } catch { formAttachments.value = [] }
  dialogVisible.value = true
}
function openAdd() { Object.assign(form, { id:null, title:'', chapter:'', content:'' }); formAttachments.value = []; dialogVisible.value = true }

async function uploadAtt(options) {
  const fd = new FormData(); fd.append('file', options.file)
  try {
    const { data } = await http.post('/knowledge-points/'+form.id+'/upload', fd)
    formAttachments.value.push(data.attachment)
    ElMessage.success('已上传')
  } catch { ElMessage.error('上传失败') }
}

async function deleteAtt(att) {
  try {
    await http.delete('/knowledge-points/'+form.id+'/attachments/'+att.path)
    formAttachments.value = formAttachments.value.filter(a => a.path !== att.path)
    ElMessage.success('已删除')
  } catch { ElMessage.error('删除失败') }
}

function insertMD(before, after) {
  const ta = document.querySelector('.editor-textarea textarea')
  if (!ta) return
  const s = ta.selectionStart; const e = ta.selectionEnd
  const text = form.content
  form.content = text.substring(0, s) + before + text.substring(s, e) + after + text.substring(e)
  setTimeout(() => { ta.selectionStart = ta.selectionEnd = s + before.length }, 50)
}

function renderMD(text) {
  if (!text) return ''
  return text
    .replace(/\*\*(.+?)\*\*/g, '<b>$1</b>')
    .replace(/\*(.+?)\*/g, '<i>$1</i>')
    .replace(/^## (.+)/gm, '<h3>$1</h3>')
    .replace(/^- (.+)/gm, '<li>$1</li>')
    .replace(/^(\d+)\. (.+)/gm, '<li>$2</li>')
    .replace(/```\n([\s\S]+?)```/g, '<pre><code>$1</code></pre>')
    .replace(/\n/g, '<br>')
}

function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }

async function loadAll() {
  loading.value = true
  try { const { data } = await http.get('/knowledge-points'); knowledgePoints.value = data || [] } catch {}
  loading.value = false
}

function deleteKp(row) { ElMessageBox.confirm('删除「'+row.title+'」？').then(async()=>{ await http.delete('/knowledge-points/'+row.id); ElMessage.success('已删除'); loadAll() }).catch(()=>{}) }

async function save() {
  if (!form.title) return ElMessage.warning('请输入标题')
  saving.value = true
  try {
    if (form.id) await http.put('/knowledge-points', { ...form, attachments: JSON.stringify(formAttachments.value) })
    else await http.post('/knowledge-points', { ...form })
    dialogVisible.value = false; ElMessage.success('已保存'); loadAll()
  } catch { ElMessage.error('保存失败') }
  saving.value = false
}

onMounted(loadAll)
</script>

<style scoped>
.kp-manage { max-width:1000px; margin:0 auto; }
.kp-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.kp-header h3 { margin:0; }
.editor-wrapper { border:1px solid #dcdfe6; border-radius:6px; overflow:hidden; }
.editor-toolbar { display:flex; gap:2px; padding:6px 10px; background:#f5f7fa; border-bottom:1px solid #dcdfe6; }
.editor-textarea :deep(.el-textarea__inner) { border:none; border-radius:0; }
.md-preview { padding:12px 16px; min-height:200px; line-height:1.8; }
.md-preview :deep(h3) { margin:8px 0 4px; font-size:15px; }
.md-preview :deep(li) { margin-left:16px; }
.md-preview :deep(pre) { background:#f5f7fa; padding:10px; border-radius:4px; font-size:13px; }
.att-chip { display:flex; align-items:center; gap:4px; background:#ecf5ff; padding:2px 8px; border-radius:4px; font-size:13px; }
</style>
