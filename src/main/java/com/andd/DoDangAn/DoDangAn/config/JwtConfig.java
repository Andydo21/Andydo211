package com.andd.DoDangAn.DoDangAn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public byte[] getSecret() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    public long getExpiration() {
        return Long.reverseBytes(expiration);
    }
}