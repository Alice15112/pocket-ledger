package com.pocketledger.controller;

import com.pocketledger.domain.Account;
import com.pocketledger.domain.Transaction;
import com.pocketledger.dto.BalanceView;
import com.pocketledger.dto.TransactionCreateReq;
import com.pocketledger.dto.TransactionView;
import com.pocketledger.events.TransactionCreatedEvent;
import com.pocketledger.messaging.TransactionEventPublisher;
import com.pocketledger.repo.AccountRepository;
import com.pocketledger.repo.TransactionRepository;
import com.pocketledger.web.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId}")
public class TransactionController {

    private final AccountRepository accounts;
    private final TransactionRepository txRepo;
    private final TransactionEventPublisher publisher;

    // POST /accounts/{accountId}/transactions
    @PostMapping(
            value = "/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TransactionView> create(@PathVariable UUID accountId,
                                                  @RequestBody @Valid TransactionCreateReq req,
                                                  Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        Account acc = accounts.findById(accountId)
                .filter(a -> Objects.equals(a.getOwnerId(), userId))
                .orElseThrow(() -> new NotFoundException("account"));

        // идемпотентность по externalId
        if (req.externalId() != null && !req.externalId().isBlank()) {
            var existing = txRepo.findByExternalId(req.externalId());
            if (existing.isPresent()) {
                // если externalId уже использован для другого счёта — конфликт
                if (!Objects.equals(existing.get().getAccountId(), accountId)) {
                    return ResponseEntity.status(409).build();
                }
                return ResponseEntity.ok(toView(existing.get()));
            }
        }

        var normalizedAmount = req.amount().setScale(2, RoundingMode.HALF_UP);
        var type = Transaction.Type.valueOf(req.type().toUpperCase(Locale.ROOT));

        var tx = Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .amount(normalizedAmount)
                .type(type)
                .externalId(req.externalId())
                .build();

        tx = txRepo.saveAndFlush(tx);

        publisher.publish(new TransactionCreatedEvent(
                tx.getId(),
                tx.getAccountId(),
                acc.getOwnerId(),
                acc.getCurrency(),
                tx.getAmount(),
                tx.getType().name(),
                tx.getExternalId(),
                tx.getCreatedAt()
        ));

        return ResponseEntity.ok(toView(tx));
    }

    // GET /accounts/{accountId}/transactions
    @GetMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransactionView> list(@PathVariable UUID accountId, Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        accounts.findById(accountId)
                .filter(a -> Objects.equals(a.getOwnerId(), userId))
                .orElseThrow(() -> new NotFoundException("account"));

        return txRepo.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::toView)
                .toList();
    }

    // GET /accounts/{accountId}/balance
    @GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public BalanceView balance(@PathVariable UUID accountId, Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        accounts.findById(accountId)
                .filter(a -> Objects.equals(a.getOwnerId(), userId))
                .orElseThrow(() -> new NotFoundException("account"));

        return new BalanceView(accountId, txRepo.calcBalance(accountId));
    }

    private TransactionView toView(Transaction t) {
        return new TransactionView(t.getId(), t.getAmount(), t.getType().name(), t.getCreatedAt());
    }
}
