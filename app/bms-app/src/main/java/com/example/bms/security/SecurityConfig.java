package com.example.bms.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 本地表单认证、RBAC、CSRF 与安全 Header 配置。
 *
 * <p>本地账号只为学习复现。生产环境应将 UserDetailsService 替换为 OIDC/企业 IdP，角色映射保持不变。</p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder,
            @Value("${bms.security.admin-password}") String adminPassword,
            @Value("${bms.security.operator-password}") String operatorPassword,
            @Value("${bms.security.viewer-password}") String viewerPassword) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode(adminPassword)).roles("ADMIN").build(),
                User.withUsername("operator").password(encoder.encode(operatorPassword)).roles("OPERATOR").build(),
                User.withUsername("viewer").password(encoder.encode(viewerPassword)).roles("VIEWER").build());
    }

    /** 定义 URL 级最小权限；写操作还可由方法级注解进一步收紧。 */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/users/**", "/monitoring-rules/**").hasRole("ADMIN")
                        .requestMatchers("/devices/new", "/devices/*/edit").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data:; style-src 'self'; script-src 'self'; "
                                        + "font-src 'self'; object-src 'none'; frame-ancestors 'none'; base-uri 'self'"))
                        .frameOptions(frame -> frame.deny()));
        return http.build();
    }
}
