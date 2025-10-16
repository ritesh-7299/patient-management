package com.pm.commonutils.enums;

public enum RoleType {
    ADMIN("Admin"),
    USER("User");

    private final String label;

    RoleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
