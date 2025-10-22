package com.pocketledger.repo;

import com.pocketledger.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);

    Optional<Transaction> findByExternalId(String externalId);

    @Query(""" 
     select coalesce(sum(case when t.type = 'CREDIT' then t.amount else -t.amount end), 0)
     from Transaction t
     where t.accountId = :accountId
     """)
    BigDecimal calcBalance(UUID accountId);
}