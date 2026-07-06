<template>
  <div class="kp-layout">
    <div class="kp-iframe-wrapper">
      <iframe :src="maxkbUrl" class="kp-iframe" title="MaxKB知识库管理" />
    </div>
    <el-divider />
    <p style="color:#909399;font-size:13px;margin-bottom:8px">
      本地知识库索引（供 AI 快速参考）：
    </p>
    <el-table :data="knowledgePoints" stripe size="small" v-loading="loading" empty-text="暂无知识点">
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/index.js'

const knowledgePoints = ref([])
const loading = ref(false)
const maxkbUrl = computed(() => {
  const loc = window.location
  return loc.protocol + '//' + loc.hostname + ':8080/'
})

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
.kp-layout { max-width:100%; margin:0 auto; }
.kp-iframe-wrapper { width:100%; height:70vh; border:1px solid #dcdfe6; border-radius:4px; overflow:hidden; }
.kp-iframe { width:100%; height:100%; border:none; }
</style>
