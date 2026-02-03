package tgb.cryptoexchange.details.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.details.kafka.DetailsResponse;

import java.util.UUID;

@Service
@Slf4j
public class DetailsResponseService {

    private final KafkaTemplate<String, DetailsResponse> detailsResponseKafkaTemplate;

    private final String detailsResponseTopic;

    public DetailsResponseService(
            KafkaTemplate<String, DetailsResponse> detailsResponseKafkaTemplate,
            @Value("${kafka.topic.details.response}") String detailsResponseTopic) {
        this.detailsResponseKafkaTemplate = detailsResponseKafkaTemplate;
        this.detailsResponseTopic = detailsResponseTopic;
    }

    public void process(DetailsResponse detailsResponse) {
        detailsResponseKafkaTemplate.send(detailsResponseTopic, UUID.randomUUID().toString(), detailsResponse);
    }

}
