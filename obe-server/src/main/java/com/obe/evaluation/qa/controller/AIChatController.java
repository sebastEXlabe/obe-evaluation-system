/**
 * AI智能问答控制器 — DeepSeek + MaxKB双通道
 * 职责：智能问答、AI出题批改、教师学情看板、测验管理
 * 学生提问自动关联课程知识库作为上下文
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
import com.obe.evaluation.course.entity.CourseObjective;
import com.obe.evaluation.course.mapper.CourseObjectiveMapper;
import com.obe.evaluation.group.entity.GroupMember;
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

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController @RequestMapping("/api/ai-chat") @RequiredArgsConstructor
@Tag(name = "AI智能问答（MaxKB）")
public class AIChatController {

    private final QaRecordMapper qaRecordMapper;
    private final KnowledgePointMapper knowledgePointMapper;
    private final SelfTestRecordMapper selfTestMapper;
    private final QuizMapper quizMapper;
    private final CourseObjectiveMapper objectiveMapper;
    private final ProjectGroupMapper groupMapper;
    private final com.obe.evaluation.group.mapper.GroupMemberMapper groupMemberMapper;
    private final com.obe.evaluation.system.mapper.SysUserMapper userMapper;

    @Value("${maxkb.base-url:}")
    private String maxkbBaseUrl;

    @Value("${maxkb.api-key:}")
    private String maxkbApiKey;

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

        // 有课程上下文时附加上课程目标
        if (courseId != null) {
            var objs = objectiveMapper.selectList(
                new LambdaQueryWrapper<CourseObjective>().eq(CourseObjective::getCourseId, courseId));
            for (var obj : objs) {
                knowledgeContext.append("课程目标[").append(obj.getDimension()).append("]：").append(obj.getTitle()).append("\n");
            }
        }

        QaRecord record = new QaRecord();
        record.setUserId(userId);
        record.setKnowledgeId(matchedKnowledgeId);
        record.setQuestion(question);
        record.setSessionId(sessionId);
        record.setQuestionType(matchedKnowledgeId != null ? "KNOWLEDGE" : "GENERAL");
        record.setIsResolved(false);
        record.setAskedAt(LocalDateTime.now());

        // 将知识库上下文传给AI以获得更精准回答
        String enrichedQuestion = knowledgeContext.length() > 0
            ? "请基于以下课程知识内容回答用户问题。\n\n=== 课程知识库 ===\n" + knowledgeContext + "\n=== 用户问题 ===\n" + question
            : question;

        String answer = callMaxKB(enrichedQuestion, userId);
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
        return R.ok(Map.of("hasAI", !deepseekApiKey.isEmpty() || !maxkbBaseUrl.isEmpty(),
            "model", !deepseekApiKey.isEmpty() ? "DeepSeek" : "MaxKB"));
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
        } catch (Exception ignored) {}

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

        // Mark alerts: low activity
        int alertCount = 0;
        for (var entry : userMap.entrySet()) {
            Map<String, Object> s = entry.getValue();
            long qaCount = (Long) s.get("qaCount");
            boolean lowActivity = qaCount < 3;
            s.put("alert", lowActivity ? "提问活跃度低" : "正常");
            if (lowActivity) alertCount++;
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
        } catch (Exception ignored) {}

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

    private String callMaxKB(String question, Long userId) {
        // 1. 优先直连 DeepSeek API
        if (!deepseekApiKey.isEmpty()) {
            try {
                return callDeepSeek(question);
            } catch (Exception e) {
                log.warn("DeepSeek API 调用失败: {}", e.getMessage());
            }
        }

        // 2. 尝试 MaxKB
        if (!maxkbBaseUrl.isEmpty()) {
            try {
                RestTemplate rt = createRestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(maxkbApiKey);

                Map<String, Object> reqBody = new LinkedHashMap<>();
                reqBody.put("query", question);
                reqBody.put("user_id", userId);

                ResponseEntity<Map> response = rt.exchange(maxkbBaseUrl + "/api/chat",
                    HttpMethod.POST, new HttpEntity<>(reqBody, headers), Map.class);
                if (response.getBody() != null && response.getBody().get("answer") != null) {
                    return response.getBody().get("answer").toString();
                }
            } catch (Exception e) {
                log.warn("MaxKB调用失败: {}", e.getMessage());
            }
        }

        // 3. 兜底
        return "🤖 关于「" + question + "」的问题：\n\n"
            + "当前AI服务未连接，建议：\n"
            + "1. 查看课程目标和指标点树了解知识点\n"
            + "2. 在「答疑解惑」模块向老师提问\n"
            + "3. 与小组同学讨论交流\n\n"
            + "💡 管理员可配置 DEEPSEEK_API_KEY 环境变量启用AI服务。";
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
