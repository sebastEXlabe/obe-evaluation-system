# OBE-CDIO 教学评价管理系统 — 全量代码深度审查报告

> 审查日期：2026-07-05  
> 审查范围：72 Java后端 + 18 Vue/JS前端 = 90个文件  
> 审查维度：7大模块 × 50+功能单元  
> 审查标准：第一性原理 + 使用者角度  

---

## 一、🔴 CRITICAL：前后端 API 不匹配（前端调不通后端）

| # | 前端调用 | 后端实际路由 | 影响页面 |
|---|---------|-------------|---------| 
| 1 | `/api/groups` (复数) | `/api/group` (单数) | group, student, evaluation, analysis, project, settings |
| 2 | `/api/settings/*` (7个端点) | **不存在** | settings |
| 3 | `/api/users` | `/api/auth/users` | settings |
| 4 | `/api/users/search` | **不存在** | group, qa |
| 5 | `/api/student/scores` | **不存在** | student/Scores |
| 6 | PUT `/{path}/${id}` 模式 | PUT `/{path}` (无路径ID) | project(milestones/tasks/journals), evaluation |
| 7 | `/api/evaluation/group` (单数) | `/api/evaluation/groups` (复数) | evaluation |
| 8 | `/api/project/git-repos` | `/api/git-sync/repos` | project |
| 9 | `/api/analysis/ahp-check` | `/api/analysis/validate-weights` | analysis |
| 10 | `/api/qa/sessions/${userId}` | `/api/qa/sessions` (无路径参数) | qa |

**结论：12个前端页面中，至少9个页面的API调用和后端路由不匹配，发版后直接404。**

---

## 二、🔴 CRITICAL：后端功能缺陷

| # | 模块 | 问题 | 严重度 |
|---|------|------|--------|
| 1 | 小组 | deleteGroup 调用 removeById 而非 removeGroupCascade → 删除小组后成员成孤儿 | 数据完整性 |
| 2 | 评价 | 批量计算失败回滚所有进度 → 返回成功计数是假的 | 事务Bug |
| 3 | 反搭便车 | 地板值0.3太高 → 不做任何事和做一点得分一样(24分) | 算法设计 |
| 4 | 反搭便车 | 无Bayesian平滑 → 简单 myScore/maxScore | 算法缺失 |
| 5 | MaxKB | 用 System.getProperty 而非 Spring @Value → 集成永远不会工作 | 配置Bug |
| 6 | Git同步 | Gitee提交行数永远为0 → 50%平台数据偏差 | 功能缺陷 |
| 7 | 达成度 | generateSuggestions 逻辑存在但无API端点调用 → 孤立代码 | 功能缺失 |
| 8 | 达成度 | getStudentAchievement 无API端点 → 孤立代码 | 功能缺失 |
| 9 | 达成度 | MANUAL/MAXKB数据源未实现 → 回退到group_eval | 功能缺失 |
| 10 | QA | 文件上传无磁盘存储、无类型验证、无安全命名 | 功能虚假 |
| 11 | 课程 | update允许教师通过修改teacherId窃取他人课程 | 权限漏洞 |
| 12 | 认证 | 创建用户默认密码硬编码"123456" | 安全风险 |
| 13 | JWT | TokenBlacklist纯内存无过期清理 → 内存泄漏 | 运维风险 |

---

## 三、🟠 HIGH：数据隔离/鉴权缺失

| # | 问题 | 影响 |
|---|------|------|
| 1 | 所有列表端点不传groupId时返回全库数据 | 学生看到其他组数据 |
| 2 | 里程碑/任务 增删改无任何权限检查 | 任何人可改任何人数据 |
| 3 | KnowledgePoint CRUD 无任何权限检查 | 任何人可管理知识库 |
| 4 | Git Repo配置 CRUD 无权限检查 | 任何人可配置同步 |
| 5 | 贡献度审批只需"非申请人" → 学生可审学生 | 审批形同虚设 |
| 6 | removeMember 无teacherId校验 | 教师跨组删人 |
| 7 | QA 删除/测试删除缺少管理员权限 | 只有owner能删 |

---

## 四、🟡 MEDIUM：功能不完整

| # | 问题 | 位置 |
|---|------|------|
| 1 | 课程目标/指标/方法/CDIO 缺失单条查询端点(GET /{id}) | CourseController |
| 2 | Git提交缺失手动录入端点 | ProjectController |
| 3 | 任务看板缺失负责人筛选参数 | ProjectController |
| 4 | 项目日志 journalDate 未自动设置 | ProjectController |
| 5 | 自测CRUD缺少更新端点 | QaController |
| 6 | 小组详情不返回教师姓名 | GroupService |
| 7 | 用户禁用可禁用自己(无自保护) | AuthController |
| 8 | 密码修改无审计日志 | AuthController |
| 9 | 登录无暴力破解保护(无速率限制) | AuthController |
| 10 | 课程详情不追踪updatedAt | Course实体 |
| 11 | 里程碑 finishedAt 每次保存都重置 | ProjectController |
| 12 | 邀请码手动输入时绕过唯一性检查 | GroupService |
| 13 | 前端 AHP 弹窗显示 cr/lambdaMax 但后端不返回这些字段 | analysis/Index.vue |
| 14 | 前端 settings disableUser 发送 enabled 但后端期望 disabled | settings/Index.vue |
| 15 | 前端 addMember 发送 role 但后端期望 roleCode | group/Index.vue |

---

## 五、🔵 LOW：代码质量/用户体验

| # | 问题 |
|---|------|
| 1 | CourseService.getFullTree N+1查询(110次DB调用) |
| 2 | GroupService.getGroupDetail N+1查询(每成员一次user查询) |
| 3 | my-groups N+1查询(每组再查详情) |
| 4 | 前端无加载状态(v-loading)在多数数据表格 |
| 5 | Reactive/ElMessageBox 在 analysis/Index.vue 导入但未使用 |
| 6 | Pinia auth store 集成不完整(Login直接用localStorage) |
| 7 | 项目task/journal/git对话框不重置表单 |
| 8 | gid=1 硬编码默认值 |
| 9 | 贡献度列表无删除按钮 |
| 10 | cdioPhase/status/dimension 字段无枚举校验 |

---

## 六、统计

| 级别 | 数量 |
|------|------|
| 🔴 CRITICAL 阻断 | 23 |
| 🟠 HIGH 重要 | 7 |
| 🟡 MEDIUM 中等 | 15 |
| 🔵 LOW 优化 | 10 |
| **合计** | **55** |
