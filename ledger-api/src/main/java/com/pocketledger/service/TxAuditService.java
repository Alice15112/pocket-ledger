package com.pocketledger.service;

import com.pocketledger.domain.TxAudit;
import com.pocketledger.messaging.TransactionEvent;
import com.pocketledger.repo.TxAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TxAuditService {
    private final TxAuditRepository repo;

    public void saveIfNew(TransactionEvent e) {
        if (repo.findByEventId(e.eventId()).isPresent()) return; // идемпотентность

        long minor = toMinor(e.amount());
        var row = TxAudit.builder()
                .eventId(e.eventId())
                .accountId(e.accountId())
                .txId(e.txId())
                .amountMinor(minor)
                .txType(e.type())
                .producedAt(e.createdAt())
                .build();
        repo.save(row);
    }

    private long toMinor(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, java.math.RoundingMode.UNNECESSARY).longValueExact();
    }
}
