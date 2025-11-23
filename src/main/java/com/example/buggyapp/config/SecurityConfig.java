package com.example.buggyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // DEFECT: Disabling CSRF protection
    // DEFECT: Permitting all requests without authentication
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // DEFECT: CSRF disabled
            .csrf(csrf -> csrf.disable())
            // DEFECT: All requests permitted
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // DEFECT: Disabling frame options (clickjacking vulnerability)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    // DEFECT: Weak password encoder
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        // DEFECT: Using NoOpPasswordEncoder (passwords stored in plain text)
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }
}
