--
-- PostgreSQL database dump
--

\restrict FXsTBUfUhBJibp5ghmjqUtP5ShyrJGgTQ0lzQdVEEI7hZF5sZYKvFUn28kgSIcy

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

ALTER TABLE IF EXISTS ONLY public.self_test_record DROP CONSTRAINT IF EXISTS self_test_record_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.role_evaluation DROP CONSTRAINT IF EXISTS role_evaluation_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.role_evaluation DROP CONSTRAINT IF EXISTS role_evaluation_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.role_evaluation DROP CONSTRAINT IF EXISTS role_evaluation_evaluator_id_fkey;
ALTER TABLE IF EXISTS ONLY public.requirement_change DROP CONSTRAINT IF EXISTS requirement_change_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.requirement_change DROP CONSTRAINT IF EXISTS requirement_change_created_by_fkey;
ALTER TABLE IF EXISTS ONLY public.repo_config DROP CONSTRAINT IF EXISTS repo_config_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.quiz DROP CONSTRAINT IF EXISTS quiz_teacher_id_fkey;
ALTER TABLE IF EXISTS ONLY public.quiz DROP CONSTRAINT IF EXISTS quiz_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.question_answer DROP CONSTRAINT IF EXISTS question_answer_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.question_answer DROP CONSTRAINT IF EXISTS question_answer_ask_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.question_answer DROP CONSTRAINT IF EXISTS question_answer_answer_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.qa_record DROP CONSTRAINT IF EXISTS qa_record_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.qa_record DROP CONSTRAINT IF EXISTS qa_record_knowledge_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_task DROP CONSTRAINT IF EXISTS project_task_milestone_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_task DROP CONSTRAINT IF EXISTS project_task_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_task DROP CONSTRAINT IF EXISTS project_task_assignee_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_milestone DROP CONSTRAINT IF EXISTS project_milestone_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_journal DROP CONSTRAINT IF EXISTS project_journal_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_journal DROP CONSTRAINT IF EXISTS project_journal_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_group DROP CONSTRAINT IF EXISTS project_group_teacher_id_fkey;
ALTER TABLE IF EXISTS ONLY public.project_group DROP CONSTRAINT IF EXISTS project_group_course_id_fkey;
ALTER TABLE IF EXISTS ONLY public.position_history DROP CONSTRAINT IF EXISTS position_history_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.position_history DROP CONSTRAINT IF EXISTS position_history_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.position_history DROP CONSTRAINT IF EXISTS position_history_changed_by_fkey;
ALTER TABLE IF EXISTS ONLY public.personal_score DROP CONSTRAINT IF EXISTS personal_score_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.personal_score DROP CONSTRAINT IF EXISTS personal_score_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.improvement_suggestion DROP CONSTRAINT IF EXISTS improvement_suggestion_objective_id_fkey;
ALTER TABLE IF EXISTS ONLY public.improvement_suggestion DROP CONSTRAINT IF EXISTS improvement_suggestion_indicator_id_fkey;
ALTER TABLE IF EXISTS ONLY public.group_member DROP CONSTRAINT IF EXISTS group_member_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.group_member DROP CONSTRAINT IF EXISTS group_member_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.group_evaluation DROP CONSTRAINT IF EXISTS group_evaluation_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.group_evaluation DROP CONSTRAINT IF EXISTS group_evaluation_evaluator_id_fkey;
ALTER TABLE IF EXISTS ONLY public.graduation_indicator DROP CONSTRAINT IF EXISTS graduation_indicator_objective_id_fkey;
ALTER TABLE IF EXISTS ONLY public.git_commit_log DROP CONSTRAINT IF EXISTS git_commit_log_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.git_commit_log DROP CONSTRAINT IF EXISTS git_commit_log_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.evaluation_method DROP CONSTRAINT IF EXISTS evaluation_method_indicator_id_fkey;
ALTER TABLE IF EXISTS ONLY public.course DROP CONSTRAINT IF EXISTS course_teacher_id_fkey;
ALTER TABLE IF EXISTS ONLY public.course_objective DROP CONSTRAINT IF EXISTS course_objective_course_id_fkey;
ALTER TABLE IF EXISTS ONLY public.contribution_log DROP CONSTRAINT IF EXISTS contribution_log_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.contribution_log DROP CONSTRAINT IF EXISTS contribution_log_group_id_fkey;
ALTER TABLE IF EXISTS ONLY public.contribution_log DROP CONSTRAINT IF EXISTS contribution_log_approved_by_fkey;
ALTER TABLE IF EXISTS ONLY public.cdio_phase_mapping DROP CONSTRAINT IF EXISTS cdio_phase_mapping_indicator_id_fkey;
ALTER TABLE IF EXISTS ONLY public.cdio_phase_mapping DROP CONSTRAINT IF EXISTS cdio_phase_mapping_course_id_fkey;
ALTER TABLE IF EXISTS ONLY public.achievement_result DROP CONSTRAINT IF EXISTS achievement_result_objective_id_fkey;
ALTER TABLE IF EXISTS ONLY public.achievement_result DROP CONSTRAINT IF EXISTS achievement_result_group_id_fkey;
DROP INDEX IF EXISTS public.sys_user_username_active;
DROP INDEX IF EXISTS public.idx_user_role;
DROP INDEX IF EXISTS public.idx_task_assignee;
DROP INDEX IF EXISTS public.idx_qa_user;
DROP INDEX IF EXISTS public.idx_qa_session;
DROP INDEX IF EXISTS public.idx_personal_score;
DROP INDEX IF EXISTS public.idx_member_group;
DROP INDEX IF EXISTS public.idx_indicator_obj;
DROP INDEX IF EXISTS public.idx_git_user;
DROP INDEX IF EXISTS public.idx_eval_indicator;
DROP INDEX IF EXISTS public.idx_achievement;
DROP INDEX IF EXISTS public.group_member_unique;
DROP INDEX IF EXISTS public.graduation_indicator_no_active;
DROP INDEX IF EXISTS public.course_objective_no_active;
DROP INDEX IF EXISTS public.ach_unique;
ALTER TABLE IF EXISTS ONLY public.sys_user DROP CONSTRAINT IF EXISTS sys_user_pkey;
ALTER TABLE IF EXISTS ONLY public.self_test_record DROP CONSTRAINT IF EXISTS self_test_record_pkey;
ALTER TABLE IF EXISTS ONLY public.score_dispute DROP CONSTRAINT IF EXISTS score_dispute_pkey;
ALTER TABLE IF EXISTS ONLY public.role_evaluation DROP CONSTRAINT IF EXISTS role_evaluation_pkey;
ALTER TABLE IF EXISTS ONLY public.requirement_change DROP CONSTRAINT IF EXISTS requirement_change_pkey;
ALTER TABLE IF EXISTS ONLY public.repo_config DROP CONSTRAINT IF EXISTS repo_config_pkey;
ALTER TABLE IF EXISTS ONLY public.quiz DROP CONSTRAINT IF EXISTS quiz_pkey;
ALTER TABLE IF EXISTS ONLY public.question_answer DROP CONSTRAINT IF EXISTS question_answer_pkey;
ALTER TABLE IF EXISTS ONLY public.qa_record DROP CONSTRAINT IF EXISTS qa_record_pkey;
ALTER TABLE IF EXISTS ONLY public.project_task DROP CONSTRAINT IF EXISTS project_task_pkey;
ALTER TABLE IF EXISTS ONLY public.project_milestone DROP CONSTRAINT IF EXISTS project_milestone_pkey;
ALTER TABLE IF EXISTS ONLY public.project_journal DROP CONSTRAINT IF EXISTS project_journal_pkey;
ALTER TABLE IF EXISTS ONLY public.project_group DROP CONSTRAINT IF EXISTS project_group_pkey;
ALTER TABLE IF EXISTS ONLY public.project_group DROP CONSTRAINT IF EXISTS project_group_invite_code_key;
ALTER TABLE IF EXISTS ONLY public.position_history DROP CONSTRAINT IF EXISTS position_history_pkey;
ALTER TABLE IF EXISTS ONLY public.position_criteria DROP CONSTRAINT IF EXISTS position_criteria_pkey;
ALTER TABLE IF EXISTS ONLY public.personal_score DROP CONSTRAINT IF EXISTS personal_score_user_id_group_id_key;
ALTER TABLE IF EXISTS ONLY public.personal_score DROP CONSTRAINT IF EXISTS personal_score_pkey;
ALTER TABLE IF EXISTS ONLY public.objective_requirement_mapping DROP CONSTRAINT IF EXISTS objective_requirement_mapping_pkey;
ALTER TABLE IF EXISTS ONLY public.notification DROP CONSTRAINT IF EXISTS notification_pkey;
ALTER TABLE IF EXISTS ONLY public.knowledge_point DROP CONSTRAINT IF EXISTS knowledge_point_pkey;
ALTER TABLE IF EXISTS ONLY public.improvement_task DROP CONSTRAINT IF EXISTS improvement_task_pkey;
ALTER TABLE IF EXISTS ONLY public.improvement_suggestion DROP CONSTRAINT IF EXISTS improvement_suggestion_pkey;
ALTER TABLE IF EXISTS ONLY public.group_member DROP CONSTRAINT IF EXISTS group_member_pkey;
ALTER TABLE IF EXISTS ONLY public.group_member DROP CONSTRAINT IF EXISTS group_member_group_id_user_id_key;
ALTER TABLE IF EXISTS ONLY public.group_evaluation DROP CONSTRAINT IF EXISTS group_evaluation_pkey;
ALTER TABLE IF EXISTS ONLY public.graduation_requirement DROP CONSTRAINT IF EXISTS graduation_requirement_req_no_key;
ALTER TABLE IF EXISTS ONLY public.graduation_requirement DROP CONSTRAINT IF EXISTS graduation_requirement_pkey;
ALTER TABLE IF EXISTS ONLY public.graduation_indicator DROP CONSTRAINT IF EXISTS graduation_indicator_pkey;
ALTER TABLE IF EXISTS ONLY public.git_commit_log DROP CONSTRAINT IF EXISTS git_commit_log_pkey;
ALTER TABLE IF EXISTS ONLY public.evaluation_method DROP CONSTRAINT IF EXISTS evaluation_method_pkey;
ALTER TABLE IF EXISTS ONLY public.course DROP CONSTRAINT IF EXISTS course_pkey;
ALTER TABLE IF EXISTS ONLY public.course_objective DROP CONSTRAINT IF EXISTS course_objective_pkey;
ALTER TABLE IF EXISTS ONLY public.contribution_log DROP CONSTRAINT IF EXISTS contribution_log_pkey;
ALTER TABLE IF EXISTS ONLY public.cdio_phase_mapping DROP CONSTRAINT IF EXISTS cdio_phase_mapping_pkey;
ALTER TABLE IF EXISTS ONLY public.audit_log DROP CONSTRAINT IF EXISTS audit_log_pkey;
ALTER TABLE IF EXISTS ONLY public.achievement_result DROP CONSTRAINT IF EXISTS achievement_result_pkey;
ALTER TABLE IF EXISTS public.sys_user ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.self_test_record ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.score_dispute ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.role_evaluation ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.requirement_change ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.repo_config ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.quiz ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.question_answer ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.qa_record ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.project_task ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.project_milestone ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.project_journal ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.project_group ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.position_history ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.position_criteria ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.personal_score ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.objective_requirement_mapping ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.notification ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.knowledge_point ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.improvement_task ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.improvement_suggestion ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.group_member ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.group_evaluation ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.graduation_requirement ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.graduation_indicator ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.git_commit_log ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.evaluation_method ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.course_objective ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.course ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.contribution_log ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.cdio_phase_mapping ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.audit_log ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.achievement_result ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.sys_user_id_seq;
DROP TABLE IF EXISTS public.sys_user;
DROP SEQUENCE IF EXISTS public.self_test_record_id_seq;
DROP TABLE IF EXISTS public.self_test_record;
DROP SEQUENCE IF EXISTS public.score_dispute_id_seq;
DROP TABLE IF EXISTS public.score_dispute;
DROP SEQUENCE IF EXISTS public.role_evaluation_id_seq;
DROP TABLE IF EXISTS public.role_evaluation;
DROP SEQUENCE IF EXISTS public.requirement_change_id_seq;
DROP TABLE IF EXISTS public.requirement_change;
DROP SEQUENCE IF EXISTS public.repo_config_id_seq;
DROP TABLE IF EXISTS public.repo_config;
DROP SEQUENCE IF EXISTS public.quiz_id_seq;
DROP TABLE IF EXISTS public.quiz;
DROP SEQUENCE IF EXISTS public.question_answer_id_seq;
DROP TABLE IF EXISTS public.question_answer;
DROP SEQUENCE IF EXISTS public.qa_record_id_seq;
DROP TABLE IF EXISTS public.qa_record;
DROP SEQUENCE IF EXISTS public.project_task_id_seq;
DROP TABLE IF EXISTS public.project_task;
DROP SEQUENCE IF EXISTS public.project_milestone_id_seq;
DROP TABLE IF EXISTS public.project_milestone;
DROP SEQUENCE IF EXISTS public.project_journal_id_seq;
DROP TABLE IF EXISTS public.project_journal;
DROP SEQUENCE IF EXISTS public.project_group_id_seq;
DROP TABLE IF EXISTS public.project_group;
DROP SEQUENCE IF EXISTS public.position_history_id_seq;
DROP TABLE IF EXISTS public.position_history;
DROP SEQUENCE IF EXISTS public.position_criteria_id_seq;
DROP TABLE IF EXISTS public.position_criteria;
DROP SEQUENCE IF EXISTS public.personal_score_id_seq;
DROP TABLE IF EXISTS public.personal_score;
DROP SEQUENCE IF EXISTS public.objective_requirement_mapping_id_seq;
DROP TABLE IF EXISTS public.objective_requirement_mapping;
DROP SEQUENCE IF EXISTS public.notification_id_seq;
DROP TABLE IF EXISTS public.notification;
DROP SEQUENCE IF EXISTS public.knowledge_point_id_seq;
DROP TABLE IF EXISTS public.knowledge_point;
DROP SEQUENCE IF EXISTS public.improvement_task_id_seq;
DROP TABLE IF EXISTS public.improvement_task;
DROP SEQUENCE IF EXISTS public.improvement_suggestion_id_seq;
DROP TABLE IF EXISTS public.improvement_suggestion;
DROP SEQUENCE IF EXISTS public.group_member_id_seq;
DROP TABLE IF EXISTS public.group_member;
DROP SEQUENCE IF EXISTS public.group_evaluation_id_seq;
DROP TABLE IF EXISTS public.group_evaluation;
DROP SEQUENCE IF EXISTS public.graduation_requirement_id_seq;
DROP TABLE IF EXISTS public.graduation_requirement;
DROP SEQUENCE IF EXISTS public.graduation_indicator_id_seq;
DROP TABLE IF EXISTS public.graduation_indicator;
DROP SEQUENCE IF EXISTS public.git_commit_log_id_seq;
DROP TABLE IF EXISTS public.git_commit_log;
DROP SEQUENCE IF EXISTS public.evaluation_method_id_seq;
DROP TABLE IF EXISTS public.evaluation_method;
DROP SEQUENCE IF EXISTS public.course_objective_id_seq;
DROP TABLE IF EXISTS public.course_objective;
DROP SEQUENCE IF EXISTS public.course_id_seq;
DROP TABLE IF EXISTS public.course;
DROP SEQUENCE IF EXISTS public.contribution_log_id_seq;
DROP TABLE IF EXISTS public.contribution_log;
DROP SEQUENCE IF EXISTS public.cdio_phase_mapping_id_seq;
DROP TABLE IF EXISTS public.cdio_phase_mapping;
DROP SEQUENCE IF EXISTS public.audit_log_id_seq;
DROP TABLE IF EXISTS public.audit_log;
DROP SEQUENCE IF EXISTS public.achievement_result_id_seq;
DROP TABLE IF EXISTS public.achievement_result;
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
-- Data for Name: achievement_result; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.achievement_result (id, objective_id, group_id, achievement_value, dimension, calculated_at, deleted, calc_round) FROM stdin;
480	88	6	0.3296	KNOWLEDGE	2026-07-07 23:13:27.621841	0	1
481	89	6	0.6562	ABILITY	2026-07-07 23:13:27.628437	0	1
482	90	6	0.2266	QUALITY	2026-07-07 23:13:27.633569	0	1
483	91	6	0.6334	ABILITY	2026-07-07 23:13:27.640433	0	1
484	88	7	0.2208	KNOWLEDGE	2026-07-07 23:13:27.662251	0	1
485	89	7	0.5398	ABILITY	2026-07-07 23:13:27.666588	0	1
486	90	7	0.1514	QUALITY	2026-07-07 23:13:27.670754	0	1
487	91	7	0.5861	ABILITY	2026-07-07 23:13:27.675814	0	1
488	88	6	0.3296	KNOWLEDGE	2026-07-07 23:14:58.563511	0	2
489	89	6	0.6562	ABILITY	2026-07-07 23:14:58.570625	0	2
490	90	6	0.2266	QUALITY	2026-07-07 23:14:58.575404	0	2
491	91	6	0.6334	ABILITY	2026-07-07 23:14:58.584603	0	2
\.


