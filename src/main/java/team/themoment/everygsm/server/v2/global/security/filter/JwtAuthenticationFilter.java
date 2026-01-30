package team.themoment.everygsm.server.v2.global.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.everygsm.server.v2.global.security.jwt.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (token != null) {
                if (jwtTokenProvider.isTokenValid(token)) {
                    Claims claims = jwtTokenProvider.getClaims(token);

                    Long userId = Long.parseLong(claims.getSubject());
                    String role = claims.get("role", String.class);

                    if (role == null || role.isEmpty()) {
                        throw new ExpectedException("권한 정보가 없는 토큰입니다.", HttpStatus.UNAUTHORIZED);
                    }

                    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId,
                            null,
                            authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpectedException e) {
            log.error("JWT 인증  중 예외 발생: {}", e.getMessage());
            request.setAttribute("exception", e);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 필터에서 예상치 못한 예외 발생: {}", e.getMessage(), e);
            request.setAttribute("exception", e);
            filterChain.doFilter(request, response);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
