package com.pocketledger.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID txId,
        UUID accountId,
        UUID userId,
        String currency,
        BigDecimal amount,
        String type,         // "CREDIT" | "DEBIT"
        String externalId,
        Instant createdAt
) {}
