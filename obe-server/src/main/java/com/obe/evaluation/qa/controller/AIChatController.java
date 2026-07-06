/**
 * 智能问答控制器
 * 职责：智能问答、自动出题批改、学情看板、测验管理
 */
package com.obe.evaluation.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.qa.entity.KnowledgePoint;
import com.obe.evaluation.qa.entity.QaRecord;
import com.obe.evaluation.qa.entity.SelfTestRecord;
import com.obe.evaluation.qa.entity.Quiz;
import com.obe.evaluation.qa.mapper.KnowledgePointMapper;
import com.obe.evaluation.qa.mapper.QaRecordMapper;
import com.obe.evaluation.qa.mapper.QuizMapper;
import com.obe.evaluation.qa.mapper.SelfTestRecordMapper;
import com.obe.evaluation.analysis.entity.AchievementResult;
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.evaluation.entity.PersonalScore;
import com.obe.evaluation.project.entity.GitCommitLog;
import com.obe.evaluation.project.entity.ProjectTask;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.entity.ProjectGroup;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController @RequestMapping("/api/ai-chat") @RequiredArgsConstructor
@Tag(name = "智能问答")
public class AIChatController {

    private final QaRecordMapper qaRecordMapper;
    private final KnowledgePointMapper knowledgePointMapper;
    private final SelfTestRecordMapper selfTestMapper;
    private final QuizMapper quizMapper;
    private final CourseObjectiveMapper objectiveMapper;
    private final ProjectGroupMapper groupMapper;
    private final com.obe.evaluation.group.mapper.GroupMemberMapper groupMemberMapper;
    private final com.obe.evaluation.system.mapper.SysUserMapper userMapper;
    private final com.obe.evaluation.analysis.mapper.AchievementResultMapper resultMapper;
    private final com.obe.evaluation.evaluation.mapper.PersonalScoreMapper scoreMapper;
    private final com.obe.evaluation.project.mapper.GitCommitLogMapper gitCommitMapper;
    private final com.obe.evaluation.project.mapper.ProjectTaskMapper taskMapper;

    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com/v1}")
    private String deepseekBaseUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String deepseekModel;

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long))
            throw new IllegalArgumentException("未登录");
        return (Long) auth.getPrincipal();
    }

    private boolean isTeacherOrAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    @PostMapping("/ask")
    @Operation(summary = "向AI提问（自动关联知识点）")
    public R<QaRecord> ask(@RequestBody Map<String, Object> body) {
        Long userId = currentUserId();
        String question = (String) body.getOrDefault("question", "");
        String sessionId = (String) body.getOrDefault("sessionId", UUID.randomUUID().toString());
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;

        if (question == null || question.isBlank()) return R.fail(400, "问题不能为空");

        // 自动关联知识点：关键词匹配
        Long matchedKnowledgeId = null;
        int bestScore = 0;
        StringBuilder knowledgeContext = new StringBuilder();
        var allKps = knowledgePointMapper.selectList(null);
        String qLower = question.toLowerCase();
        for (var kp : allKps) {
            knowledgeContext.append("【").append(kp.getChapter()).append("】").append(kp.getTitle()).append("：").append(kp.getContent()).append("\n");
            // 多维度关键词匹配
            int score = 0;
            if (kp.getTitle() != null) {
                String tLower = kp.getTitle().toLowerCase();
                if (qLower.contains(tLower)) { score += 10; } // 全匹配
                else {
                    // 逐词匹配
                    for (String word : tLower.split("[\\s，,、()（）]+")) {
                        if (word.length() >= 2 && qLower.contains(word)) score += 3;
                    }
                }
            }
            if (kp.getContent() != null) {
                for (String word : kp.getContent().toLowerCase().split("[\\s，,、()（）。]+")) {
                    if (word.length() >= 3 && qLower.contains(word)) score += 1;
                }
            }
            if (kp.getChapter() != null && qLower.contains(kp.getChapter().toLowerCase())) score += 5;
            if (score > bestScore) { bestScore = score; matchedKnowledgeId = kp.getId(); }
        }

        // 收集课程目标上下文
        if (courseId != null) {
            var objs = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));
            for (var obj : objs) {
                knowledgeContext.append("课程目标[").append(obj.getDimension()).append("]：").append(obj.getTitle()).append("\n");
            }
        }

        // 收集用户实时系统数据（小组列表、达成度、成绩）
        StringBuilder systemContext = new StringBuilder();
        try {
            // 用户可见的小组列表
            List<Long> visibleGroupIds;
            if (isTeacherOrAdmin()) {
                visibleGroupIds = groupMapper.selectList(null).stream().map(ProjectGroup::getId).toList();
            } else {
                visibleGroupIds = groupMemberMapper.selectList(
                    new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, userId))
                    .stream().map(GroupMember::getGroupId).toList();
            }
            for (Long gid : visibleGroupIds) {
                var group = groupMapper.selectById(gid);
                if (group == null) continue;
                // 成员列表
                var members = groupMemberMapper.selectList(
                    new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, gid));
                StringBuilder memberStr = new StringBuilder();
                for (var m : members) {
                    var u = userMapper.selectById(m.getUserId());
                    memberStr.append(u != null ? u.getRealName() : "U"+m.getUserId())
                        .append("(").append(m.getRoleCode()).append(") ");
                }
                // 达成度
                double achAvg = 0;
                try {
                    var ach = resultMapper.selectList(new LambdaQueryWrapper<AchievementResult>()
                        .eq(AchievementResult::getGroupId, gid).orderByDesc(AchievementResult::getCalcRound));
                    if (!ach.isEmpty()) {
                        int lr = ach.get(0).getCalcRound() != null ? ach.get(0).getCalcRound() : 0;
                        achAvg = ach.stream().filter(r -> r.getCalcRound() != null && r.getCalcRound().equals(lr) && r.getAchievementValue() != null)
                            .mapToDouble(AchievementResult::getAchievementValue).average().orElse(0);
                    }
                } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }
                // Git
                long gitCount = 0;
                try { gitCount = gitCommitMapper.selectCount(new LambdaQueryWrapper<GitCommitLog>().eq(GitCommitLog::getGroupId, gid)); } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }
                // 任务
                long taskDone = 0, taskTotal = 0;
                try {
                    taskTotal = taskMapper.selectCount(new LambdaQueryWrapper<ProjectTask>().eq(ProjectTask::getGroupId, gid));
                    taskDone = taskMapper.selectCount(new LambdaQueryWrapper<ProjectTask>().eq(ProjectTask::getGroupId, gid).eq(ProjectTask::getStatus, "DONE"));
                } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }

                // 成员成绩
                StringBuilder scoreStr = new StringBuilder();
                for (var m : members) {
                    var ps = scoreMapper.selectList(new LambdaQueryWrapper<PersonalScore>()
                        .eq(PersonalScore::getUserId, m.getUserId())
                        .eq(PersonalScore::getGroupId, gid));
                    if (!ps.isEmpty()) {
                        var s = ps.get(0);
                        scoreStr.append(String.format("%s:%.0f分(系数%.2f) ",
                            m.getRoleCode(), s.getFinalScore(), s.getContributionRatio()));
                    }
                }
                systemContext.append(String.format("%s(id=%d) 成员%d人[%s] 达成度=%.4f Git=%d 任务=%d/%d 成绩{%s}\n",
                    group.getGroupName(), gid, members.size(), memberStr.toString().trim(),
                    achAvg, gitCount, taskDone, taskTotal, scoreStr.toString().trim()));
            }

            // 用户个人统计数据 + 全部成绩（不按小组过滤，直接查全部）
            try {
                long myQaCount = qaRecordMapper.selectCount(new LambdaQueryWrapper<QaRecord>().eq(QaRecord::getUserId, userId));
                long myTestCount = selfTestMapper.selectCount(new LambdaQueryWrapper<SelfTestRecord>().eq(SelfTestRecord::getUserId, userId));
                systemContext.append("\n用户个人统计: AI提问").append(myQaCount).append("次, 自测").append(myTestCount).append("次\n");

                // 直接查该用户全部个人成绩（不按小组过滤）
                var allMyScores = scoreMapper.selectList(
                    new LambdaQueryWrapper<PersonalScore>().eq(PersonalScore::getUserId, userId));
                if (allMyScores.isEmpty()) {
                    systemContext.append("个人成绩: 暂无\n");
                } else {
                    for (var s : allMyScores) {
                        String gname = groupName(s.getGroupId());
                        systemContext.append("  小组[").append(gname).append("] id=").append(s.getGroupId())
                            .append(" 小组分=").append(s.getGroupTotalScore())
                            .append(" 贡献系数=").append(s.getContributionRatio())
                            .append(" 最终分=").append(s.getFinalScore())
                            .append(" 加分=").append(s.getBonusTotal()).append("\n");
                    }
                }
            } catch (Exception e) {
                systemContext.append("个人成绩: 查询失败(").append(e.getMessage()).append(")\n");
            }

            // 当前用户的个人成绩（按小组明细）
            systemContext.append("\n当前用户个人成绩:\n");
            var memberships = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, userId));
            for (var m : memberships) {
                var group = groupMapper.selectById(m.getGroupId());
                if (group == null) continue;
                systemContext.append("  小组[").append(group.getGroupName()).append("] 角色=").append(m.getRoleCode()).append(": ");
                try {
                    var scores = scoreMapper.selectList(
                        new LambdaQueryWrapper<PersonalScore>()
                            .eq(PersonalScore::getUserId, userId)
                            .eq(PersonalScore::getGroupId, group.getId()));
                    if (scores.isEmpty()) {
                        systemContext.append("无成绩记录\n");
                    }
                    for (var s : scores) {
                        systemContext.append("  个人成绩: 小组分=").append(s.getGroupTotalScore())
                            .append(" 贡献系数=").append(s.getContributionRatio())
                            .append(" 最终分=").append(s.getFinalScore()).append("\n");
                    }
                } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }
            }
        } catch (Exception e) {
            log.debug("Failed to collect system context: {}", e.getMessage());
        }

        QaRecord record = new QaRecord();
        record.setUserId(userId);
        record.setKnowledgeId(matchedKnowledgeId);
        record.setQuestion(question);
        record.setSessionId(sessionId);
        record.setQuestionType(matchedKnowledgeId != null ? "KNOWLEDGE" : "GENERAL");
        record.setIsResolved(false);
        record.setAskedAt(LocalDateTime.now());

        // 拼接知识库 + 实时系统数据 + 用户问题
        // 统计知识库规模
        long kpCount = knowledgePointMapper.selectCount(null);
        StringBuilder prompt = new StringBuilder(
            "你是OBE-CDIO课程AI助手。你已接入以下数据源：\n"
            + "1. 课程知识库（MaxKB, " + kpCount + "个知识点）\n"
            + "2. OBE系统实时数据库（学生成绩、小组达成度、Git提交、任务进度等）\n"
            + "3. 当前对话实时获取的系统数据见下方" + (systemContext.length() > 0 ? "【用户实时系统数据】" : "（暂无上下文数据）") + "\n\n"
            + "请严格遵守以下规则：\n"
            + "1. 只能基于上述数据源回答，不得编造任何数据\n"
            + "2. 如果数据中没有相关信息，请明确说「系统中暂无此数据」\n"
            + "3. 不要虚构任何人名、数字、日期或事件\n"
            + "4. 如果问是否连接到MaxKB，回答：是的，MaxKB知识库已连接，当前有" + kpCount + "个知识点可供检索\n\n");
        if (knowledgeContext.length() > 0) {
            prompt.append("=== 课程知识库 ===\n").append(knowledgeContext).append("\n");
        }
        if (systemContext.length() > 0) {
            prompt.append("=== 用户实时系统数据 ===\n").append(systemContext).append("\n");
        }
        prompt.append("=== 用户问题 ===\n").append(question);
        String enrichedQuestion = prompt.toString();

        String answer = callDeepSeek(enrichedQuestion);
        record.setAnswer(answer);
        record.setIsResolved(true);
        qaRecordMapper.insert(record);

        return R.ok(record);
    }

    @GetMapping("/sessions")
    @Operation(summary = "查询AI聊天会话历史")
    public R<List<Map<String, Object>>> sessions() {
        Long userId = currentUserId();
        List<QaRecord> records = qaRecordMapper.selectList(
            new LambdaQueryWrapper<QaRecord>()
                .eq(QaRecord::getUserId, userId)
                .orderByDesc(QaRecord::getAskedAt));

        Map<String, List<QaRecord>> grouped = new LinkedHashMap<>();
        for (QaRecord r : records) {
            String sid = r.getSessionId() != null ? r.getSessionId() : "default";
            grouped.computeIfAbsent(sid, k -> new ArrayList<>()).add(r);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            Map<String, Object> ses = new LinkedHashMap<>();
            ses.put("sessionId", entry.getKey());
            ses.put("records", entry.getValue());
            ses.put("count", entry.getValue().size());
            var first = entry.getValue().get(0);
            ses.put("title", first.getQuestion().length() > 30
                ? first.getQuestion().substring(0, 30) + "..." : first.getQuestion());
            ses.put("createTime", first.getAskedAt());
            result.add(ses);
        }
        return R.ok(result);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "删除AI聊天会话")
    public R<Void> deleteSession(@PathVariable String sessionId) {
        Long userId = currentUserId();
        List<QaRecord> records = qaRecordMapper.selectList(
            new LambdaQueryWrapper<QaRecord>()
                .eq(QaRecord::getSessionId, sessionId));
        if (records.isEmpty()) return R.ok();
        if (!records.get(0).getUserId().equals(userId))
            return R.fail(403, "只能删除自己的会话");
        for (QaRecord r : records) qaRecordMapper.deleteById(r.getId());
        return R.ok();
    }

    @GetMapping("/config")
    @Operation(summary = "AI模块配置状态")
    public R<Map<String, Object>> config() {
        return R.ok(Map.of("hasAI", !deepseekApiKey.isEmpty(), "model", "DeepSeek"));
    }

    // ========== AI 自测 ==========

    @PostMapping("/start-quiz")
    @Operation(summary = "AI自动出题（基于课程知识库）")
    public R<Map<String, Object>> startQuiz(@RequestBody Map<String, Object> body) {
        int questionCount = body.get("count") != null ? Integer.parseInt(body.get("count").toString()) : 5;
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;

        // 收集知识点作为出题素材
        StringBuilder knowledgeContext = new StringBuilder();
        if (courseId != null) {
            var objs = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));
            for (var obj : objs) {
                knowledgeContext.append("课程目标：").append(obj.getTitle()).append("\n");
            }
        }
        var kps = knowledgePointMapper.selectList(null);
        for (var kp : kps) {
            knowledgeContext.append("知识点[").append(kp.getTitle()).append("]：").append(kp.getContent()).append("\n");
        }

        // AI 出题
        String prompt = knowledgeContext.length() > 0
            ? "根据以下课程知识内容，生成" + questionCount + "道单选题（每题4个选项），以JSON格式返回：[{\"question\":\"...\",\"options\":[\"A...\",\"B...\",\"C...\",\"D...\"],\"answer\":\"A\"}]。\n\n知识内容：\n" + knowledgeContext
            : "生成" + questionCount + "道关于OBE工程教育的单选题（每题4个选项），以JSON格式返回：[{\"question\":\"...\",\"options\":[\"A...\",\"B...\",\"C...\",\"D...\"],\"answer\":\"A\"}]";

        String aiResponse = callDeepSeek(prompt);
        // 提取JSON
        String json = extractJson(aiResponse);
        return R.ok(Map.of("questions", json, "count", questionCount, "raw", aiResponse));
    }

    @PostMapping("/submit-quiz")
    @Operation(summary = "提交自测答案，AI批改")
    @Transactional
    public R<Map<String, Object>> submitQuiz(@RequestBody Map<String, Object> body) {
        Long userId = currentUserId();
        @SuppressWarnings("unchecked")
        var answers = (List<Map<String, Object>>) body.get("answers");
        if (answers == null || answers.isEmpty()) return R.fail(400, "请提供答案");

        int correct = 0;
        StringBuilder gradingPrompt = new StringBuilder("请批改以下自测答案，给出每题是否正确，并在最后给出200字以内的总体反馈和改进建议。\n\n");
        for (int i = 0; i < answers.size(); i++) {
            var a = answers.get(i);
            gradingPrompt.append("题").append(i + 1).append("：")
                .append(a.get("question")).append("\n")
                .append("正确答案：").append(a.get("answer")).append("\n")
                .append("学生答案：").append(a.get("userAnswer")).append("\n\n");
        }
        gradingPrompt.append("请对以上").append(answers.size()).append("题进行批改，必须严格返回如下JSON格式（不要包含markdown标记）：{\"results\":[{\"correct\":true/false,\"explanation\":\"解释\"}],\"feedback\":\"总体反馈\"}");

        String aiResponse = callDeepSeek(gradingPrompt.toString());
        String json = extractJson(aiResponse);

        // 计算得分 — 兼容两种格式
        List<Map<String, Object>> results = null;
        String feedbackText = aiResponse;
        try {
            Object parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Object.class);
            if (parsed instanceof List) {
                // AI返回了纯数组 [{correct,explanation},...]
                results = (List<Map<String, Object>>) parsed;
            } else if (parsed instanceof Map) {
                var resultMap = (Map<String, Object>) parsed;
                results = (List<Map<String, Object>>) resultMap.get("results");
                if (resultMap.get("feedback") != null) feedbackText = resultMap.get("feedback").toString();
            }
            if (results != null) {
                for (var r : results) {
                    if (Boolean.TRUE.equals(r.get("correct"))) correct++;
                }
            }
        } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }

        // 保存自测记录
        SelfTestRecord record = new SelfTestRecord();
        record.setUserId(userId);
        record.setQuestions(toJson(answers));
        record.setScore((double) correct);
        record.setTotal((double) answers.size());
        record.setFeedback(feedbackText);
        record.setTakenAt(LocalDateTime.now());
        selfTestMapper.insert(record);

        return R.ok(Map.of("correct", correct, "total", answers.size(),
            "score", Math.round(correct * 100.0 / answers.size()),
            "gradingResult", json, "recordId", record.getId()));
    }

    @GetMapping("/quiz-history")
    @Operation(summary = "自测历史记录")
    public R<List<SelfTestRecord>> quizHistory() {
        return R.ok(selfTestMapper.selectList(
            new LambdaQueryWrapper<SelfTestRecord>()
                .eq(SelfTestRecord::getUserId, currentUserId())
                .orderByDesc(SelfTestRecord::getTakenAt)));
    }

    // ========== 学习行为采集（教师端） ==========

    @GetMapping("/learning-behavior")
    @Operation(summary = "教师查看学生学习行为（问答+自测）")
    public R<Map<String, Object>> learningBehavior(@RequestParam(required = false) Long groupId) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可查看");
        // 按组查询成员
        List<Long> memberIds = new ArrayList<>();
        if (groupId != null) {
            var members = groupMapper.selectById(groupId); // just for validation
            memberIds = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId))
                .stream().map(GroupMember::getUserId).toList();
        }

        // 问答统计
        var qaQuery = new LambdaQueryWrapper<QaRecord>().orderByDesc(QaRecord::getAskedAt);
        if (!memberIds.isEmpty()) qaQuery.in(QaRecord::getUserId, memberIds);
        List<QaRecord> qaRecords = qaRecordMapper.selectList(qaQuery);

        // 自测统计
        var testQuery = new LambdaQueryWrapper<SelfTestRecord>().orderByDesc(SelfTestRecord::getTakenAt);
        if (!memberIds.isEmpty()) testQuery.in(SelfTestRecord::getUserId, memberIds);
        List<SelfTestRecord> tests = selfTestMapper.selectList(testQuery);

        // 按用户汇总
        Map<Long, Map<String, Object>> userStats = new LinkedHashMap<>();
        for (QaRecord q : qaRecords) {
            userStats.computeIfAbsent(q.getUserId(), k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", k);
                var u = userMapper.selectById(k);
                m.put("userName", u != null ? u.getRealName() : "");
                m.put("questionCount", 0); m.put("resolvedCount", 0);
                m.put("quizCount", 0); m.put("avgQuizScore", 0.0);
                return m;
            });
            Map<String, Object> s = userStats.get(q.getUserId());
            s.put("questionCount", ((Integer) s.get("questionCount")) + 1);
            if (Boolean.TRUE.equals(q.getIsResolved())) {
                s.put("resolvedCount", ((Integer) s.get("resolvedCount")) + 1);
            }
        }
        for (SelfTestRecord t : tests) {
            userStats.computeIfAbsent(t.getUserId(), k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", k); m.put("questionCount", 0); m.put("resolvedCount", 0);
                m.put("quizCount", 0); m.put("avgQuizScore", 0.0);
                var u = userMapper.selectById(k);
                m.put("userName", u != null ? u.getRealName() : "");
                return m;
            });
            Map<String, Object> s = userStats.get(t.getUserId());
            int quizCount = (Integer) s.get("quizCount") + 1;
            double totalScore = (Double) s.get("avgQuizScore") * (quizCount - 1)
                + (t.getTotal() != null && t.getTotal() > 0 ? t.getScore() / t.getTotal() * 100 : 0);
            s.put("quizCount", quizCount);
            s.put("avgQuizScore", Math.round(totalScore / quizCount * 10.0) / 10.0);
        }

        return R.ok(Map.of(
            "userStats", new ArrayList<>(userStats.values()),
            "totalQuestions", qaRecords.size(),
            "totalQuizzes", tests.size(),
            "knowledgePoints", knowledgePointMapper.selectCount(null)
        ));
    }

    // ========== 教师学情看板（P0-2） ==========

    @GetMapping("/heatmap")
    @Operation(summary = "薄弱知识点热力图数据")
    public R<List<Map<String, Object>>> knowledgeHeatmap(@RequestParam(required = false) Long groupId) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可查看");

        List<Map<String, Object>> data = new ArrayList<>();
        var allKps = knowledgePointMapper.selectList(null);
        long totalQuestions = qaRecordMapper.selectCount(null);
        if (totalQuestions == 0) totalQuestions = 1;

        for (var kp : allKps) {
            long count = qaRecordMapper.selectCount(
                new LambdaQueryWrapper<QaRecord>().eq(QaRecord::getKnowledgeId, kp.getId()));
            long unresolved = qaRecordMapper.selectCount(
                new LambdaQueryWrapper<QaRecord>().eq(QaRecord::getKnowledgeId, kp.getId()).eq(QaRecord::getIsResolved, false));

            if (count > 0) {
                double ratio = Math.round(count * 10000.0 / totalQuestions) / 100.0;
                data.add(Map.of(
                    "knowledgeId", kp.getId(), "title", kp.getTitle(),
                    "chapter", kp.getChapter() != null ? kp.getChapter() : "",
                    "questionCount", count, "unresolvedCount", unresolved,
                    "ratio", ratio,
                    "level", count >= 8 ? "高频" : count >= 4 ? "中频" : "低频",
                    "color", count >= 8 ? "#F56C6C" : count >= 4 ? "#E6A23C" : "#409EFF"
                ));
            }
        }
        data.sort((a, b) -> Long.compare((long) b.get("questionCount"), (long) a.get("questionCount")));
        return R.ok(data);
    }

    @GetMapping("/analytics")
    @Operation(summary = "教师学情看板")
    public R<Map<String, Object>> teacherAnalytics(@RequestParam(required = false) Long groupId) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可查看学情数据");
        // 1. 高频疑难知识点分析
        List<Map<String, Object>> hotKnowledge = new ArrayList<>();
        var allKps = knowledgePointMapper.selectList(null);
        for (var kp : allKps) {
            long count = qaRecordMapper.selectCount(new LambdaQueryWrapper<QaRecord>()
                .eq(QaRecord::getKnowledgeId, kp.getId()));
            long unresolved = qaRecordMapper.selectCount(new LambdaQueryWrapper<QaRecord>()
                .eq(QaRecord::getKnowledgeId, kp.getId()).eq(QaRecord::getIsResolved, false));
            if (count > 0) {
                hotKnowledge.add(Map.of("knowledgeId", kp.getId(), "title", kp.getTitle(),
                    "chapter", kp.getChapter() != null ? kp.getChapter() : "",
                    "questionCount", count, "unresolvedCount", unresolved,
                    "level", count >= 5 ? "高频" : count >= 2 ? "中频" : "低频"));
            }
        }
        hotKnowledge.sort((a, b) -> Long.compare((long) b.get("questionCount"), (long) a.get("questionCount")));

        // 2. 学生活跃度统计（含预警）
        List<Map<String, Object>> studentActivity = new ArrayList<>();
        List<Long> memberIds = new ArrayList<>();
        if (groupId != null) {
            var members = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
            for (var m : members) memberIds.add(m.getUserId());
        }

        // Get all users who have any Q&A activity
        var qaQuery = new LambdaQueryWrapper<QaRecord>().orderByDesc(QaRecord::getAskedAt);
        if (!memberIds.isEmpty()) qaQuery.in(QaRecord::getUserId, memberIds);
        List<QaRecord> allQa = qaRecordMapper.selectList(qaQuery);

        var testQuery = new LambdaQueryWrapper<SelfTestRecord>().orderByDesc(SelfTestRecord::getTakenAt);
        if (!memberIds.isEmpty()) testQuery.in(SelfTestRecord::getUserId, memberIds);
        List<SelfTestRecord> allTests = selfTestMapper.selectList(testQuery);

        Map<Long, Map<String, Object>> userMap = new LinkedHashMap<>();
        for (QaRecord q : allQa) {
            if (q.getUserId() == null) continue; // skip orphan records
            userMap.computeIfAbsent(q.getUserId(), k -> {
                var u = userMapper.selectById(k);
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", k); m.put("userName", u != null ? u.getRealName() : "");
                m.put("qaCount", 0L); m.put("resolvedCount", 0L);
                m.put("quizCount", 0); m.put("avgQuizScore", 0.0);
                return m;
            });
            Map<String, Object> s = userMap.get(q.getUserId());
            s.put("qaCount", (Long) s.get("qaCount") + 1);
            if (Boolean.TRUE.equals(q.getIsResolved())) s.put("resolvedCount", (Long) s.get("resolvedCount") + 1);
        }
        for (SelfTestRecord t : allTests) {
            if (t.getUserId() == null) continue;
            userMap.computeIfAbsent(t.getUserId(), k -> {
                var u = userMapper.selectById(k);
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", k); m.put("userName", u != null ? u.getRealName() : "");
                m.put("qaCount", 0L); m.put("resolvedCount", 0L);
                m.put("quizCount", 0); m.put("avgQuizScore", 0.0);
                return m;
            });
            Map<String, Object> s = userMap.get(t.getUserId());
            int qc = (Integer) s.get("quizCount") + 1;
            double oldAvg = (Double) s.get("avgQuizScore");
            double newScore = t.getTotal() != null && t.getTotal() > 0 ? t.getScore() / t.getTotal() * 100 : 0;
            s.put("quizCount", qc);
            s.put("avgQuizScore", Math.round(((oldAvg * (qc - 1) + newScore) / qc) * 10.0) / 10.0);
        }

        // Mark alerts and enrich with achievement/contribution/score data
        int alertCount = 0;
        for (var entry : userMap.entrySet()) {
            Map<String, Object> s = entry.getValue();
            long qaCount = (Long) s.get("qaCount");
            boolean lowActivity = qaCount < 3;
            s.put("alert", lowActivity ? "提问活跃度低" : "正常");
            if (lowActivity) alertCount++;

            // Enrich with personal score data
            Long uid = (Long) s.get("userId");
            try {
                var scores = scoreMapper.selectList(
                    new LambdaQueryWrapper<PersonalScore>().eq(PersonalScore::getUserId, uid));
                if (!scores.isEmpty()) {
                    var latest = scores.get(0);
                    s.put("contributionRatio", latest.getContributionRatio());
                    s.put("finalScore", latest.getFinalScore());
                    s.put("groupTotalScore", latest.getGroupTotalScore());
                } else {
                    s.put("contributionRatio", null);
                    s.put("finalScore", null);
                    s.put("groupTotalScore", null);
                }
                // Get achievement average
                var memberships = groupMemberMapper.selectList(
                    new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, uid));
                double achSum = 0; int achCount = 0;
                for (var m : memberships) {
                    var results = resultMapper.selectList(
                        new LambdaQueryWrapper<AchievementResult>()
                            .eq(AchievementResult::getGroupId, m.getGroupId())
                            .orderByDesc(AchievementResult::getCalcRound));
                    if (!results.isEmpty()) {
                        int lr = results.get(0).getCalcRound() != null ? results.get(0).getCalcRound() : 0;
                        achSum += results.stream()
                            .filter(r -> r.getCalcRound() != null && r.getCalcRound().equals(lr) && r.getAchievementValue() != null)
                            .mapToDouble(AchievementResult::getAchievementValue).average().orElse(0);
                        achCount++;
                    }
                }
                s.put("achievement", achCount > 0 ? Math.round(achSum / achCount * 10000.0) / 10000.0 : 0);
            } catch (Exception e) { log.debug("Skipping score enrichment for userId={}: {}", uid, e.getMessage()); }

            studentActivity.add(s);
        }
        studentActivity.sort((a, b) -> Long.compare((Long) a.get("qaCount"), (Long) b.get("qaCount")));

        return R.ok(Map.of(
            "hotKnowledgePoints", hotKnowledge,
            "studentActivity", studentActivity,
            "alertCount", alertCount,
            "totalQuestions", allQa.size(),
            "totalQuizzes", allTests.size(),
            "knowledgeCount", allKps.size()
        ));
    }

    @GetMapping("/student-trajectory/{userId}")
    @Operation(summary = "单个学生问答轨迹（教师查看）")
    public R<Map<String, Object>> studentTrajectory(@PathVariable Long userId) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可查看");
        var questions = qaRecordMapper.selectList(
            new LambdaQueryWrapper<QaRecord>().eq(QaRecord::getUserId, userId)
                .orderByAsc(QaRecord::getAskedAt));
        var tests = selfTestMapper.selectList(
            new LambdaQueryWrapper<SelfTestRecord>().eq(SelfTestRecord::getUserId, userId)
                .orderByAsc(SelfTestRecord::getTakenAt));
        var user = userMapper.selectById(userId);

        return R.ok(Map.of(
            "userId", userId,
            "userName", user != null ? user.getRealName() : "",
            "questions", questions,
            "tests", tests,
            "totalQuestions", questions.size(),
            "totalTests", tests.size()
        ));
    }

    // ========== 教师出题管理 ==========

    @PostMapping("/generate-questions")
    @Operation(summary = "AI辅助出题（教师审核用）")
    public R<Map<String, Object>> generateQuestions(@RequestBody Map<String, Object> body) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可出题");
        int count = body.get("count") != null ? Integer.parseInt(body.get("count").toString()) : 5;
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;
        String topic = (String) body.getOrDefault("topic", "");

        StringBuilder ctx = new StringBuilder();
        if (courseId != null) {
            var objs = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));
            for (var obj : objs) ctx.append("课程目标：").append(obj.getTitle()).append("\n");
        }
        var kps = knowledgePointMapper.selectList(null);
        for (var kp : kps) ctx.append("【").append(kp.getChapter()).append("】").append(kp.getTitle()).append("：").append(kp.getContent()).append("\n");

        String prompt = "生成" + count + "道" + (topic.isEmpty() ? "" : "关于「" + topic + "」的") + "单选题（4选项），严格JSON格式：[{\"question\":\"...\",\"options\":[\"A...\",\"B...\",\"C...\",\"D...\"],\"answer\":\"A\"}]";
        if (ctx.length() > 0) prompt += "\n参考知识：\n" + ctx;

        String aiResp = callDeepSeek(prompt);
        return R.ok(Map.of("questions", extractJson(aiResp), "raw", aiResp));
    }

    @PostMapping("/quizzes")
    @Operation(summary = "教师保存/创建测验")
    public R<Quiz> createQuiz(@RequestBody Quiz quiz) {
        quiz.setTeacherId(currentUserId());
        quiz.setStatus("DRAFT");
        quiz.setCreatedAt(LocalDateTime.now());
        quizMapper.insert(quiz);
        return R.ok(quiz);
    }

    @PutMapping("/quizzes/{id}")
    @Operation(summary = "教师编辑测验题目")
    public R<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
        Quiz existing = quizMapper.selectById(id);
        if (existing == null) return R.fail(404, "测验不存在");
        if (!existing.getTeacherId().equals(currentUserId()))
            return R.fail(403, "只能编辑自己的测验");
        quiz.setId(id); quizMapper.updateById(quiz);
        return R.ok(quiz);
    }

    @PostMapping("/quizzes/{id}/publish")
    @Operation(summary = "教师发布测验给学生")
    public R<Quiz> publishQuiz(@PathVariable Long id) {
        Quiz quiz = quizMapper.selectById(id);
        if (quiz == null) return R.fail(404, "不存在");
        if (!quiz.getTeacherId().equals(currentUserId())) return R.fail(403, "无权限");
        quiz.setStatus("PUBLISHED"); quiz.setPublishedAt(LocalDateTime.now());
        quizMapper.updateById(quiz);
        return R.ok(quiz);
    }

    @DeleteMapping("/quizzes/{id}")
    @Operation(summary = "删除测验")
    public R<Void> deleteQuiz(@PathVariable Long id) {
        Quiz q = quizMapper.selectById(id);
        if (q != null && !q.getTeacherId().equals(currentUserId())) return R.fail(403, "无权限");
        quizMapper.deleteById(id);
        return R.ok();
    }

    @GetMapping("/quizzes")
    @Operation(summary = "教师查看自己的测验列表")
    public R<List<Quiz>> teacherQuizzes() {
        return R.ok(quizMapper.selectList(new LambdaQueryWrapper<Quiz>()
            .eq(Quiz::getTeacherId, currentUserId()).orderByDesc(Quiz::getCreatedAt)));
    }

    @GetMapping("/quizzes/{id}")
    @Operation(summary = "测验详情")
    public R<Quiz> getQuiz(@PathVariable Long id) {
        Quiz q = quizMapper.selectById(id);
        if (q == null) return R.fail(404, "不存在");
        return R.ok(q);
    }

    // ========== 学生端 ==========

    @GetMapping("/student-quizzes")
    @Operation(summary = "学生查看已发布的测验")
    public R<List<Quiz>> studentQuizzes(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<Quiz>().eq(Quiz::getStatus, "PUBLISHED").orderByDesc(Quiz::getPublishedAt);
        if (groupId != null) wq.and(w -> w.isNull(Quiz::getGroupId).or().eq(Quiz::getGroupId, groupId));
        else wq.isNull(Quiz::getGroupId);
        return R.ok(quizMapper.selectList(wq));
    }

    @PostMapping("/quizzes/{id}/submit")
    @Operation(summary = "学生提交测验答案")
    @Transactional
    public R<Map<String, Object>> submitQuizAnswer(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = currentUserId();
        Quiz quiz = quizMapper.selectById(id);
        if (quiz == null || !"PUBLISHED".equals(quiz.getStatus())) return R.fail(400, "测验不可用");

        @SuppressWarnings("unchecked")
        var answers = (List<Map<String, Object>>) body.get("answers");
        if (answers == null || answers.isEmpty()) return R.fail(400, "请提供答案");

        int correct = 0;
        try {
            var origQuestions = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(quiz.getQuestions(), List.class);
            for (int i = 0; i < answers.size(); i++) {
                var a = answers.get(i);
                if (i < origQuestions.size()) {
                    var orig = (Map<String, Object>) origQuestions.get(i);
                    if (a.getOrDefault("userAnswer", "").equals(orig.get("answer"))) correct++;
                }
            }
        } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }

        SelfTestRecord record = new SelfTestRecord();
        record.setUserId(userId);
        record.setQuestions(toJson(answers));
        record.setScore((double) correct);
        record.setTotal((double) answers.size());
        record.setTakenAt(LocalDateTime.now());
        selfTestMapper.insert(record);

        return R.ok(Map.of("correct", correct, "total", answers.size(),
            "score", answers.size() > 0 ? Math.round(correct * 100.0 / answers.size()) : 0));
    }

    private String groupName(Long gid) {
        if (gid == null) return "?";
        var g = groupMapper.selectById(gid);
        if (g != null) return g.getGroupName();
        // 已删除小组，直接查库
        try {
            var all = groupMapper.selectList(new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getId, gid));
            if (!all.isEmpty()) return all.get(0).getGroupName() + "(已删除)";
        } catch (Exception e) { log.debug("Context enrichment skipped: {}", e.getMessage()); }
        return "小组" + gid;
    }

    // ========== helpers ==========

    private String extractJson(String text) {
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        start = text.indexOf('{');
        end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }

    private String toJson(Object obj) {
        try { return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj); }
        catch (Exception e) { return "{}"; }
    }

    /** 调用 AI：优先 DeepSeek，失败回退本地提示 */
    private String callAI(String question) {
        if (!deepseekApiKey.isEmpty()) {
            try { return callDeepSeek(question); }
            catch (Exception e) { log.warn("AI调用失败: {}", e.getMessage()); }
        }
        return "🤖 AI服务暂不可用。建议：\n1. 查看课程目标树了解知识点\n2. 在「答疑解惑」向老师提问\n3. 与小组同学讨论";
    }

    /** 直连 DeepSeek API (OpenAI 兼容格式) */
    private String callDeepSeek(String question) {
        RestTemplate rt = createRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepseekApiKey);

        Map<String, Object> reqBody = new LinkedHashMap<>();
        reqBody.put("model", deepseekModel);
        reqBody.put("messages", List.of(
            Map.of("role", "system", "content", "你是OBE-CDIO课程AI助手，专注于工程教育、成果导向教育(OBE)和CDIO教学理念。请用简洁中文回答，不超过500字。"),
            Map.of("role", "user", "content", question)
        ));
        reqBody.put("max_tokens", 1000);
        reqBody.put("temperature", 0.7);

        String apiUrl = deepseekBaseUrl.endsWith("/") ? deepseekBaseUrl + "chat/completions" : deepseekBaseUrl + "/chat/completions";
        ResponseEntity<Map> response = rt.exchange(apiUrl,
            HttpMethod.POST, new HttpEntity<>(reqBody, headers), Map.class);

        if (response.getBody() != null) {
            var choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                var message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null && message.get("content") != null) {
                    return message.get("content").toString();
                }
            }
        }
        throw new RuntimeException("DeepSeek API returned empty response");
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(10000); f.setReadTimeout(30000);
        return new RestTemplate(f);
    }
}