--
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_log (id, user_id, username, action, target, ip, created_at) FROM stdin;
665	155	teacher1	LOGIN	用户登录	172.18.0.1	2026-07-07 23:13:27.558323
666	155	teacher1	LOGIN	用户登录	172.18.0.1	2026-07-07 23:13:44.903525
667	155	teacher1	LOGIN	用户登录	172.18.0.1	2026-07-07 23:14:33.15079
668	154	admin	LOGIN	用户登录	172.18.0.1	2026-07-07 23:14:52.201766
669	155	teacher1	LOGIN	用户登录	172.18.0.1	2026-07-08 02:45:28.512536
670	155	teacher1	LOGIN	用户登录	172.18.0.1	2026-07-08 02:45:55.859304
671	154	admin	LOGIN	用户登录	172.18.0.1	2026-07-10 09:51:12.334016
672	154	admin	LOGIN	用户登录	172.18.0.1	2026-07-10 09:55:58.952851
\.


--
-- Data for Name: cdio_phase_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cdio_phase_mapping (id, indicator_id, cdio_phase, sort_order, created_at, deleted, course_id) FROM stdin;
5	6	CONCEIVE	1	2026-07-07 23:13:08.545261	0	5
6	7	CONCEIVE	2	2026-07-07 23:13:08.545261	0	5
7	8	DESIGN	1	2026-07-07 23:13:08.545261	0	5
8	9	DESIGN	2	2026-07-07 23:13:08.545261	0	5
9	10	DESIGN	3	2026-07-07 23:13:08.545261	0	5
10	11	IMPLEMENT	1	2026-07-07 23:13:08.545261	0	5
11	12	IMPLEMENT	2	2026-07-07 23:13:08.545261	0	5
12	13	OPERATE	1	2026-07-07 23:13:08.545261	0	5
13	14	OPERATE	2	2026-07-07 23:13:08.545261	0	5
\.


