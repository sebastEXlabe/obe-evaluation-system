<template>
  <div class="ai-chat-container">
    <div class="chat-main">
      <div class="chat-header">
        <span>🤖 AI助手 · DeepSeek</span>
        <el-button size="small" text @click="newChat">新对话</el-button>
      </div>
      <div class="chat-messages" ref="msgBox">
        <div v-if="messages.length===0" class="welcome">
          <div class="welcome-icon">🤖</div>
          <h3>DeepSeek AI 课程助手</h3>
          <p>关于 OBE 达成度、AHP 权重、CDIO 项目等问题，随时问我</p>
        </div>
        <div v-for="(m,i) in messages" :key="i" class="msg-row" :class="m.role">
          <div class="msg-avatar">{{ m.role==='user'?'👤':'🤖' }}</div>
          <div class="msg-bubble">{{ m.content }}</div>
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
import { ref, nextTick } from 'vue'
import http from '../../api/index.js'

const messages = ref([])
const input = ref('')
const thinking = ref(false)
const msgBox = ref(null)

function scrollEnd() { nextTick(() => { if(msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight }) }

function newChat() { messages.value = []; input.value = '' }

async function send() {
  const q = input.value.trim(); if(!q) return
  input.value = ''
  messages.value.push({ role:'user', content:q })
  thinking.value = true; scrollEnd()
  try {
    const { data } = await http.post('/ai-chat/ask', { question: q })
    messages.value.push({ role:'ai', content: data.answer })
  } catch {
    messages.value.push({ role:'ai', content:'抱歉，AI 服务暂时不可用，请稍后重试。' })
  }
  thinking.value = false; scrollEnd()
}
</script>

<style scoped>
.ai-chat-container { height:calc(100vh - 120px); background:#fff; border-radius:8px; box-shadow:0 1px 4px rgba(0,0,0,.06); display:flex; overflow:hidden; }
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
.msg-avatar { width:34px; height:34px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:16px; background:#f0f0f0; flex-shrink:0; }
.msg-bubble { padding:10px 14px; border-radius:12px; font-size:14px; line-height:1.7; white-space:pre-wrap; }
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
