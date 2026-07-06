/**
 * 师生答疑控制器 — 学生提问 → 教师回答 → 标记解决
 * 支持文件上传下载、按小组权限隔离、角色校验
 */
package com.obe.evaluation.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obe.evaluation.common.PageQuery;
import com.obe.evaluation.common.R;
import com.obe.evaluation.qa.entity.QuestionAnswer;
import com.obe.evaluation.qa.mapper.QuestionAnswerMapper;
import com.obe.evaluation.group.entity.GroupMember;
import com.obe.evaluation.group.entity.ProjectGroup;
import com.obe.evaluation.group.mapper.GroupMemberMapper;
import com.obe.evaluation.group.mapper.ProjectGroupMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController @RequestMapping("/api/qa") @RequiredArgsConstructor
@Tag(name = "留言问答模块（学生→教师）")
public class QaController {

    private final QuestionAnswerMapper questionMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final ProjectGroupMapper groupMapper;
    private final SysUserMapper userMapper;
    private final com.obe.evaluation.common.StorageService storageService;

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

    private String currentRole() {
        if (isTeacherOrAdmin()) return "TEACHER";
        return "STUDENT";
    }

    private boolean isMemberOfGroup(Long userId, Long groupId) {
        return groupMemberMapper.selectCount(
            new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getUserId, userId)
                .eq(GroupMember::getGroupId, groupId)) > 0;
    }

    /** 学生所属的小组ID列表 */
    private List<Long> getStudentGroupIds(Long userId) {
        return groupMemberMapper.selectList(
            new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, userId))
            .stream().map(GroupMember::getGroupId).toList();
    }

    /** 教师负责的小组ID列表 */
    private List<Long> getTeacherGroupIds(Long teacherId) {
        return groupMapper.selectList(
            new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getTeacherId, teacherId))
            .stream().map(ProjectGroup::getId).toList();
    }

    /** 获取当前用户可见的小组ID列表 */
    private List<Long> getVisibleGroupIds(Long userId) {
        if (isTeacherOrAdmin()) {
            // 教师：可看所有小组（管理员看全部，教师看自己负责的）
            if (currentRole().equals("ADMIN")) {
                return groupMapper.selectList(null).stream().map(ProjectGroup::getId).toList();
            }
            return getTeacherGroupIds(userId);
        }
        // 学生：只能看自己所在的小组
        return getStudentGroupIds(userId);
    }

    // ========== 学生提问 ==========

    @PostMapping("/questions")
    @Operation(summary = "学生发布问题（自动校验小组归属）")
    @Transactional
    public R<QuestionAnswer> askQuestion(@RequestBody QuestionAnswer q) {
        Long userId = currentUserId();
        // 小组归属校验
        if (q.getGroupId() != null) {
            if (!isTeacherOrAdmin() && !isMemberOfGroup(userId, q.getGroupId())) {
                return R.fail(403, "你不在该小组中，无法向该小组提问");
            }
        } else if (!isTeacherOrAdmin()) {
            // 学生未指定小组 → 自动设为第一个所在小组
            List<Long> myGroups = getStudentGroupIds(userId);
            if (!myGroups.isEmpty()) q.setGroupId(myGroups.get(0));
        }
        q.setAskUserId(userId);
        q.setStatus("PENDING");
        q.setAskedAt(LocalDateTime.now());
        q.setCreatedAt(LocalDateTime.now());
        if (q.getTitle() == null || q.getTitle().isBlank()) return R.fail(400, "问题标题不能为空");
        if (q.getQuestion() == null || q.getQuestion().isBlank()) return R.fail(400, "问题内容不能为空");
        questionMapper.insert(q);
        return R.ok(q);
    }

    // ========== 文件上传 ==========

    @PostMapping("/questions/{id}/upload")
    @Operation(summary = "为问题上传附件")
    public R<Map<String, Object>> uploadAttachment(@PathVariable Long id,
                                                    @RequestParam("file") MultipartFile file) {
        QuestionAnswer q = questionMapper.selectById(id);
        if (q == null) return R.fail(404, "问题不存在");
        Long userId = currentUserId();
        if (!q.getAskUserId().equals(userId) && !isTeacherOrAdmin())
            return R.fail(403, "只能为自己的问题上传附件");

        try {
            String originalName = file.getOriginalFilename();
            String storedName = storageService.upload("qa-attachments", file);

            // Update attachments JSON
            List<Map<String, Object>> attachments = parseAttachments(q.getAttachments());
            Map<String, Object> att = new LinkedHashMap<>();
            att.put("name", originalName);
            att.put("path", storedName);
            att.put("size", file.getSize());
            att.put("contentType", file.getContentType());
            attachments.add(att);
            q.setAttachments(toJson(attachments));
            questionMapper.updateById(q);

            return R.ok(Map.of("attachment", att, "totalAttachments", attachments.size()));
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return R.fail(500, "文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/questions/{id}/attachments/{filename}")
    @Operation(summary = "下载附件")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id, @PathVariable String filename) throws IOException {
        QuestionAnswer q = questionMapper.selectById(id);
        if (q == null) return ResponseEntity.notFound().build();

        Long userId = currentUserId();
        boolean isAsker = q.getAskUserId().equals(userId);
        boolean isTeacher = isTeacherOrAdmin();
        boolean isSameGroup = q.getGroupId() != null && isMemberOfGroup(userId, q.getGroupId());

        if (!isAsker && !isTeacher && !isSameGroup)
            return ResponseEntity.status(403).build();

        // 从MinIO下载
        InputStream is = storageService.download("qa-attachments", filename);
        String originalName = filename;
        List<Map<String, Object>> atts = parseAttachments(q.getAttachments());
        for (var att : atts) {
            if (filename.equals(att.get("path"))) {
                originalName = (String) att.getOrDefault("name", filename);
                break;
            }
        }

        byte[] content = is.readAllBytes();
        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                new String(originalName.getBytes(java.nio.charset.StandardCharsets.UTF_8), java.nio.charset.StandardCharsets.ISO_8859_1) + "\"")
            .body(content);
    }

    // ========== 查询 ==========

    @GetMapping("/questions")
    @Operation(summary = "查询问题列表（按组过滤，自动根据角色限范围）")
    public R<Page<Map<String, Object>>> listQuestions(@RequestParam(required = false) Long groupId,
                                                       @RequestParam(required = false) String status,
                                                       PageQuery q) {
        Long userId = currentUserId();
        var wq = new LambdaQueryWrapper<QuestionAnswer>().orderByDesc(QuestionAnswer::getAskedAt);

        // 角色范围限制
        if (isTeacherOrAdmin()) {
            // 教师/管理员：只看自己管理的组
            List<Long> teacherGroupIds = getVisibleGroupIds(userId);
            if (!teacherGroupIds.isEmpty()) {
                wq.in(QuestionAnswer::getGroupId, teacherGroupIds);
            }
            if (groupId != null) wq.eq(QuestionAnswer::getGroupId, groupId);
        } else {
            // 学生：只看自己的问题 + 自己所在组的已回答问题
            List<Long> myGroupIds = getStudentGroupIds(userId);
            if (myGroupIds.isEmpty()) {
                wq.eq(QuestionAnswer::getAskUserId, userId);
            } else {
                wq.and(w -> w.eq(QuestionAnswer::getAskUserId, userId)
                    .or().in(QuestionAnswer::getGroupId, myGroupIds));
            }
            if (groupId != null && myGroupIds.contains(groupId)) {
                wq.eq(QuestionAnswer::getGroupId, groupId);
            }
        }

        if (status != null && !status.isEmpty()) {
            wq.eq(QuestionAnswer::getStatus, status);
        }

        Page<QuestionAnswer> page = questionMapper.selectPage(
            new Page<>(q.getPage(), q.getSize()), wq);

        // Enrich with user names
        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (QuestionAnswer qa : page.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", qa.getId());
            item.put("groupId", qa.getGroupId());
            item.put("askUserId", qa.getAskUserId());
            item.put("answerUserId", qa.getAnswerUserId());
            item.put("title", qa.getTitle());
            item.put("question", qa.getQuestion());
            item.put("answer", qa.getAnswer());
            item.put("status", qa.getStatus());
            item.put("attachments", parseAttachments(qa.getAttachments()));
            item.put("askedAt", qa.getAskedAt());
            item.put("answeredAt", qa.getAnsweredAt());
            item.put("resolvedAt", qa.getResolvedAt());

            // Resolve names
            if (qa.getAskUserId() != null) {
                var askUser = userMapper.selectById(qa.getAskUserId());
                item.put("askUserName", askUser != null ? askUser.getRealName() : "未知");
            }
            if (qa.getAnswerUserId() != null) {
                var answerUser = userMapper.selectById(qa.getAnswerUserId());
                item.put("answerUserName", answerUser != null ? answerUser.getRealName() : "未知");
            }

            // Parse attachments count
            List<Map<String, Object>> atts = parseAttachments(qa.getAttachments());
            item.put("attachmentCount", atts.size());

            enriched.add(item);
        }
        result.setRecords(enriched);
        return R.ok(result);
    }

    @GetMapping("/questions/my")
    @Operation(summary = "我的提问")
    public R<List<QuestionAnswer>> myQuestions() {
        Long userId = currentUserId();
        return R.ok(questionMapper.selectList(
            new LambdaQueryWrapper<QuestionAnswer>()
                .eq(QuestionAnswer::getAskUserId, userId)
                .orderByDesc(QuestionAnswer::getAskedAt)));
    }

    @GetMapping("/questions/pending")
    @Operation(summary = "待回答的问题（教师端，仅显示自己负责的小组）")
    public R<List<Map<String, Object>>> pendingQuestions(@RequestParam(required = false) Long groupId) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可查看待回答问题");

        var wq = new LambdaQueryWrapper<QuestionAnswer>()
            .eq(QuestionAnswer::getStatus, "PENDING")
            .orderByDesc(QuestionAnswer::getAskedAt);

        // 限制为教师负责的组
        List<Long> teacherGroupIds = getVisibleGroupIds(currentUserId());
        if (!teacherGroupIds.isEmpty()) wq.in(QuestionAnswer::getGroupId, teacherGroupIds);
        if (groupId != null) wq.eq(QuestionAnswer::getGroupId, groupId);

        List<QuestionAnswer> questions = questionMapper.selectList(wq);
        List<Map<String, Object>> result = new ArrayList<>();
        for (QuestionAnswer qa : questions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", qa.getId());
            item.put("groupId", qa.getGroupId());
            item.put("title", qa.getTitle());
            item.put("question", qa.getQuestion());
            item.put("status", qa.getStatus());
            item.put("askedAt", qa.getAskedAt());
            item.put("attachmentCount", parseAttachments(qa.getAttachments()).size());

            var askUser = userMapper.selectById(qa.getAskUserId());
            item.put("askUserName", askUser != null ? askUser.getRealName() : "未知");
            result.add(item);
        }
        return R.ok(result);
    }

    @GetMapping("/questions/{id}")
    @Operation(summary = "问题详情")
    public R<Map<String, Object>> questionDetail(@PathVariable Long id) {
        QuestionAnswer qa = questionMapper.selectById(id);
        if (qa == null) return R.fail(404, "问题不存在");

        Long userId = currentUserId();
        boolean isAsker = qa.getAskUserId().equals(userId);
        boolean isTeacher = isTeacherOrAdmin();
        boolean isInGroup = qa.getGroupId() != null && isMemberOfGroup(userId, qa.getGroupId());

        if (!isAsker && !isTeacher && !isInGroup)
            return R.fail(403, "无权查看此问题");

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", qa.getId());
        item.put("groupId", qa.getGroupId());
        item.put("askUserId", qa.getAskUserId());
        item.put("answerUserId", qa.getAnswerUserId());
        item.put("title", qa.getTitle());
        item.put("question", qa.getQuestion());
        item.put("answer", qa.getAnswer());
        item.put("status", qa.getStatus());
        item.put("attachments", parseAttachments(qa.getAttachments()));
        item.put("askedAt", qa.getAskedAt());
        item.put("answeredAt", qa.getAnsweredAt());
        item.put("resolvedAt", qa.getResolvedAt());

        if (qa.getAskUserId() != null) {
            var askUser = userMapper.selectById(qa.getAskUserId());
            item.put("askUserName", askUser != null ? askUser.getRealName() : "未知");
        }
        if (qa.getAnswerUserId() != null) {
            var answerUser = userMapper.selectById(qa.getAnswerUserId());
            item.put("answerUserName", answerUser != null ? answerUser.getRealName() : "未知");
        }

        return R.ok(item);
    }

    // ========== 教师回答 ==========

    @PostMapping("/questions/{id}/answer")
    @Operation(summary = "教师回答问题")
    @Transactional
    public R<QuestionAnswer> answerQuestion(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (!isTeacherOrAdmin()) return R.fail(403, "仅教师可回答问题");

        QuestionAnswer qa = questionMapper.selectById(id);
        if (qa == null) return R.fail(404, "问题不存在");

        String answer = body.get("answer");
        if (answer == null || answer.isBlank()) return R.fail(400, "回答内容不能为空");

        qa.setAnswer(answer);
        qa.setAnswerUserId(currentUserId());
        qa.setStatus("ANSWERED");
        qa.setAnsweredAt(LocalDateTime.now());
        questionMapper.updateById(qa);

        return R.ok(qa);
    }

    @PutMapping("/questions/{id}/resolve")
    @Operation(summary = "标记问题为已解决")
    public R<QuestionAnswer> resolveQuestion(@PathVariable Long id) {
        QuestionAnswer qa = questionMapper.selectById(id);
        if (qa == null) return R.fail(404, "问题不存在");

        Long userId = currentUserId();
        if (!qa.getAskUserId().equals(userId) && !isTeacherOrAdmin())
            return R.fail(403, "仅提问者或教师可标记已解决");

        qa.setStatus("RESOLVED");
        qa.setResolvedAt(LocalDateTime.now());
        questionMapper.updateById(qa);
        return R.ok(qa);
    }

    @PutMapping("/questions/{id}/reopen")
    @Operation(summary = "重新打开问题")
    public R<QuestionAnswer> reopenQuestion(@PathVariable Long id) {
        QuestionAnswer qa = questionMapper.selectById(id);
        if (qa == null) return R.fail(404, "问题不存在");

        Long userId = currentUserId();
        if (!qa.getAskUserId().equals(userId))
            return R.fail(403, "仅提问者可重新打开");

        qa.setStatus("PENDING");
        questionMapper.updateById(qa);
        return R.ok(qa);
    }

    @DeleteMapping("/questions/{id}")
    @Operation(summary = "删除问题")
    public R<Void> deleteQuestion(@PathVariable Long id) {
        QuestionAnswer qa = questionMapper.selectById(id);
        if (qa == null) return R.fail(404, "问题不存在");

        Long userId = currentUserId();
        if (!qa.getAskUserId().equals(userId) && !isTeacherOrAdmin())
            return R.fail(403, "无权删除");

        questionMapper.deleteById(id);
        return R.ok();
    }

    // ========== 统计 ==========

    @GetMapping("/stats")
    @Operation(summary = "问答统计（教师看板）")
    public R<Map<String, Object>> stats(@RequestParam(required = false) Long groupId) {
        var wq = new LambdaQueryWrapper<QuestionAnswer>();
        if (groupId != null) wq.eq(QuestionAnswer::getGroupId, groupId);

        long pending = questionMapper.selectCount(
            wq.clone().eq(QuestionAnswer::getStatus, "PENDING"));
        long answered = questionMapper.selectCount(
            wq.clone().eq(QuestionAnswer::getStatus, "ANSWERED"));
        long resolved = questionMapper.selectCount(
            wq.clone().eq(QuestionAnswer::getStatus, "RESOLVED"));

        return R.ok(Map.of(
            "pendingCount", pending,
            "answeredCount", answered,
            "resolvedCount", resolved,
            "totalCount", pending + answered + resolved
        ));
    }

    // ========== helpers ==========

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseAttachments(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String toJson(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
}
