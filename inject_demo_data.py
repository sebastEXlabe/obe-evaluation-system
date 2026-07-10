"""
演示数据注入 — 基于 demo-data-plan.md 逐行定义
所有值与实际 schema 逐列核对
"""
import psycopg2
import bcrypt
from datetime import datetime, timedelta, date

conn = psycopg2.connect(host='localhost', port=5432, dbname='obe_evaluation', user='obe_admin', password='ObePass123!')
cur = conn.cursor()
now = datetime.now()

def d(n): return now - timedelta(days=n)  # D-N
def dd(n): return (now - timedelta(days=n)).date()  # date only

print("=" * 50)
print("清除旧数据...")
for t in ['achievement_result','improvement_suggestion','improvement_task',
    'score_dispute','personal_score','role_evaluation','group_evaluation',
    'position_history','self_test_record','qa_record','question_answer','quiz',
    'git_commit_log','contribution_log','requirement_change',
    'project_journal','project_task','project_milestone','repo_config',
    'group_member','project_group',
    'evaluation_method','cdio_phase_mapping','objective_requirement_mapping',
    'graduation_indicator','graduation_requirement','knowledge_point',
    'course_objective','course','notification','audit_log','sys_user']:
    cur.execute(f'DELETE FROM {t}')
conn.commit()
print("清除完成\n")

# ============================================================
# 1. sys_user (6)
# ============================================================
print("1. sys_user...")
users = [
    ('admin',    bcrypt.hashpw('admin123'.encode(), bcrypt.gensalt()).decode(), '系统管理员', 'admin@school.edu.cn', 'ADMIN'),
    ('teacher1', bcrypt.hashpw('teacher123'.encode(), bcrypt.gensalt()).decode(), '张教授', 'zhang@school.edu.cn', 'TEACHER'),
    ('student1', bcrypt.hashpw('student123'.encode(), bcrypt.gensalt()).decode(), '李明', 'liming@school.edu.cn', 'STUDENT'),
    ('student2', bcrypt.hashpw('student123'.encode(), bcrypt.gensalt()).decode(), '王小红', 'wangxh@school.edu.cn', 'STUDENT'),
    ('student3', bcrypt.hashpw('student123'.encode(), bcrypt.gensalt()).decode(), '陈刚', 'chengang@school.edu.cn', 'STUDENT'),
    ('student4', bcrypt.hashpw('student123'.encode(), bcrypt.gensalt()).decode(), '赵丽', 'zhaoli@school.edu.cn', 'STUDENT'),
]
uid = {}
for uname, pwd, rname, email, role in users:
    cur.execute("INSERT INTO sys_user (username,password,real_name,email,role_code,status,created_at,updated_at) VALUES (%s,%s,%s,%s,%s,1,NOW(),NOW()) RETURNING id",
        (uname, pwd, rname, email, role))
    uid[uname] = cur.fetchone()[0]
print(f"  插入 {len(users)} 条")

# ============================================================
# 2. course (1)
# ============================================================
print("2. course...")
cur.execute("INSERT INTO course (course_name,semester,teacher_id,created_at) VALUES (%s,%s,%s,NOW()) RETURNING id",
    ('软件工程','2025-2026学年春季学期',uid['teacher1']))
cid = cur.fetchone()[0]
print(f"  id={cid}")

# ============================================================
# 3. course_objective (4)
# ============================================================
print("3. course_objective...")
objs = [
    ('CO1','软件工程基本概念与原理','理解软件生命周期、开发模型、需求工程、设计原则等基本概念','KNOWLEDGE',0.25,1),
    ('CO2','面向对象系统分析与设计','运用UML进行需求建模和系统设计,掌握面向对象分析设计方法','ABILITY',0.30,2),
    ('CO3','团队协作与项目管理','在团队中有效沟通协作,运用项目管理工具推进项目进度','QUALITY',0.20,3),
    ('CO4','软件测试与质量保证','设计测试用例,使用自动化测试工具进行软件测试和质量评估','ABILITY',0.25,4),
]
oid = {}
for code, title, desc, dim, w, sort in objs:
    cur.execute("INSERT INTO course_objective (objective_no,title,description,dimension,weight,sort_order,course_id,created_at,updated_at) VALUES (%s,%s,%s,%s,%s,%s,%s,NOW(),NOW()) RETURNING id",
        (code, title, desc, dim, w, sort, cid))
    oid[code] = cur.fetchone()[0]
print(f"  插入 {len(objs)} 条")

# ============================================================
# 4. graduation_requirement (12)
# ============================================================
print("4. graduation_requirement...")
grs = [
    ('GR1','工程知识','能够将数学、自然科学、工程基础和专业知识用于解决复杂工程问题',1),
    ('GR2','问题分析','能够应用基本原理识别、表达并通过文献研究分析复杂工程问题',2),
    ('GR3','设计/开发解决方案','能够设计针对复杂工程问题的解决方案及满足特定需求的系统',3),
    ('GR4','研究','能够基于科学原理和方法对复杂工程问题进行研究',4),
    ('GR5','使用现代工具','能够开发、选择与使用恰当的技术和工具',5),
    ('GR6','工程与社会','能够评价工程实践对社会、健康、安全、法律和文化的影响',6),
    ('GR7','环境和可持续发展','能够理解和评价工程实践对环境和社会可持续发展的影响',7),
    ('GR8','职业规范','具有人文素养和社会责任感,遵守工程职业道德和规范',8),
    ('GR9','个人和团队','能够在多学科团队中承担个体、成员及负责人角色',9),
    ('GR10','沟通','能够就复杂工程问题与同行及公众进行有效沟通',10),
    ('GR11','项目管理','理解并掌握工程管理原理与经济决策方法',11),
    ('GR12','终身学习','具有自主学习和终身学习的意识和能力',12),
]
grid = {}
for code, title, desc, sort in grs:
    cur.execute("INSERT INTO graduation_requirement (req_no,title,description,sort_order,created_at,updated_at) VALUES (%s,%s,%s,%s,NOW(),NOW()) RETURNING id",
        (code, title, desc, sort))
    grid[code] = cur.fetchone()[0]
