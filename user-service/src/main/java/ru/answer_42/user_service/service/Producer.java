package ru.answer_42.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.answer_42.user_service.dto.UserOrder;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix="topic")
public class Producer {

  @NotEmpty
  private String name;

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public String sendMessage(UserOrder userOrder)
      throws JsonProcessingException {
    String orderAsMessage = objectMapper.writeValueAsString(userOrder);
    kafkaTemplate.send(name, orderAsMessage);

    log.info("User order produced {}", orderAsMessage);

    return "message sent";
  }
}