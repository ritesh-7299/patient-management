package com.pm.authservice.controller;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.dto.SignupRequestDTO;
import com.pm.authservice.model.Role;
import com.pm.authservice.model.User;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.RoleService;
import com.pm.commonutils.exceptions.NotFoundException;
import com.pm.commonutils.exceptions.UnauthorizedException;
import com.pm.commonutils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Generate token on login")
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        Optional<String> token = authService.authenticate(dto);
        if (token.isEmpty()) {
            throw new UnauthorizedException();
        }
        return new LoginResponseDTO(token.get());
    }

    @Operation(summary = "Register a new user into the system")
    @PostMapping("/signup")
    String signup(@Valid @RequestBody SignupRequestDTO dto) {
        
        return authService.signup(dto);
    }

    @Operation(summary = "Validate the token")
    @GetMapping("/validate")
    public String validateToken(@RequestHeader("Authorization") String authToken) {
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }
        if (!authService.validateToken(authToken.substring(7))) {
            throw new UnauthorizedException();
        }
        return "Authorized";
    }
}
