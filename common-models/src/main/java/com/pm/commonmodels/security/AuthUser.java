package com.pm.commonmodels.security;


import com.pm.commonmodels.enums.RoleType;

public record AuthUser(
        String email,
        RoleType role
) {
}
