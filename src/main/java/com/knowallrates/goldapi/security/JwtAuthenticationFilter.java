//package com.knowallrates.goldapi.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String email = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            try {
//                email = jwtUtil.getEmailFromToken(token);
//            } catch (Exception e) {
//                System.err.println("Error extracting email from token: " + e.getMessage());
//            }
//        }
//
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
//                String role = jwtUtil.getRoleFromToken(token);
//
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(
//                                email,
//                                null,
//                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
//                        );
//
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}


package com.knowallrates.goldapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("JWT Filter - Processing request: " + method + " " + requestPath);

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(requestPath)) {
            System.out.println("JWT Filter - Skipping authentication for public endpoint: " + requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Handle OPTIONS requests
        if ("OPTIONS".equals(method)) {
            System.out.println("JWT Filter - Handling OPTIONS request");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("JWT Filter - Authorization header: " + (authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) + "..." : "null"));

        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("JWT Filter - Extracted token: " + token.substring(0, Math.min(30, token.length())) + "...");

            try {
                if (jwtUtil.validateToken(token)) {
                    email = jwtUtil.getEmailFromToken(token);
                    System.out.println("JWT Filter - Extracted email: " + email);
                } else {
                    System.out.println("JWT Filter - Token validation failed");
                }
            } catch (Exception e) {
                System.err.println("JWT Filter - Error processing token: " + e.getMessage());
            }
        } else {
            System.out.println("JWT Filter - No valid Authorization header found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authorization header required\"}");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String role = jwtUtil.getRoleFromToken(token);
                System.out.println("JWT Filter - Token valid, email: " + email + ", role: " + role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("JWT Filter - Authentication set successfully for user: " + email + " with role: ROLE_" + role);
            } catch (Exception e) {
                System.err.println("JWT Filter - Error setting authentication: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }
        } else if (email == null) {
            System.out.println("JWT Filter - Email extraction failed, returning 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return  path.startsWith("/api/rate/") ||
                path.startsWith("/api/auth/signin") ||
                path.startsWith("/api/auth/signup") ||
                path.equals("/health") ||
                path.equals("/") ||
                path.startsWith("/h2-console");
    }
}
