package com.pocketledger.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionEvent(
        UUID eventId,
        UUID accountId,
        UUID txId,
        BigDecimal amount,
        String type,
        Instant createdAt
) {}
