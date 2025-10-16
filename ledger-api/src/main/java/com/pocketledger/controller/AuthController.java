package com.pocketledger.controller;

import com.pocketledger.security.JwtUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwt;
    public AuthController(JwtUtil jwt) { this.jwt = jwt; }

    record LoginReq(@Email String email, @NotBlank String password) {}
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        // Заглушка пока
        UUID userId = UUID.nameUUIDFromBytes(req.email().getBytes());
        String token = jwt.issueToken(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "token", token));
    }
}
