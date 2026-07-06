# OBE-CDIO 系统 — 第3轮全量审计报告

> 审计日期：2026-07-05（更新后代码）  
> 审计方法：3 Agent 并行 × 第一性原理 + 使用者角度  
> 审计范围：全部 Controller + Service + Vue 页面

---

## 🔴 CRITICAL（8 个）

| # | 位置 | 问题 | 用户影响 |
|---|------|------|---------|
| 1 | SecurityConfig | `/api/export/**`、`/api/graduation-requirements/**` 无角色限制 | **学生可下载全组成绩、删除毕业要求** |
| 2 | SecurityConfig | group 规则写的是 `/api/group` 但 Controller 在 `/api/groups` | 小组 POST/PUT/DELETE 权限规则全部失效 |
| 3 | group/Index.vue | 无编辑按钮——小组创建后永远无法修改 | 删除+重建才能改小组名 |
| 4 | ExportController | 无 CSV 转义——标题含逗号时列错位 | 导出数据乱码 |
| 5 | ExportController | 统计数字未按 groupId 过滤——全局数据混入单组报告 | 每次导出数字都不一样 |
| 6 | analysis/Index.vue | CSV 导出用 `window.open` 绕过 axios——不带 JWT | 导出 HTTP 401，下载失败 |
| 7 | QaController | `POST /sessions` 接受请求体中的 userId——身份伪造 | 学生可冒充他人创建会话 |
| 8 | GitSyncController | `GET /repos` 返回 accessToken 明文——令牌泄露 | Token 被前端/日志暴露 |

---

## 🟠 HIGH（10 个）

| # | 位置 | 问题 | 用户影响 |
|---|------|------|---------|
| 9 | AnalysisController | PO 达成度无映射时静默返回 0——无诊断提示 | 看不到"为什么是0" |
| 10 | ExportController | PO 加权平均分母可能为0——NaN/Infinity | CSV 数字异常 |
| 11 | AhpService | 负值/零值无校验——输出虚假权重和 CR | AHP 结果不可信 |
| 12 | Anti-freerider | 地板值 0.3 太高——不做任何事也得 24 分 | 搭便车无惩罚 |
| 13 | Anti-freerider | Git 空提交(+10) > 5行真实提交(+5)——反常激励 | 鼓励空提交 |
| 14 | Anti-freerider | 日志权重(50/条)过高——一篇日志=30行代码 | 鼓励刷日志 |
| 15 | ProjectController | journal/git/contribution 的 POST 被 SecurityConfig 阻止给学生 | 学生端项目追踪不可用 |
| 16 | SettingsController | group-overview 无角色限制——任何认证用户可枚举所有小组 | 隐私泄露 |
| 17 | SettingsController | student-achievements 无 groupId 校验——可查任何组 | 隐私泄露 |
| 18 | AnalysisController | ahp-check 的 λmax 是假公式计算而非真实特征值 | 论文数据不严谨 |

---

## 🟡 MEDIUM（12 个）

| # | 位置 | 问题 |
|---|------|------|
| 19 | 7个Controller | `getAuthentication().getPrincipal()` 无 null 检查——NPE风险 |
| 20 | AnalysisController | radar 端点 courseId=null 时返回 200+假数据（其他端点返回 400） |
| 21 | analysis/Index.vue | PDCA 改进工单前端控件缺失 |
| 22 | course/Index.vue | CO→PO 映射无 UI |
| 23 | project/Index.vue | 任务创建缺 dueDate 字段 |
| 24 | evaluation/Index.vue | 角色评价编辑时不可改用户 |
| 25 | 全部5页面 | `catch {}` 空块——无组件级错误恢复 |
| 26 | 全部5页面 | 无 `v-loading` 加载指示器 |
| 27 | AuthController | 默认密码 8 位 hex——32bit 熵过低 |
| 28 | AuthController | 无登录失败锁定——暴力破解 |
| 29 | GraduationRequirementController | 缺失 GET by ID |
| 30 | QaController | `GET /sessions/{id}` 数值型可查看任何用户 QA——信息披露 |
| 31 | QaController | `deleteSession()` 无事务——部分删除 |

---

## 🔵 LOW（10 个）

| # | 位置 | 问题 |
|---|------|------|
| 32 | evaluation/Index.vue | `computed` 导入未使用 + `user` 死代码 |
| 33 | analysis/Index.vue | `ElMessageBox` 导入未使用 |
| 34 | EvaluationController | 缺 GET by ID 端点 |
| 35 | CourseManageController | DELETE 缺 defense-in-depth 鉴权 |
| 36 | project/Index.vue | Git commit 表单缺 repoId 关联 |
| 37 | AchievementService | 指标点权重硬编码为 1.0 |
| 38 | AchievementService | generateSuggestions 跨组污染（取课程均值而非组数据） |
| 39 | EvaluationService | role bonus 实际范围仅 0-2 分，区分度太低 |
| 40 | SecurityConfig | knowledge 路径写 `/api/knowledge/**` 但 Controller 在 `/api/knowledge-points` |

---

## 📊 统计

| 级别 | 数量 | 需优先修复 |
|------|------|-----------|
| 🔴 CRITICAL | 8 | SecurityConfig(2) + 导出(3) + 小组UI(1) + QA伪造(1) + Token泄露(1) |
| 🟠 HIGH | 10 | 反搭便车算法(3) + 权限(3) + AHP(2) + PO(1) + 学生端阻断(1) |
| 🟡 MEDIUM | 12 | NPE(1) + UI缺失(5) + Auth(2) + QA(2) + CRUD缺失(1) + 事务(1) |
| 🔵 LOW | 10 | 死代码(2) + 缺端点(2) + 硬编码(4) + 配置(1) + 算法细节(1) |
| **合计** | **40** | |
