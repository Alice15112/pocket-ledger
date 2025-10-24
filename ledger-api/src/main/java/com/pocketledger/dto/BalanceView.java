package com.pocketledger.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceView(
        UUID accountId,
        BigDecimal balance
) {}
