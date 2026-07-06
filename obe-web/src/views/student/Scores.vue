<template>
  <div>
    <!-- 达成度概览 -->
    <el-card shadow="hover" class="overview-card">
      <template #header><span style="font-weight:bold">个人达成度概览</span></template>
      <div v-if="scores" class="overview-body">
        <div class="overview-item">
          <div class="overview-label">整体达成度</div>
          <div class="overview-value" :class="(scores.overallAchievement || 0) >= 0.6 ? 'pass' : 'fail'">
            {{ ((scores.overallAchievement || 0) * 100).toFixed(1) }}%
          </div>
        </div>
        <div class="overview-progress">
          <el-progress :percentage="Math.round((scores.overallAchievement || 0) * 1000) / 10" :stroke-width="18" :color="(scores.overallAchievement || 0) >= 0.6 ? '#67c23a' : '#f56c6c'" :format="() => ((scores.overallAchievement || 0) * 100).toFixed(1) + '%'" />
        </div>
      </div>
      <el-empty v-else description="暂无成绩数据" :image-size="80" />
    </el-card>

    <!-- 各目标达成详情 -->
    <el-card shadow="hover" style="margin-top:16px" v-if="objectiveDetails.length > 0">
      <template #header><span style="font-weight:bold">课程目标达成详情</span></template>
      <el-table :data="objectiveDetails" border stripe>
        <el-table-column prop="title" label="目标" min-width="150" />
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
        <el-table-column prop="score" label="得分" width="80" />
        <el-table-column prop="fullScore" label="满分" width="80" />
      </el-table>
    </el-card>

    <!-- 成绩明细 -->
    <el-card shadow="hover" style="margin-top:16px">
      <template #header><span style="font-weight:bold">成绩明细</span></template>
      <el-descriptions v-if="scores" :column="2" border size="small">
        <el-descriptions-item label="小组得分">{{ scores.groupScore ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="贡献系数">{{ ((scores.contributionRatio || 0) * 100).toFixed(0) }}%</el-descriptions-item>
        <el-descriptions-item label="加分">{{ scores.bonus ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="最终成绩">
          <span style="font-weight:bold;font-size:16px">{{ scores.finalScore ?? '-' }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import http from '../../api/index.js'

const scores = ref(null)
const objectiveDetails = ref([])

onMounted(() => { loadScores() })

async function loadScores() {
  try {
    const { data } = await http.get('/student/scores')
    if (data) {
      scores.value = data
      objectiveDetails.value = data.objectiveDetails || []
    }
  } catch (e) { console.error('Failed to load scores:', e) }
}
</script>

<style scoped>
.overview-card { border-radius: 8px; }
.overview-body { text-align: center; }
.overview-item { margin-bottom: 12px; }
.overview-label { font-size: 14px; color: #909399; margin-bottom: 6px; }
.overview-value { font-size: 36px; font-weight: bold; }
.overview-value.pass { color: #67c23a; }
.overview-value.fail { color: #f56c6c; }
.overview-progress { max-width: 400px; margin: 0 auto; }
.progress-cell { display: flex; align-items: center; gap: 8px; }
.progress-cell .el-progress { flex: 1; }
.progress-text { font-size: 13px; min-width: 48px; }
</style>