print(f"  插入 {len(grs)} 条")

# ============================================================
# 5. graduation_indicator (9)
# ============================================================
print("5. graduation_indicator...")
indicators = [
    ('CO1-1','软件生命周期模型理解','能够解释各阶段及不同开发模型的特点',oid['CO1'],1),
    ('CO1-2','软件工程原理应用','能够应用软件工程基本原理分析实际问题',oid['CO1'],2),
    ('CO2-1','UML需求建模','能够使用UML用例图、类图、时序图进行需求分析和系统建模',oid['CO2'],1),
    ('CO2-2','设计模式应用','能够识别和应用常用设计模式优化系统架构',oid['CO2'],2),
    ('CO2-3','技术文档撰写','能够编写符合规范的需求规格说明和系统设计文档',oid['CO2'],3),
    ('CO3-1','角色职责履行','能够在小组中有效履行所分配角色的职责',oid['CO3'],1),
    ('CO3-2','项目管理工具使用','能够使用Git和项目管理工具进行任务分解与进度跟踪',oid['CO3'],2),
    ('CO4-1','测试用例设计','能够设计有效的单元测试和集成测试用例',oid['CO4'],1),
    ('CO4-2','自动化测试实践','能够使用JUnit等框架进行自动化测试',oid['CO4'],2),
]
iid = {}
for code, title, desc, obj_id, sort in indicators:
    cur.execute("INSERT INTO graduation_indicator (indicator_no,title,description,objective_id,sort_order,created_at,updated_at) VALUES (%s,%s,%s,%s,%s,NOW(),NOW()) RETURNING id",
        (code, title, desc, obj_id, sort))
    iid[code] = cur.fetchone()[0]
print(f"  插入 {len(indicators)} 条")

# ============================================================
# 6. objective_requirement_mapping (9)
# ============================================================
print("6. objective_requirement_mapping...")
mappings = [
    (oid['CO1'],grid['GR1'],'H'),(oid['CO1'],grid['GR2'],'M'),
    (oid['CO2'],grid['GR3'],'H'),(oid['CO2'],grid['GR5'],'M'),(oid['CO2'],grid['GR10'],'L'),
    (oid['CO3'],grid['GR9'],'H'),(oid['CO3'],grid['GR11'],'M'),
    (oid['CO4'],grid['GR4'],'H'),(oid['CO4'],grid['GR5'],'H'),
]
for obj_id, gr_id, level in mappings:
    cur.execute("INSERT INTO objective_requirement_mapping (objective_id,requirement_id,support_level,created_at) VALUES (%s,%s,%s,NOW())",(obj_id,gr_id,level))
print(f"  插入 {len(mappings)} 条")

# ============================================================
# 7. evaluation_method (14)
# ============================================================
print("7. evaluation_method...")
methods = [
    ('课堂测验',0.50,'group_eval',100.00,iid['CO1-1'],'SUMMATIVE','随堂测验成绩'),
    ('课后作业',0.50,'journal',100.00,iid['CO1-1'],'SUMMATIVE','作业提交评分'),
    ('需求分析报告',0.60,'group_eval',100.00,iid['CO2-1'],'SUMMATIVE','小组需求文档评审'),
    ('UML建模作业',0.40,'journal',100.00,iid['CO2-1'],'SUMMATIVE','个人建模练习'),
    ('系统设计评审',0.50,'group_eval',100.00,iid['CO2-2'],'SUMMATIVE','设计文档评审'),
    ('代码审查',0.50,'git',100.00,iid['CO2-2'],'SUMMATIVE','代码提交质量'),
    ('技术文档评分',0.50,'group_eval',100.00,iid['CO2-3'],'SUMMATIVE','文档规范性评审'),
    ('Git提交活跃度',0.50,'git',100.00,iid['CO2-3'],'SUMMATIVE','提交频率与质量'),
    ('角色评价',1.00,'role_eval',100.00,iid['CO3-1'],'SUMMATIVE','岗位职责评分'),
    ('任务完成度',0.60,'git',100.00,iid['CO3-2'],'SUMMATIVE','Git任务完成'),
    ('项目日志',0.40,'journal',100.00,iid['CO3-2'],'SUMMATIVE','日志完整度'),
    ('测试用例设计',0.50,'group_eval',100.00,iid['CO4-1'],'SUMMATIVE','测试文档评审'),
    ('自测成绩',0.50,'self_test',100.00,iid['CO4-1'],'SUMMATIVE','自测正确率'),
    ('自动化测试',1.00,'git',100.00,iid['CO4-2'],'SUMMATIVE','测试代码提交'),
]
for name, w, src, full, ind_id, etype, remark in methods:
    cur.execute("INSERT INTO evaluation_method (method_name,weight,data_source,full_score,indicator_id,eval_type,remark,created_at,updated_at) VALUES (%s,%s,%s,%s,%s,%s,%s,NOW(),NOW())",
        (name,w,src,full,ind_id,etype,remark))
