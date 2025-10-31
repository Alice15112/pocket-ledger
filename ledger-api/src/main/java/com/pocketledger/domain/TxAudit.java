package com.pocketledger.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions_audit",
        indexes = {
                @Index(name = "idx_transactions_audit_account", columnList = "accountId, producedAt DESC")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_transactions_audit_event", columnNames = {"eventId"})
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TxAudit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID eventId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID txId;

    @Column(nullable = false)
    private Long amountMinor;

    @Column(nullable = false, length = 16)
    private String txType;

    @Column(nullable = false)
    private Instant producedAt;
}
