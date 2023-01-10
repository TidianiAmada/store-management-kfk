package com.sts.internals.messaging.logic;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProductStoreJob {
    private final Serde<String> STRING_SERDES = Serdes.String();

    void buildPipeline(StreamsBuilder streamsBuilder){
        KStream<String,String> productStream=streamsBuilder.stream("products",
                Consumed.with(STRING_SERDES,STRING_SERDES));
        KTable<String,Long> productCounts=productStream.mapValues((ValueMapper<String, String>) String::toLowerCase)
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .groupBy((key, word) -> word, Grouped.with(STRING_SERDES, STRING_SERDES))
                .count(Materialized.as("counts"));

        productCounts.toStream().to("stores");
    }
}
