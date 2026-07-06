<template>
  <div class="teacher-dashboard">
    <!-- 顶部概览卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card class="stat-card" shadow="hover" @click="card.click">
          <div class="stat-icon" :style="{background:card.color}">{{ card.icon }}</div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主体：评价→达成度管道 -->
    <el-row :gutter="16" class="pipeline-row">
      <el-col :span="12">
        <el-card header="📊 评价数据源" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>📊 评价数据源</span>
              <el-select v-model="selectedGroupId" @change="loadAll" placeholder="选择小组" size="small" style="width:180px" filterable>
                <el-option v-for="g in groups" :key="g.id" :label="g.groupName" :value="g.id" />
              </el-select>
            </div>
          </template>
          <div class="data-source-list">
            <div class="source-item" v-for="ds in dataSources" :key="ds.key">
              <div class="source-header">
                <span class="source-name">{{ ds.icon }} {{ ds.label }}</span>
                <el-tag size="small" :type="ds.auto ? 'success' : 'warning'">{{ ds.auto ? '自动采集' : '教师评分' }}</el-tag>
              </div>
              <el-progress :percentage="Math.round(ds.score)" :color="ds.score > 60 ? '#67C23A' : '#E6A23C'" :stroke-width="8" />
              <div class="source-detail">{{ ds.detail }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card header="🎯 达成度计算结果" v-loading="loading">
          <div v-if="achievement" class="achievement-section">
            <div class="overall-score">
              <el-progress type="dashboard" :percentage="Math.round(achievement.overallAchievement * 100)" color="#409EFF" />
              <div class="overall-label">课程总达成度</div>
            </div>
            <el-divider />
            <div class="dim-scores">
              <div class="dim-item" v-for="(val, key) in achievement.dimensionScores" :key="key">
                <span class="dim-label">{{ dimLabel(key) }}</span>
                <el-progress :percentage="Math.round(val * 100)" :color="dimColor(key)" :stroke-width="12" :format="() => (val * 100).toFixed(0) + '%'" />
              </div>
            </div>
            <el-divider />
            <div class="obj-list">
              <div class="obj-item" v-for="obj in achievement.objectiveDetails" :key="obj.id">
                <span class="obj-title">{{ obj.title }}</span>
                <el-tag size="small" :type="obj.achievement >= 0.6 ? 'success' : 'danger'">
                  {{ (obj.achievement * 100).toFixed(1) }}%
                </el-tag>
              </div>
            </div>
          </div>
          <el-empty v-else description="请先选择小组并计算达成度" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 学情看板（P0-2） -->
    <el-row :gutter="16" class="analytics-row" v-if="analytics">
      <el-col :span="12">
        <el-card header="🔍 知识点问题热力图">
          <div v-if="heatmap.length" class="heatmap-grid">
            <div class="heat-cell" v-for="kp in heatmap.slice(0,8)" :key="kp.knowledgeId"
              :style="{background:kp.color,opacity:Math.min(0.35+kp.questionCount*0.08,1)}">
              <div class="heat-title">{{ kp.title }}</div>
              <div class="heat-stats">
                <span>{{ kp.questionCount }}次提问</span>
                <el-tag size="small" :type="kp.level==='高频'?'danger':kp.level==='中频'?'warning':''" effect="dark">{{ kp.level }}</el-tag>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无知识点提问数据" :image-size="40" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="📊 数据闭环看板（问答→评价→达成度）">
          <el-table :data="dataLoop" size="small" stripe max-height="280">
            <el-table-column prop="userName" label="学生" width="80" />
            <el-table-column label="问答" width="60">
              <template #default="{row}"><el-tag size="small" :type="row.qaCount>5?'success':row.qaCount>0?'warning':'info'">{{ row.qaCount }}</el-tag></template>
            </el-table-column>
            <el-table-column label="自测" width="60">
              <template #default="{row}">{{ row.quizCount>0?row.avgQuizScore+'%':'-' }}</template>
            </el-table-column>
            <el-table-column label="贡献" width="70">
              <template #default="{row}">{{ ((row.contributionRatio||0)*100).toFixed(0) }}%</template>
            </el-table-column>
            <el-table-column label="成绩" width="60">
              <template #default="{row}"><b :style="{color:row.finalScore>=60?'#67C23A':'#F56C6C'}">{{ row.finalScore?.toFixed(0)||'-' }}</b></template>
            </el-table-column>
            <el-table-column label="达成度" width="70">
              <template #default="{row}"><el-progress :percentage="Math.round((row.achievement||0)*100)" :stroke-width="6" :color="row.achievement>=0.6?'#67C23A':'#E6A23C'" /></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预警条 -->
    <el-row :gutter="16" v-if="analytics" style="margin-top:0">
      <el-col :span="24">
        <el-alert :title="analytics.alertCount + ' 人活跃度低 | 共 ' + analytics.totalQuestions + ' 次提问 · ' + analytics.totalQuizzes + ' 次自测'"
          type="warning" show-icon :closable="false" />
      </el-col>
    </el-row>

    <!-- 底部：快捷操作 -->
    <el-row :gutter="16" class="action-row">
      <el-col :span="24">
        <el-card header="⚡ 快捷操作">
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/course')">📋 课程目标 → 指标点 → 评价方式</el-button>
            <el-button type="success" @click="$router.push('/evaluation')">📝 小组评价打分</el-button>
            <el-button type="warning" @click="triggerCalc" :loading="calcLoading">🔄 重新计算达成度</el-button>
            <el-button type="info" @click="$router.push('/analysis')">📈 查看详细分析</el-button>
            <el-button type="danger" @click="$router.push('/project')">📋 项目追踪看板</el-button>
            <el-button @click="$router.push('/qa')">💬 答疑解惑</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/index.js'

const loading = ref(false), calcLoading = ref(false)
const selectedGroupId = ref(null)  // 初始无选中，loadAll第一阶段加载groups后自动赋值
const groups = ref([])
const achievement = ref(null)
const analytics = ref(null)
const heatmap = ref([])
const dataLoop = ref([])

const statCards = computed(() => [
  { icon: '📚', label: '课程数', value: courses.value.length, color: '#409EFF', click: () => router.push('/course') },
  { icon: '👥', label: '小组数', value: groups.value.length, color: '#67C23A', click: () => router.push('/groups') },
  { icon: '✅', label: '达成度', value: achievement.value ? (achievement.value.overallAchievement * 100).toFixed(0) + '%' : '--', color: '#E6A23C' },
  { icon: '📝', label: '目标数', value: achievement.value ? achievement.value.objectiveDetails.length : '--', color: '#F56C6C' },
])
const courses = ref([])

const dataSources = reactive([
  { key:'git', icon:'🔀', label:'Git提交活动', auto:true, score:0, detail:'加载中...' },
  { key:'journal', icon:'📓', label:'项目日志', auto:true, score:0, detail:'加载中...' },
  { key:'test', icon:'📝', label:'自测成绩', auto:true, score:0, detail:'加载中...' },
  { key:'group_eval', icon:'👨‍🏫', label:'教师小组评价', auto:false, score:0, detail:'加载中...' },
])

const dimLabel = (k) => ({ knowledge:'📖 知识掌握', ability:'🔧 工程实践能力', quality:'🎖️ 综合素养' }[k] || k)
const dimColor = (k) => ({ knowledge:'#409EFF', ability:'#E6A23C', quality:'#67C23A' }[k] || '#909399')

async function loadAll() {
  loading.value = true
  try {
    // Phase 1: 加载不依赖 groupId 的数据
    const [groupRes, courseRes, heatRes] = await Promise.all([
      http.get('/groups'),
      http.get('/courses'),
      http.get('/ai-chat/heatmap'),
    ])
    groups.value = groupRes.data || []
    courses.value = courseRes.data || []
    heatmap.value = heatRes.data || []
    if (!selectedGroupId.value && groups.value.length > 0)
      selectedGroupId.value = groups.value[0].id

    // Phase 2: 加载依赖 groupId 的数据
    const gid = selectedGroupId.value
    if (gid) {
      const [achRes, scoresRes, gitRes, analyticsRes] = await Promise.all([
        http.get('/analysis/achievement?groupId=' + gid),
        http.get('/evaluation/personal-scores?groupId=' + gid),
        http.get('/project/git-commits?groupId=' + gid),
        http.get('/ai-chat/analytics?groupId=' + gid),
      ])
      achievement.value = achRes.data || null
      analytics.value = analyticsRes.data || null
      dataLoop.value = (scoresRes.data || []).map(u => ({
        ...u, achievement: u.achievement || (u.finalScore ? u.finalScore / 100 : 0)
      }))

      // Update data source scores
      const gitData = gitRes?.data?.records || []
      dataSources[0].score = Math.min(100, gitData.length * 5)
      dataSources[0].detail = `${gitData.length} 次提交`

      const scores = scoresRes?.data || []
      const avgScore = scores.length > 0 ? scores.reduce((s, x) => s + (x.groupTotalScore||0), 0) / scores.length : 0
      dataSources[3].score = avgScore
      dataSources[3].detail = `${scores.length} 名学生已评分，均分 ${avgScore.toFixed(1)}`
    }
  } catch (e) {
    console.error('Load dashboard error:', e)
  } finally { loading.value = false }
}

async function triggerCalc() {
  calcLoading.value = true
  try {
    await http.post('/analysis/calculate?groupId=' + selectedGroupId.value)
    ElMessage.success('达成度已重新计算')
    loadAll()
  } catch { ElMessage.error('计算失败') }
  finally { calcLoading.value = false }
}

import { useRouter } from 'vue-router'
const router = useRouter()

onMounted(loadAll)
</script>

<style scoped>
.teacher-dashboard { display: flex; flex-direction: column; gap: 16px; padding: 16px; }
.stat-row { margin-bottom: 0; }
.stat-card { cursor: pointer; }
.stat-card :deep(.el-card__body) { display: flex; align-items: center; gap: 16px; padding: 20px; }
.stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.pipeline-row, .action-row { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; width: 100%; }
.data-source-list { display: flex; flex-direction: column; gap: 16px; }
.source-item { background: #f5f7fa; border-radius: 8px; padding: 12px; }
.source-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.source-name { font-weight: 500; }
.source-detail { margin-top: 4px; font-size: 12px; color: #909399; }
.achievement-section { text-align: center; }
.overall-score { display: flex; flex-direction: column; align-items: center; }
.overall-label { margin-top: 8px; font-weight: 600; color: #303133; }
.dim-scores { display: flex; flex-direction: column; gap: 12px; }
.dim-item { display: flex; align-items: center; gap: 12px; }
.dim-label { width: 120px; text-align: right; font-size: 13px; font-weight: 500; }
.dim-item :deep(.el-progress) { flex: 1; }
.obj-list { display: flex; flex-direction: column; gap: 8px; }
.obj-item { display: flex; justify-content: space-between; align-items: center; }
.obj-title { font-size: 13px; flex: 1; text-align: left; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.quick-actions { display: flex; flex-wrap: wrap; gap: 10px; }
</style>
