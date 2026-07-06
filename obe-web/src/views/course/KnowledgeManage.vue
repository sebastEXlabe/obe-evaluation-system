<template>
  <div class="kp-layout">
    <div class="kp-info">
      <el-alert type="info" :closable="false" show-icon>
        <template #title>
          知识库管理已迁移到 <b>MaxKB</b>（端口 8080）。MaxKB 提供文档上传、自动分段、向量语义检索等完整功能。
        </template>
      </el-alert>
      <div style="display:flex;gap:12px;margin-top:12px">
        <el-button type="primary" @click="openMaxKB">📚 打开 MaxKB 知识库管理</el-button>
        <el-button @click="$router.push('/ai-chat')">🤖 使用 MaxKB 知识库问答</el-button>
      </div>
      <el-divider />
      <p style="color:#909399;font-size:13px">
        当前知识库索引（仅供 AI 快速参考，主要内容在 MaxKB 中管理）：
      </p>
      <el-table :data="knowledgePoints" stripe size="small" v-loading="loading">
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="chapter" label="章节" width="100" />
        <el-table-column label="内容" min-width="200" show-overflow-tooltip>
          <template #default="{row}">{{ row.content?.substring(0,80) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button size="small" text type="danger" @click="deleteKp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const knowledgePoints = ref([])
const loading = ref(false)

function openMaxKB() { window.open('http://localhost:8080/admin', '_blank') }

async function loadAll() {
  loading.value = true
  try { const { data } = await http.get('/knowledge-points'); knowledgePoints.value = (data?.records || data || []) } catch (e) { console.error('Failed to load knowledge points:', e) }
  loading.value = false
}

function deleteKp(row) {
  ElMessageBox.confirm('删除「'+row.title+'」？').then(async()=>{
    await http.delete('/knowledge-points/'+row.id); ElMessage.success('已删除'); loadAll()
  }).catch(()=>{})
}

onMounted(loadAll)
</script>

<style scoped>
.kp-layout { max-width:900px; margin:0 auto; }
.kp-info { padding:8px; }
</style>
