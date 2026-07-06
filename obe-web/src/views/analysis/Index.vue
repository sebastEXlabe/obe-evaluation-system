<template>
  <div>
    <div class="toolbar">
      <el-select v-model="groupId" @change="loadAnalysis" placeholder="选择小组" style="width:220px" clearable>
        <el-option v-for="g in groups" :key="g.id" :label="g.groupName" :value="g.id" />
      </el-select>
      <el-button type="primary" @click="calculateAchievement" :loading="loading" :disabled="!groupId">计算达成度</el-button>
      <el-button type="warning" @click="openAhpDialog">AHP权重校验</el-button>
      <el-button @click="loadAnalysis">刷新</el-button>
      <el-button type="success" @click="exportCSV" :disabled="!groupId">📥 导出CSV报告</el-button>
    </div>

    <!-- 达成度概览 -->
    <el-alert v-if="overallAchievement !== null" :title="`整体达成度：${(overallAchievement * 100).toFixed(1)}%`" :type="overallAchievement >= 0.6 ? 'success' : 'error'" :closable="false" show-icon class="achievement-alert" />

    <el-row :gutter="16" style="margin-top:16px">
      <!-- 左侧：目标达成详情 -->
      <el-col :span="15">
        <el-card shadow="never">
          <template #header><span style="font-weight:bold">课程目标达成度详情</span></template>
          <el-table :data="objectiveDetails" border stripe v-loading="loading">
            <el-table-column prop="title" label="目标标题" min-width="150" />
            <el-table-column label="维度" width="80">
              <template #default="{ row }">
                <el-tag :type="row.dimension === 'KNOWLEDGE' ? 'success' : row.dimension === 'ABILITY' ? 'warning' : 'danger'" size="small">
                  {{ row.dimension === 'KNOWLEDGE' ? '知识' : row.dimension === 'ABILITY' ? '能力' : '素养' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="权重" width="90">
              <template #default="{ row }">{{ ((row.weight || 0) * 100).toFixed(0) }}%</template>
            </el-table-column>
            <el-table-column label="达成度" min-width="160">
              <template #default="{ row }">
                <div class="progress-cell">
                  <el-progress :percentage="Math.round((row.achievement || 0) * 1000) / 10" :stroke-width="14" :color="row.achievement >= 0.6 ? '#67c23a' : '#f56c6c'" />
                  <span class="progress-text">{{ ((row.achievement || 0) * 100).toFixed(1) }}%</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="过程性" width="80">
              <template #default="{ row }">{{ ((row.formativeScore || 0)).toFixed(1) }}</template>
            </el-table-column>
            <el-table-column label="终结性" width="80">
              <template #default="{ row }">{{ ((row.summativeScore || 0)).toFixed(1) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：雷达图 -->
      <el-col :span="9">
        <el-card shadow="never" style="height:100%">
          <template #header><span style="font-weight:bold">能力雷达图</span></template>
          <v-chart v-if="achieveData" :option="radarOption" style="height:380px" autoresize />
          <el-empty v-else description="暂无数据，请先选择小组并计算达成度" :image-size="100" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 毕业要求达成度 -->
    <el-row :gutter="16" style="margin-top:16px" v-if="poData">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <span style="font-weight:bold">毕业要求达成度（CO→PO映射·最小值法）</span>
            <el-tag :type="poData.overallPOAchievement>=0.6?'success':'danger'" style="margin-left:8px">总体 {{(poData.overallPOAchievement*100).toFixed(1)}}%</el-tag>
            <span style="font-size:12px;color:#909399;margin-left:8px">{{poData.method}}</span>
          </template>
          <div style="display:flex;flex-wrap:wrap;gap:12px">
            <el-card v-for="p in poData.poDetails" :key="p.requirementId" shadow="hover"
              :style="{width:'300px',borderLeft:'4px solid '+(p.passed?'#67c23a':'#f56c6c')}">
              <div style="font-weight:bold;font-size:14px">{{p.requirementNo}} {{p.title}}</div>
              <div style="margin-top:8px">
                <el-progress :percentage="Math.round(p.minAchievement*1000)/10" :color="p.passed?'#67c23a':'#f56c6c'" :stroke-width="16">
                  <span style="font-size:12px">{{(p.minAchievement*100).toFixed(1)}}%</span>
                </el-progress>
              </div>
              <div style="font-size:11px;color:#909399;margin-top:4px">
                关联{{p.objectiveCount}}个课程目标 | 加权{{(p.weightedAchievement*100).toFixed(0)}}%
                <el-tag :type="p.passed?'success':'danger'" size="small">{{p.passed?'已达成':'未达成'}}</el-tag>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- PDCA改进工单 -->
    <el-row :gutter="16" style="margin-top:16px" v-if="groupId">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:bold">PDCA改进工单</span>
              <el-button type="primary" size="small" @click="generateImprovementTasks" :loading="loading">生成改进工单</el-button>
            </div>
          </template>
          <el-table v-if="improvementTasks.length" :data="improvementTasks" border stripe>
            <el-table-column prop="title" label="工单标题" min-width="150" />
            <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
            <el-table-column label="优先级" width="100">
              <template #default="{ row }">
                <el-tag :type="row.priority==='HIGH'?'danger':row.priority==='MEDIUM'?'warning':'info'" size="small">
                  {{ row.priority==='HIGH'?'高':row.priority==='MEDIUM'?'中':'低' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status==='DONE'?'success':row.status==='IN_PROGRESS'?'warning':'info'" size="small">
                  {{ row.status==='DONE'?'已完成':row.status==='IN_PROGRESS'?'进行中':'待处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="assigneeName" label="负责人" width="100" />
            <el-table-column label="截止日期" width="120">
                <template #default="{ row }">{{ (row.deadline || row.dueDate || '').substring(0, 10) }}</template>
              </el-table-column>
          </el-table>
          <el-empty v-else description="暂无改进工单，点击上方按钮生成" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- AHP权重校验对话框 -->
    <el-dialog v-model="ahpVisible" title="AHP权重校验" width="700px">
      <div v-if="ahpResult">
        <el-alert :title="ahpResult.valid ? '权重一致性校验通过 (CR < 0.1)' : '权重一致性校验不通过 (CR >= 0.1)'" :type="ahpResult.valid ? 'success' : 'error'" :closable="false" show-icon style="margin-bottom:16px" />
        <div style="margin-bottom:8px">CR值: {{ ahpResult.cr?.toFixed(4) || '-' }} | λmax: {{ ahpResult.lambdaMax?.toFixed(4) || '-' }}</div>
        <el-table :data="ahpResult.objectives || []" border stripe>
          <el-table-column prop="title" label="目标" />
          <el-table-column label="权重">
            <template #default="{ row }">{{ ((row.weight || 0) * 100).toFixed(0) }}%</template>
          </el-table-column>
          <el-table-column label="维度">
            <template #default="{ row }">
              <el-tag size="small">{{ row.dimension }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="请先选择课程" />
      <template #footer><el-button @click="ahpVisible = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { RadarChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import http from '../../api/index.js'

use([RadarChart, TooltipComponent, LegendComponent, CanvasRenderer])

const loading = ref(false)
const groups = ref([])
const groupId = ref(null)
const overallAchievement = ref(null)
const objectiveDetails = ref([])
const achieveData = ref(null)

const ahpVisible = ref(false)
const ahpResult = ref(null)
const poData = ref(null)
const improvementTasks = ref([])

const radarOption = computed(() => {
  if (!achieveData.value) return null
  const dim = achieveData.value
  return {
    tooltip: { trigger: 'item' },
    legend: { data: ['达成度'] },
    radar: {
      indicator: [
        { name: '知识掌握', max: 1 },
        { name: '工程实践能力', max: 1 },
        { name: '综合素养', max: 1 }
      ],
      center: ['50%', '55%'],
      radius: '70%'
    },
    series: [{
      type: 'radar',
      data: [{
        value: [dim.knowledge || 0, dim.ability || 0, dim.quality || 0],
        name: '达成度',
        areaStyle: { color: 'rgba(64, 158, 255, 0.25)' },
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' }
      }]
    }]
  }
})

onMounted(() => { loadGroups() })

async function loadGroups() {
  try { const { data } = await http.get('/groups'); groups.value = data || [] } catch (e) { console.error(e) }
}

async function loadAnalysis() {
  if (!groupId.value) {
    overallAchievement.value = null
    objectiveDetails.value = []
    achieveData.value = null
    return
  }
  loading.value = true
  try {
    const { data } = await http.get(`/analysis/achievement?groupId=${groupId.value}`)
    if (data) {
      overallAchievement.value = data.overallAchievement ?? null
      objectiveDetails.value = data.objectiveDetails || []
      achieveData.value = data.dimensionScores || null
    }
    // Load PO achievement
    try {
      const { data: po } = await http.get(`/analysis/po-achievement?groupId=${groupId.value}`)
      poData.value = po
    } catch { poData.value = null }
    // Load improvement tasks
    try {
      const { data: tasks } = await http.get(`/analysis/improvement-tasks?groupId=${groupId.value}`)
      improvementTasks.value = tasks || []
    } catch { improvementTasks.value = [] }
  } catch (e) { console.error(e) } finally { loading.value = false }
}

async function calculateAchievement() {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  loading.value = true
  try {
    await http.post(`/analysis/calculate?groupId=${groupId.value}`)
    ElMessage.success('达成度计算完成')
    loadAnalysis()
  } catch { ElMessage.error('操作失败') } finally { loading.value = false }
}

async function exportCSV() {
  if (!groupId.value) return
  try {
    const response = await http.get(`/export/achievement-csv?groupId=${groupId.value}`, { responseType: 'blob' })
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `achievement-group-${groupId.value}.csv`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('CSV导出成功')
  } catch {
    ElMessage.error('CSV导出失败')
  }
}

async function openAhpDialog() {
  ahpVisible.value = true
  try {
    const { data } = await http.get(`/analysis/ahp-check?groupId=${groupId.value || ''}`)
    ahpResult.value = data
  } catch (e) { console.error(e) }
}

async function generateImprovementTasks() {
  if (!groupId.value) return ElMessage.warning('请先选择小组')
  try {
    const { data } = await http.post(`/analysis/improvement-tasks/generate?groupId=${groupId.value}`)
    improvementTasks.value = data || []
    ElMessage.success('改进工单已生成')
  } catch {
    ElMessage.error('改进工单生成失败')
  }
}
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.achievement-alert { margin-bottom: 0; font-size: 16px; font-weight: bold; }
.progress-cell { display: flex; align-items: center; gap: 8px; }
.progress-cell .el-progress { flex: 1; }
.progress-text { font-size: 13px; min-width: 48px; }
</style>
