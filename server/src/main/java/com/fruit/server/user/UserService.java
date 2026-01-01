package com.fruit.server.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fruit.server.config.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                jwtToken,
                user.getEmail() + " registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String jwtToken = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                jwtToken,
                user.getEmail() + " logged in successfully");
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
