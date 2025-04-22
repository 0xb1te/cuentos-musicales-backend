package com.storyteller.platform.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// First, explicitly permit all file access without authentication
						.requestMatchers(HttpMethod.GET, "/files/**").permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/api/admin/login")
						.permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
								"/swagger-ui/index.html")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/api/menu", "/api/stories/**", "/api/menu-options/**",
								"/api/menu-level/*/menu-options", "/api/admin/menu-options/all",
								"/api/menu-options/flat")
						.permitAll().requestMatchers("/api/payments/webhook").permitAll()
						// Allow upload endpoints explicitly
						.requestMatchers(HttpMethod.POST, "/api/admin/upload").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/admin/menu-option", "/api/admin/menu-option/**")
						.authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/admin/menu-option", "/api/admin/menu-option/**")
						.authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/admin/menu-option", "/api/admin/menu-option/**")
						.authenticated().anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Collections.singletonList("*")); // Allow all origins in development
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept",
				"Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
		configuration
				.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L); // 1 hour

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
}