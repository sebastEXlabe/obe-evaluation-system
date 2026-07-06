package com.obe.evaluation.qa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * MaxKB 知识库检索服务
 * 直连 MaxKB PostgreSQL 查询文档段落，带超时保护
 */
@Slf4j
@Service
public class MaxKBService {

    @Value("${maxkb.db.url:jdbc:postgresql://localhost:5433/maxkb}")
    private String dbUrl;

    @Value("${maxkb.db.username:root}")
    private String dbUser;

    @Value("${maxkb.db.password:}")
    private String dbPassword;

    private boolean dbAvailable = true;
    private long lastCheckTime = 0;

    private Connection getConnection() throws SQLException {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("user", dbUser);
        props.setProperty("password", dbPassword);
        props.setProperty("connectTimeout", "3");
        props.setProperty("socketTimeout", "5");
        return DriverManager.getConnection(dbUrl, props);
    }

    /**
     * 根据关键词搜索 MaxKB 知识库段落（带超时保护，3秒内未响应则跳过）
     */
    public List<String> searchParagraphs(String keyword, int limit) {
        if (!dbAvailable && System.currentTimeMillis() - lastCheckTime < 30000) {
            return List.of(); // DB不可用时30秒内不再重试
        }
        try {
            return CompletableFuture.supplyAsync(() -> doSearch(keyword, limit))
                .get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            dbAvailable = false;
            lastCheckTime = System.currentTimeMillis();
            log.debug("MaxKB search timeout/unavailable: {}", e.getMessage());
            return List.of();
        }
    }

    private List<String> doSearch(String keyword, int limit) {
        List<String> results = new ArrayList<>();
        String sql = """
            SELECT p.content, d.name as doc_name
            FROM paragraph p
            JOIN document d ON d.id = p.document_id
            WHERE p.content ILIKE ?
            LIMIT ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String doc = rs.getString("doc_name");
                    String content = rs.getString("content");
                    if (content != null && !content.isBlank()) {
                        results.add("【" + (doc != null ? doc : "知识库") + "】" + content.trim());
                    }
                }
            }
            dbAvailable = true;
        } catch (SQLException e) {
            log.warn("MaxKB查询失败: {}", e.getMessage());
            dbAvailable = false;
            lastCheckTime = System.currentTimeMillis();
        }
        return results;
    }

    /**
     * 获取全部知识点摘要
     */
    public List<String> getAllParagraphs(int limit) {
        List<String> results = new ArrayList<>();
        String sql = """
            SELECT p.content, d.name as doc_name
            FROM paragraph p
            JOIN document d ON d.id = p.document_id
            WHERE p.content IS NOT NULL AND p.content != ''
            ORDER BY p.create_time DESC
            LIMIT ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String doc = rs.getString("doc_name");
                    String content = rs.getString("content");
                    if (content != null && !content.isBlank()) {
                        results.add("【" + (doc != null ? doc : "知识库") + "】" + content.trim());
                    }
                }
            }
        } catch (SQLException e) {
            log.warn("MaxKB知识库全量查询失败: {}", e.getMessage());
        }
        return results;
    }
}
