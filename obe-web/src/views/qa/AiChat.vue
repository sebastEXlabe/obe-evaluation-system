<template>
  <div class="ai-chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <span>历史会话</span>
        <el-button size="small" text @click="newChat">+ 新建</el-button>
      </div>
      <div class="session-list" v-loading="sessionsLoading">
        <div v-for="s in sessions" :key="s.sessionId" class="session-item" :class="{ active: currentSession===s.sessionId }" @click="switchSession(s)">
          <div class="session-title">{{ s.title || '新对话' }}</div>
          <div class="session-actions">
            <span class="session-count">{{ s.count }}条</span>
            <el-button size="small" text type="danger" @click.stop="delSession(s)">🗑</el-button>
          </div>
        </div>
        <el-empty v-if="!sessionsLoading && sessions.length===0" description="暂无历史" :image-size="40" />
      </div>
    </div>
    <div class="chat-main">
      <div class="chat-header">
        <span>🤖 AI助手 · DeepSeek</span>
        <span style="flex:1"></span>
        <el-button size="small" text @click="newChat">清空当前</el-button>
      </div>
      <div class="chat-messages" ref="msgBox">
        <div v-if="messages.length===0" class="welcome">
          <div class="welcome-icon">🤖</div>
          <h3>DeepSeek AI 课程助手</h3>
          <p>关于 OBE 达成度、AHP 权重、CDIO 项目等问题，随时问我</p>
        </div>
        <div v-for="(m,i) in messages" :key="i" class="msg-row" :class="m.role">
          <div class="msg-avatar">{{ m.role==='user'?'👤':'🤖' }}</div>
          <div v-if="m.role==='user'" class="msg-bubble">{{ m.content }}</div>
          <div v-else class="msg-bubble md-body" v-html="renderMD(m.content)"></div>
        </div>
        <div v-if="thinking" class="msg-row ai">
          <div class="msg-avatar">🤖</div>
          <div class="msg-bubble typing"><span></span><span></span><span></span></div>
        </div>
      </div>
      <div class="chat-input-bar">
        <el-input v-model="input" @keydown.enter.exact.prevent="send" placeholder="输入问题，Enter 发送..." size="large" />
        <el-button type="primary" @click="send" :loading="thinking" :disabled="!input.trim()" size="large">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/index.js'

const messages = ref([])
const input = ref('')
const thinking = ref(false)
const msgBox = ref(null)
const sessions = ref([])
const sessionsLoading = ref(false)
const currentSession = ref(null)

