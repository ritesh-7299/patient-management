package com.pm.commonutils.security;

import com.pm.commonutils.exceptions.UnauthorizedException;
import com.pm.commonutils.filter.SecurityUserFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final SecurityUserFilter securityUserFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(securityUserFilter, AuthorizationFilter.class)
                .authorizeHttpRequests(req -> req.anyRequest().authenticated())
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(((request, response, authException) ->
                                        {
                                            response.getWriter().write("Unauthorized");
                                        }))
                                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                                            response.getWriter().write("Unauthorized");
                                        }))
                )
                .build();
    }
}
