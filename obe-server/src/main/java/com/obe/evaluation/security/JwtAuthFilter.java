package com.obe.evaluation.security;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j @Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StrUtil.isNotBlank(token) && jwtUtil.validateToken(token)) {
            if (TokenBlacklist.contains(token)) {
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            }
            Claims claims = jwtUtil.parseClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);
            String username = claims.get("username", String.class);
            var auth = new UsernamePasswordAuthenticationToken(userId, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            auth.setDetails(Map.of("username", username, "role", role));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(header) && header.startsWith("Bearer ")) return header.substring(7);
        return null;
    }
}
