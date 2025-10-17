package com.pm.commonutils.filter;

import com.pm.commonmodels.security.AuthUser;
import com.pm.commonmodels.security.AuthUserMapper;
import com.pm.commonutils.security.UserAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class SecurityUserFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        AuthUser user = AuthUserMapper.toObject(request.getHeader("user"));
        System.out.println("USER HEADER email::" + user.email());
        System.out.println("USER HEADER role::" + user.role());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UserAuthentication(user));
        SecurityContextHolder.setContext(securityContext);
        filterChain.doFilter(request, response);
    }
}
