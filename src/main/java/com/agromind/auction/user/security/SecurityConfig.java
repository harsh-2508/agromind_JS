package com.agromind.auction.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Forces Spring to prioritize OUR rules over the defaults
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Kills the default CSRF block for REST APIs
                .formLogin(AbstractHttpConfigurer::disable) // Disable form login — not needed for REST APIs
                .httpBasic(AbstractHttpConfigurer::disable) // Disable HTTP Basic — prevents auth popups/interference
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // REST APIs should be stateless
                .authorizeHttpRequests(auth -> auth
                        // Explicitly allow POST requests to this exact URL
                        .requestMatchers(HttpMethod.POST, "/api/users/register","/api/users/login").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}