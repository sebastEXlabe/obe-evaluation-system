# OBE-CDIO 教学评价管理系统

基于 OBE 成果导向教育理念和 CDIO 工程教育模式的软件工程课程教学评价系统。

## 快速启动

```bash
git clone https://github.com/sebastEXlabe/obe-evaluation-system.git
cd obe-evaluation-system
cp .env.example .env
# 编辑 .env 填入 DEEPSEEK_API_KEY
docker compose up -d --build
# 访问 http://localhost （admin / admin123）

# 注入演示数据（可选）
pip install psycopg2-binary bcrypt
python inject_demo_data.py
```
演示账号：admin/admin123, teacher1/teacher123, student1~4/student123

## 功能模块

- 课程目标管理
- 小组分工与角色管理
- 项目过程追踪
- 多元融合评价
- 达成度分析
- AI 智能问答
- 知识库管理

## 技术栈

Spring Boot + Vue 3 + Element Plus + PostgreSQL + MyBatis-Plus + MinIO + DeepSeek

## 部署说明

详见 [DEPLOY.md](DEPLOY.md)