print(f"  插入 {len(methods)} 条")

# ============================================================
# 8. cdio_phase_mapping (9)
# ============================================================
print("8. cdio_phase_mapping...")
cdio = [
    (iid['CO1-1'],'CONCEIVE',1),(iid['CO1-2'],'CONCEIVE',2),
    (iid['CO2-1'],'DESIGN',1),(iid['CO2-2'],'DESIGN',2),(iid['CO2-3'],'DESIGN',3),
    (iid['CO3-1'],'IMPLEMENT',1),(iid['CO3-2'],'IMPLEMENT',2),
    (iid['CO4-1'],'OPERATE',1),(iid['CO4-2'],'OPERATE',2),
]
for ind_id, phase, sort in cdio:
    cur.execute("INSERT INTO cdio_phase_mapping (indicator_id,cdio_phase,sort_order,course_id,created_at) VALUES (%s,%s,%s,%s,NOW())",(ind_id,phase,sort,cid))
print(f"  插入 {len(cdio)} 条")

# ============================================================
# 9. project_group (2) — invite_code UNIQUE!
# ============================================================
print("9. project_group...")
gid = {}
cur.execute("INSERT INTO project_group (group_name,teacher_id,max_members,status,invite_code,course_id,created_at,updated_at) VALUES (%s,%s,4,1,%s,%s,NOW(),NOW()) RETURNING id",
    ('第1组',uid['teacher1'],'A1B2C3',cid)); gid['G1'] = cur.fetchone()[0]
cur.execute("INSERT INTO project_group (group_name,teacher_id,max_members,status,invite_code,course_id,created_at,updated_at) VALUES (%s,%s,4,1,%s,%s,NOW(),NOW()) RETURNING id",
    ('第2组',uid['teacher1'],'D4E5F6',cid)); gid['G2'] = cur.fetchone()[0]
print(f"  G1={gid['G1']}, G2={gid['G2']}")

# ============================================================
# 10. group_member (8) — UNIQUE(group_id, user_id)!
# ============================================================
print("10. group_member...")
members = [
    (gid['G1'],uid['student1'],'PM',d(60)),(gid['G1'],uid['student2'],'DEVELOPER',d(60)),
    (gid['G1'],uid['student3'],'TESTER',d(60)),(gid['G1'],uid['student4'],'DOC_ADMIN',d(60)),
    (gid['G2'],uid['student2'],'PM',d(55)),(gid['G2'],uid['student1'],'DEVELOPER',d(55)),
    (gid['G2'],uid['student4'],'TESTER',d(55)),(gid['G2'],uid['student3'],'DOC_ADMIN',d(55)),
]
for g, u, r, jt in members:
    cur.execute("INSERT INTO group_member (group_id,user_id,role_code,join_at) VALUES (%s,%s,%s,%s)",(g,u,r,jt))
print(f"  插入 {len(members)} 条")

# ============================================================
# 11. position_history (3)
# ============================================================
print("11. position_history...")
ph = [
    (gid['G1'],uid['student1'],'DEVELOPER','PM',uid['teacher1'],'项目需要,组长调整',d(55)),
    (gid['G2'],uid['student2'],'DEVELOPER','PM',uid['teacher1'],'岗位轮换',d(50)),
    (gid['G2'],uid['student4'],'DOC_ADMIN','TESTER',uid['teacher1'],'测试人手不足',d(40)),
]
for g, u, old, new, by, reason, ts in ph:
    cur.execute("INSERT INTO position_history (group_id,user_id,old_role,new_role,changed_by,reason,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s)",(g,u,old,new,by,reason,ts))
print(f"  插入 {len(ph)} 条")

# ============================================================
# 12. group_evaluation (12) — dimension+score NOT NULL
# ============================================================
print("12. group_evaluation...")
dims = ['REQUIREMENT','DESIGN','CODE','TEST','DOC','PRESENTATION']
g1s = [85,82,78,80,88,90]
g2s = [72,70,75,68,73,76]
geval_ts = d(30)
for i, dim in enumerate(dims):
    cur.execute("INSERT INTO group_evaluation (group_id,evaluator_id,dimension,score,comment,created_at) VALUES (%s,%s,%s,%s,%s,%s)",
        (gid['G1'],uid['teacher1'],dim,g1s[i],f'第1组{dim}维度表现良好',geval_ts))
    cur.execute("INSERT INTO group_evaluation (group_id,evaluator_id,dimension,score,comment,created_at) VALUES (%s,%s,%s,%s,%s,%s)",
        (gid['G2'],uid['teacher1'],dim,g2s[i],f'第2组{dim}维度有待提升',d(25)))
print(f"  插入 12 条")

