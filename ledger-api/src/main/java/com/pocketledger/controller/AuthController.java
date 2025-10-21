package com.pocketledger.controller;

import com.pocketledger.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwt;

    public record LoginRequest(@Email String email, @NotBlank String password) {}
    public record LoginResponse(UUID userId, String token) {}

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        // заглушка
        UUID userId = UUID.nameUUIDFromBytes(req.email().getBytes(StandardCharsets.UTF_8));
        String token = jwt.issueToken(userId);
        return ResponseEntity.ok(new LoginResponse(userId, token));
    }
}
