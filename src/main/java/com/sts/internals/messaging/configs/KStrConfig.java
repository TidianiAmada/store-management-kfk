package com.sts.internals.messaging.configs;

import com.sts.internals.messaging.logic.ProductStoreJob;
import com.sts.internals.messaging.schema.Product;
import com.sts.internals.messaging.schema.ProductSerde;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KStrConfig {

    @Autowired
    private Environment env;
    @Autowired
    private ProductStoreJob kstreamProcessor;


    @Qualifier
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfig(){
        Map<String,Object> props=new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.properties.bootstrap.servers",
                "localhost:9092"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG,450);
        props.put(StreamsConfig.POLL_MS_CONFIG,"45000");
        return new KafkaStreamsConfiguration(props);

    }

    @Bean
    public NewTopic inputTopic(){
        return TopicBuilder.name("products")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic outputTopic(){
        return TopicBuilder.name("stores")
                .partitions(1)
                .replicas(1)
                .build();
    }
  /*  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME)
    public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder() {
        StreamsBuilderFactoryBean streamsBuilderFactoryBean = new StreamsBuilderFactoryBean();
        streamsBuilderFactoryBean.setAutoStartup(false);
        return streamsBuilderFactoryBean;
    }*/
    @Bean
    public KStream<String, Product> kStream(StreamsBuilder kStreamBuilder) {

        KStream<String, Product> stream = kStreamBuilder.stream(inputTopic().name(), Consumed.with(Serdes.String(),new ProductSerde()));
        //kstreamProcessor.buildPipeline(stream);
        kstreamProcessor.globalSyncPipeline(stream);

        return stream;
    }


}
