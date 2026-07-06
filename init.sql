--
-- PostgreSQL database dump
--

\restrict e4LdYfbK7b3Os9QYrcDNqEp1JfY3mFMxafpsAR3brOcuBhMqKFmFsbxD1Dc3RYV

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

-- ============================================================
-- Seed data: default admin user (password: admin123)
-- ============================================================
INSERT INTO public.sys_user (username, password, real_name, role_code, status, git_username, git_email)
VALUES ('admin', '$2a$10$k4cFBGkbK1L/V/c3D1A6heCBGs5AmLGrgwWxQfQXvUmjIb3a3qzVG', '系统管理员', 'ADMIN', 1, 'sebastEXlabe', 'sebasttt@163.com')
ON CONFLICT (username) DO NOTHING;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: achievement_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.achievement_result (
    id bigint NOT NULL,
    objective_id bigint NOT NULL,
    group_id bigint,
    achievement_value numeric(8,4) DEFAULT 0 NOT NULL,
    dimension character varying(32) DEFAULT 'BATCH'::character varying NOT NULL,
    calculated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    calc_round integer DEFAULT 1
);


--
-- Name: TABLE achievement_result; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.achievement_result IS '课程目标达成度计算结果';


--
-- Name: achievement_result_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.achievement_result_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: achievement_result_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.achievement_result_id_seq OWNED BY public.achievement_result.id;


