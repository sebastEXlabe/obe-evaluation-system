--
-- PostgreSQL database dump
--

\restrict H2DIGtbRa01W9yJArJ9wh1N7eqjRJ9afE1fLdl2VyYgoUU5cut7FhOIHUf6CUTl

-- Dumped from database version 16.14 (Debian 16.14-1.pgdg12+1)
-- Dumped by pg_dump version 16.14 (Debian 16.14-1.pgdg12+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: sys_user; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.sys_user VALUES (154, 'admin', '$2b$12$Sbeq1/JspI79LtupXFPpievkWziXt8QcL3K0UwJHC70fbiA0c7qHq', '系统管理员', 'admin@school.edu.cn', NULL, 'ADMIN', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.sys_user VALUES (155, 'teacher1', '$2b$12$ayGPZsOsKdzOkuw3HxFFd.5UG/J7Q24ODlRpGuQ1JrQvtAvV2BVry', '张教授', 'zhang@school.edu.cn', NULL, 'TEACHER', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.sys_user VALUES (156, 'student1', '$2b$12$pMTykLR1GAyhFnMBwXPGHe7VDDxqwmGJSoQZCF2JG25OJHBLhk7FK', '李明', 'liming@school.edu.cn', NULL, 'STUDENT', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.sys_user VALUES (157, 'student2', '$2b$12$Yp42Lyt0nMU3BI9K14Y/yuSG4vbMwa/2T0QLi6m0KN3xcRXWjFZMy', '王小红', 'wangxh@school.edu.cn', NULL, 'STUDENT', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.sys_user VALUES (158, 'student3', '$2b$12$QsXqTJWIanynYTYz/hwu1eQmys5eM8apIhCaSgL/g0urkp1PKou/q', '陈刚', 'chengang@school.edu.cn', NULL, 'STUDENT', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.sys_user VALUES (159, 'student4', '$2b$12$Lazh.YpZ6tAXWiSIQnlyJ.JlZ5T9SoccZfshl35nxBNgHJdkbPAS2', '赵丽', 'zhaoli@school.edu.cn', NULL, 'STUDENT', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, NULL, NULL) ON CONFLICT DO NOTHING;


--
-- Data for Name: course; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.course VALUES (5, '软件工程', '2025-2026学年春季学期', 155, '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;


--
-- Data for Name: course_objective; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.course_objective VALUES (88, 'CO1', '软件工程基本概念与原理', '理解软件生命周期、开发模型、需求工程、设计原则等基本概念', 'KNOWLEDGE', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 0.2500, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.course_objective VALUES (89, 'CO2', '面向对象系统分析与设计', '运用UML进行需求建模和系统设计,掌握面向对象分析设计方法', 'ABILITY', 2, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 0.3000, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.course_objective VALUES (90, 'CO3', '团队协作与项目管理', '在团队中有效沟通协作,运用项目管理工具推进项目进度', 'QUALITY', 3, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 0.2000, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.course_objective VALUES (91, 'CO4', '软件测试与质量保证', '设计测试用例,使用自动化测试工具进行软件测试和质量评估', 'ABILITY', 4, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 0.2500, 5) ON CONFLICT DO NOTHING;


--
-- Data for Name: graduation_indicator; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.graduation_indicator VALUES (6, 88, 'CO1-1', '软件生命周期模型理解', '能够解释各阶段及不同开发模型的特点', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (7, 88, 'CO1-2', '软件工程原理应用', '能够应用软件工程基本原理分析实际问题', 2, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (8, 89, 'CO2-1', 'UML需求建模', '能够使用UML用例图、类图、时序图进行需求分析和系统建模', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (9, 89, 'CO2-2', '设计模式应用', '能够识别和应用常用设计模式优化系统架构', 2, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (10, 89, 'CO2-3', '技术文档撰写', '能够编写符合规范的需求规格说明和系统设计文档', 3, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (11, 90, 'CO3-1', '角色职责履行', '能够在小组中有效履行所分配角色的职责', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (12, 90, 'CO3-2', '项目管理工具使用', '能够使用Git和项目管理工具进行任务分解与进度跟踪', 2, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (13, 91, 'CO4-1', '测试用例设计', '能够设计有效的单元测试和集成测试用例', 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_indicator VALUES (14, 91, 'CO4-2', '自动化测试实践', '能够使用JUnit等框架进行自动化测试', 2, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: cdio_phase_mapping; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.cdio_phase_mapping VALUES (5, 6, 'CONCEIVE', 1, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (6, 7, 'CONCEIVE', 2, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (7, 8, 'DESIGN', 1, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (8, 9, 'DESIGN', 2, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (9, 10, 'DESIGN', 3, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (10, 11, 'IMPLEMENT', 1, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (11, 12, 'IMPLEMENT', 2, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (12, 13, 'OPERATE', 1, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.cdio_phase_mapping VALUES (13, 14, 'OPERATE', 2, '2026-07-07 23:13:08.545261', 0, 5) ON CONFLICT DO NOTHING;


--
-- Data for Name: project_group; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.project_group VALUES (6, '第1组', 155, 4, 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'A1B2C3', 5) ON CONFLICT DO NOTHING;
INSERT INTO public.project_group VALUES (7, '第2组', 155, 4, 1, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'D4E5F6', 5) ON CONFLICT DO NOTHING;


--
-- Data for Name: contribution_log; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.contribution_log VALUES (61, 156, 6, '主动承担架构设计和技术难点攻关', 5.00, 155, '2026-05-24 07:13:07.395643', 0, true) ON CONFLICT DO NOTHING;
INSERT INTO public.contribution_log VALUES (62, 159, 6, '额外完成部署文档和运维指南', 3.00, 155, '2026-06-08 07:13:07.395643', 0, true) ON CONFLICT DO NOTHING;
INSERT INTO public.contribution_log VALUES (63, 158, 6, '帮助组员解决测试环境问题', 2.00, 155, '2026-06-16 07:13:07.395643', 0, true) ON CONFLICT DO NOTHING;
INSERT INTO public.contribution_log VALUES (64, 156, 7, '跨组协助搭建基础架构', 4.00, NULL, '2026-05-19 07:13:07.395643', 0, false) ON CONFLICT DO NOTHING;
INSERT INTO public.contribution_log VALUES (65, 158, 7, '超额完成用户手册编写', 2.00, 155, '2026-06-20 07:13:07.395643', 0, true) ON CONFLICT DO NOTHING;


--
-- Data for Name: evaluation_method; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.evaluation_method VALUES (13, 6, '课堂测验', 0.5000, 'group_eval', 100.00, '随堂测验成绩', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (14, 6, '课后作业', 0.5000, 'journal', 100.00, '作业提交评分', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (15, 8, '需求分析报告', 0.6000, 'group_eval', 100.00, '小组需求文档评审', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (16, 8, 'UML建模作业', 0.4000, 'journal', 100.00, '个人建模练习', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (17, 9, '系统设计评审', 0.5000, 'group_eval', 100.00, '设计文档评审', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (18, 9, '代码审查', 0.5000, 'git', 100.00, '代码提交质量', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (19, 10, '技术文档评分', 0.5000, 'group_eval', 100.00, '文档规范性评审', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (20, 10, 'Git提交活跃度', 0.5000, 'git', 100.00, '提交频率与质量', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (21, 11, '角色评价', 1.0000, 'role_eval', 100.00, '岗位职责评分', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (22, 12, '任务完成度', 0.6000, 'git', 100.00, 'Git任务完成', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (23, 12, '项目日志', 0.4000, 'journal', 100.00, '日志完整度', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (24, 13, '测试用例设计', 0.5000, 'group_eval', 100.00, '测试文档评审', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (25, 13, '自测成绩', 0.5000, 'self_test', 100.00, '自测正确率', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;
INSERT INTO public.evaluation_method VALUES (26, 14, '自动化测试', 1.0000, 'git', 100.00, '测试代码提交', '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261', 0, 'SUMMATIVE') ON CONFLICT DO NOTHING;


--
-- Data for Name: git_commit_log; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.git_commit_log VALUES (126, 156, 6, 'grade-mgmt', 'a1b2c3d4', 'feat: 完成需求分析文档初版', 120, 30, '2026-05-11 07:13:07.395643', '2026-05-11 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (127, 156, 6, 'grade-mgmt', 'a1b2c3d5', 'feat: 添加学生信息管理模块', 200, 45, '2026-05-19 07:13:07.395643', '2026-05-19 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (128, 156, 6, 'grade-mgmt', 'a1b2c3d6', 'fix: 修复成绩查询分页bug', 15, 8, '2026-05-27 07:13:07.395643', '2026-05-27 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (129, 156, 6, 'grade-mgmt', 'a1b2c3d7', 'feat: 添加成绩统计图表', 160, 25, '2026-06-08 07:13:07.395643', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (130, 156, 6, 'grade-mgmt', 'a1b2c3d8', 'refactor: 优化数据库查询性能', 50, 35, '2026-06-20 07:13:07.395643', '2026-06-20 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (131, 157, 6, 'grade-mgmt', 'b2c3d4e5', 'feat: 实现成绩录入接口', 180, 20, '2026-05-21 07:13:07.395643', '2026-05-21 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (132, 157, 6, 'grade-mgmt', 'b2c3d4e6', 'refactor: 优化DTO转换逻辑', 45, 28, '2026-05-31 07:13:07.395643', '2026-05-31 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (133, 157, 6, 'grade-mgmt', 'b2c3d4e7', 'feat: 前端成绩管理页面整合', 85, 15, '2026-06-13 07:13:07.395643', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (134, 157, 6, 'grade-mgmt', 'b2c3d4e8', 'fix: 修正小数点精度问题', 8, 3, '2026-06-24 07:13:07.395643', '2026-06-24 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (135, 158, 6, 'grade-mgmt', 'c3d4e5f6', 'test: 成绩管理模块单元测试', 100, 15, '2026-05-29 07:13:07.395643', '2026-05-29 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (136, 158, 6, 'grade-mgmt', 'c3d4e5f7', 'test: 添加集成测试用例', 80, 10, '2026-06-10 07:13:07.395643', '2026-06-10 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (137, 158, 6, 'grade-mgmt', 'c3d4e5f8', 'test: 补充边界条件测试', 45, 5, '2026-06-22 07:13:07.395643', '2026-06-22 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (138, 156, 7, 'exam-platform', 'd4e5f6g7', 'feat: 搭建考试平台基础架构', 250, 60, '2026-05-17 07:13:07.395643', '2026-05-17 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (139, 156, 7, 'exam-platform', 'd4e5f6g8', 'feat: 实现题库管理功能', 180, 35, '2026-05-29 07:13:07.395643', '2026-05-29 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (140, 156, 7, 'exam-platform', 'd4e5f6g9', 'feat: 添加考试结果统计', 120, 20, '2026-06-18 07:13:07.395643', '2026-06-18 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (141, 157, 7, 'exam-platform', 'e5f6g7h8', 'feat: 完成考试安排模块', 140, 25, '2026-05-24 07:13:07.395643', '2026-05-24 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (142, 157, 7, 'exam-platform', 'e5f6g7h9', 'fix: 修复自动阅卷分数计算错误', 30, 12, '2026-06-06 07:13:07.395643', '2026-06-06 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (143, 157, 7, 'exam-platform', 'e5f6g7h0', 'feat: 考场管理前端页面', 95, 18, '2026-06-20 07:13:07.395643', '2026-06-20 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (144, 159, 7, 'exam-platform', 'f6g7h8i9', 'test: 自动阅卷功能测试用例', 120, 20, '2026-06-03 07:13:07.395643', '2026-06-03 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.git_commit_log VALUES (145, 159, 7, 'exam-platform', 'f6g7h8i0', 'test: 性能压力测试脚本', 60, 8, '2026-06-23 07:13:07.395643', '2026-06-23 07:13:07.395643', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: graduation_requirement; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.graduation_requirement VALUES (13, 'GR1', '工程知识', '能够将数学、自然科学、工程基础和专业知识用于解决复杂工程问题', 1, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (14, 'GR2', '问题分析', '能够应用基本原理识别、表达并通过文献研究分析复杂工程问题', 2, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (15, 'GR3', '设计/开发解决方案', '能够设计针对复杂工程问题的解决方案及满足特定需求的系统', 3, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (16, 'GR4', '研究', '能够基于科学原理和方法对复杂工程问题进行研究', 4, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (17, 'GR5', '使用现代工具', '能够开发、选择与使用恰当的技术和工具', 5, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (18, 'GR6', '工程与社会', '能够评价工程实践对社会、健康、安全、法律和文化的影响', 6, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (19, 'GR7', '环境和可持续发展', '能够理解和评价工程实践对环境和社会可持续发展的影响', 7, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (20, 'GR8', '职业规范', '具有人文素养和社会责任感,遵守工程职业道德和规范', 8, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (21, 'GR9', '个人和团队', '能够在多学科团队中承担个体、成员及负责人角色', 9, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (22, 'GR10', '沟通', '能够就复杂工程问题与同行及公众进行有效沟通', 10, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (23, 'GR11', '项目管理', '理解并掌握工程管理原理与经济决策方法', 11, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.graduation_requirement VALUES (24, 'GR12', '终身学习', '具有自主学习和终身学习的意识和能力', 12, 0, '2026-07-07 23:13:08.545261', '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;


--
-- Data for Name: group_evaluation; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.group_evaluation VALUES (82, 6, 155, 'REQUIREMENT', 85.00, '第1组REQUIREMENT维度表现良好', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (83, 7, 155, 'REQUIREMENT', 72.00, '第2组REQUIREMENT维度有待提升', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (84, 6, 155, 'DESIGN', 82.00, '第1组DESIGN维度表现良好', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (85, 7, 155, 'DESIGN', 70.00, '第2组DESIGN维度有待提升', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (86, 6, 155, 'CODE', 78.00, '第1组CODE维度表现良好', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (87, 7, 155, 'CODE', 75.00, '第2组CODE维度有待提升', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (88, 6, 155, 'TEST', 80.00, '第1组TEST维度表现良好', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (89, 7, 155, 'TEST', 68.00, '第2组TEST维度有待提升', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (90, 6, 155, 'DOC', 88.00, '第1组DOC维度表现良好', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (91, 7, 155, 'DOC', 73.00, '第2组DOC维度有待提升', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (92, 6, 155, 'PRESENTATION', 90.00, '第1组PRESENTATION维度表现良好', '2026-06-08 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_evaluation VALUES (93, 7, 155, 'PRESENTATION', 76.00, '第2组PRESENTATION维度有待提升', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: group_member; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.group_member VALUES (26, 6, 156, 'PM', '2026-05-09 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (27, 6, 157, 'DEVELOPER', '2026-05-09 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (28, 6, 158, 'TESTER', '2026-05-09 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (29, 6, 159, 'DOC_ADMIN', '2026-05-09 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (30, 7, 157, 'PM', '2026-05-14 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (31, 7, 156, 'DEVELOPER', '2026-05-14 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (32, 7, 159, 'TESTER', '2026-05-14 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.group_member VALUES (33, 7, 158, 'DOC_ADMIN', '2026-05-14 07:13:07.395643', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: knowledge_point; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.knowledge_point VALUES (9, '软件生命周期模型', '第1章', '瀑布模型、敏捷开发、螺旋模型、V模型等软件生命周期模型的概念、阶段划分、优缺点及适用场景。重点对比瀑布模型与敏捷开发的核心理念差异。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (10, '软件开发过程', '第1章', '需求分析、概要设计、详细设计、编码实现、测试、部署维护各阶段的任务、交付物和质量标准。CMMI成熟度模型简介。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (11, '需求工程', '第2章', '需求获取方法(访谈、问卷、观察、原型)、需求分析技术(结构化分析、面向对象分析)、需求规格说明文档编写规范(IEEE830标准)、需求验证与评审。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (12, 'UML建模', '第2章', 'UML2.x的14种图型概述。重点掌握用例图、类图、时序图、活动图、状态图的语法和绘制方法。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (13, '面向对象分析', '第2章', '面向对象基本概念(封装、继承、多态)。领域建模、用例驱动分析、CRC卡片方法、健壮性分析。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (14, '设计模式', '第3章', '创建型、结构型、行为型三大类共23种设计模式的核心思想、类结构和典型应用场景。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (15, '软件架构设计', '第3章', '分层架构、微服务架构、事件驱动架构的风格对比。SOLID、DRY、KISS原则。RESTful API设计规范。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (16, '软件测试方法', '第4章', '测试层次(单元、集成、系统、验收)。白盒、黑盒、灰盒测试技术。等价类划分、边界值分析等测试用例设计方法。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (17, '自动化测试', '第4章', 'JUnit5框架使用、Mockito模拟框架、SeleniumWeb自动化、测试覆盖率分析(JaCoCo)。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (18, 'Git版本控制', '第5章', 'Git基本操作、分支策略(GitFlow/GitHubFlow)、冲突解决、PullRequest代码评审流程。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (19, '持续集成', '第5章', 'CI/CD概念与工具链。自动化构建、测试、部署流水线。Docker容器化与DockerCompose服务编排。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.knowledge_point VALUES (20, 'AHP层次分析法', '第6章', 'Saaty1-9标度法、判断矩阵构造、特征向量法计算权重、一致性检验(CI/CR计算)。AHP在教育评价中的应用。', '2026-07-07 23:13:08.545261', 0, NULL) ON CONFLICT DO NOTHING;


--
-- Data for Name: notification; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.notification VALUES (399, 156, '岗位变更通知', '你在第1组的岗位已从DEVELOPER变更为PM', true, '2026-05-14 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (400, 157, '岗位变更通知', '你在第2组的岗位已从DEVELOPER变更为PM', true, '2026-05-19 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (401, 159, '岗位变更通知', '你在第2组的岗位已从DOC_ADMIN变更为TESTER', true, '2026-05-29 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (402, 156, '新的小组评价', '第1组已收到教师提交的小组评价,请查看成绩', false, '2026-06-08 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (403, 158, '成绩申诉已提交', '你对第2组DESIGN评分的申诉已提交,等待审核', false, '2026-06-28 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (404, 156, '新里程碑创建', '第1组新里程碑编码实现已创建', false, '2026-05-31 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (405, 157, '新里程碑创建', '第2组新里程碑编码实现已创建', false, '2026-06-05 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.notification VALUES (406, 156, '贡献加分已审批', '你提交的架构设计技术攻关贡献已审批,+5分', false, '2026-05-25 07:13:07.395643') ON CONFLICT DO NOTHING;


--
-- Data for Name: objective_requirement_mapping; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.objective_requirement_mapping VALUES (13, 88, 13, 0.2500, 'H', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (14, 88, 14, 0.2500, 'M', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (15, 89, 15, 0.2500, 'H', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (16, 89, 17, 0.2500, 'M', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (17, 89, 22, 0.2500, 'L', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (18, 90, 21, 0.2500, 'H', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (19, 90, 23, 0.2500, 'M', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (20, 91, 16, 0.2500, 'H', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;
INSERT INTO public.objective_requirement_mapping VALUES (21, 91, 17, 0.2500, 'H', 0, '2026-07-07 23:13:08.545261') ON CONFLICT DO NOTHING;


--
-- Data for Name: position_criteria; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.position_criteria VALUES (1, 'PM', '计划制定合理性', 0.35, '项目计划是否完善、可行') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (2, 'PM', '进度控制能力', 0.35, '是否按时推进项目') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (3, 'PM', '团队协调沟通', 0.30, '成员协调和冲突解决') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (4, 'DEVELOPER', '代码质量', 0.40, '代码规范性、可维护性') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (5, 'DEVELOPER', '技术实现能力', 0.35, '功能完成度和技术方案') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (6, 'DEVELOPER', '问题解决能力', 0.25, '技术难题攻关') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (7, 'TESTER', '测试用例覆盖', 0.40, '测试设计全面性') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (8, 'TESTER', 'Bug发现与跟踪', 0.40, '缺陷发现和管理') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (9, 'TESTER', '质量报告', 0.20, '测试报告质量') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (10, 'DOC_ADMIN', '文档规范性', 0.40, '文档格式和结构') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (11, 'DOC_ADMIN', '文档完整性', 0.35, '内容是否全面') ON CONFLICT DO NOTHING;
INSERT INTO public.position_criteria VALUES (12, 'DOC_ADMIN', '文档及时性', 0.25, '是否按时提交') ON CONFLICT DO NOTHING;


--
-- Data for Name: position_history; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.position_history VALUES (1, 6, 156, 'DEVELOPER', 'PM', 155, '项目需要,组长调整', '2026-05-14 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.position_history VALUES (2, 7, 157, 'DEVELOPER', 'PM', 155, '岗位轮换', '2026-05-19 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.position_history VALUES (3, 7, 159, 'DOC_ADMIN', 'TESTER', 155, '测试人手不足', '2026-05-29 07:13:07.395643') ON CONFLICT DO NOTHING;


--
-- Data for Name: project_journal; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.project_journal VALUES (78, 156, 6, '完成项目需求调研,与教师确认功能范围,整理用户故事15条,绘制系统用例图', '2026-05-11', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (79, 156, 6, '完成系统架构设计,确定技术栈为SpringBoot+Vue3+PostgreSQL,绘制ER图', '2026-05-19', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (80, 156, 6, '完成数据库表结构设计,编写建表SQL脚本,完成索引优化方案', '2026-05-27', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (81, 156, 6, '完成学生信息管理模块后端CRUD接口,编写单元测试8个', '2026-06-08', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (82, 157, 6, '学习SpringBoot框架,搭建后端项目骨架,配置MyBatis-Plus和PostgreSQL', '2026-05-21', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (83, 157, 6, '完成成绩管理模块后端接口开发和Swagger文档配置', '2026-05-31', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (84, 157, 6, '完成前后端联调,解决跨域问题,实现成绩录入和查询完整流程', '2026-06-13', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (85, 157, 6, '性能优化:添加Redis缓存,数据库查询响应时间从200ms降至50ms', '2026-06-24', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (86, 158, 6, '编写单元测试用例20个,覆盖成绩管理核心业务逻辑,全部通过', '2026-05-29', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (87, 158, 6, '完成集成测试,使用Postman测试全部API接口,编写测试报告', '2026-06-10', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (88, 159, 6, '开始编写系统设计文档,整理需求规格说明', '2026-05-14', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (89, 159, 6, '完成API接口文档和用户手册初稿', '2026-06-03', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (90, 156, 7, '完成在线考试平台需求分析和技术方案评审,确定采用微服务雏形架构', '2026-05-17', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (91, 156, 7, '完成题库管理模块开发,支持单选题、多选题、判断题三种题型', '2026-05-29', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (92, 157, 7, '完成考试安排功能,支持定时考试和即时考试两种模式', '2026-05-24', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_journal VALUES (93, 157, 7, '完成自动阅卷功能,客观题自动批改,主观题支持关键词匹配评分', '2026-06-06', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: project_milestone; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.project_milestone VALUES (72, 6, '需求分析', 'CONCEIVE', '2026-05-19', true, '2026-05-17 07:13:07.395643', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (73, 6, '系统设计', 'DESIGN', '2026-06-03', true, '2026-05-31 07:13:07.395643', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (74, 6, '编码实现', 'IMPLEMENT', '2026-07-22', false, NULL, '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (75, 6, '测试部署', 'OPERATE', '2026-08-12', false, NULL, '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (76, 7, '需求分析', 'CONCEIVE', '2026-05-24', true, '2026-05-21 07:13:07.395643', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (77, 7, '系统设计', 'DESIGN', '2026-06-08', true, '2026-06-05 07:13:07.395643', '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (78, 7, '编码实现', 'IMPLEMENT', '2026-07-29', false, NULL, '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.project_milestone VALUES (79, 7, '测试部署', 'OPERATE', '2026-08-19', false, NULL, '2026-07-07 23:13:08.545261', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: project_task; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.project_task VALUES (67, 6, 74, 156, '学生信息CRUD接口开发', 'DONE', 2, '2026-05-24 07:13:07.395643', '2026-05-27 07:13:07.395643', 0, '2026-05-27') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (68, 6, 74, 156, '成绩统计图表前端', 'DOING', 1, '2026-06-08 07:13:07.395643', '2026-06-08 07:13:07.395643', 0, '2026-06-24') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (69, 6, 74, 157, '成绩录入接口对接', 'DONE', 1, '2026-05-21 07:13:07.395643', '2026-05-24 07:13:07.395643', 0, '2026-05-24') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (70, 6, 74, 157, '成绩查询分页优化', 'DONE', 2, '2026-05-27 07:13:07.395643', '2026-05-31 07:13:07.395643', 0, '2026-06-03') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (71, 6, 74, 158, '成绩模块单元测试', 'DOING', 1, '2026-05-29 07:13:07.395643', '2026-05-29 07:13:07.395643', 0, '2026-06-28') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (72, 6, 74, 159, 'API文档更新', 'TODO', 3, '2026-06-03 07:13:07.395643', '2026-06-03 07:13:07.395643', 0, '2026-07-01') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (73, 6, 73, 156, '数据库ER图设计', 'DONE', 1, '2026-05-19 07:13:07.395643', '2026-05-21 07:13:07.395643', 0, '2026-05-21') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (74, 6, 73, 159, '系统设计文档', 'DONE', 1, '2026-05-14 07:13:07.395643', '2026-05-27 07:13:07.395643', 0, '2026-05-24') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (75, 7, 78, 156, '题库管理模块开发', 'DONE', 1, '2026-05-21 07:13:07.395643', '2026-05-27 07:13:07.395643', 0, '2026-05-27') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (76, 7, 78, 156, '考试结果统计功能', 'DOING', 2, '2026-06-08 07:13:07.395643', '2026-06-08 07:13:07.395643', 0, '2026-06-24') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (77, 7, 78, 157, '考试安排后端接口', 'DONE', 1, '2026-05-24 07:13:07.395643', '2026-05-29 07:13:07.395643', 0, '2026-05-29') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (78, 7, 78, 157, '自动阅卷算法优化', 'DOING', 1, '2026-06-03 07:13:07.395643', '2026-06-03 07:13:07.395643', 0, '2026-06-28') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (79, 7, 78, 159, '阅卷功能测试用例', 'TODO', 1, '2026-06-03 07:13:07.395643', '2026-06-03 07:13:07.395643', 0, '2026-07-01') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (80, 7, 77, 156, '技术选型方案文档', 'DONE', 1, '2026-05-19 07:13:07.395643', '2026-05-24 07:13:07.395643', 0, '2026-05-24') ON CONFLICT DO NOTHING;
INSERT INTO public.project_task VALUES (81, 7, 77, 157, '数据库表设计', 'DONE', 1, '2026-05-21 07:13:07.395643', '2026-05-27 07:13:07.395643', 0, '2026-05-27') ON CONFLICT DO NOTHING;


--
-- Data for Name: qa_record; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.qa_record VALUES (105, 156, 9, '瀑布模型和敏捷开发有什么区别?', '瀑布模型是线性顺序的开发模型,每个阶段完成后才能进入下一阶段,适合需求明确的场景。敏捷开发是迭代式的,每个Sprint(2-4周)交付可工作软件,强调快速响应变化和持续反馈。两者核心区别:瀑布重计划和文档,敏捷重适应和沟通。', true, NULL, '2026-05-19 07:13:07.395643', 0, 'demo-s1', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (106, 156, 20, 'AHP一致性比率CR怎么算?如果CR>0.1怎么办?', 'CR=CI/RI。CI=(λmax-n)/(n-1),RI查表获取。如果CR≥0.1,说明判断矩阵存在逻辑矛盾,需要重新调整两两比较的取值,直到CR<0.1通过一致性检验。', true, NULL, '2026-06-03 07:13:07.395643', 0, 'demo-s1', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (107, 156, 15, '前后端分离架构中,前端路由和后端API怎么对应?', '前端使用VueRouter的Hash模式,所有业务请求通过Nginx代理到/api/路径,后端SpringBoot处理。前端路由是客户端页面跳转,后端API是数据接口,两者通过axios通信。', true, NULL, '2026-06-23 07:13:07.395643', 0, 'demo-s1', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (108, 157, 12, 'UML类图中关联、聚合、组合怎么区分?', '关联(Association)是类之间最基本的结构关系,用实线表示。聚合(Aggregation)是整体-部分关系,部分可脱离整体存在,用空心菱形。组合(Composition)是更强的聚合,部分不能脱离整体,用实心菱形。', true, NULL, '2026-05-21 07:13:07.395643', 0, 'demo-s2', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (109, 157, 14, '什么时候用工厂模式,什么时候用建造者模式?', '工厂模式(Factory)用于创建单个对象,隐藏具体类的实例化逻辑。建造者模式(Builder)用于创建复杂对象,分步骤构建。简记:工厂一步创建,建造者分步构建。', true, NULL, '2026-06-08 07:13:07.395643', 0, 'demo-s2', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (110, 157, 18, 'Git merge和rebase有什么区别?团队协作推荐哪个?', 'merge保留完整的分支历史,产生一个合并提交,适合公共分支。rebase将当前分支的提交变基到目标分支,产生线性历史,适合个人分支整理。团队协作推荐:公共分支用merge,个人分支可先rebase。', true, NULL, '2026-06-26 07:13:07.395643', 0, 'demo-s2', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (111, 158, 16, '单元测试应该覆盖哪些内容?覆盖率多少合适?', '单元测试应覆盖:正常路径(HappyPath)、边界条件(空值/零值/最大值)、异常处理。覆盖率目标:行覆盖率≥80%,分支覆盖率≥70%。但高覆盖率不等于高质量测试,关键测试用例的设计比数字更重要。', true, NULL, '2026-05-27 07:13:07.395643', 0, 'demo-s3', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (112, 158, 17, 'JUnit5中@Mock和@InjectMocks有什么区别?', '@Mock创建被测类的依赖模拟对象,所有方法返回默认值。@InjectMocks将@Mock对象自动注入到被测类中。无需手动调用setter或构造器即可完成依赖注入。', true, NULL, '2026-06-10 07:13:07.395643', 0, 'demo-s3', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (113, 158, 16, '什么是Mock测试?什么时候需要Mock?', 'Mock测试用模拟对象替代真实依赖,隔离被测代码。需要Mock的场景:依赖的外部服务不可用、依赖的行为不可控、依赖的操作有副作用、依赖的响应速度影响测试效率。', false, NULL, '2026-06-28 07:13:07.395643', 0, 'demo-s3', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (114, 159, 11, '如何判断需求的优先级?MoSCoW方法怎么用?', 'MoSCoW方法将需求分为四类:MustHave(必须有)、ShouldHave(应该有)、CouldHave(可以有)、Won''tHave(暂不做)。优先级排序时结合业务价值和实现成本两个维度。', true, NULL, '2026-05-24 07:13:07.395643', 0, 'demo-s4', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (115, 159, 15, '软件架构评估方法ATAM怎么操作?', 'ATAM(架构权衡分析法)步骤:收集场景、描述架构方案、分析每个场景下架构的响应、识别风险点和非风险点、输出评估报告。核心思想:不存在完美的架构,只有最适合当前场景的权衡。', true, NULL, '2026-06-18 07:13:07.395643', 0, 'demo-s4', NULL, 'text') ON CONFLICT DO NOTHING;
INSERT INTO public.qa_record VALUES (116, 159, 10, '需求规格说明文档应该包含哪些章节?', '按IEEE830标准:引言(目的/范围/定义)、总体描述(产品视角/用户特征/约束)、具体需求(功能需求/非功能需求/接口需求)。功能需求用系统应...句式,避免在需求文档中混入设计方案。', false, NULL, '2026-07-03 07:13:07.395643', 0, 'demo-s4', NULL, 'text') ON CONFLICT DO NOTHING;


--
-- Data for Name: question_answer; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.question_answer VALUES (56, 6, 156, 155, '项目架构方案咨询', '微服务还是单体架构更适合?', '建议先用单体,SpringBoot单体即可满足,后续如有性能瓶颈再拆微服务。重点是模块划分清晰,为未来拆分做好准备。', 'RESOLVED', NULL, '2026-05-24 07:13:07.395643', '2026-05-26 07:13:07.395643', NULL, 0, '2026-05-24 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.question_answer VALUES (57, 6, 157, 155, '测试框架选择', 'JUnit5还是TestNG更适合?', 'JUnit5更主流,社区活跃度更高,与SpringBoot集成更好。推荐使用JUnit5+Mockito组合。', 'ANSWERED', NULL, '2026-06-03 07:13:07.395643', '2026-06-05 07:13:07.395643', NULL, 0, '2026-06-03 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.question_answer VALUES (58, 7, 158, NULL, '数据库设计评审', '考试平台是否需要读写分离?', NULL, 'PENDING', NULL, '2026-06-18 07:13:07.395643', NULL, NULL, 0, '2026-06-18 07:13:07.395643') ON CONFLICT DO NOTHING;


--
-- Data for Name: quiz; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.quiz VALUES (3, 155, 6, 5, '软件工程基础测验', '[{"q": "软件生命周期不包括哪个阶段?", "opts": ["需求分析", "系统设计", "市场推广", "测试部署"], "ans": 2}, {"q": "UML中用于描述系统功能的图是?", "opts": ["类图", "用例图", "时序图", "部署图"], "ans": 1}, {"q": "AHP的CR小于多少通过一致性检验?", "opts": ["0.05", "0.1", "0.15", "0.2"], "ans": 1}]', 'PUBLISHED', 100, '2026-06-08 07:13:07.395643', '2026-06-13 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.quiz VALUES (4, 155, 7, 5, 'UML建模测验', '[{"q": "类图中表示组合关系的符号是?", "opts": ["空心菱形", "实心菱形", "箭头", "无特殊符号"], "ans": 1}, {"q": "时序图的生命线表示什么?", "opts": ["类的生命周期", "对象的生存时间", "方法的执行时间", "系统的运行时间"], "ans": 1}]', 'DRAFT', 100, '2026-06-23 07:13:07.395643', NULL, 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: repo_config; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.repo_config VALUES (5, 6, 'GITHUB', 'student1', 'grade-mgmt', 'ghp_demo123', 'main', '2026-05-11 07:13:07.395643', true, '2026-05-11 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.repo_config VALUES (6, 7, 'GITEE', 'student2', 'exam-platform', 'gitee_demo456', 'master', '2026-05-17 07:13:07.395643', true, '2026-05-17 07:13:07.395643', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: requirement_change; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.requirement_change VALUES (2, 6, '增加成绩导出CSV功能', '教师要求增加成绩数据导出功能', 'ADD', '教师提出新需求', 156, '2026-06-18 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.requirement_change VALUES (3, 6, '修改学生信息字段', '增加学生手机号和邮箱字段', 'MODIFY', '数据库设计调整', 156, '2026-06-23 07:13:07.395643') ON CONFLICT DO NOTHING;
INSERT INTO public.requirement_change VALUES (4, 7, '取消手动阅卷功能', '改用自动阅卷替代手动阅卷', 'REMOVE', '简化教师操作', 157, '2026-06-13 07:13:07.395643') ON CONFLICT DO NOTHING;


--
-- Data for Name: role_evaluation; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.role_evaluation VALUES (24, 156, 6, 155, 'PM', 'LEADERSHIP', 88.00, '计划制定合理', '2026-06-10 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (25, 157, 6, 155, 'DEVELOPER', 'CODING_QUALITY', 82.00, '代码质量较好', '2026-06-10 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (26, 158, 6, 155, 'TESTER', 'TEST_COVERAGE', 85.00, '测试用例全面', '2026-06-10 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (27, 159, 6, 155, 'DOC_ADMIN', 'DOC_QUALITY', 90.00, '文档规范完整', '2026-06-10 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (28, 157, 7, 155, 'PM', 'LEADERSHIP', 72.00, '计划执行需加强', '2026-06-15 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (29, 156, 7, 155, 'DEVELOPER', 'CODING_QUALITY', 75.00, '代码可维护性待提升', '2026-06-15 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (30, 159, 7, 155, 'TESTER', 'TEST_COVERAGE', 70.00, '测试用例不足', '2026-06-15 07:13:07.395643', 0) ON CONFLICT DO NOTHING;
INSERT INTO public.role_evaluation VALUES (31, 158, 7, 155, 'DOC_ADMIN', 'DOC_QUALITY', 78.00, '文档结构清晰', '2026-06-15 07:13:07.395643', 0) ON CONFLICT DO NOTHING;


--
-- Data for Name: score_dispute; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.score_dispute VALUES (1, 157, NULL, 'GROUP', '对第2组DESIGN评分70分有异议,认为应≥75分', 'PENDING', NULL, NULL, '2026-06-28 07:13:07.395643', '2026-06-28 07:13:07.395643') ON CONFLICT DO NOTHING;


--
-- Data for Name: self_test_record; Type: TABLE DATA; Schema: public; Owner: obe_admin
--

INSERT INTO public.self_test_record VALUES (3, 156, 85.00, 100.00, '2026-06-08 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (4, 157, 72.00, 100.00, '2026-06-08 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (5, 158, 90.00, 100.00, '2026-06-08 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (6, 159, 78.00, 100.00, '2026-06-08 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (7, 156, 92.00, 100.00, '2026-06-23 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (8, 157, 80.00, 100.00, '2026-06-23 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (9, 158, 88.00, 100.00, '2026-06-23 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (10, 159, 85.00, 100.00, '2026-06-23 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (11, 156, 78.00, 100.00, '2026-07-03 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (12, 157, 85.00, 100.00, '2026-07-03 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (13, 158, 95.00, 100.00, '2026-07-03 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.self_test_record VALUES (14, 159, 72.00, 100.00, '2026-07-03 07:13:07.395643', 0, NULL, NULL, NULL) ON CONFLICT DO NOTHING;


--
-- Name: cdio_phase_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.cdio_phase_mapping_id_seq', 13, true);


--
-- Name: contribution_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.contribution_log_id_seq', 65, true);


--
-- Name: course_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.course_id_seq', 5, true);


--
-- Name: course_objective_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.course_objective_id_seq', 91, true);


--
-- Name: evaluation_method_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.evaluation_method_id_seq', 26, true);


--
-- Name: git_commit_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.git_commit_log_id_seq', 145, true);


--
-- Name: graduation_indicator_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.graduation_indicator_id_seq', 14, true);


--
-- Name: graduation_requirement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.graduation_requirement_id_seq', 24, true);


--
-- Name: group_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.group_evaluation_id_seq', 93, true);


--
-- Name: group_member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.group_member_id_seq', 33, true);


--
-- Name: knowledge_point_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.knowledge_point_id_seq', 20, true);


--
-- Name: notification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.notification_id_seq', 406, true);


--
-- Name: objective_requirement_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.objective_requirement_mapping_id_seq', 21, true);


--
-- Name: position_criteria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.position_criteria_id_seq', 12, true);


--
-- Name: position_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.position_history_id_seq', 3, true);


--
-- Name: project_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.project_group_id_seq', 7, true);


--
-- Name: project_journal_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.project_journal_id_seq', 93, true);


--
-- Name: project_milestone_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.project_milestone_id_seq', 79, true);


--
-- Name: project_task_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.project_task_id_seq', 81, true);


--
-- Name: qa_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.qa_record_id_seq', 116, true);


--
-- Name: question_answer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.question_answer_id_seq', 58, true);


--
-- Name: quiz_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.quiz_id_seq', 4, true);


--
-- Name: repo_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.repo_config_id_seq', 6, true);


--
-- Name: requirement_change_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.requirement_change_id_seq', 4, true);


--
-- Name: role_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.role_evaluation_id_seq', 31, true);


--
-- Name: score_dispute_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.score_dispute_id_seq', 1, true);


--
-- Name: self_test_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.self_test_record_id_seq', 14, true);


--
-- Name: sys_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: obe_admin
--

SELECT pg_catalog.setval('public.sys_user_id_seq', 159, true);


--
-- PostgreSQL database dump complete
--

\unrestrict H2DIGtbRa01W9yJArJ9wh1N7eqjRJ9afE1fLdl2VyYgoUU5cut7FhOIHUf6CUTl

