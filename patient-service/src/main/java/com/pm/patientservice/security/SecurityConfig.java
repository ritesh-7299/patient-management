package com.pm.patientservice.security;

import com.pm.patientservice.filter.SecurityContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig extends com.pm.commonutils.security.SecurityConfig {

    public SecurityConfig(SecurityContext securityContext) {
        super(securityContext);
    }

}
