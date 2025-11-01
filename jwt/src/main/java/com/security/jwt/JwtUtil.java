package com.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final String SECURE_STRING_KEY =
      "AverySuperSecretKeyForJWTSigningThatIsAtLeast32BytesLongToSatisfyHS256Algorithm";
  private static final long EXPIRATION_TIME = 1000 * 60 * 5;

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return Jwts.builder()
        .addClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenExpired(String jwtToken) {
    return extractClaim(jwtToken, Claims::getExpiration).before(new Date());
  }

  public String getUserNameFromToken(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    boolean isTokenExpired = isTokenExpired(token);
    boolean isValidUserName = getUserNameFromToken(token).equals(userDetails.getUsername());

    if (!isTokenExpired && isValidUserName) {
      return true;
    }

    return false;
  }

  private Key getSigningKey() {
    byte[] bytes = Decoders.BASE64.decode(SECURE_STRING_KEY);
    return Keys.hmacShaKeyFor(bytes);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String jwtToken)
      throws MalformedJwtException, ExpiredJwtException {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(jwtToken)
        .getBody();
  }
}
