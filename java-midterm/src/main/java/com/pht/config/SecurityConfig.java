package com.pht.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
	UserDetailsService userDetailsService;
	JwtInterceptor jwtInterceptor;
	AuthenticationEntryPoint authenticationEntryPoint;
	CorsConfigurationSource configurationSource;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(request -> {
			request.requestMatchers("/api/auth/**", "/api/files/**").permitAll();
			request.requestMatchers(HttpMethod.GET, "/api/brands/").permitAll();
			request.requestMatchers(HttpMethod.GET, "/api/categories/").permitAll();
			request.requestMatchers(HttpMethod.POST, "/api/products/filter/").permitAll();
			request.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll();
			request.anyRequest().authenticated();
		}).csrf(csrf -> {
			csrf.disable();
		}).cors(cors -> {
			cors.configurationSource(configurationSource);
		}).sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}).exceptionHandling(ex -> {
			ex.authenticationEntryPoint(authenticationEntryPoint);
		}).addFilterBefore(jwtInterceptor, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder);
		return authProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
