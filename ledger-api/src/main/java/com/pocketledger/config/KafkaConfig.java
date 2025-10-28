package com.pocketledger.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    public static final String TRANSACTIONS_TOPIC = "pl.transactions";

    @Bean
    NewTopic transactionsTopic(
            @Value("${pl.kafka.transactions.partitions:3}") int partitions
    ) {
        return TopicBuilder.name(TRANSACTIONS_TOPIC)
                .partitions(partitions)
                .replicas(1)   // один брокер
                .compact()
                .build();
    }

}
