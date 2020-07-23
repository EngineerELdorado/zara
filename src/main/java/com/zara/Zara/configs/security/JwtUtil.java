package com.zara.Zara.configs.security;

import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.User;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.AccountRepository;
import com.zara.Zara.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final String SECRET = "1234567890";

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {


        Map<String, Object> claims = new HashMap<>();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new Zaka500Exception("User not found for username " + userDetails.getUsername())
        );
        claims.put("id", user.getId());

        if (accountRepository.findByUserId(user.getId()).isPresent()) {
            Account account = accountRepository.findByUserId(user.getId()).get();
            claims.put("role", account.getType().name());
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subjet) {

        return Jwts.builder().setClaims(claims).setSubject(subjet).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(DateUtils.addDays(new Date(), 7))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
