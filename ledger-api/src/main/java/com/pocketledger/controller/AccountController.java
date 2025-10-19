package com.pocketledger.controller;

import com.pocketledger.domain.Account;
import com.pocketledger.dto.AccountView;
import com.pocketledger.repo.AccountRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository repo;

    public record CreateReq(@NotBlank String currency) {}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountView> create(@RequestBody CreateReq req, Authentication auth) {
        UUID ownerId = (UUID) auth.getPrincipal();

        Account acc = Account.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .currency(req.currency())
                .build();  // status=ACTIVE из @Builder.Default

        repo.save(acc);

        return ResponseEntity.ok(toView(acc));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AccountView> myAccounts(Authentication auth) {
        UUID ownerId = (UUID) auth.getPrincipal();
        return repo.findByOwnerId(ownerId).stream()
                .map(this::toView)
                .toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(@PathVariable UUID id, Authentication auth) {
        UUID ownerId = (UUID) auth.getPrincipal();

        return repo.findById(id)
                .filter(a -> Objects.equals(a.getOwnerId(), ownerId))
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toView(a)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ErrorView("not found")));
    }

    private AccountView toView(Account a) {
        return new AccountView(a.getId(), a.getCurrency(), a.getStatus(), a.getCreatedAt());
    }

    public record ErrorView(String error) {}
}

