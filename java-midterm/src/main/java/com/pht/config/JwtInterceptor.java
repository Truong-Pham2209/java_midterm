package com.pht.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pht.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtInterceptor extends OncePerRequestFilter {
	TokenService tokenService;
	UserDetailsService userDetailsService;

	private static final List<String> PERMISSALL_ENDPOINTS = new ArrayList<String>();
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	static {
		PERMISSALL_ENDPOINTS.add("/api/auth/**");
		PERMISSALL_ENDPOINTS.add("/api/files/**");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getRequestURI();
		System.out.println("Path: " + path);
		boolean isPermitted = PERMISSALL_ENDPOINTS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
		System.out.println("Is match: " + isPermitted);
		if (isPermitted) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = getJWTFromRequest(request);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (StringUtils.hasText(token) && authentication == null) {
			String userName = tokenService.extractUsername(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

			if (tokenService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}

		doFilter(request, response, filterChain);
	}

	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}
}
