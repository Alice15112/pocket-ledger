package com.pocketledger.repo;

import com.pocketledger.domain.TxAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TxAuditRepository extends JpaRepository<TxAudit, Long> {
    Optional<TxAudit> findByEventId(UUID eventId);
}
