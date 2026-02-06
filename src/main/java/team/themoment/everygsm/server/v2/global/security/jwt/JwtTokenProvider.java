package team.themoment.everygsm.server.v2.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long validityInMinutes;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long validityInMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMinutes = validityInMinutes;
    }

    public String createToken(Long userId, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMinutes * 60 * 1000);

        return Jwts.builder().subject(userId.toString()).claim("role", role).issuedAt(now).expiration(validity)
                .signWith(secretKey).compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new ExpectedException("만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
            throw new ExpectedException("잘못된 JWT 서명입니다.", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new ExpectedException("지원되지 않는 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
            throw new ExpectedException("JWT 토큰이 잘못되었습니다.", HttpStatus.UNAUTHORIZED);
        }
    }
}
