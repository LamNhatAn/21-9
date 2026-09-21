package com.example.shop.config;

import com.example.shop.service.AccountService;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, AccountService accounts) throws Exception {
        http.userDetailsService(accounts).authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/examples", "/register", "/verify", "/forgot-password", "/reset-password",
                        "/css/**", "/uploads/**", "/h2-console/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
                .usernameParameter("identifier").defaultSuccessUrl("/profile", true)
                .failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .sessionManagement(session -> session.maximumSessions(1).maxSessionsPreventsLogin(true))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
