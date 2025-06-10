package com.andd.DoDangAn.DoDangAn.config;

import com.andd.DoDangAn.DoDangAn.Util.JwtUtil;
import com.andd.DoDangAn.DoDangAn.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "user/register", "user/login", "user/home", "categories", "/", "movie/preshow", "assets",
            "uploads", "upload_dir", "views", "error", "movie/movies"
    );

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        path = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
        path = path.startsWith("/") ? path.substring(1) : path;
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

        final String finalPath = path;
        boolean shouldNotFilter = EXCLUDED_PATHS.stream().anyMatch(excluded ->
                finalPath.equals(excluded) || finalPath.startsWith(excluded + "/"));
        logger.info("shouldNotFilter for path {}: {}", finalPath, shouldNotFilter);
        return shouldNotFilter;
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        logger.info("Checking cookies for request: {}", request.getRequestURI());
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.debug("Cookie found: name={}, value={}", cookie.getName(), cookie.getValue());
                if ("jwt".equals(cookie.getName())) {
                    logger.info("Found JWT in cookie for request {}: {}", request.getRequestURI(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        logger.warn("No JWT found in cookies for request: {}", request.getRequestURI());
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("Processing JWT filter for path: {}", request.getRequestURI());
        try {
            String jwt = getTokenFromCookies(request);
            String username = null;

            if (jwt != null) {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Extracted username from JWT: {}", username);
            } else {
                logger.warn("No JWT provided for request: {}", request.getRequestURI());
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.info("Loaded user details for: {}", username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.info("JWT is valid for user: {}", username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("SecurityContextHolder set for user: {}", username);
                } else {
                    logger.warn("JWT token is invalid for user: {}", username);
                    response.sendRedirect("/user/login?error=InvalidToken");
                    return;
                }
            } else if (!shouldNotFilter(request) && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.warn("No valid JWT token found for protected path: {}", request.getRequestURI());
                response.sendRedirect("/user/login?error=PleaseLogin");
                return;
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JWT authentication error for path {}: {}", request.getRequestURI(), e.getMessage());
            e.printStackTrace();
            response.sendRedirect("/user/login?error=JwtError");
        }
    }
}