--
-- Data for Name: contribution_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.contribution_log (id, user_id, group_id, description, bonus_score, approved_by, created_at, deleted, approved) FROM stdin;
61	156	6	主动承担架构设计和技术难点攻关	5.00	155	2026-05-24 07:13:07.395643	0	t
62	159	6	额外完成部署文档和运维指南	3.00	155	2026-06-08 07:13:07.395643	0	t
63	158	6	帮助组员解决测试环境问题	2.00	155	2026-06-16 07:13:07.395643	0	t
64	156	7	跨组协助搭建基础架构	4.00	\N	2026-05-19 07:13:07.395643	0	f
65	158	7	超额完成用户手册编写	2.00	155	2026-06-20 07:13:07.395643	0	t
\.


--
-- Data for Name: course; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.course (id, course_name, semester, teacher_id, created_at, deleted, pairwise_matrix) FROM stdin;
5	软件工程	2025-2026学年春季学期	155	2026-07-07 23:13:08.545261	0	\N
\.


--
-- Data for Name: course_objective; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.course_objective (id, objective_no, title, description, dimension, sort_order, created_at, updated_at, deleted, weight, course_id) FROM stdin;
88	CO1	软件工程基本概念与原理	理解软件生命周期、开发模型、需求工程、设计原则等基本概念	KNOWLEDGE	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	0.2500	5
89	CO2	面向对象系统分析与设计	运用UML进行需求建模和系统设计,掌握面向对象分析设计方法	ABILITY	2	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	0.3000	5
90	CO3	团队协作与项目管理	在团队中有效沟通协作,运用项目管理工具推进项目进度	QUALITY	3	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	0.2000	5
91	CO4	软件测试与质量保证	设计测试用例,使用自动化测试工具进行软件测试和质量评估	ABILITY	4	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	0.2500	5
\.


--
-- Data for Name: evaluation_method; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.evaluation_method (id, indicator_id, method_name, weight, data_source, full_score, remark, created_at, updated_at, deleted, eval_type) FROM stdin;
13	6	课堂测验	0.5000	group_eval	100.00	随堂测验成绩	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
14	6	课后作业	0.5000	journal	100.00	作业提交评分	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
15	8	需求分析报告	0.6000	group_eval	100.00	小组需求文档评审	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
16	8	UML建模作业	0.4000	journal	100.00	个人建模练习	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
17	9	系统设计评审	0.5000	group_eval	100.00	设计文档评审	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
18	9	代码审查	0.5000	git	100.00	代码提交质量	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
19	10	技术文档评分	0.5000	group_eval	100.00	文档规范性评审	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
20	10	Git提交活跃度	0.5000	git	100.00	提交频率与质量	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
21	11	角色评价	1.0000	role_eval	100.00	岗位职责评分	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
22	12	任务完成度	0.6000	git	100.00	Git任务完成	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
23	12	项目日志	0.4000	journal	100.00	日志完整度	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
24	13	测试用例设计	0.5000	group_eval	100.00	测试文档评审	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
25	13	自测成绩	0.5000	self_test	100.00	自测正确率	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
26	14	自动化测试	1.0000	git	100.00	测试代码提交	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	SUMMATIVE
\.


