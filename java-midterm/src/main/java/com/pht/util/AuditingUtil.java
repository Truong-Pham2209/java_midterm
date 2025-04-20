package com.pht.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.pht.exception.security.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditingUtil {
	public static final String getCurrentUser() {
		log.info("Extract username");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("Cannot get authentication");
			throw new UnauthorizedException("Unauthorized");
		}

		return authentication.getName();
	}
}
