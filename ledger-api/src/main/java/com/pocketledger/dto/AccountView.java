package com.pocketledger.dto;

import java.time.Instant;
import java.util.UUID;

public record AccountView (
    UUID id,
    String currency,
    String status,
    Instant createdAt
    ){}
