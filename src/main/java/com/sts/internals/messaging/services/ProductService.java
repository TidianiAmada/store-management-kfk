package com.sts.internals.messaging.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ProductService {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message){
        kafkaTemplate.send("products",message).whenComplete(
                (stringStringSendResult, throwable) -> {
                    log.info("Message sent to topic: {} ",stringStringSendResult);
                    log.error("Failed to send message ",throwable);
                }
        );
    }
}