# ============================================================
# 13. role_evaluation (8)
# ============================================================
print("13. role_evaluation...")
roles = [
    (uid['student1'],gid['G1'],'PM','LEADERSHIP',88.00,'计划制定合理',d(28)),
    (uid['student2'],gid['G1'],'DEVELOPER','CODING_QUALITY',82.00,'代码质量较好',d(28)),
    (uid['student3'],gid['G1'],'TESTER','TEST_COVERAGE',85.00,'测试用例全面',d(28)),
    (uid['student4'],gid['G1'],'DOC_ADMIN','DOC_QUALITY',90.00,'文档规范完整',d(28)),
    (uid['student2'],gid['G2'],'PM','LEADERSHIP',72.00,'计划执行需加强',d(23)),
    (uid['student1'],gid['G2'],'DEVELOPER','CODING_QUALITY',75.00,'代码可维护性待提升',d(23)),
    (uid['student4'],gid['G2'],'TESTER','TEST_COVERAGE',70.00,'测试用例不足',d(23)),
    (uid['student3'],gid['G2'],'DOC_ADMIN','DOC_QUALITY',78.00,'文档结构清晰',d(23)),
]
for u, g, rc, dim, sc, cmt, ts in roles:
    cur.execute("INSERT INTO role_evaluation (user_id,group_id,evaluator_id,role_code,dimension,score,comment,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
        (u,g,uid['teacher1'],rc,dim,sc,cmt,ts))
print(f"  插入 {len(roles)} 条")

# ============================================================
# 14. score_dispute (1)
# ============================================================
print("14. score_dispute...")
cur.execute("INSERT INTO score_dispute (user_id,score_type,reason,status,created_at,updated_at) VALUES (%s,%s,%s,'PENDING',%s,%s)",
    (uid['student2'],'GROUP','对第2组DESIGN评分70分有异议,认为应≥75分',d(10),d(10)))
print("  插入 1 条")

# ============================================================
# 15. git_commit_log (20) — committed_at NOT NULL!
# ============================================================
print("15. git_commit_log...")
gits = [
    (uid['student1'],gid['G1'],'grade-mgmt','a1b2c3d4','feat: 完成需求分析文档初版',120,30,d(58),d(58)),
    (uid['student1'],gid['G1'],'grade-mgmt','a1b2c3d5','feat: 添加学生信息管理模块',200,45,d(50),d(50)),
    (uid['student1'],gid['G1'],'grade-mgmt','a1b2c3d6','fix: 修复成绩查询分页bug',15,8,d(42),d(42)),
    (uid['student1'],gid['G1'],'grade-mgmt','a1b2c3d7','feat: 添加成绩统计图表',160,25,d(30),d(30)),
    (uid['student1'],gid['G1'],'grade-mgmt','a1b2c3d8','refactor: 优化数据库查询性能',50,35,d(18),d(18)),
    (uid['student2'],gid['G1'],'grade-mgmt','b2c3d4e5','feat: 实现成绩录入接口',180,20,d(48),d(48)),
    (uid['student2'],gid['G1'],'grade-mgmt','b2c3d4e6','refactor: 优化DTO转换逻辑',45,28,d(38),d(38)),
    (uid['student2'],gid['G1'],'grade-mgmt','b2c3d4e7','feat: 前端成绩管理页面整合',85,15,d(25),d(25)),
    (uid['student2'],gid['G1'],'grade-mgmt','b2c3d4e8','fix: 修正小数点精度问题',8,3,d(14),d(14)),
    (uid['student3'],gid['G1'],'grade-mgmt','c3d4e5f6','test: 成绩管理模块单元测试',100,15,d(40),d(40)),
    (uid['student3'],gid['G1'],'grade-mgmt','c3d4e5f7','test: 添加集成测试用例',80,10,d(28),d(28)),
    (uid['student3'],gid['G1'],'grade-mgmt','c3d4e5f8','test: 补充边界条件测试',45,5,d(16),d(16)),
    (uid['student1'],gid['G2'],'exam-platform','d4e5f6g7','feat: 搭建考试平台基础架构',250,60,d(52),d(52)),
    (uid['student1'],gid['G2'],'exam-platform','d4e5f6g8','feat: 实现题库管理功能',180,35,d(40),d(40)),
    (uid['student1'],gid['G2'],'exam-platform','d4e5f6g9','feat: 添加考试结果统计',120,20,d(20),d(20)),
    (uid['student2'],gid['G2'],'exam-platform','e5f6g7h8','feat: 完成考试安排模块',140,25,d(45),d(45)),
    (uid['student2'],gid['G2'],'exam-platform','e5f6g7h9','fix: 修复自动阅卷分数计算错误',30,12,d(32),d(32)),
    (uid['student2'],gid['G2'],'exam-platform','e5f6g7h0','feat: 考场管理前端页面',95,18,d(18),d(18)),
    (uid['student4'],gid['G2'],'exam-platform','f6g7h8i9','test: 自动阅卷功能测试用例',120,20,d(35),d(35)),
    (uid['student4'],gid['G2'],'exam-platform','f6g7h8i0','test: 性能压力测试脚本',60,8,d(15),d(15)),
]
for u, g, repo, ch, msg, adds, dels, ct, st in gits:
    cur.execute("INSERT INTO git_commit_log (user_id,group_id,repo_name,commit_hash,message,additions,deletions,committed_at,synced_at) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)",
        (u,g,repo,ch,msg,adds,dels,ct,st))
print(f"  插入 {len(gits)} 条")

# ============================================================
# 16. repo_config (2)
# ============================================================
print("16. repo_config...")
cur.execute("INSERT INTO repo_config (group_id,platform,owner,repo,access_token,branch,last_synced_at,enabled,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s,true,%s)",
    (gid['G1'],'GITHUB','student1','grade-mgmt','ghp_demo123','main',d(58),d(58)))