--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_log (
    id bigint NOT NULL,
    user_id bigint,
    username character varying(64),
    action character varying(128),
    target character varying(256),
    ip character varying(64),
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: audit_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.audit_log_id_seq OWNED BY public.audit_log.id;


--
-- Name: cdio_phase_mapping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cdio_phase_mapping (
    id bigint NOT NULL,
    indicator_id bigint NOT NULL,
    cdio_phase character varying(32) NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    course_id bigint
);


--
-- Name: TABLE cdio_phase_mapping; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.cdio_phase_mapping IS 'CDIO阶段映射（指标点→CDIO阶段）';


--
-- Name: cdio_phase_mapping_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cdio_phase_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cdio_phase_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cdio_phase_mapping_id_seq OWNED BY public.cdio_phase_mapping.id;


--
-- Name: contribution_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contribution_log (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    group_id bigint NOT NULL,
    description character varying(512) NOT NULL,
    bonus_score numeric(5,2) DEFAULT 0 NOT NULL,
    approved_by bigint,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    approved boolean DEFAULT false
);


--
-- Name: TABLE contribution_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.contribution_log IS '贡献度加分记录';


--
-- Name: contribution_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.contribution_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: contribution_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.contribution_log_id_seq OWNED BY public.contribution_log.id;


--
-- Name: course; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.course (
    id bigint NOT NULL,
    course_name character varying(256) NOT NULL,
    semester character varying(64),
    teacher_id bigint,
    created_at timestamp without time zone DEFAULT now(),
    deleted integer DEFAULT 0,
    pairwise_matrix text
);


--
-- Name: course_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.course_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: course_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.course_id_seq OWNED BY public.course.id;


--
-- Name: course_objective; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.course_objective (
    id bigint NOT NULL,
    objective_no character varying(32) NOT NULL,
    title character varying(256) NOT NULL,
    description text,
    dimension character varying(32) DEFAULT 'KNOWLEDGE'::character varying NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    weight numeric(5,4) DEFAULT 0.25,
    course_id bigint
);


--
-- Name: TABLE course_objective; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.course_objective IS '课程目标';


--
-- Name: COLUMN course_objective.dimension; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.course_objective.dimension IS '维度：KNOWLEDGE-知识 ABILITY-能力 QUALITY-素养';


--
-- Name: course_objective_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.course_objective_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: course_objective_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.course_objective_id_seq OWNED BY public.course_objective.id;


--
-- Name: evaluation_method; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.evaluation_method (
    id bigint NOT NULL,
    indicator_id bigint NOT NULL,
    method_name character varying(128) NOT NULL,
    weight numeric(5,4) DEFAULT 0 NOT NULL,
    data_source character varying(64) DEFAULT 'MANUAL'::character varying NOT NULL,
    full_score numeric(8,2) DEFAULT 100 NOT NULL,
    remark character varying(512),
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    eval_type character varying(32) DEFAULT 'SUMMATIVE'::character varying
);


--
-- Name: TABLE evaluation_method; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.evaluation_method IS '评价方式（关联指标点）';


--
-- Name: COLUMN evaluation_method.data_source; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.evaluation_method.data_source IS '数据源：MANUAL-手动 MAXKB-问答 GIT-代码提交 TEST-测验 JOURNAL-日志';


--
-- Name: evaluation_method_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.evaluation_method_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: evaluation_method_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.evaluation_method_id_seq OWNED BY public.evaluation_method.id;


--
-- Name: git_commit_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.git_commit_log (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    group_id bigint,
    repo_name character varying(256),
    commit_hash character varying(64),
    message text,
    additions integer DEFAULT 0 NOT NULL,
    deletions integer DEFAULT 0 NOT NULL,
    committed_at timestamp without time zone NOT NULL,
    synced_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE git_commit_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.git_commit_log IS 'Git提交记录（同步自Gitee/GitHub）';


--
-- Name: git_commit_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.git_commit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: git_commit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.git_commit_log_id_seq OWNED BY public.git_commit_log.id;


--
-- Name: graduation_indicator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.graduation_indicator (
    id bigint NOT NULL,
    objective_id bigint NOT NULL,
    indicator_no character varying(32) NOT NULL,
    title character varying(256) NOT NULL,
    description text,
    sort_order integer DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE graduation_indicator; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.graduation_indicator IS '毕业要求指标点（关联课程目标）';


--
-- Name: graduation_indicator_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.graduation_indicator_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: graduation_indicator_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.graduation_indicator_id_seq OWNED BY public.graduation_indicator.id;


--
-- Name: graduation_requirement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.graduation_requirement (
    id bigint NOT NULL,
    req_no character varying(32) NOT NULL,
    title character varying(256) NOT NULL,
    description text,
    sort_order integer DEFAULT 0,
    deleted integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: graduation_requirement_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.graduation_requirement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: graduation_requirement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.graduation_requirement_id_seq OWNED BY public.graduation_requirement.id;


--
-- Name: group_evaluation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.group_evaluation (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    evaluator_id bigint NOT NULL,
    dimension character varying(64) NOT NULL,
    score numeric(8,2) DEFAULT 0 NOT NULL,
    comment text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE group_evaluation; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.group_evaluation IS '小组成果评价';


--
-- Name: COLUMN group_evaluation.dimension; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.group_evaluation.dimension IS '评价维度：REQUIREMENT-需求 DESIGN-设计 CODE-代码 TEST-测试 DOC-文档 PRESENTATION-答辩';


--
-- Name: group_evaluation_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.group_evaluation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: group_evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.group_evaluation_id_seq OWNED BY public.group_evaluation.id;


--
-- Name: group_member; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.group_member (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    user_id bigint NOT NULL,
    role_code character varying(32) DEFAULT 'DEVELOPER'::character varying NOT NULL,
    join_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE group_member; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.group_member IS '小组成员';


--
-- Name: COLUMN group_member.role_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.group_member.role_code IS '角色：PM-项目经理 DEVELOPER-开发 TESTER-测试 DOC_ADMIN-文档管理员';


--
-- Name: group_member_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.group_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: group_member_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.group_member_id_seq OWNED BY public.group_member.id;


--
-- Name: improvement_suggestion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.improvement_suggestion (
    id bigint NOT NULL,
    objective_id bigint NOT NULL,
    indicator_id bigint,
    suggestion text NOT NULL,
    is_auto boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE improvement_suggestion; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.improvement_suggestion IS '改进建议';


--
-- Name: improvement_suggestion_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.improvement_suggestion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: improvement_suggestion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.improvement_suggestion_id_seq OWNED BY public.improvement_suggestion.id;


--
-- Name: improvement_task; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.improvement_task (
    id bigint NOT NULL,
    objective_id bigint,
    group_id bigint,
    current_achievement numeric(8,4),
    suggestion text,
    action text,
    assignee character varying(128),
    status character varying(32) DEFAULT 'PENDING'::character varying,
    due_date timestamp without time zone,
    deleted integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    title character varying(255),
    description text,
    priority character varying(20) DEFAULT 'LOW'::character varying,
    assignee_name character varying(100)
);


--
-- Name: improvement_task_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.improvement_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: improvement_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.improvement_task_id_seq OWNED BY public.improvement_task.id;


--
-- Name: knowledge_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.knowledge_point (
    id bigint NOT NULL,
    title character varying(256) NOT NULL,
    chapter character varying(128),
    content text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    attachments text
);


--
-- Name: TABLE knowledge_point; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.knowledge_point IS '知识点库';


--
-- Name: knowledge_point_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.knowledge_point_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: knowledge_point_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.knowledge_point_id_seq OWNED BY public.knowledge_point.id;


--
-- Name: notification; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification (
    id bigint NOT NULL,
    user_id bigint,
    title character varying(256),
    content text,
    is_read boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: notification_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notification_id_seq OWNED BY public.notification.id;


--
-- Name: objective_requirement_mapping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.objective_requirement_mapping (
    id bigint NOT NULL,
    objective_id bigint NOT NULL,
    requirement_id bigint NOT NULL,
    weight numeric(5,4) DEFAULT 0.25,
    support_level character varying(8) DEFAULT 'M'::character varying,
    deleted integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: objective_requirement_mapping_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.objective_requirement_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: objective_requirement_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.objective_requirement_mapping_id_seq OWNED BY public.objective_requirement_mapping.id;


--
-- Name: personal_score; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.personal_score (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    group_id bigint NOT NULL,
    group_total_score numeric(8,2) DEFAULT 0 NOT NULL,
    contribution_ratio numeric(5,4) DEFAULT 0 NOT NULL,
    final_score numeric(8,2) DEFAULT 0 NOT NULL,
    bonus_total numeric(5,2) DEFAULT 0 NOT NULL,
    calculated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE personal_score; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.personal_score IS '个人成绩（反搭便车核心表）';


--
-- Name: personal_score_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.personal_score_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: personal_score_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.personal_score_id_seq OWNED BY public.personal_score.id;


--
-- Name: position_criteria; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.position_criteria (
    id bigint NOT NULL,
    role_code character varying(32) NOT NULL,
    dimension character varying(64) NOT NULL,
    weight numeric(3,2) DEFAULT 0.25 NOT NULL,
    description character varying(256)
);


--
-- Name: position_criteria_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.position_criteria_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: position_criteria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.position_criteria_id_seq OWNED BY public.position_criteria.id;


--
-- Name: position_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.position_history (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    user_id bigint NOT NULL,
    old_role character varying(32),
    new_role character varying(32) NOT NULL,
    changed_by bigint,
    reason character varying(256),
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: position_history_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.position_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: position_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.position_history_id_seq OWNED BY public.position_history.id;


--
-- Name: project_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project_group (
    id bigint NOT NULL,
    group_name character varying(128) NOT NULL,
    teacher_id bigint,
    max_members integer DEFAULT 4 NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    invite_code character varying(8),
    course_id bigint
);


--
-- Name: TABLE project_group; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.project_group IS '项目小组';


--
-- Name: project_group_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.project_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: project_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.project_group_id_seq OWNED BY public.project_group.id;


--
-- Name: project_journal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project_journal (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    group_id bigint NOT NULL,
    content text NOT NULL,
    journal_date date NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE project_journal; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.project_journal IS '项目日志';


--
-- Name: project_journal_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.project_journal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: project_journal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.project_journal_id_seq OWNED BY public.project_journal.id;


--
-- Name: project_milestone; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project_milestone (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    title character varying(256) NOT NULL,
    cdio_phase character varying(32) NOT NULL,
    due_date date,
    is_done boolean DEFAULT false NOT NULL,
    finished_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE project_milestone; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.project_milestone IS '项目里程碑（CDIO四阶段节点）';


--
-- Name: project_milestone_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.project_milestone_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: project_milestone_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.project_milestone_id_seq OWNED BY public.project_milestone.id;


--
-- Name: project_task; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.project_task (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    milestone_id bigint,
    assignee_id bigint,
    title character varying(256) NOT NULL,
    status character varying(32) DEFAULT 'TODO'::character varying NOT NULL,
    priority smallint DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    due_date character varying(10)
);


--
-- Name: TABLE project_task; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.project_task IS '项目任务看板';


--
-- Name: project_task_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.project_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: project_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.project_task_id_seq OWNED BY public.project_task.id;


--
-- Name: qa_record; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qa_record (
    id bigint NOT NULL,
    user_id bigint,
    knowledge_id bigint,
    question text,
    answer text,
    is_resolved boolean DEFAULT false NOT NULL,
    question_type character varying(32),
    asked_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    session_id character varying(64),
    attachments text,
    content_type character varying(32) DEFAULT 'text'::character varying
);


--
-- Name: TABLE qa_record; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.qa_record IS 'MaxKB问答记录';


--
-- Name: qa_record_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.qa_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: qa_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.qa_record_id_seq OWNED BY public.qa_record.id;


--
-- Name: question_answer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.question_answer (
    id bigint NOT NULL,
    group_id bigint,
    ask_user_id bigint NOT NULL,
    answer_user_id bigint,
    title character varying(256) NOT NULL,
    question text NOT NULL,
    answer text,
    status character varying(32) DEFAULT 'PENDING'::character varying NOT NULL,
    attachments text,
    asked_at timestamp without time zone DEFAULT now() NOT NULL,
    answered_at timestamp without time zone,
    resolved_at timestamp without time zone,
    deleted integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: question_answer_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.question_answer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: question_answer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.question_answer_id_seq OWNED BY public.question_answer.id;


--
-- Name: quiz; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.quiz (
    id bigint NOT NULL,
    teacher_id bigint,
    group_id bigint,
    course_id bigint,
    title character varying(256) NOT NULL,
    questions text,
    status character varying(32) DEFAULT 'DRAFT'::character varying NOT NULL,
    total_score integer DEFAULT 100,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    published_at timestamp without time zone,
    deleted integer DEFAULT 0
);


--
-- Name: quiz_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.quiz_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: quiz_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.quiz_id_seq OWNED BY public.quiz.id;


--
-- Name: repo_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.repo_config (
    id bigint NOT NULL,
    group_id bigint,
    platform character varying(16) DEFAULT 'GITEE'::character varying NOT NULL,
    owner character varying(128),
    repo character varying(128),
    access_token character varying(256),
    branch character varying(64) DEFAULT 'master'::character varying,
    last_synced_at timestamp without time zone,
    enabled boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    deleted integer DEFAULT 0
);


--
-- Name: repo_config_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.repo_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: repo_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.repo_config_id_seq OWNED BY public.repo_config.id;


--
-- Name: requirement_change; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.requirement_change (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    title character varying(256) NOT NULL,
    description text,
    change_type character varying(32) DEFAULT 'ADD'::character varying NOT NULL,
    reason text,
    created_by bigint,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: requirement_change_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.requirement_change_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: requirement_change_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.requirement_change_id_seq OWNED BY public.requirement_change.id;


--
-- Name: role_evaluation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.role_evaluation (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    group_id bigint NOT NULL,
    evaluator_id bigint NOT NULL,
    role_code character varying(32) NOT NULL,
    dimension character varying(64) NOT NULL,
    score numeric(8,2) DEFAULT 0 NOT NULL,
    comment text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0
);


--
-- Name: TABLE role_evaluation; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.role_evaluation IS '角色专项评价';


--
-- Name: COLUMN role_evaluation.dimension; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.role_evaluation.dimension IS 'PM维度: PLAN/SCHEDULE/COORDINATION DEVELOPER维度: ARCHITECTURE/CODE_QUALITY/BUG_FIX TESTER维度: COVERAGE/BUG_FOUND DOC_ADMIN维度: STANDARD/TIMELINESS/COMPLETENESS';


--
-- Name: role_evaluation_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.role_evaluation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: role_evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.role_evaluation_id_seq OWNED BY public.role_evaluation.id;


--
-- Name: score_dispute; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.score_dispute (
    id bigint NOT NULL,
    user_id bigint,
    score_id bigint,
    score_type character varying(32),
    reason text,
    status character varying(32) DEFAULT 'PENDING'::character varying,
    resolved_by bigint,
    resolution text,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: score_dispute_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.score_dispute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: score_dispute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.score_dispute_id_seq OWNED BY public.score_dispute.id;


--
-- Name: self_test_record; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.self_test_record (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    score numeric(5,2) DEFAULT 0 NOT NULL,
    total numeric(5,2) DEFAULT 100 NOT NULL,
    taken_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    knowledge_id bigint,
    questions text,
    feedback text
);


--
-- Name: TABLE self_test_record; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.self_test_record IS '自主测验记录';


--
-- Name: self_test_record_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.self_test_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: self_test_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.self_test_record_id_seq OWNED BY public.self_test_record.id;


--
-- Name: sys_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_user (
    id bigint NOT NULL,
    username character varying(64) NOT NULL,
    password character varying(256) NOT NULL,
    real_name character varying(64),
    email character varying(128),
    phone character varying(20),
    role_code character varying(32) DEFAULT 'STUDENT'::character varying NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted integer DEFAULT 0,
    git_username character varying(128),
    git_email character varying(256)
);


--
-- Name: TABLE sys_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_user IS '用户表';


--
-- Name: COLUMN sys_user.role_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.role_code IS '角色：ADMIN-管理员 TEACHER-教师 STUDENT-学生';


--
-- Name: sys_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.sys_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.sys_user_id_seq OWNED BY public.sys_user.id;


--
-- Name: achievement_result id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.achievement_result ALTER COLUMN id SET DEFAULT nextval('public.achievement_result_id_seq'::regclass);


--
-- Name: audit_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_log ALTER COLUMN id SET DEFAULT nextval('public.audit_log_id_seq'::regclass);


--
-- Name: cdio_phase_mapping id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cdio_phase_mapping ALTER COLUMN id SET DEFAULT nextval('public.cdio_phase_mapping_id_seq'::regclass);


--
-- Name: contribution_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contribution_log ALTER COLUMN id SET DEFAULT nextval('public.contribution_log_id_seq'::regclass);


--
-- Name: course id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course ALTER COLUMN id SET DEFAULT nextval('public.course_id_seq'::regclass);


--
-- Name: course_objective id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_objective ALTER COLUMN id SET DEFAULT nextval('public.course_objective_id_seq'::regclass);


--
-- Name: evaluation_method id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.evaluation_method ALTER COLUMN id SET DEFAULT nextval('public.evaluation_method_id_seq'::regclass);


--
-- Name: git_commit_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.git_commit_log ALTER COLUMN id SET DEFAULT nextval('public.git_commit_log_id_seq'::regclass);


--
-- Name: graduation_indicator id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.graduation_indicator ALTER COLUMN id SET DEFAULT nextval('public.graduation_indicator_id_seq'::regclass);


--
-- Name: graduation_requirement id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.graduation_requirement ALTER COLUMN id SET DEFAULT nextval('public.graduation_requirement_id_seq'::regclass);


--
-- Name: group_evaluation id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_evaluation ALTER COLUMN id SET DEFAULT nextval('public.group_evaluation_id_seq'::regclass);


--
-- Name: group_member id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_member ALTER COLUMN id SET DEFAULT nextval('public.group_member_id_seq'::regclass);


--
-- Name: improvement_suggestion id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.improvement_suggestion ALTER COLUMN id SET DEFAULT nextval('public.improvement_suggestion_id_seq'::regclass);


--
-- Name: improvement_task id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.improvement_task ALTER COLUMN id SET DEFAULT nextval('public.improvement_task_id_seq'::regclass);


--
-- Name: knowledge_point id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.knowledge_point ALTER COLUMN id SET DEFAULT nextval('public.knowledge_point_id_seq'::regclass);


--
-- Name: notification id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification ALTER COLUMN id SET DEFAULT nextval('public.notification_id_seq'::regclass);


--
-- Name: objective_requirement_mapping id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.objective_requirement_mapping ALTER COLUMN id SET DEFAULT nextval('public.objective_requirement_mapping_id_seq'::regclass);


--
-- Name: personal_score id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_score ALTER COLUMN id SET DEFAULT nextval('public.personal_score_id_seq'::regclass);


--
-- Name: position_criteria id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_criteria ALTER COLUMN id SET DEFAULT nextval('public.position_criteria_id_seq'::regclass);


--
-- Name: position_history id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_history ALTER COLUMN id SET DEFAULT nextval('public.position_history_id_seq'::regclass);


--
-- Name: project_group id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_group ALTER COLUMN id SET DEFAULT nextval('public.project_group_id_seq'::regclass);


--
-- Name: project_journal id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_journal ALTER COLUMN id SET DEFAULT nextval('public.project_journal_id_seq'::regclass);


--
-- Name: project_milestone id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_milestone ALTER COLUMN id SET DEFAULT nextval('public.project_milestone_id_seq'::regclass);


--
-- Name: project_task id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_task ALTER COLUMN id SET DEFAULT nextval('public.project_task_id_seq'::regclass);


--
-- Name: qa_record id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qa_record ALTER COLUMN id SET DEFAULT nextval('public.qa_record_id_seq'::regclass);


--
-- Name: question_answer id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answer ALTER COLUMN id SET DEFAULT nextval('public.question_answer_id_seq'::regclass);


--
-- Name: quiz id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz ALTER COLUMN id SET DEFAULT nextval('public.quiz_id_seq'::regclass);


--
-- Name: repo_config id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.repo_config ALTER COLUMN id SET DEFAULT nextval('public.repo_config_id_seq'::regclass);


--
-- Name: requirement_change id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requirement_change ALTER COLUMN id SET DEFAULT nextval('public.requirement_change_id_seq'::regclass);


--
-- Name: role_evaluation id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role_evaluation ALTER COLUMN id SET DEFAULT nextval('public.role_evaluation_id_seq'::regclass);


--
-- Name: score_dispute id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_dispute ALTER COLUMN id SET DEFAULT nextval('public.score_dispute_id_seq'::regclass);


--
-- Name: self_test_record id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.self_test_record ALTER COLUMN id SET DEFAULT nextval('public.self_test_record_id_seq'::regclass);


--
-- Name: sys_user id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_user ALTER COLUMN id SET DEFAULT nextval('public.sys_user_id_seq'::regclass);


--
-- Name: achievement_result achievement_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.achievement_result
    ADD CONSTRAINT achievement_result_pkey PRIMARY KEY (id);


--
-- Name: audit_log audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);


--
-- Name: cdio_phase_mapping cdio_phase_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cdio_phase_mapping
    ADD CONSTRAINT cdio_phase_mapping_pkey PRIMARY KEY (id);


--
-- Name: contribution_log contribution_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contribution_log
    ADD CONSTRAINT contribution_log_pkey PRIMARY KEY (id);


--
-- Name: course_objective course_objective_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_objective
    ADD CONSTRAINT course_objective_pkey PRIMARY KEY (id);


--
-- Name: course course_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course
    ADD CONSTRAINT course_pkey PRIMARY KEY (id);


--
-- Name: evaluation_method evaluation_method_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.evaluation_method
    ADD CONSTRAINT evaluation_method_pkey PRIMARY KEY (id);


--
-- Name: git_commit_log git_commit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.git_commit_log
    ADD CONSTRAINT git_commit_log_pkey PRIMARY KEY (id);


--
-- Name: graduation_indicator graduation_indicator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.graduation_indicator
    ADD CONSTRAINT graduation_indicator_pkey PRIMARY KEY (id);


--
-- Name: graduation_requirement graduation_requirement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.graduation_requirement
    ADD CONSTRAINT graduation_requirement_pkey PRIMARY KEY (id);


--
-- Name: graduation_requirement graduation_requirement_req_no_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.graduation_requirement
    ADD CONSTRAINT graduation_requirement_req_no_key UNIQUE (req_no);


--
-- Name: group_evaluation group_evaluation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_evaluation
    ADD CONSTRAINT group_evaluation_pkey PRIMARY KEY (id);


--
-- Name: group_member group_member_group_id_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_member
    ADD CONSTRAINT group_member_group_id_user_id_key UNIQUE (group_id, user_id);


--
-- Name: group_member group_member_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_member
    ADD CONSTRAINT group_member_pkey PRIMARY KEY (id);


--
-- Name: improvement_suggestion improvement_suggestion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.improvement_suggestion
    ADD CONSTRAINT improvement_suggestion_pkey PRIMARY KEY (id);


--
-- Name: improvement_task improvement_task_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.improvement_task
    ADD CONSTRAINT improvement_task_pkey PRIMARY KEY (id);


--
-- Name: knowledge_point knowledge_point_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.knowledge_point
    ADD CONSTRAINT knowledge_point_pkey PRIMARY KEY (id);


--
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- Name: objective_requirement_mapping objective_requirement_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.objective_requirement_mapping
    ADD CONSTRAINT objective_requirement_mapping_pkey PRIMARY KEY (id);


--
-- Name: personal_score personal_score_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_score
    ADD CONSTRAINT personal_score_pkey PRIMARY KEY (id);


--
-- Name: personal_score personal_score_user_id_group_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_score
    ADD CONSTRAINT personal_score_user_id_group_id_key UNIQUE (user_id, group_id);


--
-- Name: position_criteria position_criteria_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_criteria
    ADD CONSTRAINT position_criteria_pkey PRIMARY KEY (id);


--
-- Name: position_history position_history_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_history
    ADD CONSTRAINT position_history_pkey PRIMARY KEY (id);


--
-- Name: project_group project_group_invite_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_group
    ADD CONSTRAINT project_group_invite_code_key UNIQUE (invite_code);


--
-- Name: project_group project_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_group
    ADD CONSTRAINT project_group_pkey PRIMARY KEY (id);


--
-- Name: project_journal project_journal_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_journal
    ADD CONSTRAINT project_journal_pkey PRIMARY KEY (id);


--
-- Name: project_milestone project_milestone_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_milestone
    ADD CONSTRAINT project_milestone_pkey PRIMARY KEY (id);


--
-- Name: project_task project_task_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_task
    ADD CONSTRAINT project_task_pkey PRIMARY KEY (id);


--
-- Name: qa_record qa_record_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qa_record
    ADD CONSTRAINT qa_record_pkey PRIMARY KEY (id);


--
-- Name: question_answer question_answer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answer
    ADD CONSTRAINT question_answer_pkey PRIMARY KEY (id);


--
-- Name: quiz quiz_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz
    ADD CONSTRAINT quiz_pkey PRIMARY KEY (id);


--
-- Name: repo_config repo_config_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.repo_config
    ADD CONSTRAINT repo_config_pkey PRIMARY KEY (id);


--
-- Name: requirement_change requirement_change_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requirement_change
    ADD CONSTRAINT requirement_change_pkey PRIMARY KEY (id);


--
-- Name: role_evaluation role_evaluation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role_evaluation
    ADD CONSTRAINT role_evaluation_pkey PRIMARY KEY (id);


--
-- Name: score_dispute score_dispute_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_dispute
    ADD CONSTRAINT score_dispute_pkey PRIMARY KEY (id);


--
-- Name: self_test_record self_test_record_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.self_test_record
    ADD CONSTRAINT self_test_record_pkey PRIMARY KEY (id);


--
-- Name: sys_user sys_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_pkey PRIMARY KEY (id);


--
-- Name: ach_unique; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX ach_unique ON public.achievement_result USING btree (objective_id, group_id, COALESCE(calc_round, 1));


--
-- Name: course_objective_no_active; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX course_objective_no_active ON public.course_objective USING btree (objective_no) WHERE (deleted = 0);


--
-- Name: graduation_indicator_no_active; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX graduation_indicator_no_active ON public.graduation_indicator USING btree (indicator_no) WHERE (deleted = 0);


--
-- Name: group_member_unique; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX group_member_unique ON public.group_member USING btree (group_id, user_id) WHERE (deleted = 0);


--
-- Name: idx_achievement; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_achievement ON public.achievement_result USING btree (objective_id, group_id);


--
-- Name: idx_eval_indicator; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_eval_indicator ON public.evaluation_method USING btree (indicator_id);


--
-- Name: idx_git_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_git_user ON public.git_commit_log USING btree (user_id, committed_at);


--
-- Name: idx_indicator_obj; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_indicator_obj ON public.graduation_indicator USING btree (objective_id);


--
-- Name: idx_member_group; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_member_group ON public.group_member USING btree (group_id);


--
-- Name: idx_personal_score; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_personal_score ON public.personal_score USING btree (user_id, group_id);


--
-- Name: idx_qa_session; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qa_session ON public.qa_record USING btree (session_id, asked_at);


--
-- Name: idx_qa_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qa_user ON public.qa_record USING btree (user_id, asked_at);


--
-- Name: idx_task_assignee; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_assignee ON public.project_task USING btree (assignee_id, status);


--
-- Name: idx_user_role; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_role ON public.sys_user USING btree (role_code);


--
-- Name: sys_user_username_active; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX sys_user_username_active ON public.sys_user USING btree (username) WHERE (deleted = 0);


--
-- Name: achievement_result achievement_result_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.achievement_result
    ADD CONSTRAINT achievement_result_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: achievement_result achievement_result_objective_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.achievement_result
    ADD CONSTRAINT achievement_result_objective_id_fkey FOREIGN KEY (objective_id) REFERENCES public.course_objective(id);


--
-- Name: cdio_phase_mapping cdio_phase_mapping_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cdio_phase_mapping
    ADD CONSTRAINT cdio_phase_mapping_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.course(id);


--
-- Name: cdio_phase_mapping cdio_phase_mapping_indicator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cdio_phase_mapping
    ADD CONSTRAINT cdio_phase_mapping_indicator_id_fkey FOREIGN KEY (indicator_id) REFERENCES public.graduation_indicator(id);


--
-- Name: contribution_log contribution_log_approved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contribution_log
    ADD CONSTRAINT contribution_log_approved_by_fkey FOREIGN KEY (approved_by) REFERENCES public.sys_user(id);


--
-- Name: contribution_log contribution_log_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contribution_log
    ADD CONSTRAINT contribution_log_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: contribution_log contribution_log_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contribution_log
    ADD CONSTRAINT contribution_log_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: course_objective course_objective_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_objective
    ADD CONSTRAINT course_objective_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.course(id);


--
-- Name: course course_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course
    ADD CONSTRAINT course_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.sys_user(id);


--
-- Name: evaluation_method evaluation_method_indicator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.evaluation_method
    ADD CONSTRAINT evaluation_method_indicator_id_fkey FOREIGN KEY (indicator_id) REFERENCES public.graduation_indicator(id);


--
-- Name: git_commit_log git_commit_log_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.git_commit_log
    ADD CONSTRAINT git_commit_log_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: git_commit_log git_commit_log_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.git_commit_log
    ADD CONSTRAINT git_commit_log_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: graduation_indicator graduation_indicator_objective_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.graduation_indicator
    ADD CONSTRAINT graduation_indicator_objective_id_fkey FOREIGN KEY (objective_id) REFERENCES public.course_objective(id);


--
-- Name: group_evaluation group_evaluation_evaluator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_evaluation
    ADD CONSTRAINT group_evaluation_evaluator_id_fkey FOREIGN KEY (evaluator_id) REFERENCES public.sys_user(id);


--
-- Name: group_evaluation group_evaluation_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_evaluation
    ADD CONSTRAINT group_evaluation_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: group_member group_member_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_member
    ADD CONSTRAINT group_member_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: group_member group_member_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_member
    ADD CONSTRAINT group_member_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: improvement_suggestion improvement_suggestion_indicator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.improvement_suggestion
    ADD CONSTRAINT improvement_suggestion_indicator_id_fkey FOREIGN KEY (indicator_id) REFERENCES public.graduation_indicator(id);


--
-- Name: improvement_suggestion improvement_suggestion_objective_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.improvement_suggestion
    ADD CONSTRAINT improvement_suggestion_objective_id_fkey FOREIGN KEY (objective_id) REFERENCES public.course_objective(id);


--
-- Name: personal_score personal_score_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_score
    ADD CONSTRAINT personal_score_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: personal_score personal_score_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_score
    ADD CONSTRAINT personal_score_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: position_history position_history_changed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_history
    ADD CONSTRAINT position_history_changed_by_fkey FOREIGN KEY (changed_by) REFERENCES public.sys_user(id);


--
-- Name: position_history position_history_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_history
    ADD CONSTRAINT position_history_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: position_history position_history_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_history
    ADD CONSTRAINT position_history_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: project_group project_group_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_group
    ADD CONSTRAINT project_group_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.course(id);


--
-- Name: project_group project_group_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_group
    ADD CONSTRAINT project_group_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.sys_user(id);


--
-- Name: project_journal project_journal_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_journal
    ADD CONSTRAINT project_journal_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: project_journal project_journal_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_journal
    ADD CONSTRAINT project_journal_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: project_milestone project_milestone_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_milestone
    ADD CONSTRAINT project_milestone_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: project_task project_task_assignee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_task
    ADD CONSTRAINT project_task_assignee_id_fkey FOREIGN KEY (assignee_id) REFERENCES public.sys_user(id);


--
-- Name: project_task project_task_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_task
    ADD CONSTRAINT project_task_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: project_task project_task_milestone_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.project_task
    ADD CONSTRAINT project_task_milestone_id_fkey FOREIGN KEY (milestone_id) REFERENCES public.project_milestone(id);


--
-- Name: qa_record qa_record_knowledge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qa_record
    ADD CONSTRAINT qa_record_knowledge_id_fkey FOREIGN KEY (knowledge_id) REFERENCES public.knowledge_point(id);


--
-- Name: qa_record qa_record_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qa_record
    ADD CONSTRAINT qa_record_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: question_answer question_answer_answer_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answer
    ADD CONSTRAINT question_answer_answer_user_id_fkey FOREIGN KEY (answer_user_id) REFERENCES public.sys_user(id);


--
-- Name: question_answer question_answer_ask_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answer
    ADD CONSTRAINT question_answer_ask_user_id_fkey FOREIGN KEY (ask_user_id) REFERENCES public.sys_user(id);


--
-- Name: question_answer question_answer_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answer
    ADD CONSTRAINT question_answer_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: quiz quiz_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz
    ADD CONSTRAINT quiz_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: quiz quiz_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz
    ADD CONSTRAINT quiz_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.sys_user(id);


--
-- Name: repo_config repo_config_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.repo_config
    ADD CONSTRAINT repo_config_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: requirement_change requirement_change_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requirement_change
    ADD CONSTRAINT requirement_change_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.sys_user(id);


--
-- Name: requirement_change requirement_change_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.requirement_change
    ADD CONSTRAINT requirement_change_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: role_evaluation role_evaluation_evaluator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role_evaluation
    ADD CONSTRAINT role_evaluation_evaluator_id_fkey FOREIGN KEY (evaluator_id) REFERENCES public.sys_user(id);


--
-- Name: role_evaluation role_evaluation_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role_evaluation
    ADD CONSTRAINT role_evaluation_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.project_group(id);


--
-- Name: role_evaluation role_evaluation_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role_evaluation
    ADD CONSTRAINT role_evaluation_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- Name: self_test_record self_test_record_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.self_test_record
    ADD CONSTRAINT self_test_record_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.sys_user(id);


--
-- PostgreSQL database dump complete
--

\unrestrict e4LdYfbK7b3Os9QYrcDNqEp1JfY3mFMxafpsAR3brOcuBhMqKFmFsbxD1Dc3RYV

