package com.obe.evaluation.config;

import com.obe.evaluation.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * Spring Security 安全配置
 *
 * 采用三层权限控制：
 *   1. URL 模式拦截（本类）：基于 Ant 路径匹配，按角色限制访问
 *   2. Controller 层方法检查（各 Controller）：isTeacherOrAdmin() / isAdmin() 细粒度控制
 *   3. Service 层数据过滤：getStudentGroupIds() / getTeacherGroupIds() 自动注入
 *
 * JWT 认证流程：请求 → TrailingSlashFilter(去尾斜杠) → JwtAuthFilter(解析Token) → SecurityConfig(角色匹配)
 */
@Configuration @EnableWebSecurity @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of("*"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(false);
        c.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(c -> c.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/doc.html", "/webjars/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/users").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/auth/users/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/**").authenticated()
                // Course + Analysis + Export + GraduationRequirements: TEACHER/ADMIN only
                .requestMatchers("/api/course/**", "/api/courses/**", "/api/analysis/calculate", "/api/analysis/suggestions/**", "/api/analysis/improvement-tasks/**", "/api/analysis/ahp-check", "/api/analysis/ahp-calculate", "/api/analysis/ahp-matrix", "/api/analysis/validate-weights").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/analysis/**").authenticated()
                .requestMatchers("/api/export/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/graduation-requirements/**").hasAnyRole("ADMIN", "TEACHER")
                // Git sync + Knowledge points
                .requestMatchers("/api/git-sync/**", "/api/knowledge-points/**").hasAnyRole("ADMIN", "TEACHER")
                // Settings restricted
                .requestMatchers("/api/settings/group-overview", "/api/settings/student-achievements").hasAnyRole("ADMIN", "TEACHER")
                // Groups: POST/PUT/DELETE → TEACHER/ADMIN, GET → authenticated
                .requestMatchers(HttpMethod.POST, "/api/groups").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/groups/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/groups/**").hasAnyRole("ADMIN", "TEACHER")
                // Project: milestones/tasks/repos/requirement-changes → ADMIN/TEACHER; journals/git-commits/contributions → authenticated (students can write)
                .requestMatchers(HttpMethod.POST, "/api/project/milestones", "/api/project/tasks", "/api/project/requirement-changes").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/project/milestones/**", "/api/project/tasks/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/project/milestones/**", "/api/project/tasks/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/project/git-repos", "/api/project/git-repos/**").hasAnyRole("ADMIN", "TEACHER")
                // Journals, Git commits, Contributions: open to all authenticated (controller has ownership checks)
                .requestMatchers(HttpMethod.POST, "/api/project/journals", "/api/project/git-commits", "/api/project/contributions").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/project/journals/**", "/api/project/git-commits/**", "/api/project/contributions/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/project/journals/**", "/api/project/git-commits/**", "/api/project/contributions/**").authenticated()
                // Evaluation: POST/PUT/DELETE → TEACHER/ADMIN
                .requestMatchers(HttpMethod.POST, "/api/evaluation/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/evaluation/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/evaluation/**").hasAnyRole("ADMIN", "TEACHER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