--
-- Data for Name: git_commit_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.git_commit_log (id, user_id, group_id, repo_name, commit_hash, message, additions, deletions, committed_at, synced_at, deleted) FROM stdin;
126	156	6	grade-mgmt	a1b2c3d4	feat: 完成需求分析文档初版	120	30	2026-05-11 07:13:07.395643	2026-05-11 07:13:07.395643	0
127	156	6	grade-mgmt	a1b2c3d5	feat: 添加学生信息管理模块	200	45	2026-05-19 07:13:07.395643	2026-05-19 07:13:07.395643	0
128	156	6	grade-mgmt	a1b2c3d6	fix: 修复成绩查询分页bug	15	8	2026-05-27 07:13:07.395643	2026-05-27 07:13:07.395643	0
129	156	6	grade-mgmt	a1b2c3d7	feat: 添加成绩统计图表	160	25	2026-06-08 07:13:07.395643	2026-06-08 07:13:07.395643	0
130	156	6	grade-mgmt	a1b2c3d8	refactor: 优化数据库查询性能	50	35	2026-06-20 07:13:07.395643	2026-06-20 07:13:07.395643	0
131	157	6	grade-mgmt	b2c3d4e5	feat: 实现成绩录入接口	180	20	2026-05-21 07:13:07.395643	2026-05-21 07:13:07.395643	0
132	157	6	grade-mgmt	b2c3d4e6	refactor: 优化DTO转换逻辑	45	28	2026-05-31 07:13:07.395643	2026-05-31 07:13:07.395643	0
133	157	6	grade-mgmt	b2c3d4e7	feat: 前端成绩管理页面整合	85	15	2026-06-13 07:13:07.395643	2026-06-13 07:13:07.395643	0
134	157	6	grade-mgmt	b2c3d4e8	fix: 修正小数点精度问题	8	3	2026-06-24 07:13:07.395643	2026-06-24 07:13:07.395643	0
135	158	6	grade-mgmt	c3d4e5f6	test: 成绩管理模块单元测试	100	15	2026-05-29 07:13:07.395643	2026-05-29 07:13:07.395643	0
136	158	6	grade-mgmt	c3d4e5f7	test: 添加集成测试用例	80	10	2026-06-10 07:13:07.395643	2026-06-10 07:13:07.395643	0
137	158	6	grade-mgmt	c3d4e5f8	test: 补充边界条件测试	45	5	2026-06-22 07:13:07.395643	2026-06-22 07:13:07.395643	0
138	156	7	exam-platform	d4e5f6g7	feat: 搭建考试平台基础架构	250	60	2026-05-17 07:13:07.395643	2026-05-17 07:13:07.395643	0
139	156	7	exam-platform	d4e5f6g8	feat: 实现题库管理功能	180	35	2026-05-29 07:13:07.395643	2026-05-29 07:13:07.395643	0
140	156	7	exam-platform	d4e5f6g9	feat: 添加考试结果统计	120	20	2026-06-18 07:13:07.395643	2026-06-18 07:13:07.395643	0
141	157	7	exam-platform	e5f6g7h8	feat: 完成考试安排模块	140	25	2026-05-24 07:13:07.395643	2026-05-24 07:13:07.395643	0
142	157	7	exam-platform	e5f6g7h9	fix: 修复自动阅卷分数计算错误	30	12	2026-06-06 07:13:07.395643	2026-06-06 07:13:07.395643	0
143	157	7	exam-platform	e5f6g7h0	feat: 考场管理前端页面	95	18	2026-06-20 07:13:07.395643	2026-06-20 07:13:07.395643	0
144	159	7	exam-platform	f6g7h8i9	test: 自动阅卷功能测试用例	120	20	2026-06-03 07:13:07.395643	2026-06-03 07:13:07.395643	0
145	159	7	exam-platform	f6g7h8i0	test: 性能压力测试脚本	60	8	2026-06-23 07:13:07.395643	2026-06-23 07:13:07.395643	0
\.


--
-- Data for Name: graduation_indicator; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.graduation_indicator (id, objective_id, indicator_no, title, description, sort_order, created_at, updated_at, deleted) FROM stdin;
6	88	CO1-1	软件生命周期模型理解	能够解释各阶段及不同开发模型的特点	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
7	88	CO1-2	软件工程原理应用	能够应用软件工程基本原理分析实际问题	2	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
8	89	CO2-1	UML需求建模	能够使用UML用例图、类图、时序图进行需求分析和系统建模	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
9	89	CO2-2	设计模式应用	能够识别和应用常用设计模式优化系统架构	2	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
10	89	CO2-3	技术文档撰写	能够编写符合规范的需求规格说明和系统设计文档	3	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
11	90	CO3-1	角色职责履行	能够在小组中有效履行所分配角色的职责	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
12	90	CO3-2	项目管理工具使用	能够使用Git和项目管理工具进行任务分解与进度跟踪	2	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
13	91	CO4-1	测试用例设计	能够设计有效的单元测试和集成测试用例	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
14	91	CO4-2	自动化测试实践	能够使用JUnit等框架进行自动化测试	2	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0
\.


--
-- Data for Name: graduation_requirement; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.graduation_requirement (id, req_no, title, description, sort_order, deleted, created_at, updated_at) FROM stdin;
13	GR1	工程知识	能够将数学、自然科学、工程基础和专业知识用于解决复杂工程问题	1	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
14	GR2	问题分析	能够应用基本原理识别、表达并通过文献研究分析复杂工程问题	2	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
15	GR3	设计/开发解决方案	能够设计针对复杂工程问题的解决方案及满足特定需求的系统	3	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
16	GR4	研究	能够基于科学原理和方法对复杂工程问题进行研究	4	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
17	GR5	使用现代工具	能够开发、选择与使用恰当的技术和工具	5	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
18	GR6	工程与社会	能够评价工程实践对社会、健康、安全、法律和文化的影响	6	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
19	GR7	环境和可持续发展	能够理解和评价工程实践对环境和社会可持续发展的影响	7	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
20	GR8	职业规范	具有人文素养和社会责任感,遵守工程职业道德和规范	8	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
21	GR9	个人和团队	能够在多学科团队中承担个体、成员及负责人角色	9	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
22	GR10	沟通	能够就复杂工程问题与同行及公众进行有效沟通	10	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
23	GR11	项目管理	理解并掌握工程管理原理与经济决策方法	11	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
24	GR12	终身学习	具有自主学习和终身学习的意识和能力	12	0	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261
\.


--
-- Data for Name: group_evaluation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.group_evaluation (id, group_id, evaluator_id, dimension, score, comment, created_at, deleted) FROM stdin;
82	6	155	REQUIREMENT	85.00	第1组REQUIREMENT维度表现良好	2026-06-08 07:13:07.395643	0
83	7	155	REQUIREMENT	72.00	第2组REQUIREMENT维度有待提升	2026-06-13 07:13:07.395643	0
84	6	155	DESIGN	82.00	第1组DESIGN维度表现良好	2026-06-08 07:13:07.395643	0
85	7	155	DESIGN	70.00	第2组DESIGN维度有待提升	2026-06-13 07:13:07.395643	0
86	6	155	CODE	78.00	第1组CODE维度表现良好	2026-06-08 07:13:07.395643	0
87	7	155	CODE	75.00	第2组CODE维度有待提升	2026-06-13 07:13:07.395643	0
88	6	155	TEST	80.00	第1组TEST维度表现良好	2026-06-08 07:13:07.395643	0
89	7	155	TEST	68.00	第2组TEST维度有待提升	2026-06-13 07:13:07.395643	0
90	6	155	DOC	88.00	第1组DOC维度表现良好	2026-06-08 07:13:07.395643	0
91	7	155	DOC	73.00	第2组DOC维度有待提升	2026-06-13 07:13:07.395643	0
92	6	155	PRESENTATION	90.00	第1组PRESENTATION维度表现良好	2026-06-08 07:13:07.395643	0
93	7	155	PRESENTATION	76.00	第2组PRESENTATION维度有待提升	2026-06-13 07:13:07.395643	0
\.


