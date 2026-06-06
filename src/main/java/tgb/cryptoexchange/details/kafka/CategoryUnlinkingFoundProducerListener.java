package tgb.cryptoexchange.details.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("!disabled-kafka")
public class CategoryUnlinkingFoundProducerListener implements ProducerListener<String, CategoryUnlinkingEvent> {

    @Override
    public void onSuccess(ProducerRecord<String, CategoryUnlinkingEvent> producerRecord, RecordMetadata recordMetadata) {
        log.debug("Успешно отправлен ивент. Key={}, event={}.", producerRecord.key(), producerRecord.value());
    }

    @Override
    public void onError(ProducerRecord<String, CategoryUnlinkingEvent> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.error("Ошибка при попытке отправить ивент в топик. Key={}, event={}: {}.",
                producerRecord.key(), producerRecord.value(), exception.getMessage(), exception);
    }
}
