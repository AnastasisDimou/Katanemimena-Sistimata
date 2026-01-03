package gr.hua.dit.project.mycitygov.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * JWT (JSON Web Token) service.
 */
@Service
public class JwtService {

   private final Key key;
   private final String issuer;
   private final String audience;
   private final long ttlMinutes;

   public JwtService(
         @Value("${app.jwt.secret}") final String secret,
         @Value("${app.jwt.issuer:mycitygov}") final String issuer,
         @Value("${app.jwt.audience:mycitygov-api}") final String audience,
         @Value("${app.jwt.ttl-minutes:1440}") final long ttlMinutes) {
      if (secret == null || secret.isBlank())
         throw new IllegalArgumentException("JWT secret cannot be blank");
      if (issuer == null || issuer.isBlank())
         throw new IllegalArgumentException("JWT issuer cannot be blank");
      if (audience == null || audience.isBlank())
         throw new IllegalArgumentException("JWT audience cannot be blank");
      if (ttlMinutes <= 0)
         throw new IllegalArgumentException("JWT ttl-minutes must be positive");

      this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      this.issuer = issuer;
      this.audience = audience;
      this.ttlMinutes = ttlMinutes;
   }

   public String issue(final String subject, final Collection<String> roles, final Map<String, Object> extraClaims) {
      final Instant now = Instant.now();
      return Jwts.builder()
            .setSubject(subject)
            .setIssuer(this.issuer)
            .setAudience(this.audience)
            .claim("roles", roles)
            .addClaims(extraClaims)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(Duration.ofMinutes(this.ttlMinutes))))
            .signWith(this.key, SignatureAlgorithm.HS256)
            .compact();
   }

   public Claims parse(final String token) {
      return Jwts.parserBuilder()
            .requireAudience(this.audience)
            .requireIssuer(this.issuer)
            .setSigningKey(this.key)
            .build()
            .parseClaimsJws(token)
            .getBody();
   }

   public long getTtlSeconds() {
      return this.ttlMinutes * 60;
   }
}
