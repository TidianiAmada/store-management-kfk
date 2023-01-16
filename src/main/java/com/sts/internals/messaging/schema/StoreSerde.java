package com.sts.internals.messaging.schema;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class StoreSerde extends Serdes.WrapperSerde<Store>{


    public StoreSerde() {
        super(new JsonSerializer<>(),new JsonDeserializer<>(Store.class));
    }
}
