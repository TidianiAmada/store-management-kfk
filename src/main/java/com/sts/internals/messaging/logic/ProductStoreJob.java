package com.sts.internals.messaging.logic;

import com.sts.internals.messaging.schema.Product;
import com.sts.internals.messaging.schema.Store;
import com.sts.internals.messaging.schema.StoreSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;


@Component
public class ProductStoreJob {
    private final Serde<String> STRING_SERDES = Serdes.String();



    public void globalSyncPipeline(KStream<String, Product> productStream){
        KeyValueBytesStoreSupplier stores= Stores.persistentKeyValueStore("stores");

        KGroupedStream<String,Double> storesByProductId =productStream.
                map((key, product) -> new KeyValue<>(product.getProductId(),Double.parseDouble(product.getQuantityInLocalStore())))
                .groupByKey();
        KTable<String, Store> states=storesByProductId.aggregate(
                Store::new,(s, aDouble, store) ->{
                    store.setCount(store.getCount()+1);

                    return store;
                },Materialized.with(STRING_SERDES,new StoreSerde()));
        states.mapValues(Store::getCount, Materialized.as(stores));
    }

}
