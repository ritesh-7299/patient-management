package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.SignupRequestDTO;
import com.pm.authservice.model.Role;
import com.pm.authservice.model.User;
import com.pm.authservice.util.JwtUtil;
import com.pm.commonutils.enums.RoleType;
import com.pm.commonutils.exceptions.NotFoundException;
import com.pm.commonutils.exceptions.ValidationErrorException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;


    public String signup(SignupRequestDTO dto) {
        RoleType role;
        try {
            role = RoleType.valueOf(dto.roleId().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationErrorException("Role id is invalid");
        }
        Optional<Role> roleValue = roleService.getBySlug(role);
        if (roleValue.isEmpty()) {
            throw new NotFoundException("Role not found");
        }
        User user = new User();
        user.setEmail(dto.email());
        user.setRole(roleValue.get());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userService.saveUser(user);
        return jwtUtil.generateToken(user.getEmail(), user.getRole().getName());
    }

    public Optional<String> authenticate(LoginRequestDTO dto) {
        return userService.findByEmail(dto.email())
                .filter(u -> passwordEncoder.matches(dto.password(), u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole().getName()));
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
