package com.sts.internals.messaging.schema;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class ProductSerde extends Serdes.WrapperSerde<Product> {
    public ProductSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(Product.class));
    }
}
