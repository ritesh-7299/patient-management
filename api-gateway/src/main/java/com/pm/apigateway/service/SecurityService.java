package com.pm.apigateway.service;

import com.pm.apigateway.util.JwtUtil;
import com.pm.commonmodels.security.AuthUser;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private final JwtUtil jwtUtil;

    public SecurityService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public AuthUser parseToken(String token) {
        try {
            return jwtUtil.parseToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
