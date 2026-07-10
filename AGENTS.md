# AGENTS.md — OBE-CDIO 教学评价管理系统

基于 OBE 成果导向 + CDIO 工程教育模式的软件工程课程教学评价平台。

## 项目结构

```
obe-server/          Spring Boot 3.4.1 后端 (Java 21, Maven)
  src/main/java/com/obe/evaluation/
    analysis/        达成度分析 & AHP 权重计算
    common/          全局异常处理、分页查询、统一响应 R、文件存储
    config/          MinIO、MyBatis-Plus、Security、TrailingSlashFilter
    course/          课程目标、毕业要求、评价方式、CDIO阶段映射
    evaluation/      小组评价、个人评分、角色评价、成绩申诉
    group/           项目小组、成员、岗位历史
    project/         项目里程碑、任务、日志、Git 同步、需求变更
    qa/              AI 问答、知识点、自测题库、MaxKB 集成
    security/        JWT 认证过滤器、Token 黑名单(Redis)
    system/          认证、用户管理、课程管理、通知、审计日志
  src/main/resources/application.yml  配置入口（环境变量占位）

obe-web/             Vue 3 + Element Plus 前端 (Vite)
  src/
    api/index.js     axios 实例，/api 请求
    stores/auth.js   Pinia 认证状态
    router/index.js  Vue Router 路由
    views/           各功能模块页面
      analysis/      达成度分析
      course/        课程目标管理、知识库管理
      evaluation/    评价管理
      group/         小组管理
      project/       项目管理
      qa/            AI 助手 (AiChat.vue)、自测 (SelfTest.vue)
      student/       学生视图
      teacher/       教师仪表盘
      settings/      系统设置
      docs/          文档
      Login.vue      登录
      Layout.vue     主布局
  nginx.conf         生产 nginx 配置（API 代理、MaxKB 同源嵌入）
  vite.config.js     dev server 代理 /api → localhost:8989

docker-compose.yml   6 容器编排：postgres(pgvector) + redis + minio + maxkb + obe-server + obe-web
init.sql             34 张业务表 + 默认账号
```

## 常用命令

```bash
# 开发（需要本地 PostgreSQL/Redis/MinIO 或指向 Docker 基础设施）
cd obe-web && npm run dev         # 前端 dev (localhost:5173, 代理 /api → :8989)
cd obe-server && mvn spring-boot:run  # 后端 dev (localhost:8989)

# 完整部署
docker compose up -d --build      # 一键启动全部 6 服务

# 单服务重建
docker compose build obe-server
docker compose up -d --no-deps --force-recreate obe-server
docker compose build --no-cache obe-web
docker compose up -d --no-deps --force-recreate obe-web

# 日志
docker compose logs -f obe-server
docker compose logs -f obe-web

# 数据库
docker exec -it obe-postgres psql -U obe_admin -d obe_evaluation

# 前端构建
cd obe-web && npm run build       # 输出到 dist/

# 后端构建
cd obe-server && mvn package -DskipTests  # 输出到 target/*.jar

# E2E 测试
npx playwright test               # Playwright (配置见 playwright.config.js)

# 重置数据库
docker compose down -v postgres
docker compose up -d postgres
```

## 架构边界

1. **API 路由**：所有后端 API 在 `/api/` 下，前端 nginx 反向代理到 `obe-server:8989`。
2. **认证**：JWT (jjwt 0.12.6)，Token 存储为 `obe_token`（localStorage key 不能叫 `token`——会与 MaxKB 覆盖冲突）。登出时 Token 加入 Redis 黑名单。
3. **MaxKB 知识库**：作为 iframe 同源嵌入 `/admin/` 路径，nginx 直接提供静态资源绕过 Django 认证。MaxKB API 通过 `/api/application/` 代理。
4. **AI 对话**：后端 `/api/ai/chat` 调用 DeepSeek API，nginx `proxy_read_timeout` 设为 120s（长响应）。移除 max_tokens 限制，使用 DeepSeek V4 原生 384K 输出。
5. **文件存储**：MinIO（Docker 环境）或本地 `uploads/`（开发环境）。StorageService 封装。
6. **评分权重**：贡献度 0.7 + 角色 0.3（`application.yml` 中 `evaluation.contribution-weight` / `evaluation.role-weight`）。

## 技术约定

- **后端响应格式**：统一使用 `com.obe.evaluation.common.R<T>` 包装（`R.ok(data)` / `R.error(msg)`）。
- **分页**：使用 `PageQuery` 基类。
- **ORM**：MyBatis-Plus 3.5.9，启用 `map-underscore-to-camel-case`，ID 自增。
- **密码**：BCrypt 哈希（`SysUserService`）。
- **数据库**：PostgreSQL 16 + pgvector 扩展，日期格式 `yyyy-MM-dd HH:mm:ss`，时区 `Asia/Shanghai`。
- **前端请求**：`src/api/index.js` 中 axios 实例，baseURL 为空（nginx 同源代理），拦截器自动携带 `obe_token`。
- **前端路由**：hash 模式（`createWebHashHistory`），nginx 已配置 SPA fallback。
- **API 文档**：Knife4j 4.5.0，部署后访问 `http://localhost:8989/doc.html`。

## 已知注意事项

- **localStorage key 冲突**：Token key 已改为 `obe_token`，不要改回 `token`——MaxKB iframe 会写入同名 key 导致覆盖。
- **MaxKB 双路径代理**：`/admin/` 和 `/maxkb/` 都指向 MaxKB，`/maxkb/` 302 重定向到 `/admin/`。静态资源路径 `/admin/assets/` 和 `/maxkb/admin/assets/` 都映射到 MaxKB 静态卷。
- **nginx 超时**：AI 对话可能耗时较长，`proxy_read_timeout` 已设为 120s，修改 nginx.conf 时不要缩短此值。
- **TrailingSlashFilter**：解决 Spring Boot 3 对 `/api/endpoint/` vs `/api/endpoint` 的差异处理。
- **pgvector**：`init.sql` 中使用了 `vector` 类型，需要 pgvector 镜像（`pgvector/pgvector:pg16`），不能用标准 PostgreSQL 镜像。
- **Docker 网络**：容器间通过服务名通信（`postgres`、`minio`、`maxkb`、`obe-server`），不要硬编码 localhost。

## 端口分配

| 端口 | 服务 |
|------|------|
| 80 | 前端 (nginx) |
| 8989 | 后端 (Spring Boot) |
| 5432 | PostgreSQL |
| 6379 | Redis |
| 9000 | MinIO API |
| 9001 | MinIO Console |
| 8080 | MaxKB |
| 5433 | MaxKB PostgreSQL |
| 5173 | 前端 dev server |

## 相关文档

- `DEPLOY.md`：完整部署文档（架构、配置、故障排查）
- `init.sql`：数据库 DDL + 种子数据
- `.env.example`：环境变量模板
