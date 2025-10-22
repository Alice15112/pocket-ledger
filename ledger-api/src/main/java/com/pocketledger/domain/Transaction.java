package com.pocketledger.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name="transactions")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private UUID id;

    @Column(name="account_id", nullable=false)
    private UUID accountId;

    @Column(nullable=false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=6)
    private Type type;

    @Column(name="external_id", unique = true)
    private String externalId;

    @CreationTimestamp
    @Column(name="created_at", updatable=false, nullable=false)
    private Instant createdAt;

    public enum Type { CREDIT, DEBIT }
}