--
-- Data for Name: group_member; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.group_member (id, group_id, user_id, role_code, join_at, deleted) FROM stdin;
26	6	156	PM	2026-05-09 07:13:07.395643	0
27	6	157	DEVELOPER	2026-05-09 07:13:07.395643	0
28	6	158	TESTER	2026-05-09 07:13:07.395643	0
29	6	159	DOC_ADMIN	2026-05-09 07:13:07.395643	0
30	7	157	PM	2026-05-14 07:13:07.395643	0
31	7	156	DEVELOPER	2026-05-14 07:13:07.395643	0
32	7	159	TESTER	2026-05-14 07:13:07.395643	0
33	7	158	DOC_ADMIN	2026-05-14 07:13:07.395643	0
\.


--
-- Data for Name: improvement_suggestion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.improvement_suggestion (id, objective_id, indicator_id, suggestion, is_auto, created_at, deleted) FROM stdin;
\.


--
-- Data for Name: improvement_task; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.improvement_task (id, objective_id, group_id, current_achievement, suggestion, action, assignee, status, due_date, deleted, created_at, updated_at, title, description, priority, assignee_name) FROM stdin;
\.


--
-- Data for Name: knowledge_point; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.knowledge_point (id, title, chapter, content, created_at, deleted, attachments) FROM stdin;
9	软件生命周期模型	第1章	瀑布模型、敏捷开发、螺旋模型、V模型等软件生命周期模型的概念、阶段划分、优缺点及适用场景。重点对比瀑布模型与敏捷开发的核心理念差异。	2026-07-07 23:13:08.545261	0	\N
10	软件开发过程	第1章	需求分析、概要设计、详细设计、编码实现、测试、部署维护各阶段的任务、交付物和质量标准。CMMI成熟度模型简介。	2026-07-07 23:13:08.545261	0	\N
11	需求工程	第2章	需求获取方法(访谈、问卷、观察、原型)、需求分析技术(结构化分析、面向对象分析)、需求规格说明文档编写规范(IEEE830标准)、需求验证与评审。	2026-07-07 23:13:08.545261	0	\N
12	UML建模	第2章	UML2.x的14种图型概述。重点掌握用例图、类图、时序图、活动图、状态图的语法和绘制方法。	2026-07-07 23:13:08.545261	0	\N
13	面向对象分析	第2章	面向对象基本概念(封装、继承、多态)。领域建模、用例驱动分析、CRC卡片方法、健壮性分析。	2026-07-07 23:13:08.545261	0	\N
14	设计模式	第3章	创建型、结构型、行为型三大类共23种设计模式的核心思想、类结构和典型应用场景。	2026-07-07 23:13:08.545261	0	\N
15	软件架构设计	第3章	分层架构、微服务架构、事件驱动架构的风格对比。SOLID、DRY、KISS原则。RESTful API设计规范。	2026-07-07 23:13:08.545261	0	\N
16	软件测试方法	第4章	测试层次(单元、集成、系统、验收)。白盒、黑盒、灰盒测试技术。等价类划分、边界值分析等测试用例设计方法。	2026-07-07 23:13:08.545261	0	\N
17	自动化测试	第4章	JUnit5框架使用、Mockito模拟框架、SeleniumWeb自动化、测试覆盖率分析(JaCoCo)。	2026-07-07 23:13:08.545261	0	\N
18	Git版本控制	第5章	Git基本操作、分支策略(GitFlow/GitHubFlow)、冲突解决、PullRequest代码评审流程。	2026-07-07 23:13:08.545261	0	\N
19	持续集成	第5章	CI/CD概念与工具链。自动化构建、测试、部署流水线。Docker容器化与DockerCompose服务编排。	2026-07-07 23:13:08.545261	0	\N
20	AHP层次分析法	第6章	Saaty1-9标度法、判断矩阵构造、特征向量法计算权重、一致性检验(CI/CR计算)。AHP在教育评价中的应用。	2026-07-07 23:13:08.545261	0	\N
\.


--
-- Data for Name: notification; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notification (id, user_id, title, content, is_read, created_at) FROM stdin;
399	156	岗位变更通知	你在第1组的岗位已从DEVELOPER变更为PM	t	2026-05-14 07:13:07.395643
400	157	岗位变更通知	你在第2组的岗位已从DEVELOPER变更为PM	t	2026-05-19 07:13:07.395643
401	159	岗位变更通知	你在第2组的岗位已从DOC_ADMIN变更为TESTER	t	2026-05-29 07:13:07.395643
402	156	新的小组评价	第1组已收到教师提交的小组评价,请查看成绩	f	2026-06-08 07:13:07.395643
403	158	成绩申诉已提交	你对第2组DESIGN评分的申诉已提交,等待审核	f	2026-06-28 07:13:07.395643
404	156	新里程碑创建	第1组新里程碑编码实现已创建	f	2026-05-31 07:13:07.395643
405	157	新里程碑创建	第2组新里程碑编码实现已创建	f	2026-06-05 07:13:07.395643
406	156	贡献加分已审批	你提交的架构设计技术攻关贡献已审批,+5分	f	2026-05-25 07:13:07.395643
\.


--
-- Data for Name: objective_requirement_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.objective_requirement_mapping (id, objective_id, requirement_id, weight, support_level, deleted, created_at) FROM stdin;
13	88	13	0.2500	H	0	2026-07-07 23:13:08.545261
14	88	14	0.2500	M	0	2026-07-07 23:13:08.545261
15	89	15	0.2500	H	0	2026-07-07 23:13:08.545261
16	89	17	0.2500	M	0	2026-07-07 23:13:08.545261
17	89	22	0.2500	L	0	2026-07-07 23:13:08.545261
18	90	21	0.2500	H	0	2026-07-07 23:13:08.545261
19	90	23	0.2500	M	0	2026-07-07 23:13:08.545261
20	91	16	0.2500	H	0	2026-07-07 23:13:08.545261
21	91	17	0.2500	H	0	2026-07-07 23:13:08.545261
\.


--
-- Data for Name: personal_score; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.personal_score (id, user_id, group_id, group_total_score, contribution_ratio, final_score, bonus_total, calculated_at, deleted) FROM stdin;
14	156	6	83.83	0.8844	75.54	1.40	2026-07-07 23:14:36.254981	0
15	157	6	83.83	0.5458	46.86	1.10	2026-07-07 23:14:36.277056	0
16	158	6	83.83	0.3881	33.79	1.25	2026-07-07 23:14:36.292685	0
17	159	6	83.83	0.0930	9.30	1.50	2026-07-07 23:14:36.306998	0
19	157	7	72.33	0.4803	35.34	0.60	2026-07-07 23:14:36.343473	0
20	156	7	72.33	0.8774	64.21	0.75	2026-07-07 23:14:36.359333	0
21	159	7	72.33	0.3265	24.12	0.50	2026-07-07 23:14:36.370362	0
22	158	7	72.33	0.0830	6.90	0.90	2026-07-07 23:14:36.381007	0
\.


--
-- Data for Name: position_criteria; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.position_criteria (id, role_code, dimension, weight, description) FROM stdin;
1	PM	计划制定合理性	0.35	项目计划是否完善、可行
2	PM	进度控制能力	0.35	是否按时推进项目
3	PM	团队协调沟通	0.30	成员协调和冲突解决
4	DEVELOPER	代码质量	0.40	代码规范性、可维护性
5	DEVELOPER	技术实现能力	0.35	功能完成度和技术方案
6	DEVELOPER	问题解决能力	0.25	技术难题攻关
7	TESTER	测试用例覆盖	0.40	测试设计全面性
8	TESTER	Bug发现与跟踪	0.40	缺陷发现和管理
9	TESTER	质量报告	0.20	测试报告质量
10	DOC_ADMIN	文档规范性	0.40	文档格式和结构
11	DOC_ADMIN	文档完整性	0.35	内容是否全面
12	DOC_ADMIN	文档及时性	0.25	是否按时提交
\.