cur.execute("INSERT INTO repo_config (group_id,platform,owner,repo,access_token,branch,last_synced_at,enabled,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s,true,%s)",
    (gid['G2'],'GITEE','student2','exam-platform','gitee_demo456','master',d(52),d(52)))
print("  插入 2 条")

# ============================================================
# 17. project_journal (16) — date类型!
# ============================================================
print("17. project_journal...")
journals = [
    (uid['student1'],gid['G1'],'完成项目需求调研,与教师确认功能范围,整理用户故事15条,绘制系统用例图',dd(58)),
    (uid['student1'],gid['G1'],'完成系统架构设计,确定技术栈为SpringBoot+Vue3+PostgreSQL,绘制ER图',dd(50)),
    (uid['student1'],gid['G1'],'完成数据库表结构设计,编写建表SQL脚本,完成索引优化方案',dd(42)),
    (uid['student1'],gid['G1'],'完成学生信息管理模块后端CRUD接口,编写单元测试8个',dd(30)),
    (uid['student2'],gid['G1'],'学习SpringBoot框架,搭建后端项目骨架,配置MyBatis-Plus和PostgreSQL',dd(48)),
    (uid['student2'],gid['G1'],'完成成绩管理模块后端接口开发和Swagger文档配置',dd(38)),
    (uid['student2'],gid['G1'],'完成前后端联调,解决跨域问题,实现成绩录入和查询完整流程',dd(25)),
    (uid['student2'],gid['G1'],'性能优化:添加Redis缓存,数据库查询响应时间从200ms降至50ms',dd(14)),
    (uid['student3'],gid['G1'],'编写单元测试用例20个,覆盖成绩管理核心业务逻辑,全部通过',dd(40)),
    (uid['student3'],gid['G1'],'完成集成测试,使用Postman测试全部API接口,编写测试报告',dd(28)),
    (uid['student4'],gid['G1'],'开始编写系统设计文档,整理需求规格说明',dd(55)),
    (uid['student4'],gid['G1'],'完成API接口文档和用户手册初稿',dd(35)),
    (uid['student1'],gid['G2'],'完成在线考试平台需求分析和技术方案评审,确定采用微服务雏形架构',dd(52)),
    (uid['student1'],gid['G2'],'完成题库管理模块开发,支持单选题、多选题、判断题三种题型',dd(40)),
    (uid['student2'],gid['G2'],'完成考试安排功能,支持定时考试和即时考试两种模式',dd(45)),
    (uid['student2'],gid['G2'],'完成自动阅卷功能,客观题自动批改,主观题支持关键词匹配评分',dd(32)),
]
for u, g, content, jd in journals:
    cur.execute("INSERT INTO project_journal (user_id,group_id,content,journal_date,created_at) VALUES (%s,%s,%s,%s,NOW())",(u,g,content,jd))
print(f"  插入 {len(journals)} 条")

# ============================================================
# 18. project_milestone (8) — is_done=boolean
# ============================================================
print("18. project_milestone...")
milestones = [
    (gid['G1'],'需求分析','CONCEIVE',dd(50),True,d(52)),
    (gid['G1'],'系统设计','DESIGN',dd(35),True,d(38)),
    (gid['G1'],'编码实现','IMPLEMENT',dd(-14),False,None),
    (gid['G1'],'测试部署','OPERATE',dd(-35),False,None),
    (gid['G2'],'需求分析','CONCEIVE',dd(45),True,d(48)),
    (gid['G2'],'系统设计','DESIGN',dd(30),True,d(33)),
    (gid['G2'],'编码实现','IMPLEMENT',dd(-21),False,None),
    (gid['G2'],'测试部署','OPERATE',dd(-42),False,None),
]
mid = {}
for i, (g, title, phase, due, done, fin) in enumerate(milestones):
    fin_ts = fin if fin else None
    cur.execute("INSERT INTO project_milestone (group_id,title,cdio_phase,due_date,is_done,finished_at,created_at) VALUES (%s,%s,%s,%s,%s,%s,NOW()) RETURNING id",
        (g,title,phase,due,done,fin_ts))
    mid[f'M{i+1}'] = cur.fetchone()[0]
print(f"  插入 {len(milestones)} 条")

