<template>
  <div>
    <!-- 未加入小组：空状态 + 邀请码加入 -->
    <div v-if="!myGroup" class="empty-state">
      <el-empty description="你还没有加入任何小组" :image-size="150" />
      <div class="join-section">
        <el-input v-model="inviteCode" placeholder="请输入邀请码" style="width:260px" clearable @keyup.enter="joinByCode" />
        <el-button type="primary" @click="joinByCode" :loading="joining">加入小组</el-button>
      </div>
    </div>

    <!-- 已加入小组：小组信息 + 成员列表 -->
    <div v-else>
      <el-card shadow="hover" class="group-info-card">
        <template #header>
          <div class="group-info-header">
            <span class="group-title">{{ myGroup.name }}</span>
            <el-tag :type="myGroup.myRole === 'LEADER' ? 'warning' : 'info'">{{ myGroup.myRole === 'LEADER' ? '组长' : '成员' }}</el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="所属课程">{{ myGroup.courseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="成员数">{{ myGroup.memberCount || 0 }} / {{ myGroup.maxMembers || 8 }}</el-descriptions-item>
          <el-descriptions-item label="邀请码">
            <el-tag type="warning" style="cursor:pointer" @click="copyCode(myGroup.inviteCode)">{{ myGroup.inviteCode || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建人">{{ myGroup.teacherName || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="hover" style="margin-top:16px">
        <template #header><span style="font-weight:bold">小组成员</span></template>
        <el-table :data="members" border stripe>
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column label="角色">
            <template #default="{ row }">
              <el-tag :type="row.role === 'LEADER' ? 'warning' : 'info'" size="small">{{ row.role === 'LEADER' ? '组长' : '成员' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="加入时间" width="160">
            <template #default="{ row }">{{ row.joinTime || row.createTime || '-' }}</template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/index.js'

const myGroup = ref(null)
const members = ref([])
const inviteCode = ref('')
const joining = ref(false)

onMounted(() => { loadMyGroup() })

async function loadMyGroup() {
  try {
    const { data } = await http.get('/groups/my-groups')
    if (data && data.length > 0) {
      // API returns: {group:{id,groupName,...}, members:[...], memberCount, myRole, courseName}
      const raw = data[0]
      myGroup.value = {
        id: raw.group?.id,
        name: raw.group?.groupName,
        inviteCode: raw.group?.inviteCode,
        maxMembers: raw.group?.maxMembers,
        memberCount: raw.memberCount,
        courseName: raw.courseName,
        myRole: raw.myRole,
        teacherName: raw.group?.teacherName
      }
      members.value = (raw.members || []).map(m => ({
        realName: m.realName, username: m.username || '',
        role: m.roleCode, joinTime: m.joinTime || m.createTime
      }))
    } else {
      myGroup.value = null; members.value = []
    }
  } catch {}
}

async function joinByCode() {
  if (!inviteCode.value.trim()) return ElMessage.warning('请输入邀请码')
  joining.value = true
  try {
    const { data } = await http.post('/groups/join-by-code', { inviteCode: inviteCode.value.trim() })
    ElMessage.success('成功加入小组！')
    inviteCode.value = ''
    loadMyGroup()
  } catch (e) {
    const msg = e?.response?.data?.message || '操作失败'
    ElMessage.error(msg)
  } finally { joining.value = false }
}

async function copyCode(code) {
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success('邀请码已复制到剪贴板')
  } catch {
    ElMessage.info('邀请码: ' + code)
  }
}
</script>

<style scoped>
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 60px 0; }
.join-section { display: flex; gap: 12px; margin-top: 20px; align-items: center; }
.group-info-card { border-radius: 8px; }
.group-info-header { display: flex; justify-content: space-between; align-items: center; }
.group-title { font-weight: bold; font-size: 18px; }
</style>
