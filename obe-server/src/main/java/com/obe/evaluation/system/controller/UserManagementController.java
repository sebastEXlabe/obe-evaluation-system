package com.obe.evaluation.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obe.evaluation.common.R;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserManagementController {

    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long id))
            throw new IllegalArgumentException("未登录");
        return id;
    }

    @GetMapping
    @Operation(summary = "用户列表")
    public R<List<SysUser>> list() {
        return R.ok(userService.list().stream().peek(u -> u.setPassword(null)).toList());
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户")
    public R<List<SysUser>> search(@RequestParam String keyword) {
        if (keyword == null || keyword.isBlank()) return R.ok(List.of());
        List<SysUser> users = userService.list(new LambdaQueryWrapper<SysUser>()
                .and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword))
                .eq(SysUser::getStatus, 1));
        users.forEach(u -> u.setPassword(null));
        return R.ok(users);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public R<SysUser> create(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", UUID.randomUUID().toString().substring(0, 8));
        String realName = body.getOrDefault("realName", username).trim();
        String roleCode = body.getOrDefault("roleCode", "STUDENT");
        if (StrUtil.isBlank(username) || username.length() < 3) return R.fail(400, "用户名至少3位");
        if (password.length() < 6) return R.fail(400, "密码至少6位");
        if (userService.usernameExists(username)) return R.fail(400, "用户名已存在");
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setRealName(realName);
        u.setRoleCode(roleCode);
        u.setStatus(1);
        u.setPassword(passwordEncoder.encode(password));
        userService.save(u);
        u.setPassword(null);
        return R.ok(u);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用用户")
    public R<Void> toggle(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Boolean enabled = body.get("enabled") != null ? (Boolean) body.get("enabled") : true;
        if (!enabled && id.equals(currentUserId())) return R.fail(400, "不能禁用自己的账号");
        SysUser u = new SysUser();
        u.setId(id);
        u.setStatus(enabled ? 1 : 0);
        userService.updateById(u);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public R<Void> delete(@PathVariable Long id) {
        if (id.equals(currentUserId())) return R.fail(400, "不能删除自己");
        userService.removeById(id);
        return R.ok();
    }
}