# ============================================================
# 19. project_task (15)
# ============================================================
print("19. project_task...")
tasks = [
    (gid['G1'],mid['M3'],uid['student1'],'学生信息CRUD接口开发','DONE',2,dd(42),d(45),d(42)),
    (gid['G1'],mid['M3'],uid['student1'],'成绩统计图表前端','DOING',1,dd(14),d(30),d(30)),
    (gid['G1'],mid['M3'],uid['student2'],'成绩录入接口对接','DONE',1,dd(45),d(48),d(45)),
    (gid['G1'],mid['M3'],uid['student2'],'成绩查询分页优化','DONE',2,dd(35),d(42),d(38)),
    (gid['G1'],mid['M3'],uid['student3'],'成绩模块单元测试','DOING',1,dd(10),d(40),d(40)),
    (gid['G1'],mid['M3'],uid['student4'],'API文档更新','TODO',3,dd(7),d(35),d(35)),
    (gid['G1'],mid['M2'],uid['student1'],'数据库ER图设计','DONE',1,dd(48),d(50),d(48)),
    (gid['G1'],mid['M2'],uid['student4'],'系统设计文档','DONE',1,dd(45),d(55),d(42)),
    (gid['G2'],mid['M7'],uid['student1'],'题库管理模块开发','DONE',1,dd(42),d(48),d(42)),
    (gid['G2'],mid['M7'],uid['student1'],'考试结果统计功能','DOING',2,dd(14),d(30),d(30)),
    (gid['G2'],mid['M7'],uid['student2'],'考试安排后端接口','DONE',1,dd(40),d(45),d(40)),
    (gid['G2'],mid['M7'],uid['student2'],'自动阅卷算法优化','DOING',1,dd(10),d(35),d(35)),
    (gid['G2'],mid['M7'],uid['student4'],'阅卷功能测试用例','TODO',1,dd(7),d(35),d(35)),
    (gid['G2'],mid['M6'],uid['student1'],'技术选型方案文档','DONE',1,dd(45),d(50),d(45)),
    (gid['G2'],mid['M6'],uid['student2'],'数据库表设计','DONE',1,dd(42),d(48),d(42)),
]
for g, ms, a, title, status, pri, due, cr, up in tasks:
    cur.execute("INSERT INTO project_task (group_id,milestone_id,assignee_id,title,status,priority,due_date,created_at,updated_at) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)",
        (g,ms,a,title,status,pri,due.strftime('%Y-%m-%d'),cr,up))
print(f"  插入 {len(tasks)} 条")

# ============================================================
# 20. requirement_change (3)
# ============================================================
print("20. requirement_change...")
reqs = [
    (gid['G1'],'增加成绩导出CSV功能','教师要求增加成绩数据导出功能','ADD','教师提出新需求',uid['student1'],d(20)),
    (gid['G1'],'修改学生信息字段','增加学生手机号和邮箱字段','MODIFY','数据库设计调整',uid['student1'],d(15)),
    (gid['G2'],'取消手动阅卷功能','改用自动阅卷替代手动阅卷','REMOVE','简化教师操作',uid['student2'],d(25)),
]
for g, title, desc, ctype, reason, by, ts in reqs:
    cur.execute("INSERT INTO requirement_change (group_id,title,description,change_type,reason,created_by,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s)",
        (g,title,desc,ctype,reason,by,ts))
print(f"  插入 {len(reqs)} 条")

# ============================================================
# 21. contribution_log (5) — approved默认false
# ============================================================
print("21. contribution_log...")
contribs = [
    (uid['student1'],gid['G1'],'主动承担架构设计和技术难点攻关',5.00,True,uid['teacher1'],d(45)),
    (uid['student4'],gid['G1'],'额外完成部署文档和运维指南',3.00,True,uid['teacher1'],d(30)),
    (uid['student3'],gid['G1'],'帮助组员解决测试环境问题',2.00,True,uid['teacher1'],d(22)),
    (uid['student1'],gid['G2'],'跨组协助搭建基础架构',4.00,False,None,d(50)),
    (uid['student3'],gid['G2'],'超额完成用户手册编写',2.00,True,uid['teacher1'],d(18)),
]
for u, g, desc, bonus, approved, by, ts in contribs:
    cur.execute("INSERT INTO contribution_log (user_id,group_id,description,bonus_score,approved,approved_by,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s)",
        (u,g,desc,bonus,approved,by,ts))
print(f"  插入 {len(contribs)} 条")

# ============================================================
# 22. knowledge_point (12)
# ============================================================
print("22. knowledge_point...")
kps = [
    ('软件生命周期模型','第1章','瀑布模型、敏捷开发、螺旋模型、V模型等软件生命周期模型的概念、阶段划分、优缺点及适用场景。重点对比瀑布模型与敏捷开发的核心理念差异。'),
    ('软件开发过程','第1章','需求分析、概要设计、详细设计、编码实现、测试、部署维护各阶段的任务、交付物和质量标准。CMMI成熟度模型简介。'),
    ('需求工程','第2章','需求获取方法(访谈、问卷、观察、原型)、需求分析技术(结构化分析、面向对象分析)、需求规格说明文档编写规范(IEEE830标准)、需求验证与评审。'),
    ('UML建模','第2章','UML2.x的14种图型概述。重点掌握用例图、类图、时序图、活动图、状态图的语法和绘制方法。'),
    ('面向对象分析','第2章','面向对象基本概念(封装、继承、多态)。领域建模、用例驱动分析、CRC卡片方法、健壮性分析。'),
    ('设计模式','第3章','创建型、结构型、行为型三大类共23种设计模式的核心思想、类结构和典型应用场景。'),
    ('软件架构设计','第3章','分层架构、微服务架构、事件驱动架构的风格对比。SOLID、DRY、KISS原则。RESTful API设计规范。'),
    ('软件测试方法','第4章','测试层次(单元、集成、系统、验收)。白盒、黑盒、灰盒测试技术。等价类划分、边界值分析等测试用例设计方法。'),
    ('自动化测试','第4章','JUnit5框架使用、Mockito模拟框架、SeleniumWeb自动化、测试覆盖率分析(JaCoCo)。'),
    ('Git版本控制','第5章','Git基本操作、分支策略(GitFlow/GitHubFlow)、冲突解决、PullRequest代码评审流程。'),
    ('持续集成','第5章','CI/CD概念与工具链。自动化构建、测试、部署流水线。Docker容器化与DockerCompose服务编排。'),
    ('AHP层次分析法','第6章','Saaty1-9标度法、判断矩阵构造、特征向量法计算权重、一致性检验(CI/CR计算)。AHP在教育评价中的应用。'),
]
kpid = {}
for i, (title, chapter, content) in enumerate(kps):
    cur.execute("INSERT INTO knowledge_point (title,chapter,content,created_at) VALUES (%s,%s,%s,NOW()) RETURNING id",(title,chapter,content))
    kpid[f'KP{i+1}'] = cur.fetchone()[0]
