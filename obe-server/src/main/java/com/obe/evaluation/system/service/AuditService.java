package com.obe.evaluation.system.service;

import com.obe.evaluation.system.entity.AuditLog;
import com.obe.evaluation.system.mapper.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j @Service @RequiredArgsConstructor
public class AuditService {
    private final AuditLogMapper auditLogMapper;

    public void log(Long userId, String username, String action, String target) {
        log(userId, username, action, target, null);
    }

    public void log(Long userId, String username, String action, String target, HttpServletRequest request) {
        try {
            AuditLog log = new AuditLog();
            log.setUserId(userId); log.setUsername(username);
            log.setAction(action); log.setTarget(target);
            // FIX 4: Extract real IP from X-Forwarded-For or getRemoteAddr
            String ip = "127.0.0.1";
            if (request != null) {
                String forwarded = request.getHeader("X-Forwarded-For");
                if (forwarded != null && !forwarded.isBlank()) {
                    ip = forwarded.split(",")[0].trim();
                } else {
                    ip = request.getRemoteAddr();
                }
            }
            log.setIp(ip);
            log.setCreatedAt(LocalDateTime.now());
            auditLogMapper.insert(log);
        } catch (Exception e) { log.warn("Audit log failed: {}", e.getMessage()); }
    }
}