--
-- Data for Name: position_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.position_history (id, group_id, user_id, old_role, new_role, changed_by, reason, created_at) FROM stdin;
1	6	156	DEVELOPER	PM	155	项目需要,组长调整	2026-05-14 07:13:07.395643
2	7	157	DEVELOPER	PM	155	岗位轮换	2026-05-19 07:13:07.395643
3	7	159	DOC_ADMIN	TESTER	155	测试人手不足	2026-05-29 07:13:07.395643
\.


--
-- Data for Name: project_group; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.project_group (id, group_name, teacher_id, max_members, status, created_at, updated_at, deleted, invite_code, course_id) FROM stdin;
6	第1组	155	4	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	A1B2C3	5
7	第2组	155	4	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	D4E5F6	5
\.


--
-- Data for Name: project_journal; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.project_journal (id, user_id, group_id, content, journal_date, created_at, deleted) FROM stdin;
78	156	6	完成项目需求调研,与教师确认功能范围,整理用户故事15条,绘制系统用例图	2026-05-11	2026-07-07 23:13:08.545261	0
79	156	6	完成系统架构设计,确定技术栈为SpringBoot+Vue3+PostgreSQL,绘制ER图	2026-05-19	2026-07-07 23:13:08.545261	0
80	156	6	完成数据库表结构设计,编写建表SQL脚本,完成索引优化方案	2026-05-27	2026-07-07 23:13:08.545261	0
81	156	6	完成学生信息管理模块后端CRUD接口,编写单元测试8个	2026-06-08	2026-07-07 23:13:08.545261	0
82	157	6	学习SpringBoot框架,搭建后端项目骨架,配置MyBatis-Plus和PostgreSQL	2026-05-21	2026-07-07 23:13:08.545261	0
83	157	6	完成成绩管理模块后端接口开发和Swagger文档配置	2026-05-31	2026-07-07 23:13:08.545261	0
84	157	6	完成前后端联调,解决跨域问题,实现成绩录入和查询完整流程	2026-06-13	2026-07-07 23:13:08.545261	0
85	157	6	性能优化:添加Redis缓存,数据库查询响应时间从200ms降至50ms	2026-06-24	2026-07-07 23:13:08.545261	0
86	158	6	编写单元测试用例20个,覆盖成绩管理核心业务逻辑,全部通过	2026-05-29	2026-07-07 23:13:08.545261	0
87	158	6	完成集成测试,使用Postman测试全部API接口,编写测试报告	2026-06-10	2026-07-07 23:13:08.545261	0
88	159	6	开始编写系统设计文档,整理需求规格说明	2026-05-14	2026-07-07 23:13:08.545261	0
89	159	6	完成API接口文档和用户手册初稿	2026-06-03	2026-07-07 23:13:08.545261	0
90	156	7	完成在线考试平台需求分析和技术方案评审,确定采用微服务雏形架构	2026-05-17	2026-07-07 23:13:08.545261	0
91	156	7	完成题库管理模块开发,支持单选题、多选题、判断题三种题型	2026-05-29	2026-07-07 23:13:08.545261	0
92	157	7	完成考试安排功能,支持定时考试和即时考试两种模式	2026-05-24	2026-07-07 23:13:08.545261	0
93	157	7	完成自动阅卷功能,客观题自动批改,主观题支持关键词匹配评分	2026-06-06	2026-07-07 23:13:08.545261	0
\.


--
-- Data for Name: project_milestone; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.project_milestone (id, group_id, title, cdio_phase, due_date, is_done, finished_at, created_at, deleted) FROM stdin;
72	6	需求分析	CONCEIVE	2026-05-19	t	2026-05-17 07:13:07.395643	2026-07-07 23:13:08.545261	0
73	6	系统设计	DESIGN	2026-06-03	t	2026-05-31 07:13:07.395643	2026-07-07 23:13:08.545261	0
74	6	编码实现	IMPLEMENT	2026-07-22	f	\N	2026-07-07 23:13:08.545261	0
75	6	测试部署	OPERATE	2026-08-12	f	\N	2026-07-07 23:13:08.545261	0
76	7	需求分析	CONCEIVE	2026-05-24	t	2026-05-21 07:13:07.395643	2026-07-07 23:13:08.545261	0
77	7	系统设计	DESIGN	2026-06-08	t	2026-06-05 07:13:07.395643	2026-07-07 23:13:08.545261	0
78	7	编码实现	IMPLEMENT	2026-07-29	f	\N	2026-07-07 23:13:08.545261	0
79	7	测试部署	OPERATE	2026-08-19	f	\N	2026-07-07 23:13:08.545261	0
\.


--
-- Data for Name: project_task; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.project_task (id, group_id, milestone_id, assignee_id, title, status, priority, created_at, updated_at, deleted, due_date) FROM stdin;
67	6	74	156	学生信息CRUD接口开发	DONE	2	2026-05-24 07:13:07.395643	2026-05-27 07:13:07.395643	0	2026-05-27
68	6	74	156	成绩统计图表前端	DOING	1	2026-06-08 07:13:07.395643	2026-06-08 07:13:07.395643	0	2026-06-24
69	6	74	157	成绩录入接口对接	DONE	1	2026-05-21 07:13:07.395643	2026-05-24 07:13:07.395643	0	2026-05-24
70	6	74	157	成绩查询分页优化	DONE	2	2026-05-27 07:13:07.395643	2026-05-31 07:13:07.395643	0	2026-06-03
71	6	74	158	成绩模块单元测试	DOING	1	2026-05-29 07:13:07.395643	2026-05-29 07:13:07.395643	0	2026-06-28
72	6	74	159	API文档更新	TODO	3	2026-06-03 07:13:07.395643	2026-06-03 07:13:07.395643	0	2026-07-01
73	6	73	156	数据库ER图设计	DONE	1	2026-05-19 07:13:07.395643	2026-05-21 07:13:07.395643	0	2026-05-21
74	6	73	159	系统设计文档	DONE	1	2026-05-14 07:13:07.395643	2026-05-27 07:13:07.395643	0	2026-05-24
75	7	78	156	题库管理模块开发	DONE	1	2026-05-21 07:13:07.395643	2026-05-27 07:13:07.395643	0	2026-05-27
76	7	78	156	考试结果统计功能	DOING	2	2026-06-08 07:13:07.395643	2026-06-08 07:13:07.395643	0	2026-06-24
77	7	78	157	考试安排后端接口	DONE	1	2026-05-24 07:13:07.395643	2026-05-29 07:13:07.395643	0	2026-05-29
78	7	78	157	自动阅卷算法优化	DOING	1	2026-06-03 07:13:07.395643	2026-06-03 07:13:07.395643	0	2026-06-28
79	7	78	159	阅卷功能测试用例	TODO	1	2026-06-03 07:13:07.395643	2026-06-03 07:13:07.395643	0	2026-07-01
80	7	77	156	技术选型方案文档	DONE	1	2026-05-19 07:13:07.395643	2026-05-24 07:13:07.395643	0	2026-05-24
81	7	77	157	数据库表设计	DONE	1	2026-05-21 07:13:07.395643	2026-05-27 07:13:07.395643	0	2026-05-27
\.