function renderMD(text) {
  if (!text) return ''
  return text
    .replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
    .replace(/\*\*(.+?)\*\*/g,'<b>$1</b>')
    .replace(/\*(.+?)\*/g,'<i>$1</i>')
    .replace(/^### (.+)/gm,'<h4>$1</h4>')
    .replace(/^## (.+)/gm,'<h3>$1</h3>')
    .replace(/^- (.+)/gm,'<li>$1</li>')
    .replace(/^(\d+)\. (.+)/gm,'<li>$2</li>')
    .replace(/```(\w*)\n?([\s\S]+?)```/g,'<pre><code>$2</code></pre>')
    .replace(/`([^`]+)`/g,'<code>$1</code>')
    .replace(/\n\n/g,'<br><br>')
    .replace(/\n/g,'<br>')
}

function scrollEnd() { nextTick(() => { if(msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight }) }

async function loadSessions() {
  sessionsLoading.value = true
  try {
    const { data } = await http.get('/ai-chat/sessions')
    sessions.value = data || []
  } catch (e) { console.error(e) } finally { sessionsLoading.value = false }
}

function newChat() {
  messages.value = []
  input.value = ''
  currentSession.value = null
}

function switchSession(s) {
  currentSession.value = s.sessionId
  messages.value = (s.records || []).slice().reverse().flatMap(r => [
    { role:'user', content: r.question },
    { role:'ai', content: r.answer }
  ])
  scrollEnd()
}

async function delSession(s) {
  try {
    await http.delete('/ai-chat/sessions/' + s.sessionId)
    if (currentSession.value === s.sessionId) newChat()
    loadSessions()
  } catch (e) { console.error(e) }
}

async function send() {
  const q = input.value.trim(); if(!q) return
  input.value = ''
  messages.value.push({ role:'user', content:q })
  thinking.value = true; scrollEnd()
  try {
    const { data } = await http.post('/ai-chat/ask', {
      question: q,
      sessionId: currentSession.value || undefined
    })
    if (!currentSession.value) currentSession.value = data.sessionId
    messages.value.push({ role:'ai', content: data.answer })
    loadSessions()
  } catch {
    messages.value.push({ role:'ai', content:'抱歉，AI 服务暂时不可用，请稍后重试。' })
  }
  thinking.value = false; scrollEnd()
}

onMounted(loadSessions)
</script>

<style scoped>
.ai-chat-container { height:calc(100vh - 120px); background:#fff; border-radius:8px; box-shadow:0 1px 4px rgba(0,0,0,.06); display:flex; overflow:hidden; }
.chat-sidebar { width:220px; border-right:1px solid #ebeef5; display:flex; flex-direction:column; flex-shrink:0; }
.sidebar-header { padding:14px 16px; border-bottom:1px solid #ebeef5; display:flex; justify-content:space-between; align-items:center; font-weight:600; }
.session-list { flex:1; overflow-y:auto; padding:8px; }
.session-item { padding:10px 12px; border-radius:6px; cursor:pointer; margin-bottom:4px; transition:background .2s; }
.session-item:hover { background:#f5f7fa; }
.session-item.active { background:#ecf5ff; }
.session-title { font-size:13px; margin-bottom:4px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.session-actions { display:flex; justify-content:space-between; align-items:center; }
.session-count { font-size:11px; color:#909399; }
.chat-main { flex:1; display:flex; flex-direction:column; }
.chat-header { padding:14px 20px; border-bottom:1px solid #ebeef5; display:flex; justify-content:space-between; align-items:center; font-weight:600; }
.chat-messages { flex:1; overflow-y:auto; padding:20px; display:flex; flex-direction:column; gap:16px; }
.welcome { text-align:center; padding:60px 20px; }
.welcome-icon { font-size:48px; margin-bottom:12px; }
.welcome h3 { margin:0 0 8px; }
.welcome p { color:#909399; }
.msg-row { display:flex; gap:10px; max-width:85%; }
.msg-row.user { align-self:flex-end; flex-direction:row-reverse; }
.msg-row.ai { align-self:flex-start; }
.msg-avatar { width:34px; height:34px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:16px; background:#f0f0f5; flex-shrink:0; }
.msg-bubble { padding:10px 14px; border-radius:12px; font-size:14px; line-height:1.7; white-space:pre-wrap; }
.md-body :deep(h3) { margin:8px 0 4px; font-size:15px; font-weight:600; }
.md-body :deep(h4) { margin:6px 0 3px; font-size:14px; font-weight:600; }
.md-body :deep(li) { margin-left:16px; margin-bottom:4px; }
.md-body :deep(code) { background:rgba(0,0,0,.06); padding:1px 4px; border-radius:3px; font-size:13px; }
.md-body :deep(pre) { background:rgba(0,0,0,.06); padding:10px; border-radius:6px; overflow-x:auto; margin:8px 0; }
.md-body :deep(pre code) { background:none; padding:0; }
.md-body :deep(b) { font-weight:600; }
.msg-row.user .msg-bubble.md-body :deep(code),
.msg-row.user .msg-bubble.md-body :deep(pre) { background:rgba(255,255,255,.2); }
.msg-row.user .msg-bubble { background:#409EFF; color:#fff; border-bottom-right-radius:4px; }
.msg-row.ai .msg-bubble { background:#f0f2f5; color:#303133; border-bottom-left-radius:4px; }
.typing { display:flex; gap:4px; padding:14px 18px; }
.typing span { width:8px; height:8px; border-radius:50%; background:#909399; animation:wave 1.4s infinite; }
.typing span:nth-child(2) { animation-delay:.2s; }
.typing span:nth-child(3) { animation-delay:.4s; }
@keyframes wave { 0%,60%,100%{opacity:.3} 30%{opacity:1} }
.chat-input-bar { padding:14px 20px; border-top:1px solid #ebeef5; display:flex; gap:10px; }
.chat-input-bar :deep(.el-input){ flex:1; }
</style>
