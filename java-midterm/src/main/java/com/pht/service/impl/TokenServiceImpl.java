package com.pht.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pht.dro.response.JwtResponse;
import com.pht.dto.RoleCode;
import com.pht.exception.security.UnauthorizedException;
import com.pht.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenServiceImpl implements TokenService {
	RedisTemplate<String, Object> redisTemplate;
	UserDetailsService userDetailsService;

	@NonFinal
	@Value("${spring.jwt.secret_key}")
	String jwtSecretKey;

	@NonFinal
	@Value("${spring.jwt.ttl.access}")
	Long accessTokenTTL;

	@NonFinal
	@Value("${spring.jwt.ttl.refresh}")
	Long refreshTokenTTL;

	private static final String JWT_REFRESH_FREFIX = "jwt:refresh:";

	public JwtResponse generateToken(Authentication authentication) {
		String accessToken = createAccessToken(authentication);
		String refreshToken = createRefreshToken(authentication.getName());

		if (!StringUtils.hasText(accessToken) || !StringUtils.hasText(refreshToken)) {
			return null;
		}

		return new JwtResponse(accessToken, refreshToken);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		Date dateExprire = extractClaim(token, Claims::getExpiration);

		return (username.equals(userDetails.getUsername()) && dateExprire.after(new Date()));
	}

	public JwtResponse refresh(String refreshToken) {
		if (!validateRefreshToken(refreshToken)) {
			throw new UnauthorizedException("Invalid refresh token, please login and try again");
		}
		String username = extractFromRefreshToken(refreshToken)[0];
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
				userDetails.getAuthorities());

		String accessToken = createAccessToken(authentication);

		return new JwtResponse(accessToken, refreshToken);
	}

	private String createAccessToken(Authentication authentication) {
		String username = authentication.getName();
		return Jwts.builder().claims(generateClaims(authentication)).subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + accessTokenTTL))
				.signWith(getSecretKey(), Jwts.SIG.HS256).compact();
	}

	private String createRefreshToken(String username) {
		String key = JWT_REFRESH_FREFIX + username;
		String refreshToken = UUID.randomUUID().toString();

		redisTemplate.delete(key);
		redisTemplate.opsForValue().set(key, refreshToken, refreshTokenTTL, TimeUnit.MILLISECONDS);

		String combinedToken = username + ":" + refreshToken;
		return Base64.getUrlEncoder().encodeToString(combinedToken.getBytes(StandardCharsets.UTF_8));
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		try {
			Claims claims = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
			return claimsResolver.apply(claims);
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return null;
	}

	private SecretKey getSecretKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private Map<String, Object> generateClaims(Authentication authentication) {
		Map<String, Object> claims = new HashMap<>();
		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		claims.put("author", "Phạm Trường");
		claims.put("role", roles.isEmpty() ? RoleCode.USER.name() : roles.get(0));

		return claims;
	}

	private boolean validateRefreshToken(String encodedToken) {
		String[] parts = extractFromRefreshToken(encodedToken);
		if (parts == null)
			return false;

		String username = parts[0];
		String receivedRefreshToken = parts[1];

		String key = JWT_REFRESH_FREFIX + username;
		String storedRefreshToken = (String) redisTemplate.opsForValue().get(key);

		return receivedRefreshToken.equals(storedRefreshToken);
	}

	private String[] extractFromRefreshToken(String encodedToken) {
		try {
			String decodedToken = new String(Base64.getUrlDecoder().decode(encodedToken), StandardCharsets.UTF_8);
			String[] parts = decodedToken.split(":");
			return (parts.length == 2) ? parts : null;
		} catch (IllegalArgumentException e) {
			log.error("Invalid refresh token format");
			return null;
		}
	}
}
