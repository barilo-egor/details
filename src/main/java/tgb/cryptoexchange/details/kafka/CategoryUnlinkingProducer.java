package tgb.cryptoexchange.details.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@Profile("!disabled-kafka")
public class CategoryUnlinkingProducer {

    @Value("${kafka.topic.details.category-unlinking}")
    private String topicName;

    private final KafkaTemplate<String, CategoryUnlinkingEvent> categoryUnlinkingKafkaTemplate;


    public CategoryUnlinkingProducer(KafkaTemplate<String, CategoryUnlinkingEvent> categoryUnlinkingKafkaTemplate) {
        this.categoryUnlinkingKafkaTemplate = categoryUnlinkingKafkaTemplate;
    }

    public void send(Long paymentTypeCategoryPid) {
        String requestId = UUID.randomUUID().toString();
        log.debug("Отправка запроса на отвязку по категории {}", paymentTypeCategoryPid);
        ProducerRecord<String, CategoryUnlinkingEvent> producerRecord = new ProducerRecord<>(topicName, CategoryUnlinkingEvent.builder()
                .paymentTypeCategoryPid(paymentTypeCategoryPid)
                .build());
        producerRecord.headers().add("API-version", "0.10.0".getBytes());
        categoryUnlinkingKafkaTemplate.send(producerRecord);
        log.debug("Запрос {} отправлен.", requestId);
    }
}
