package com.pocketledger.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private  String bootstrap;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ledger-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.pocketledger.messaging");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> cf,
            KafkaTemplate<String, Object> kafkaTemplate
    ){
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(cf);

        var backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(500);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(2_000);

        var dltRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new org.apache.kafka.common.TopicPartition("pl.transactions.dlt", record.partition()));

        var errorHandler = new DefaultErrorHandler(dltRecoverer, backoff);
        factory.setCommonErrorHandler(errorHandler);

        factory.setConcurrency(2);
        factory.getContainerProperties().setPollTimeout(1500);

        return factory;
    }
}
