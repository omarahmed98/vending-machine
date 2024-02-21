package com.vendor.machine.Securtiy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    public String extractUserName(String jwtToken) {
        if (jwtToken == null) {
            // Handle null JWT token, e.g., throw an exception or return a default value
            throw new IllegalArgumentException("JWT token is null");
        }
        DecodedJWT jwt = JWT.decode(jwtToken);
        return jwt.getSubject();
    }

    public static String generateToken(String subject, Long expirationTimeInSeconds, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date expirationDate = new Date(System.currentTimeMillis() + (expirationTimeInSeconds * 1000));
        return JWT.create()
        .withSubject(subject)
        .withIssuedAt(new Date(System.currentTimeMillis()))
        .withExpiresAt(expirationDate)
        .sign(algorithm);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUserName(jwtToken);
        return username.equals(userDetails.getUsername());
    }
}
