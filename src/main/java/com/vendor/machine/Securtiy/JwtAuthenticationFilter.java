package com.vendor.machine.Securtiy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;



@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private static final String expirationDate ="7200";
    private static final String SECRET_KEY ="qVnkSD4Yq1OYNhh5YjgLKIQrA2sAUc9t6/ZOy1ShzC14xZsYW4i6QmmkYv1pSaajJuOk5g+So6lh4lJi+3BeI6AsWufcaoX";
    public static final List<String> PermittedPathUris = List.of(
        "/api/v1/users/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/v3/api-docs",
        "/swagger-resources/**",
        "/webjars/**"
    );
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            String requestURI = request.getRequestURI();
            boolean permited = false;
            for (String permittedUriPattern : PermittedPathUris) {
                // Replace ** with .*
                String regexPattern = permittedUriPattern.replace("**", ".*");
                // Check if the given URI matches the pattern
                if (Pattern.matches(regexPattern, requestURI)) {
                    permited = true;
                }
            }
            log.info("request URI {}",requestURI);
            log.info("permited {}",permited);

            if (permited) {
                filterChain.doFilter(request, response);
                return;
            }
            Date expirenDate = new Date(System.currentTimeMillis() + (Long.parseLong(expirationDate)* 1000));
            log.info("expiration Date {} ", expirenDate);

            final String authHeader = request.getHeader("Authorization");
            log.info("auth Header {} ", authHeader);
            final String tokenFromReq = authHeader.substring(7);

            log.info("tokenFromReq Jwt From Request {} ", tokenFromReq);

            final String username = jwtService.extractUserName(tokenFromReq);
            log.info("Username {} ", username);

            UserDetails userDetails = getUserDetails(username);
            log.info("Usern Details Is {} ", userDetails);
            log.info("username in userDetails {} ", jwtService.isTokenValid(tokenFromReq,userDetails));
            if (jwtService.isTokenValid(tokenFromReq,userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    userDetails.getAuthorities());
                log.info("UsernamePasswordAuthenticationToken token is {} ", authToken);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (MalformedJwtException e) {
            //The 498 Invalid Token status code is a client error that is sent by the server ,
            // when the client submits a HTTP request that does not include a valid token
            response.setStatus(498);
            response.getOutputStream().print("Invalid JWT token: {}" + e.getMessage());
        } catch (ExpiredJwtException e) {
            //Status code 401 - unauthorized / token expired
            response.setStatus(401);
            response.getOutputStream().print("JWT token is expired: {}" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            //The HTTP 415 Unsupported Media Type client error response code indicates
            // that the server refuses to accept the request,
            // because the payload format is in an unsupported format
            response.setStatus(415);
            response.getOutputStream().print("JWT token is unsupported: {}" + e.getMessage());

        } catch (IllegalArgumentException e) {
            //400 Bad Request : Invalid argument (invalid request payload)
            response.setStatus(400);
            response.getOutputStream().print("JWT claims string is empty: {}" + e.getMessage());

        } catch (Exception e) {
            response.setStatus(400);
            writeErrorMessageToResponse(response, "Error in Accessing the resource: " + e.getMessage());

        }
    }

    public void handleAccountLockStatus(HttpServletResponse response, UserDetails userDetails) throws IOException {
        if (!userDetails.isAccountNonLocked()) {
            log.info("Account is disabled");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeErrorMessageToResponse(response, "Account is disabled");
        }
    }
    private UserDetails getUserDetails(String userId) {
        return this.userDetailsService.loadUserByUsername(userId);
    }

    private void writeErrorMessageToResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println("{\"error\": \"" + errorMessage + "\"}");
        writer.flush();
    }
}