--
-- Data for Name: qa_record; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qa_record (id, user_id, knowledge_id, question, answer, is_resolved, question_type, asked_at, deleted, session_id, attachments, content_type) FROM stdin;
105	156	9	瀑布模型和敏捷开发有什么区别?	瀑布模型是线性顺序的开发模型,每个阶段完成后才能进入下一阶段,适合需求明确的场景。敏捷开发是迭代式的,每个Sprint(2-4周)交付可工作软件,强调快速响应变化和持续反馈。两者核心区别:瀑布重计划和文档,敏捷重适应和沟通。	t	\N	2026-05-19 07:13:07.395643	0	demo-s1	\N	text
106	156	20	AHP一致性比率CR怎么算?如果CR>0.1怎么办?	CR=CI/RI。CI=(λmax-n)/(n-1),RI查表获取。如果CR≥0.1,说明判断矩阵存在逻辑矛盾,需要重新调整两两比较的取值,直到CR<0.1通过一致性检验。	t	\N	2026-06-03 07:13:07.395643	0	demo-s1	\N	text
107	156	15	前后端分离架构中,前端路由和后端API怎么对应?	前端使用VueRouter的Hash模式,所有业务请求通过Nginx代理到/api/路径,后端SpringBoot处理。前端路由是客户端页面跳转,后端API是数据接口,两者通过axios通信。	t	\N	2026-06-23 07:13:07.395643	0	demo-s1	\N	text
108	157	12	UML类图中关联、聚合、组合怎么区分?	关联(Association)是类之间最基本的结构关系,用实线表示。聚合(Aggregation)是整体-部分关系,部分可脱离整体存在,用空心菱形。组合(Composition)是更强的聚合,部分不能脱离整体,用实心菱形。	t	\N	2026-05-21 07:13:07.395643	0	demo-s2	\N	text
109	157	14	什么时候用工厂模式,什么时候用建造者模式?	工厂模式(Factory)用于创建单个对象,隐藏具体类的实例化逻辑。建造者模式(Builder)用于创建复杂对象,分步骤构建。简记:工厂一步创建,建造者分步构建。	t	\N	2026-06-08 07:13:07.395643	0	demo-s2	\N	text
110	157	18	Git merge和rebase有什么区别?团队协作推荐哪个?	merge保留完整的分支历史,产生一个合并提交,适合公共分支。rebase将当前分支的提交变基到目标分支,产生线性历史,适合个人分支整理。团队协作推荐:公共分支用merge,个人分支可先rebase。	t	\N	2026-06-26 07:13:07.395643	0	demo-s2	\N	text
111	158	16	单元测试应该覆盖哪些内容?覆盖率多少合适?	单元测试应覆盖:正常路径(HappyPath)、边界条件(空值/零值/最大值)、异常处理。覆盖率目标:行覆盖率≥80%,分支覆盖率≥70%。但高覆盖率不等于高质量测试,关键测试用例的设计比数字更重要。	t	\N	2026-05-27 07:13:07.395643	0	demo-s3	\N	text
112	158	17	JUnit5中@Mock和@InjectMocks有什么区别?	@Mock创建被测类的依赖模拟对象,所有方法返回默认值。@InjectMocks将@Mock对象自动注入到被测类中。无需手动调用setter或构造器即可完成依赖注入。	t	\N	2026-06-10 07:13:07.395643	0	demo-s3	\N	text
113	158	16	什么是Mock测试?什么时候需要Mock?	Mock测试用模拟对象替代真实依赖,隔离被测代码。需要Mock的场景:依赖的外部服务不可用、依赖的行为不可控、依赖的操作有副作用、依赖的响应速度影响测试效率。	f	\N	2026-06-28 07:13:07.395643	0	demo-s3	\N	text
114	159	11	如何判断需求的优先级?MoSCoW方法怎么用?	MoSCoW方法将需求分为四类:MustHave(必须有)、ShouldHave(应该有)、CouldHave(可以有)、Won'tHave(暂不做)。优先级排序时结合业务价值和实现成本两个维度。	t	\N	2026-05-24 07:13:07.395643	0	demo-s4	\N	text
115	159	15	软件架构评估方法ATAM怎么操作?	ATAM(架构权衡分析法)步骤:收集场景、描述架构方案、分析每个场景下架构的响应、识别风险点和非风险点、输出评估报告。核心思想:不存在完美的架构,只有最适合当前场景的权衡。	t	\N	2026-06-18 07:13:07.395643	0	demo-s4	\N	text
116	159	10	需求规格说明文档应该包含哪些章节?	按IEEE830标准:引言(目的/范围/定义)、总体描述(产品视角/用户特征/约束)、具体需求(功能需求/非功能需求/接口需求)。功能需求用系统应...句式,避免在需求文档中混入设计方案。	f	\N	2026-07-03 07:13:07.395643	0	demo-s4	\N	text
\.


--
-- Data for Name: question_answer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.question_answer (id, group_id, ask_user_id, answer_user_id, title, question, answer, status, attachments, asked_at, answered_at, resolved_at, deleted, created_at) FROM stdin;
56	6	156	155	项目架构方案咨询	微服务还是单体架构更适合?	建议先用单体,SpringBoot单体即可满足,后续如有性能瓶颈再拆微服务。重点是模块划分清晰,为未来拆分做好准备。	RESOLVED	\N	2026-05-24 07:13:07.395643	2026-05-26 07:13:07.395643	\N	0	2026-05-24 07:13:07.395643
57	6	157	155	测试框架选择	JUnit5还是TestNG更适合?	JUnit5更主流,社区活跃度更高,与SpringBoot集成更好。推荐使用JUnit5+Mockito组合。	ANSWERED	\N	2026-06-03 07:13:07.395643	2026-06-05 07:13:07.395643	\N	0	2026-06-03 07:13:07.395643
58	7	158	\N	数据库设计评审	考试平台是否需要读写分离?	\N	PENDING	\N	2026-06-18 07:13:07.395643	\N	\N	0	2026-06-18 07:13:07.395643
\.


--
-- Data for Name: quiz; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.quiz (id, teacher_id, group_id, course_id, title, questions, status, total_score, created_at, published_at, deleted) FROM stdin;
3	155	6	5	软件工程基础测验	[{"q": "软件生命周期不包括哪个阶段?", "opts": ["需求分析", "系统设计", "市场推广", "测试部署"], "ans": 2}, {"q": "UML中用于描述系统功能的图是?", "opts": ["类图", "用例图", "时序图", "部署图"], "ans": 1}, {"q": "AHP的CR小于多少通过一致性检验?", "opts": ["0.05", "0.1", "0.15", "0.2"], "ans": 1}]	PUBLISHED	100	2026-06-08 07:13:07.395643	2026-06-13 07:13:07.395643	0
4	155	7	5	UML建模测验	[{"q": "类图中表示组合关系的符号是?", "opts": ["空心菱形", "实心菱形", "箭头", "无特殊符号"], "ans": 1}, {"q": "时序图的生命线表示什么?", "opts": ["类的生命周期", "对象的生存时间", "方法的执行时间", "系统的运行时间"], "ans": 1}]	DRAFT	100	2026-06-23 07:13:07.395643	\N	0
\.


--
-- Data for Name: repo_config; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.repo_config (id, group_id, platform, owner, repo, access_token, branch, last_synced_at, enabled, created_at, deleted) FROM stdin;
5	6	GITHUB	student1	grade-mgmt	ghp_demo123	main	2026-05-11 07:13:07.395643	t	2026-05-11 07:13:07.395643	0
6	7	GITEE	student2	exam-platform	gitee_demo456	master	2026-05-17 07:13:07.395643	t	2026-05-17 07:13:07.395643	0
\.


--
-- Data for Name: requirement_change; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.requirement_change (id, group_id, title, description, change_type, reason, created_by, created_at) FROM stdin;
2	6	增加成绩导出CSV功能	教师要求增加成绩数据导出功能	ADD	教师提出新需求	156	2026-06-18 07:13:07.395643
3	6	修改学生信息字段	增加学生手机号和邮箱字段	MODIFY	数据库设计调整	156	2026-06-23 07:13:07.395643
4	7	取消手动阅卷功能	改用自动阅卷替代手动阅卷	REMOVE	简化教师操作	157	2026-06-13 07:13:07.395643
\.


