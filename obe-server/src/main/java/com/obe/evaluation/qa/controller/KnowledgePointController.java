package com.obe.evaluation.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obe.evaluation.common.PageQuery;
import com.obe.evaluation.common.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obe.evaluation.qa.entity.KnowledgePoint;
import com.obe.evaluation.qa.mapper.KnowledgePointMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/knowledge-points")
@RequiredArgsConstructor
@Tag(name = "知识点管理")
public class KnowledgePointController {

    private final KnowledgePointMapper knowledgePointMapper;

    private boolean canModify() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> List.of("ROLE_ADMIN", "ROLE_TEACHER").contains(a.getAuthority()));
    }

    @GetMapping
    @Operation(summary = "查询知识点列表")
    public R<Page<KnowledgePoint>> list(@RequestParam(required = false) String chapter, PageQuery q) {
        var wq = new LambdaQueryWrapper<KnowledgePoint>();
        if (chapter != null && !chapter.isBlank()) {
            wq.eq(KnowledgePoint::getChapter, chapter);
        }
        wq.orderByAsc(KnowledgePoint::getCreatedAt);
        return R.ok(knowledgePointMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单个知识点")
    public R<KnowledgePoint> getById(@PathVariable Long id) {
        return R.ok(knowledgePointMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "创建知识点")
    public R<KnowledgePoint> create(@RequestBody KnowledgePoint point) {
        if (!canModify()) return R.fail(403, "仅管理员和教师可创建知识点");
        if (point.getCreatedAt() == null) point.setCreatedAt(LocalDateTime.now());
        knowledgePointMapper.insert(point);
        return R.ok(point);
    }

    @PutMapping
    @Operation(summary = "更新知识点")
    public R<KnowledgePoint> update(@RequestBody KnowledgePoint point) {
        if (!canModify()) return R.fail(403, "仅管理员和教师可更新知识点");
        knowledgePointMapper.updateById(point);
        return R.ok(point);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识点")
    public R<Void> delete(@PathVariable Long id) {
        if (!canModify()) return R.fail(403, "仅管理员和教师可删除知识点");
        knowledgePointMapper.deleteById(id);
        return R.ok();
    }

    @GetMapping("/chapters")
    @Operation(summary = "获取所有章节")
    public R<?> listChapters() {
        var points = knowledgePointMapper.selectList(
                new LambdaQueryWrapper<KnowledgePoint>()
                        .select(KnowledgePoint::getChapter)
                        .groupBy(KnowledgePoint::getChapter));
        return R.ok(points.stream().map(KnowledgePoint::getChapter).distinct().toList());
    }

    @GetMapping("/search")
    @Operation(summary = "搜索知识点")
    public R<Page<KnowledgePoint>> search(@RequestParam String keyword, PageQuery q) {
        var wq = new LambdaQueryWrapper<KnowledgePoint>()
                .like(KnowledgePoint::getTitle, keyword)
                .or()
                .like(KnowledgePoint::getContent, keyword);
        return R.ok(knowledgePointMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wq));
    }

    // ========== 附件 ==========

    private static final String KP_UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/knowledge/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/{id}/upload")
    @Operation(summary = "上传知识点附件（图片/PDF/文本）")
    public R<Map<String, Object>> uploadAttachment(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (!canModify()) return R.fail(403, "无权限");
        KnowledgePoint kp = knowledgePointMapper.selectById(id);
        if (kp == null) return R.fail(404, "知识点不存在");
        try {
            Path uploadPath = Paths.get(KP_UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")) : "";
            String storedName = UUID.randomUUID().toString() + ext;
            file.transferTo(uploadPath.resolve(storedName).toFile());

            List<Map<String, Object>> atts = parseJson(kp.getAttachments());
            Map<String, Object> att = new LinkedHashMap<>();
            att.put("name", file.getOriginalFilename()); att.put("path", storedName);
            att.put("size", file.getSize()); att.put("contentType", file.getContentType());
            atts.add(att);
            kp.setAttachments(toJson(atts));
            knowledgePointMapper.updateById(kp);
            return R.ok(Map.of("attachment", att, "totalAttachments", atts.size()));
        } catch (IOException e) {
            log.error("Upload failed: {}", e.getMessage());
            return R.fail(500, "上传失败");
        }
    }

    @GetMapping("/{id}/attachments/{filename}")
    @Operation(summary = "下载/查看知识点附件")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id, @PathVariable String filename) throws IOException {
        KnowledgePoint kp = knowledgePointMapper.selectById(id);
        if (kp == null) return ResponseEntity.notFound().build();
        Path filePath = Paths.get(KP_UPLOAD_DIR, filename);
        if (!Files.exists(filePath)) return ResponseEntity.notFound().build();
        byte[] content = Files.readAllBytes(filePath);
        String ct = Files.probeContentType(filePath);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
            .body(content);
    }

    @DeleteMapping("/{id}/attachments/{filename}")
    @Operation(summary = "删除知识点附件")
    public R<Void> deleteAttachment(@PathVariable Long id, @PathVariable String filename) {
        if (!canModify()) return R.fail(403, "无权限");
        KnowledgePoint kp = knowledgePointMapper.selectById(id);
        if (kp == null) return R.fail(404, "不存在");
        try {
            Files.deleteIfExists(Paths.get(KP_UPLOAD_DIR, filename));
            List<Map<String, Object>> atts = parseJson(kp.getAttachments());
            atts.removeIf(a -> filename.equals(a.get("path")));
            kp.setAttachments(toJson(atts));
            knowledgePointMapper.updateById(kp);
            return R.ok();
        } catch (IOException e) { return R.fail(500, "删除失败"); }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseJson(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try { return objectMapper.readValue(json, List.class); } catch (Exception e) { return new ArrayList<>(); }
    }
    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); } catch (Exception e) { return "[]"; }
    }
}
