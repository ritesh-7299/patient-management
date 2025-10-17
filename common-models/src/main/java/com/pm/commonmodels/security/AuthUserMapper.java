package com.pm.commonmodels.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthUserMapper {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static String toString(AuthUser user) {
        try {
            return mapper.writeValueAsString(user);
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public static AuthUser toObject(String user) {
        try {
            return mapper.readValue(user, AuthUser.class);
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }
}
