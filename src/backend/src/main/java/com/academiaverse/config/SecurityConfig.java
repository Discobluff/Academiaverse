package com.academiaverse.config;

import com.academiaverse.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    public final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/auth/**").permitAll()
                .requestMatchers("/api/courses/**").permitAll()
                .requestMatchers("/api/search/**").authenticated()
                .requestMatchers("/api/interests/**").authenticated()
                .requestMatchers("/api/reports/**").authenticated()
                .requestMatchers("/api/items/*/buy-with-points").authenticated()
                .requestMatchers("/api/chat/**").authenticated()
                .requestMatchers("/api/ratings/**").authenticated()
                .requestMatchers("/v1/admin").hasAuthority("ADMIN")
                .requestMatchers("/api/uploads/**").permitAll()
                .requestMatchers("/api/items/*/download").authenticated()              
                .requestMatchers("/api/items/purchases").authenticated()                  
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}