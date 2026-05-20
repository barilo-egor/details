package tgb.cryptoexchange.details.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import tgb.cryptoexchange.details.exception.BaseException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUnlinkingEvent {

    private Long paymentTypeCategoryPid;

    @Slf4j
    public static class KafkaSerializer implements Serializer<CategoryUnlinkingEvent> {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, CategoryUnlinkingEvent callback) {
            try {
                if (callback == null) {
                    return new byte[0];
                }
                return objectMapper.writeValueAsBytes(callback);
            } catch (JsonProcessingException e) {
                log.error("Ошибка сериализации categoryUnlinkingEvent для отправки в топик {}: {}", topic, callback);
                throw new BaseException("Error occurred while mapping categoryUnlinkingEvent", e);
            }
        }
    }

}
