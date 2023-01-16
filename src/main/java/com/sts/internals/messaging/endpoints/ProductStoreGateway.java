package com.sts.internals.messaging.endpoints;

import com.sts.internals.messaging.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProductStoreGateway {

    private final ProductService service;

    private final StreamsBuilderFactoryBean factoryBean;

    public ProductStoreGateway(ProductService service, StreamsBuilderFactoryBean factoryBean) {
        this.service = service;
        this.factoryBean = factoryBean;

    }


    @PostMapping("/message")
    public void addMessage(@RequestBody String message) {
        service.sendMessage(message);
    }

    @GetMapping("/reception")
    public KeyValueIterator<String, Long> getQuantityOnStore() throws InterruptedException {

        KafkaStreams kafkaStreams=factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams
                .store(StoreQueryParameters.fromNameAndType("stores", QueryableStoreTypes.keyValueStore()));
        //kafkaStreams.close();
        return counts.all();
    }


}
