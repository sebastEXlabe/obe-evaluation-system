-- ============================================================
-- OBE-CDIO 软件工程课程教学评价管理系统 - 数据库初始化脚本
-- PostgreSQL 16 + pgvector
-- ============================================================

-- 0. 基础表 --------------------------------------------------

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL UNIQUE,
    password    VARCHAR(256) NOT NULL,
    real_name   VARCHAR(64),
    email       VARCHAR(128),
    phone       VARCHAR(20),
    role_code   VARCHAR(32)  NOT NULL DEFAULT 'STUDENT',  -- ADMIN/TEACHER/STUDENT
    status      SMALLINT     NOT NULL DEFAULT 1,          -- 1=启用 0=禁用
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE  sys_user IS '用户表';
COMMENT ON COLUMN sys_user.role_code IS '角色：ADMIN-管理员 TEACHER-教师 STUDENT-学生';

-- 1. 模块一：课程目标与评价指标 -------------------------------

CREATE TABLE IF NOT EXISTS course_objective (
    id            BIGSERIAL PRIMARY KEY,
    objective_no  VARCHAR(32)  NOT NULL UNIQUE,   -- 编号如 OBJ-01
    title         VARCHAR(256) NOT NULL,           -- 目标标题
    description   TEXT,                            -- 目标描述
    dimension     VARCHAR(32)  NOT NULL DEFAULT 'KNOWLEDGE', -- KNOWLEDGE/ABILITY/QUALITY
    sort_order    INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE  course_objective IS '课程目标';
COMMENT ON COLUMN course_objective.dimension IS '维度：KNOWLEDGE-知识 ABILITY-能力 QUALITY-素养';

CREATE TABLE IF NOT EXISTS graduation_indicator (
    id              BIGSERIAL PRIMARY KEY,
    objective_id    BIGINT       NOT NULL REFERENCES course_objective(id),
    indicator_no    VARCHAR(32)  NOT NULL UNIQUE,  -- 指标点编号
    title           VARCHAR(256) NOT NULL,
    description     TEXT,
    sort_order      INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE graduation_indicator IS '毕业要求指标点（关联课程目标）';

CREATE TABLE IF NOT EXISTS evaluation_method (
    id              BIGSERIAL PRIMARY KEY,
    indicator_id    BIGINT       NOT NULL REFERENCES graduation_indicator(id),
    method_name     VARCHAR(128) NOT NULL,          -- 评价方式名称
    weight          DECIMAL(5,4) NOT NULL DEFAULT 0, -- 权重 (0~1)
    data_source     VARCHAR(64)  NOT NULL DEFAULT 'MANUAL', -- MANUAL/MAXKB/GIT/TEST/JOURNAL
    full_score      DECIMAL(8,2) NOT NULL DEFAULT 100,
    remark          VARCHAR(512),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE  evaluation_method IS '评价方式（关联指标点）';
COMMENT ON COLUMN evaluation_method.data_source IS '数据源：MANUAL-手动 MAXKB-问答 GIT-代码提交 TEST-测验 JOURNAL-日志';

CREATE TABLE IF NOT EXISTS cdio_phase_mapping (
    id              BIGSERIAL PRIMARY KEY,
    indicator_id    BIGINT       NOT NULL REFERENCES graduation_indicator(id),
    cdio_phase      VARCHAR(32)  NOT NULL,  -- CONCEIVE/DESIGN/IMPLEMENT/OPERATE
    sort_order      INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE cdio_phase_mapping IS 'CDIO阶段映射（指标点→CDIO阶段）';

-- 2. 模块二：小组分工与角色 --------------------------------

CREATE TABLE IF NOT EXISTS project_group (
    id          BIGSERIAL PRIMARY KEY,
    group_name  VARCHAR(128) NOT NULL,
    teacher_id  BIGINT       REFERENCES sys_user(id),
    max_members INT          NOT NULL DEFAULT 4,
    status      SMALLINT     NOT NULL DEFAULT 1,   -- 1=进行中 0=已结束
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE project_group IS '项目小组';

CREATE TABLE IF NOT EXISTS group_member (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT       NOT NULL REFERENCES project_group(id),
    user_id     BIGINT       NOT NULL REFERENCES sys_user(id),
    role_code   VARCHAR(32)  NOT NULL DEFAULT 'DEVELOPER', -- PM/DEVELOPER/TESTER/DOC_ADMIN
    join_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE(group_id, user_id)
);
COMMENT ON TABLE  group_member IS '小组成员';
COMMENT ON COLUMN group_member.role_code IS '角色：PM-项目经理 DEVELOPER-开发 TESTER-测试 DOC_ADMIN-文档管理员';

-- 3. 模块三：MaxKB 问答集成（暂为预留）---------------------

CREATE TABLE IF NOT EXISTS knowledge_point (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(256) NOT NULL,
    chapter     VARCHAR(128),
    content     TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE knowledge_point IS '知识点库';

CREATE TABLE IF NOT EXISTS qa_record (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES sys_user(id),
    knowledge_id    BIGINT       REFERENCES knowledge_point(id),
    question        TEXT         NOT NULL,
    answer          TEXT,
    is_resolved     BOOLEAN      NOT NULL DEFAULT FALSE,
    question_type   VARCHAR(32),               -- 问答分类
    asked_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE qa_record IS 'MaxKB问答记录';

CREATE TABLE IF NOT EXISTS self_test_record (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES sys_user(id),
    score       DECIMAL(5,2) NOT NULL DEFAULT 0,
    total       DECIMAL(5,2) NOT NULL DEFAULT 100,
    taken_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE self_test_record IS '自主测验记录';

-- 4. 模块四：项目过程追踪 -----------------------------------

CREATE TABLE IF NOT EXISTS project_milestone (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT       NOT NULL REFERENCES project_group(id),
    title       VARCHAR(256) NOT NULL,
    cdio_phase  VARCHAR(32)  NOT NULL,          -- CONCEIVE/DESIGN/IMPLEMENT/OPERATE
    due_date    DATE,
    is_done     BOOLEAN      NOT NULL DEFAULT FALSE,
    finished_at TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE project_milestone IS '项目里程碑（CDIO四阶段节点）';

CREATE TABLE IF NOT EXISTS project_task (
    id              BIGSERIAL PRIMARY KEY,
    group_id        BIGINT       NOT NULL REFERENCES project_group(id),
    milestone_id    BIGINT       REFERENCES project_milestone(id),
    assignee_id     BIGINT       REFERENCES sys_user(id),
    title           VARCHAR(256) NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'TODO',  -- TODO/DOING/DONE
    priority        SMALLINT     NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE project_task IS '项目任务看板';

CREATE TABLE IF NOT EXISTS git_commit_log (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES sys_user(id),
    group_id        BIGINT       REFERENCES project_group(id),
    repo_name       VARCHAR(256),
    commit_hash     VARCHAR(64),
    message         TEXT,
    additions       INT          NOT NULL DEFAULT 0,
    deletions       INT          NOT NULL DEFAULT 0,
    committed_at    TIMESTAMP    NOT NULL,
    synced_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE git_commit_log IS 'Git提交记录（同步自Gitee/GitHub）';

CREATE TABLE IF NOT EXISTS project_journal (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES sys_user(id),
    group_id    BIGINT       NOT NULL REFERENCES project_group(id),
    content     TEXT         NOT NULL,
    journal_date DATE        NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE project_journal IS '项目日志';

CREATE TABLE IF NOT EXISTS contribution_log (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES sys_user(id),
    group_id        BIGINT       NOT NULL REFERENCES project_group(id),
    description     VARCHAR(512) NOT NULL,
    bonus_score     DECIMAL(5,2) NOT NULL DEFAULT 0,
    approved_by     BIGINT       REFERENCES sys_user(id),  -- 教师审批
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE contribution_log IS '贡献度加分记录';

-- 5. 模块五：多元融合评价 -----------------------------------

CREATE TABLE IF NOT EXISTS group_evaluation (
    id              BIGSERIAL PRIMARY KEY,
    group_id        BIGINT       NOT NULL REFERENCES project_group(id),
    evaluator_id    BIGINT       NOT NULL REFERENCES sys_user(id),  -- 评价人（教师）
    dimension       VARCHAR(64)  NOT NULL,  -- REQUIREMENT/DESIGN/CODE/TEST/DOC/PRESENTATION
    score           DECIMAL(8,2) NOT NULL DEFAULT 0,
    comment         TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE  group_evaluation IS '小组成果评价';
COMMENT ON COLUMN group_evaluation.dimension IS '评价维度：REQUIREMENT-需求 DESIGN-设计 CODE-代码 TEST-测试 DOC-文档 PRESENTATION-答辩';

CREATE TABLE IF NOT EXISTS personal_score (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL REFERENCES sys_user(id),
    group_id            BIGINT       NOT NULL REFERENCES project_group(id),
    group_total_score   DECIMAL(8,2) NOT NULL DEFAULT 0,   -- 小组成果分
    contribution_ratio  DECIMAL(5,4) NOT NULL DEFAULT 0,   -- 个人贡献系数 (0~1)
    final_score         DECIMAL(8,2) NOT NULL DEFAULT 0,   -- 最终成绩
    bonus_total         DECIMAL(5,2) NOT NULL DEFAULT 0,   -- 加分合计
    calculated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, group_id)
);
COMMENT ON TABLE personal_score IS '个人成绩（反搭便车核心表）';

CREATE TABLE IF NOT EXISTS role_evaluation (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES sys_user(id),
    group_id        BIGINT       NOT NULL REFERENCES project_group(id),
    evaluator_id    BIGINT       NOT NULL REFERENCES sys_user(id),
    role_code       VARCHAR(32)  NOT NULL,
    dimension       VARCHAR(64)  NOT NULL,   -- 对应各角色的评价维度
    score           DECIMAL(8,2) NOT NULL DEFAULT 0,
    comment         TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE  role_evaluation IS '角色专项评价';
COMMENT ON COLUMN role_evaluation.dimension IS 'PM维度: PLAN/SCHEDULE/COORDINATION DEVELOPER维度: ARCHITECTURE/CODE_QUALITY/BUG_FIX TESTER维度: COVERAGE/BUG_FOUND DOC_ADMIN维度: STANDARD/TIMELINESS/COMPLETENESS';

-- 6. 模块六：达成度分析 ------------------------------------

CREATE TABLE IF NOT EXISTS achievement_result (
    id                  BIGSERIAL PRIMARY KEY,
    objective_id        BIGINT       NOT NULL REFERENCES course_objective(id),
    group_id            BIGINT       REFERENCES project_group(id),  -- NULL=班级整体
    achievement_value   DECIMAL(8,4) NOT NULL DEFAULT 0,  -- 达成度值 (0~1)
    dimension           VARCHAR(32)  NOT NULL DEFAULT 'BATCH', -- BATCH/GROUP/INDIVIDUAL
    calculated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE achievement_result IS '课程目标达成度计算结果';

CREATE TABLE IF NOT EXISTS improvement_suggestion (
    id              BIGSERIAL PRIMARY KEY,
    objective_id    BIGINT       NOT NULL REFERENCES course_objective(id),
    indicator_id    BIGINT       REFERENCES graduation_indicator(id),
    suggestion      TEXT         NOT NULL,
    is_auto         BOOLEAN      NOT NULL DEFAULT TRUE,  -- TRUE=系统自动生成
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE improvement_suggestion IS '改进建议';

-- 索引 ----------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_user_role      ON sys_user(role_code);
CREATE INDEX IF NOT EXISTS idx_indicator_obj  ON graduation_indicator(objective_id);
CREATE INDEX IF NOT EXISTS idx_eval_indicator ON evaluation_method(indicator_id);
CREATE INDEX IF NOT EXISTS idx_qa_user        ON qa_record(user_id, asked_at);
CREATE INDEX IF NOT EXISTS idx_git_user       ON git_commit_log(user_id, committed_at);
CREATE INDEX IF NOT EXISTS idx_task_assignee  ON project_task(assignee_id, status);
CREATE INDEX IF NOT EXISTS idx_member_group   ON group_member(group_id);
CREATE INDEX IF NOT EXISTS idx_personal_score ON personal_score(user_id, group_id);
CREATE INDEX IF NOT EXISTS idx_achievement    ON achievement_result(objective_id, group_id);

-- 初始化管理员 ----------------------------------------------
INSERT INTO sys_user (username, password, real_name, role_code, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 'ADMIN', 1)
ON CONFLICT (username) DO NOTHING;
-- 默认密码: admin123 (BCrypt, 后续登录后可修改)