--
-- Data for Name: role_evaluation; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.role_evaluation (id, user_id, group_id, evaluator_id, role_code, dimension, score, comment, created_at, deleted) FROM stdin;
24	156	6	155	PM	LEADERSHIP	88.00	计划制定合理	2026-06-10 07:13:07.395643	0
25	157	6	155	DEVELOPER	CODING_QUALITY	82.00	代码质量较好	2026-06-10 07:13:07.395643	0
26	158	6	155	TESTER	TEST_COVERAGE	85.00	测试用例全面	2026-06-10 07:13:07.395643	0
27	159	6	155	DOC_ADMIN	DOC_QUALITY	90.00	文档规范完整	2026-06-10 07:13:07.395643	0
28	157	7	155	PM	LEADERSHIP	72.00	计划执行需加强	2026-06-15 07:13:07.395643	0
29	156	7	155	DEVELOPER	CODING_QUALITY	75.00	代码可维护性待提升	2026-06-15 07:13:07.395643	0
30	159	7	155	TESTER	TEST_COVERAGE	70.00	测试用例不足	2026-06-15 07:13:07.395643	0
31	158	7	155	DOC_ADMIN	DOC_QUALITY	78.00	文档结构清晰	2026-06-15 07:13:07.395643	0
\.


--
-- Data for Name: score_dispute; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.score_dispute (id, user_id, score_id, score_type, reason, status, resolved_by, resolution, created_at, updated_at) FROM stdin;
1	157	\N	GROUP	对第2组DESIGN评分70分有异议,认为应≥75分	PENDING	\N	\N	2026-06-28 07:13:07.395643	2026-06-28 07:13:07.395643
\.


--
-- Data for Name: self_test_record; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.self_test_record (id, user_id, score, total, taken_at, deleted, knowledge_id, questions, feedback) FROM stdin;
3	156	85.00	100.00	2026-06-08 07:13:07.395643	0	\N	\N	\N
4	157	72.00	100.00	2026-06-08 07:13:07.395643	0	\N	\N	\N
5	158	90.00	100.00	2026-06-08 07:13:07.395643	0	\N	\N	\N
6	159	78.00	100.00	2026-06-08 07:13:07.395643	0	\N	\N	\N
7	156	92.00	100.00	2026-06-23 07:13:07.395643	0	\N	\N	\N
8	157	80.00	100.00	2026-06-23 07:13:07.395643	0	\N	\N	\N
9	158	88.00	100.00	2026-06-23 07:13:07.395643	0	\N	\N	\N
10	159	85.00	100.00	2026-06-23 07:13:07.395643	0	\N	\N	\N
11	156	78.00	100.00	2026-07-03 07:13:07.395643	0	\N	\N	\N
12	157	85.00	100.00	2026-07-03 07:13:07.395643	0	\N	\N	\N
13	158	95.00	100.00	2026-07-03 07:13:07.395643	0	\N	\N	\N
14	159	72.00	100.00	2026-07-03 07:13:07.395643	0	\N	\N	\N
\.


--
-- Data for Name: sys_user; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sys_user (id, username, password, real_name, email, phone, role_code, status, created_at, updated_at, deleted, git_username, git_email) FROM stdin;
154	admin	$2b$12$Sbeq1/JspI79LtupXFPpievkWziXt8QcL3K0UwJHC70fbiA0c7qHq	系统管理员	admin@school.edu.cn	\N	ADMIN	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	\N	\N
155	teacher1	$2b$12$ayGPZsOsKdzOkuw3HxFFd.5UG/J7Q24ODlRpGuQ1JrQvtAvV2BVry	张教授	zhang@school.edu.cn	\N	TEACHER	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	\N	\N
156	student1	$2b$12$pMTykLR1GAyhFnMBwXPGHe7VDDxqwmGJSoQZCF2JG25OJHBLhk7FK	李明	liming@school.edu.cn	\N	STUDENT	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	\N	\N
157	student2	$2b$12$Yp42Lyt0nMU3BI9K14Y/yuSG4vbMwa/2T0QLi6m0KN3xcRXWjFZMy	王小红	wangxh@school.edu.cn	\N	STUDENT	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	\N	\N
158	student3	$2b$12$QsXqTJWIanynYTYz/hwu1eQmys5eM8apIhCaSgL/g0urkp1PKou/q	陈刚	chengang@school.edu.cn	\N	STUDENT	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	\N	\N
159	student4	$2b$12$Lazh.YpZ6tAXWiSIQnlyJ.JlZ5T9SoccZfshl35nxBNgHJdkbPAS2	赵丽	zhaoli@school.edu.cn	\N	STUDENT	1	2026-07-07 23:13:08.545261	2026-07-07 23:13:08.545261	0	\N	\N
\.


--
-- Name: achievement_result_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.achievement_result_id_seq', 491, true);


--
-- Name: audit_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.audit_log_id_seq', 672, true);


--
-- Name: cdio_phase_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cdio_phase_mapping_id_seq', 13, true);


--
-- Name: contribution_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.contribution_log_id_seq', 65, true);


--
-- Name: course_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.course_id_seq', 5, true);


--
-- Name: course_objective_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.course_objective_id_seq', 91, true);


--
-- Name: evaluation_method_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.evaluation_method_id_seq', 26, true);


--
-- Name: git_commit_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.git_commit_log_id_seq', 145, true);


--
-- Name: graduation_indicator_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.graduation_indicator_id_seq', 14, true);


--
-- Name: graduation_requirement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.graduation_requirement_id_seq', 24, true);


--
-- Name: group_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.group_evaluation_id_seq', 93, true);


--
-- Name: group_member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.group_member_id_seq', 33, true);


--
-- Name: improvement_suggestion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.improvement_suggestion_id_seq', 51, true);


--
-- Name: improvement_task_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.improvement_task_id_seq', 10, true);


--
-- Name: knowledge_point_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.knowledge_point_id_seq', 20, true);


--
-- Name: notification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notification_id_seq', 406, true);


--
-- Name: objective_requirement_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.objective_requirement_mapping_id_seq', 21, true);


--
-- Name: personal_score_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.personal_score_id_seq', 22, true);


--
-- Name: position_criteria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.position_criteria_id_seq', 12, true);


--
-- Name: position_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.position_history_id_seq', 3, true);


--
-- Name: project_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.project_group_id_seq', 7, true);


--
-- Name: project_journal_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.project_journal_id_seq', 93, true);


--
-- Name: project_milestone_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.project_milestone_id_seq', 79, true);


--
-- Name: project_task_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.project_task_id_seq', 81, true);


--
-- Name: qa_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.qa_record_id_seq', 116, true);


--
-- Name: question_answer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.question_answer_id_seq', 58, true);


--
-- Name: quiz_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quiz_id_seq', 4, true);


--
-- Name: repo_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.repo_config_id_seq', 6, true);


--
-- Name: requirement_change_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.requirement_change_id_seq', 4, true);


--
-- Name: role_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.role_evaluation_id_seq', 31, true);


--
-- Name: score_dispute_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.score_dispute_id_seq', 1, true);


--
-- Name: self_test_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.self_test_record_id_seq', 14, true);


--
-- Name: sys_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.sys_user_id_seq', 159, true);


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

\unrestrict FXsTBUfUhBJibp5ghmjqUtP5ShyrJGgTQ0lzQdVEEI7hZF5sZYKvFUn28kgSIcy

