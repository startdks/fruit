package com.fruit.server.user;

public record AuthResponse(
        Long userId,
        String fullName,
        String email,
        String token,
        String message) {
}
