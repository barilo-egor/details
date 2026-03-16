package tgb.cryptoexchange.details.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import tgb.cryptoexchange.details.kafka.DetailsResponse;
import tgb.cryptoexchange.details.kafka.DetailsResponseProducerListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAsync
public class CommonConfig {

    @Bean
    @Profile("!kafka-disabled")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @Profile("!kafka-disabled")
    public ProducerFactory<String, DetailsResponse> detailsResponseProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DetailsResponse.KafkaSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @Profile("!kafka-disabled")
    public KafkaTemplate<String, DetailsResponse> detailsResponseKafkaTemplate(
            DetailsResponseProducerListener detailsResponseProducerListener,
            KafkaProperties kafkaProperties) {
        KafkaTemplate<String, DetailsResponse> kafkaTemplate = new KafkaTemplate<>(
                detailsResponseProducerFactory(kafkaProperties));
        kafkaTemplate.setProducerListener(detailsResponseProducerListener);
        return kafkaTemplate;
    }

}
