package com.pht.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import com.pht.dro.response.JwtResponse;
import com.pht.dto.RoleCode;
import com.pht.exception.security.UnauthorizedException;
import com.pht.service.impl.TokenServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private final String username = "testuser";
    private final String jwtSecretKey = "ZWKTRJAqK2r7DRDpGnKraRhvtwzxcCQnWFzEG6mCAeWHcfKdUq2VqwUHGtfBYcRE";
    private final long accessTokenTTL = 3600000; // 1 hour
    private final long refreshTokenTTL = 86400000; // 24 hours

    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "jwtSecretKey", jwtSecretKey);
        ReflectionTestUtils.setField(tokenService, "accessTokenTTL", accessTokenTTL);
        ReflectionTestUtils.setField(tokenService, "refreshTokenTTL", refreshTokenTTL);

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(RoleCode.USER.name()));
        userDetails = new User(username, "password", authorities);
        authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGenerateToken_ShouldReturnTokens() {
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        JwtResponse jwtResponse = tokenService.generateToken(authentication);

        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getAccessToken());
        assertNotNull(jwtResponse.getRefreshToken());

        verify(redisTemplate).delete(startsWith("jwt:refresh:"));
        verify(valueOperations).set(startsWith("jwt:refresh:"), anyString(), eq(refreshTokenTTL),
                eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void testExtractUsername_ShouldReturnCorrectUsername() {
        mockRedisSet();
        String token = tokenService.generateToken(authentication).getAccessToken();

        String extracted = tokenService.extractUsername(token);

        assertEquals(username, extracted);
    }

    @Test
    void testValidateToken_ValidToken_ShouldReturnTrue() {
        mockRedisSet();
        String token = tokenService.generateToken(authentication).getAccessToken();

        boolean valid = tokenService.validateToken(token, userDetails);

        assertTrue(valid);
    }

    @Test
    void testValidateToken_InvalidUsername_ShouldReturnFalse() {
        mockRedisSet();
        String token = tokenService.generateToken(authentication).getAccessToken();

        UserDetails otherUser = new User("otheruser", "pass", List.of(new SimpleGrantedAuthority("USER")));

        assertFalse(tokenService.validateToken(token, otherUser));
    }

    @Test
    void testRefresh_ValidRefreshToken_ShouldReturnNewAccessToken() {
        mockRedisSet();
        JwtResponse initial = tokenService.generateToken(authentication);
        String refreshToken = initial.getRefreshToken();

        String decoded = new String(Base64.getUrlDecoder().decode(refreshToken), StandardCharsets.UTF_8);
        String storedToken = decoded.split(":")[1];

        when(valueOperations.get("jwt:refresh:" + username)).thenReturn(storedToken);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        JwtResponse newTokens = tokenService.refresh(refreshToken);

        assertNotNull(newTokens);
        assertNotNull(newTokens.getAccessToken());
        assertEquals(refreshToken, newTokens.getRefreshToken()); // vẫn là token cũ
    }

    @Test
    void testRefresh_InvalidToken_ShouldThrowException() {
        String fakeToken = Base64.getUrlEncoder()
                .encodeToString("testuser:invalidtoken".getBytes(StandardCharsets.UTF_8));
        when(valueOperations.get("jwt:refresh:" + username)).thenReturn("differenttoken");

        assertThrows(UnauthorizedException.class, () -> tokenService.refresh(fakeToken));
    }

    private void mockRedisSet() {
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }
}
