package com.sts.internals.messaging.endpoints;

import com.sts.internals.messaging.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductStoreGateway {

    private final StreamsBuilderFactoryBean factoryBean;
    private final ProductService service;


    @PostMapping("/message")
    public void addMessage(@RequestBody String message) {
        service.sendMessage(message);
    }

    @GetMapping
    public Long getQuantityOnStore(){
        KafkaStreams kafkaStreams= factoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams
                .store(StoreQueryParameters.fromNameAndType("counts", QueryableStoreTypes.keyValueStore()));
        return counts.approximateNumEntries();
    }


}
