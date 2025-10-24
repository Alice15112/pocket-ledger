package com.pocketledger.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionView(
        UUID id,
        BigDecimal amount,
        String type,
        Instant createdAt
) {}
