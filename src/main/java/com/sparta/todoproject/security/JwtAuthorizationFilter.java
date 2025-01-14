package com.sparta.todoproject.security;

import com.sparta.todoproject.jwt.JwtTokenHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 토큰 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenHelper jwtTokenHelper;
    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath(); //
        // 리프레쉬 요청 시 액세스 토큰 검증 X..?
        if (servletPath.equals("/api/refresh")) {
            filterChain.doFilter(request, response);
        }

        String token = jwtTokenHelper.getTokenFromHeader(request); // 헤더에서 토큰값만 가져오기

        if (StringUtils.hasText(token)) {
            if (!jwtTokenHelper.validateToken(token, response)) {
                log.error("Token error");
                return;
            }
            Claims info = jwtTokenHelper.getUserInfoFromToken(token);
            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return; // 이렇게 되면 다음 체인으로 안 넘어가는 거..?
            }
        }

        log.info("Token이 존재하지 않습니다.");
        filterChain.doFilter(request, response);

    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // Authentication 객체 생성
    private Authentication createAuthentication(String username) {
        // 해당하는 user 객체 정보를 userDetails 객체에 반환 (username, password, authority)
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
