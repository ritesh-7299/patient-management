package com.pm.authservice.repository;

import com.pm.authservice.model.Role;
import com.pm.commonutils.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findBySlug(RoleType slug);
}
