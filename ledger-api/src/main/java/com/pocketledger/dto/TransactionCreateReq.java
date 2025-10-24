package com.pocketledger.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionCreateReq(
        @NotNull @Positive @Digits(integer = 19, fraction = 2)
        BigDecimal amount,

        @NotNull @Pattern(regexp = "CREDIT|DEBIT", message = "type must be CREDIT or DEBIT")
        String type,

        @Size(max = 64)
        String externalId
) {}
