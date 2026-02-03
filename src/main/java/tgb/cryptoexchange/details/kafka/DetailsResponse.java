package tgb.cryptoexchange.details.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import tgb.cryptoexchange.details.exception.BodyMappingException;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsResponse {

    private List<Long> detailsId;

    @Slf4j
    public static class KafkaSerializer implements Serializer<DetailsResponse> {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, DetailsResponse detailsResponse) {
            try {
                if (detailsResponse == null) {
                    return new byte[0];
                }
                return objectMapper.writeValueAsBytes(detailsResponse);
            } catch (JsonProcessingException e) {
                log.error("Ошибка сериализации объекта для отправки в топик {}: {}", topic, detailsResponse);
                throw new BodyMappingException("Error occurred while mapping detailsResponse", e);
            }
        }
    }
}