print(f"  插入 {len(kps)} 条")

# ============================================================
# 23. qa_record (12)
# ============================================================
print("23. qa_record...")
qas = [
    (uid['student1'],kpid['KP1'],'瀑布模型和敏捷开发有什么区别?','瀑布模型是线性顺序的开发模型,每个阶段完成后才能进入下一阶段,适合需求明确的场景。敏捷开发是迭代式的,每个Sprint(2-4周)交付可工作软件,强调快速响应变化和持续反馈。两者核心区别:瀑布重计划和文档,敏捷重适应和沟通。',True,d(50),'demo-s1'),
    (uid['student1'],kpid['KP12'],'AHP一致性比率CR怎么算?如果CR>0.1怎么办?','CR=CI/RI。CI=(λmax-n)/(n-1),RI查表获取。如果CR≥0.1,说明判断矩阵存在逻辑矛盾,需要重新调整两两比较的取值,直到CR<0.1通过一致性检验。',True,d(35),'demo-s1'),
    (uid['student1'],kpid['KP7'],'前后端分离架构中,前端路由和后端API怎么对应?','前端使用VueRouter的Hash模式,所有业务请求通过Nginx代理到/api/路径,后端SpringBoot处理。前端路由是客户端页面跳转,后端API是数据接口,两者通过axios通信。',True,d(15),'demo-s1'),
    (uid['student2'],kpid['KP4'],'UML类图中关联、聚合、组合怎么区分?','关联(Association)是类之间最基本的结构关系,用实线表示。聚合(Aggregation)是整体-部分关系,部分可脱离整体存在,用空心菱形。组合(Composition)是更强的聚合,部分不能脱离整体,用实心菱形。',True,d(48),'demo-s2'),
    (uid['student2'],kpid['KP6'],'什么时候用工厂模式,什么时候用建造者模式?','工厂模式(Factory)用于创建单个对象,隐藏具体类的实例化逻辑。建造者模式(Builder)用于创建复杂对象,分步骤构建。简记:工厂一步创建,建造者分步构建。',True,d(30),'demo-s2'),
    (uid['student2'],kpid['KP10'],'Git merge和rebase有什么区别?团队协作推荐哪个?','merge保留完整的分支历史,产生一个合并提交,适合公共分支。rebase将当前分支的提交变基到目标分支,产生线性历史,适合个人分支整理。团队协作推荐:公共分支用merge,个人分支可先rebase。',True,d(12),'demo-s2'),
    (uid['student3'],kpid['KP8'],'单元测试应该覆盖哪些内容?覆盖率多少合适?','单元测试应覆盖:正常路径(HappyPath)、边界条件(空值/零值/最大值)、异常处理。覆盖率目标:行覆盖率≥80%,分支覆盖率≥70%。但高覆盖率不等于高质量测试,关键测试用例的设计比数字更重要。',True,d(42),'demo-s3'),
    (uid['student3'],kpid['KP9'],'JUnit5中@Mock和@InjectMocks有什么区别?','@Mock创建被测类的依赖模拟对象,所有方法返回默认值。@InjectMocks将@Mock对象自动注入到被测类中。无需手动调用setter或构造器即可完成依赖注入。',True,d(28),'demo-s3'),
    (uid['student3'],kpid['KP8'],'什么是Mock测试?什么时候需要Mock?','Mock测试用模拟对象替代真实依赖,隔离被测代码。需要Mock的场景:依赖的外部服务不可用、依赖的行为不可控、依赖的操作有副作用、依赖的响应速度影响测试效率。',False,d(10),'demo-s3'),
    (uid['student4'],kpid['KP3'],'如何判断需求的优先级?MoSCoW方法怎么用?','MoSCoW方法将需求分为四类:MustHave(必须有)、ShouldHave(应该有)、CouldHave(可以有)、Won\'tHave(暂不做)。优先级排序时结合业务价值和实现成本两个维度。',True,d(45),'demo-s4'),
    (uid['student4'],kpid['KP7'],'软件架构评估方法ATAM怎么操作?','ATAM(架构权衡分析法)步骤:收集场景、描述架构方案、分析每个场景下架构的响应、识别风险点和非风险点、输出评估报告。核心思想:不存在完美的架构,只有最适合当前场景的权衡。',True,d(20),'demo-s4'),
    (uid['student4'],kpid['KP2'],'需求规格说明文档应该包含哪些章节?','按IEEE830标准:引言(目的/范围/定义)、总体描述(产品视角/用户特征/约束)、具体需求(功能需求/非功能需求/接口需求)。功能需求用系统应...句式,避免在需求文档中混入设计方案。',False,d(5),'demo-s4'),
]
for u, kid, q, a, resolved, ts, sid in qas:
    cur.execute("INSERT INTO qa_record (user_id,knowledge_id,question,answer,is_resolved,asked_at,session_id) VALUES (%s,%s,%s,%s,%s,%s,%s)",(u,kid,q,a,resolved,ts,sid))
print(f"  插入 {len(qas)} 条")

