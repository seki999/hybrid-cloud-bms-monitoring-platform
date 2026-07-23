package com.example.bms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** 为每个 HTTP 请求建立 Request ID 与可跨云传递的 Correlation ID。 */
@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        String incoming = request.getHeader(CORRELATION_HEADER);
        String correlationId = incoming == null || incoming.isBlank() || incoming.length() > 100
                ? requestId : incoming.replaceAll("[^A-Za-z0-9._:-]", "_");
        MDC.put("requestId", requestId);
        MDC.put("correlationId", correlationId);
        response.setHeader("X-Request-ID", requestId);
        response.setHeader(CORRELATION_HEADER, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("correlationId");
        }
    }
}

