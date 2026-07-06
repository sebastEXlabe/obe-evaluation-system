package com.obe.evaluation.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrailingSlashFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri != null && uri.length() > 1 && uri.endsWith("/")) {
            String newUri = uri.substring(0, uri.length() - 1);
            request = new HttpServletRequestWrapper(request) {
                @Override public String getRequestURI() { return newUri; }
                @Override public String getServletPath() {
                    String sp = super.getServletPath();
                    return sp != null && sp.endsWith("/") ? sp.substring(0, sp.length() - 1) : sp;
                }
            };
        }
        filterChain.doFilter(request, response);
    }
}
