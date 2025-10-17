package com.pm.authservice.service;

import com.pm.authservice.model.Role;
import com.pm.authservice.repository.RoleRepository;
import com.pm.commonmodels.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public Optional<Role> getBySlug(RoleType slug) {
        return repository.findBySlug(slug);
    }
}
