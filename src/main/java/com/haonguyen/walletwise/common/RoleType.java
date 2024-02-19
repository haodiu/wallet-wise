package com.haonguyen.walletwise.common;

public enum RoleType {
    ADMIN("admin"),
    USER("user");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
