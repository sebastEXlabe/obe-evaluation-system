package com.obe.evaluation.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obe.evaluation.common.PageQuery;
import com.obe.evaluation.common.R;
import com.obe.evaluation.security.JwtUtil;
import com.obe.evaluation.security.TokenBlacklist;
import com.obe.evaluation.system.entity.AuditLog;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.mapper.AuditLogMapper;
import com.obe.evaluation.system.service.AuditService;
import com.obe.evaluation.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
@Tag(name = "认证接口")
public class AuthController {
    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;
    private final AuditLogMapper auditLogMapper;
    private final HttpServletRequest request;

    /** FIX 1: In-memory login rate limiter — track failed attempts by username */
    private static final Map<String, List<Long>> FAILED_LOGINS = new ConcurrentHashMap<>();
    private static final int MAX_LOGIN_FAILURES = 5;
    private static final long FAILURE_WINDOW_MS = 15 * 60 * 1000L;

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long id))
            throw new IllegalArgumentException("未登录");
        return id;
    }

    private boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<R<Map<String, Object>>> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password))
            return ResponseEntity.badRequest().body(R.fail(400, "用户名和密码不能为空"));

        // FIX 1: Rate limiting — check if this username is locked out
        List<Long> attempts = FAILED_LOGINS.computeIfAbsent(username,
                k -> Collections.synchronizedList(new ArrayList<>()));
        long now = System.currentTimeMillis();
        synchronized (attempts) {
            attempts.removeIf(t -> now - t > FAILURE_WINDOW_MS);
            if (attempts.size() >= MAX_LOGIN_FAILURES) {
                long oldestInWindow = attempts.get(0);
                if (now - oldestInWindow < FAILURE_WINDOW_MS) {
                    return ResponseEntity.status(429).body(R.fail(429, "账户已被锁定，请15分钟后重试"));
                }
            }
        }

        SysUser user = userService.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            synchronized (attempts) {
                attempts.add(System.currentTimeMillis());
            }
            return ResponseEntity.badRequest().body(R.fail(400, "用户名或密码错误"));
        }
        // Successful login — clear failure history
        FAILED_LOGINS.remove(username);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleCode());
        auditService.log(user.getId(), user.getUsername(), "LOGIN", "用户登录", request);
        return ResponseEntity.ok(R.ok(Map.of("token", token, "id", user.getId(),
            "username", user.getUsername(), "realName", user.getRealName(), "roleCode", user.getRoleCode())));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册（仅限学生）")
    public ResponseEntity<R<SysUser>> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");
        String realName = body.getOrDefault("realName", username).trim();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password))
            return ResponseEntity.badRequest().body(R.fail(400, "用户名和密码不能为空"));
        if (username.length() < 3 || password.length() < 6)
            return ResponseEntity.badRequest().body(R.fail(400, "用户名至少3位，密码至少6位"));
        if (userService.usernameExists(username))
            return ResponseEntity.badRequest().body(R.fail(400, "用户名已存在"));
        SysUser user = new SysUser(); user.setUsername(username); user.setRealName(realName);
        user.setRoleCode("STUDENT"); user.setStatus(1);
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user); user.setPassword(null);
        auditService.log(null, "system", "REGISTER", "用户注册: " + username, request);
        return ResponseEntity.ok(R.ok(user));
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public ResponseEntity<R<Void>> changePassword(@RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = currentUserId();
        String oldPwd = body.getOrDefault("oldPassword", "");
        String newPwd = body.getOrDefault("newPassword", "");
        if (StrUtil.isBlank(oldPwd) || StrUtil.isBlank(newPwd))
            return ResponseEntity.badRequest().body(R.fail(400, "密码不能为空"));
        if (newPwd.length() < 6) return ResponseEntity.badRequest().body(R.fail(400, "新密码至少6位"));
        SysUser user = userService.getById(userId);
        if (user == null || !passwordEncoder.matches(oldPwd, user.getPassword()))
            return ResponseEntity.badRequest().body(R.fail(400, "旧密码错误"));
        user.setPassword(passwordEncoder.encode(newPwd)); userService.updateById(user);
        auditService.log(userId, user.getUsername(), "CHANGE_PASSWORD", "用户修改密码", request);
        // FIX 2: Invalidate existing token so the user must re-login
        if (authHeader != null && authHeader.startsWith("Bearer "))
            TokenBlacklist.add(authHeader.substring(7));
        return ResponseEntity.ok(R.ok());
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public R<Void> updateProfile(@RequestBody Map<String, String> body) {
        SysUser u = new SysUser(); u.setId(currentUserId());
        if (body.containsKey("realName")) u.setRealName(body.get("realName"));
        if (body.containsKey("email")) u.setEmail(body.get("email"));
        if (body.containsKey("phone")) u.setPhone(body.get("phone"));
        userService.updateById(u); return R.ok();
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public R<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer "))
            TokenBlacklist.add(authHeader.substring(7));
        return R.ok();
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public R<SysUser> info() {
        SysUser user = userService.getById(currentUserId());
        if (user != null) user.setPassword(null);
        return R.ok(user);
    }

    @GetMapping("/users")
    @Operation(summary = "用户列表（分页）")
    public R<Page<SysUser>> listUsers(PageQuery q) {
        Page<SysUser> page = userService.page(new Page<>(q.getPage(), q.getSize()));
        page.getRecords().forEach(u -> u.setPassword(null));
        return R.ok(page);
    }

    @PostMapping("/users")
    @Operation(summary = "创建用户（管理员）")
    public R<SysUser> createUser(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", UUID.randomUUID().toString().substring(0, 8));
        String realName = body.getOrDefault("realName", username).trim();
        String roleCode = body.getOrDefault("roleCode", "STUDENT");
        if (!List.of("ADMIN", "TEACHER", "STUDENT").contains(roleCode))
            return R.fail(400, "无效角色");
        if (StrUtil.isBlank(username) || username.length() < 3) return R.fail(400, "用户名至少3位");
        if (password.length() < 6) return R.fail(400, "密码至少6位");
        if (userService.usernameExists(username)) return R.fail(400, "用户名已存在");
        SysUser u = new SysUser(); u.setUsername(username); u.setRealName(realName);
        u.setRoleCode(roleCode); u.setStatus(1);
        u.setPassword(passwordEncoder.encode(password));
        userService.save(u); u.setPassword(null);
        return R.ok(u);
    }

    @PutMapping("/users/{id}/disable")
    @Operation(summary = "禁用/启用用户")
    public R<Void> toggleUser(@PathVariable Long id, @RequestParam boolean disabled) {
        if (disabled && id.equals(currentUserId())) {
            return R.fail(400, "不能禁用自己的账号");
        }
        SysUser u = new SysUser(); u.setId(id); u.setStatus(disabled ? 0 : 1);
        userService.updateById(u); return R.ok();
    }

    @PutMapping("/git")
    @Operation(summary = "绑定Git身份")
    public R<Void> bindGit(@RequestBody Map<String, String> body) {
        SysUser u = new SysUser(); u.setId(currentUserId());
        u.setGitUsername(body.get("gitUsername")); u.setGitEmail(body.get("gitEmail"));
        userService.updateById(u); return R.ok();
    }

    @GetMapping("/users/search")
    @Operation(summary = "搜索用户")
    public R<List<SysUser>> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.isBlank()) return R.ok(List.of());
        List<SysUser> users = userService.list(new LambdaQueryWrapper<SysUser>()
                .and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword))
                .eq(SysUser::getStatus, 1));
        users.forEach(u -> u.setPassword(null));
        return R.ok(users);
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "查看审计日志（管理员）")
    public R<Page<AuditLog>> auditLogs(PageQuery q,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        if (!isAdmin()) return R.fail(403, "无权限");
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) wrapper.eq(AuditLog::getUserId, userId);
        if (username != null && !username.isBlank()) wrapper.eq(AuditLog::getUsername, username);
        if (action != null && !action.isBlank()) wrapper.eq(AuditLog::getAction, action);
        if (dateFrom != null && !dateFrom.isBlank())
            wrapper.ge(AuditLog::getCreatedAt, LocalDateTime.parse(dateFrom + "T00:00:00"));
        if (dateTo != null && !dateTo.isBlank())
            wrapper.le(AuditLog::getCreatedAt, LocalDateTime.parse(dateTo + "T23:59:59"));
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        Page<AuditLog> page = auditLogMapper.selectPage(new Page<>(q.getPage(), q.getSize()), wrapper);
        return R.ok(page);
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "修改用户角色（管理员）")
    public R<Void> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (!isAdmin()) return R.fail(403, "无权限");
        String roleCode = body.get("roleCode");
        if (roleCode == null || !List.of("ADMIN", "TEACHER", "STUDENT").contains(roleCode))
            return R.fail(400, "无效角色");
        SysUser user = userService.getById(id);
        if (user == null) return R.fail(404, "用户不存在");
        String oldRole = user.getRoleCode();
        user.setRoleCode(roleCode);
        userService.updateById(user);
        SysUser admin = userService.getById(currentUserId());
        auditService.log(admin.getId(), admin.getUsername(), "UPDATE_ROLE",
                "用户" + user.getUsername() + "角色从" + oldRole + "变更为" + roleCode, request);
        return R.ok();
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户")
    public R<Void> deleteUser(@PathVariable Long id) {
        if (id.equals(currentUserId())) return R.fail(400, "不能删除自己");
        userService.removeById(id);
        return R.ok();
    }
}
