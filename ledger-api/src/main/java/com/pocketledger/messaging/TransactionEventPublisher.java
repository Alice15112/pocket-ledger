package com.pocketledger.messaging;

import com.pocketledger.config.KafkaConfig;
import com.pocketledger.events.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public void publish(TransactionCreatedEvent event) {
        var future = kafka.send(KafkaConfig.TRANSACTIONS_TOPIC,
                event.accountId().toString(), event);

        future.whenComplete((SendResult<String, Object> result, Throwable ex) -> {
            if (ex != null) {
                log.error("Kafka send FAILED for tx={} reason={}", event.txId(), ex.toString(), ex);
                return;
            }
            RecordMetadata m = result.getRecordMetadata();
            log.info("Kafka sent tx={} to {}-{}@{}",
                    event.txId(), m.topic(), m.partition(), m.offset());
        });
    }
}