# ============================================================
# 24. question_answer (3)
# ============================================================
print("24. question_answer...")
cur.execute("INSERT INTO question_answer (group_id,ask_user_id,answer_user_id,title,question,answer,status,asked_at,answered_at,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
    (gid['G1'],uid['student1'],uid['teacher1'],'项目架构方案咨询','微服务还是单体架构更适合?','建议先用单体,SpringBoot单体即可满足,后续如有性能瓶颈再拆微服务。重点是模块划分清晰,为未来拆分做好准备。','RESOLVED',d(45),d(43),d(45)))
cur.execute("INSERT INTO question_answer (group_id,ask_user_id,answer_user_id,title,question,answer,status,asked_at,answered_at,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
    (gid['G1'],uid['student2'],uid['teacher1'],'测试框架选择','JUnit5还是TestNG更适合?','JUnit5更主流,社区活跃度更高,与SpringBoot集成更好。推荐使用JUnit5+Mockito组合。','ANSWERED',d(35),d(33),d(35)))
cur.execute("INSERT INTO question_answer (group_id,ask_user_id,title,question,status,asked_at,created_at) VALUES (%s,%s,%s,%s,%s,%s,%s)",
    (gid['G2'],uid['student3'],'数据库设计评审','考试平台是否需要读写分离?','PENDING',d(20),d(20)))
print("  插入 3 条")

# ============================================================
# 25. quiz (2)
# ============================================================
print("25. quiz...")
import json
q1 = json.dumps([
    {"q":"软件生命周期不包括哪个阶段?","opts":["需求分析","系统设计","市场推广","测试部署"],"ans":2},
    {"q":"UML中用于描述系统功能的图是?","opts":["类图","用例图","时序图","部署图"],"ans":1},
    {"q":"AHP的CR小于多少通过一致性检验?","opts":["0.05","0.1","0.15","0.2"],"ans":1},
],ensure_ascii=False)
q2 = json.dumps([
    {"q":"类图中表示组合关系的符号是?","opts":["空心菱形","实心菱形","箭头","无特殊符号"],"ans":1},
    {"q":"时序图的生命线表示什么?","opts":["类的生命周期","对象的生存时间","方法的执行时间","系统的运行时间"],"ans":1},
],ensure_ascii=False)

cur.execute("INSERT INTO quiz (teacher_id,group_id,course_id,title,questions,status,total_score,created_at,published_at) VALUES (%s,%s,%s,%s,%s,%s,100,%s,%s)",
    (uid['teacher1'],gid['G1'],cid,'软件工程基础测验',q1,'PUBLISHED',d(30),d(25)))
cur.execute("INSERT INTO quiz (teacher_id,group_id,course_id,title,questions,status,total_score,created_at) VALUES (%s,%s,%s,%s,%s,%s,100,%s)",
    (uid['teacher1'],gid['G2'],cid,'UML建模测验',q2,'DRAFT',d(15)))
print("  插入 2 条")

# ============================================================
# 26. self_test_record (12)
# ============================================================
print("26. self_test_record...")
tests = [
    (uid['student1'],85,100,d(30)),(uid['student2'],72,100,d(30)),
    (uid['student3'],90,100,d(30)),(uid['student4'],78,100,d(30)),
    (uid['student1'],92,100,d(15)),(uid['student2'],80,100,d(15)),
    (uid['student3'],88,100,d(15)),(uid['student4'],85,100,d(15)),
    (uid['student1'],78,100,d(5)),(uid['student2'],85,100,d(5)),
    (uid['student3'],95,100,d(5)),(uid['student4'],72,100,d(5)),
]
for u, score, total, ts in tests:
    cur.execute("INSERT INTO self_test_record (user_id,score,total,taken_at) VALUES (%s,%s,%s,%s)",(u,score,total,ts))
print(f"  插入 {len(tests)} 条")

# ============================================================
# 27. notification (8)
# ============================================================
print("27. notification...")
notifs = [
    (uid['student1'],'岗位变更通知','你在第1组的岗位已从DEVELOPER变更为PM',True,d(55)),
    (uid['student2'],'岗位变更通知','你在第2组的岗位已从DEVELOPER变更为PM',True,d(50)),
    (uid['student4'],'岗位变更通知','你在第2组的岗位已从DOC_ADMIN变更为TESTER',True,d(40)),
    (uid['student1'],'新的小组评价','第1组已收到教师提交的小组评价,请查看成绩',False,d(30)),
    (uid['student3'],'成绩申诉已提交','你对第2组DESIGN评分的申诉已提交,等待审核',False,d(10)),
    (uid['student1'],'新里程碑创建','第1组新里程碑编码实现已创建',False,d(38)),
    (uid['student2'],'新里程碑创建','第2组新里程碑编码实现已创建',False,d(33)),
    (uid['student1'],'贡献加分已审批','你提交的架构设计技术攻关贡献已审批,+5分',False,d(44)),
]
for u, title, content, read, ts in notifs:
    cur.execute("INSERT INTO notification (user_id,title,content,is_read,created_at) VALUES (%s,%s,%s,%s,%s)",(u,title,content,read,ts))
print(f"  插入 {len(notifs)} 条")

# ============================================================
# COMMIT
# ============================================================
conn.commit()
cur.close()
conn.close()
print("\n" + "=" * 50)
print("✅ 全部完成! 224条演示数据已注入")
print("=" * 50)
