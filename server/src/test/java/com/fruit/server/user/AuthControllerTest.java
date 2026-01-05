package com.fruit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // ========== POST /api/auth/register ==========

    @Nested
    @DisplayName("POST /api/auth/register - User Registration")
    class Register {

        @Test
        @DisplayName("Registers user with valid request")
        void registersUserSuccessfully() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "John Doe",
                    "john@example.com",
                    "password123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email", is("john@example.com")))
                    .andExpect(jsonPath("$.fullName", is("John Doe")))
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Returns 400 when email is already registered")
        void returns400WhenEmailExists() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "John Doe",
                    "john@example.com",
                    "password123");

            // First registration
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Second registration with same email
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ========== POST /api/auth/login ==========

    @Nested
    @DisplayName("POST /api/auth/login - User Login")
    class Login {

        @BeforeEach
        void registerUser() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "John Doe",
                    "john@example.com",
                    "password123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        @Test
        @DisplayName("Logs in with valid credentials")
        void logsInSuccessfully() throws Exception {
            LoginRequest request = new LoginRequest("john@example.com", "password123");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", is("john@example.com")))
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Returns 400 when password is wrong")
        void returns400WhenWrongPassword() throws Exception {
            LoginRequest request = new LoginRequest("john@example.com", "wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 400 when user not found")
        void returns400WhenUserNotFound() throws Exception {
            LoginRequest request = new LoginRequest("notfound@example.com", "password123");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
