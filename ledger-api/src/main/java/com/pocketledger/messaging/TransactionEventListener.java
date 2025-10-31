package com.pocketledger.messaging;

import com.pocketledger.service.TxAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j @Component
@RequiredArgsConstructor
public class TransactionEventListener {
    private  final TxAuditService audit;

    @KafkaListener(topics = "pl.transactions", groupId = "ledger-consumer")
    public void onTransaction(@Payload TransactionEvent event){
        log.info("Consume tx event: txId={}, accountId={}, amount={}, type={}",
                event.txId(), event.accountId(), event.amount(), event.type());

        audit.saveIfNew(event);
    }
}
