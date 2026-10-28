package com.tunan.guagua.common.model;

public record LoginUser(Long id, String username, String nickname, String role) {
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
