package com.journaly.api.config;

import org.springframework.boot.CommandLineRunner; // Dòng import mới
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    // =================================================================
    // DÁN PHƯƠNG THỨC MỚI VÀO ĐÂY
    // =================================================================
    @Bean
    public CommandLineRunner testBeanCreation() {
        return args -> {
            System.out.println("\n\n\n>>>>>>>>>> SecurityConfig BEAN WAS CREATED SUCCESSFULLY! <<<<<<<<<<\n\n\n");
        };
    }
}