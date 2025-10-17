package com.pm.authservice.model;

import com.pm.commonmodels.enums.RoleType;
import com.pm.commonutils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles",
        indexes = {
                @Index(name = "idx_role_slug",
                        columnList = "slug",
                        unique = true
                )
        })
@Getter
@Setter
public class Role extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType slug;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}
