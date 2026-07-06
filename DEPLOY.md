# OBE-CDIO 教学评价管理系统 — 部署文档

## 一、系统架构

```
Browser :80 ──→ obe-web (nginx) ──/api──→ obe-server (Spring Boot :8989)
                 │                              │
                 │ /admin/ ──→ maxkb :8080       ├── postgres (pgvector:5432)
                 │ /admin/assets/ ──→ 静态卷     ├── minio (:9000/:9001)
                 │                              └── MaxKB PG (:5433)
                 
MaxKB iframe 同源嵌入 /admin/ 路径，nginx 直接提供静态资源绕过 Django 认证。
```

| 服务 | 容器名 | 端口 | 技术栈 |
|------|--------|------|--------|
| 前端 | obe-web | 80 | Vue 3 + Element Plus + nginx |
| 后端 | obe-server | 8989 | Spring Boot 3.4.1 + MyBatis-Plus |
| 数据库 | obe-postgres | 5432 | PostgreSQL 16 + pgvector |
| 缓存 | obe-redis | 6379 | Redis 7 |
| 对象存储 | obe-minio | 9000/9001 | MinIO |
| 知识库 | obe-maxkb | 8080/5433 | MaxKB v2.10.3 (Django + PostgreSQL) |

## 二、环境要求

| 软件 | 最低版本 | 说明 |
|------|---------|------|
| Docker | 24.0+ | 容器运行时 |
| Docker Compose | v2.20+ | 服务编排 |
| 内存 | 8GB+ | 6 个容器同时运行 |
| 磁盘 | 20GB+ | 含 PostgreSQL/MinIO 数据卷 |

## 三、快速部署

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd obe-evaluation-system
```

### 2. 配置环境变量

```bash
cp .env.example .env
```

编辑 `.env` 文件，填入 DeepSeek API Key：

```env
DEEPSEEK_API_KEY=sk-your-api-key-here
JWT_SECRET=your-random-secret-string
```

### 3. 一键启动

```bash
docker compose up -d --build
```

首次启动会自动：
- 初始化 PostgreSQL 数据库（`init.sql`）
- 创建默认管理员账号 `admin` / `admin123`
- 创建 MinIO 存储桶
- 注册 MaxKB 应用

### 4. 验证部署

```bash
docker compose ps                    # 全部状态应为 healthy/Up
curl http://localhost                 # 返回前端页面
curl http://localhost/api/auth/login  # API 正常
```

### 5. 访问入口

| 服务 | 地址 | 账号 |
|------|------|------|
| 前端 | http://localhost | admin / admin123 |
| API 文档 | http://localhost:8989/doc.html | - |
| MinIO 控制台 | http://localhost:9001 | minioadmin / minioadmin |
| MaxKB 知识库 | 前端 → 知识库菜单 | admin / admin123 |

## 四、默认账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 全部权限 |
| teacher1 | admin123 | 教师 | 教学管理 |
| student1 | admin123 | 学生 | 李明 |
| student2 | admin123 | 学生 | 王小红 |
| student3 | admin123 | 学生 | 陈刚 |
| student4 | admin123 | 学生 | 赵丽 |

> 部署后请立即修改所有默认密码。

## 五、配置说明

### 5.1 环境变量（.env）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `POSTGRES_DB` | obe_evaluation | 数据库名 |
| `POSTGRES_USER` | obe_admin | 数据库用户 |
| `POSTGRES_PASSWORD` | ObePass123! | 数据库密码 |
| `SERVER_PORT` | 8989 | 后端端口 |
| `WEB_PORT` | 80 | 前端端口 |
| `DEEPSEEK_API_KEY` | (必填) | DeepSeek API 密钥 |
| `DEEPSEEK_MODEL` | deepseek-chat | AI 模型 |
| `JWT_SECRET` | (建议修改) | JWT 签名密钥 |
| `JWT_EXPIRATION` | 86400000 | Token 有效期(ms) |
| `MINIO_ROOT_USER` | minioadmin | MinIO 账号 |
| `MINIO_ROOT_PASSWORD` | minioadmin | MinIO 密码 |

### 5.2 数据库初始化

`init.sql` 在 PostgreSQL 首次启动时自动执行，包含：
- 全部 34 张业务表
- 索引和约束
- 默认 admin 账号

若需重置数据库：

```bash
docker compose down -v postgres   # 删除数据卷
docker compose up -d postgres     # 重新初始化
```

## 六、常用操作

### 构建单个服务

```bash
docker compose build obe-server   # 重新构建后端
docker compose build obe-web      # 重新构建前端
```

### 重启单个服务

```bash
docker compose up -d --no-deps --force-recreate obe-server
docker compose up -d --no-deps --force-recreate obe-web
```

### 查看日志

```bash
docker compose logs -f obe-server  # 后端日志
docker compose logs -f obe-web     # nginx 日志
docker compose logs -f maxkb       # MaxKB 日志
```

### 进入容器

```bash
docker exec -it obe-server sh
docker exec -it obe-postgres psql -U obe_admin -d obe_evaluation
docker exec -it obe-maxkb psql -U root -d maxkb
```

### MaxKB 管理员密码重置

```bash
docker exec -it obe-maxkb sh
cd /opt/maxkb-app/apps && python manage.py shell
>>> from users.models.user import User
>>> from django.contrib.auth.hashers import make_password
>>> u = User.objects.get(username='admin')
>>> u.password = make_password('newpassword')
>>> u.save()
```

## 七、生产环境注意事项

1. **修改默认密码**：所有服务默认密码均需更换
2. **JWT 密钥**：使用随机 64+ 字符字符串
3. **HTTPS**：前端 nginx 前加反向代理（如 Nginx/Caddy）配置 SSL
4. **端口安全**：生产环境关闭基础设施端口对外暴露（5432/6379/9000/9001/8080）
5. **数据备份**：定期备份 PostgreSQL 和 MinIO 数据卷
6. **资源限制**：在 docker-compose.yml 中添加 `mem_limit` 和 `cpus` 限制

## 八、故障排查

### 前端页面加载异常

```bash
# 强制重建前端
docker compose build --no-cache obe-web
docker compose up -d --no-deps --force-recreate obe-web
# 浏览器清除缓存 Ctrl+Shift+Delete
```

### AI 助手不可用

```bash
# 检查 DeepSeek API Key
docker exec obe-server env | grep DEEPSEEK
# 检查 nginx 超时（需 ≥120s）
docker exec obe-web sh -c "grep timeout /etc/nginx/conf.d/default.conf"
```

### 数据库连接失败

```bash
docker compose ps postgres          # 确认状态为 healthy
docker compose logs postgres | tail # 查看启动日志
```

### MaxKB 知识库不生效

```bash
# 检查 MaxKB PG 连通性
docker exec obe-server sh -c "psql -h maxkb -U root -d maxkb -c 'SELECT count(*) FROM paragraph'"
# 确认文档已上传到 MaxKB
```

### 端口冲突

修改 `.env` 中的端口变量：

```env
SERVER_PORT=8990
WEB_PORT=8081
POSTGRES_PORT=5433
```

## 九、目录结构

```
├── docker-compose.yml      # 服务编排
├── .env.example            # 环境变量模板
├── init.sql                # 数据库初始化
├── obe-server/             # 后端
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── obe-web/                # 前端
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
└── obe-server/upload/      # 本地文件存储（Docker 中改用 MinIO）
```
