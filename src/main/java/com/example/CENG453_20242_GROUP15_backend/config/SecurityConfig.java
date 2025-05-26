package com.example.CENG453_20242_GROUP15_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/",
                                "/leaderboard",
                                "/leaderboard/all-time",
                                "/leaderboard/weekly",
                                "/leaderboard/monthly",
                                "/solo/start",
                                "/solo/play",
                                "/solo/draw",
                                "/solo/start",
                                "/solo/state",
                                "/solo/restart",
                                "/solo/cheat/skip",
                                "/solo/cheat/reverse",
                                "/solo/cheat/drawtwo",
                                "/solo/cheat/wild",
                                "/solo/cheat/wilddrawfour",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(customizer -> {});
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
