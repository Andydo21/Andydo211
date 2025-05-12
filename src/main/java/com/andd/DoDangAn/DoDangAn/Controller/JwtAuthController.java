package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.Util.JwtUtil;
import com.andd.DoDangAn.DoDangAn.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtAuthController {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    // Constructor injection
    @Autowired
    public JwtAuthController(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/api/auth/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Kiểm tra và trích xuất token từ header
            String token = extractToken(authHeader);

            // Lấy username từ token
            String username = jwtUtil.extractUsername(token);

            // Kiểm tra token hợp lệ bằng JwtUtil
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.ok().build();
            } else {
                throw new IllegalArgumentException("Token validation failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }

    @GetMapping("/api/auth/user")
    public ResponseEntity<?> getUserFromToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Kiểm tra và trích xuất token từ header
            String token = extractToken(authHeader);

            // Lấy username từ token
            String username = jwtUtil.extractUsername(token);
            return ResponseEntity.ok(username);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }

    private String extractToken(String authHeader) throws IllegalArgumentException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }
        return authHeader.substring(7); // Trích xuất token sau "Bearer "
    }
}