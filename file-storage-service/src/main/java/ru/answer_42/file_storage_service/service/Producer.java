package ru.answer_42.file_storage_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class Producer {

  @Value("${topic.name}")
  private String orderTopic;

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public String sendMessage(FileResponseDto fileResponseDto) throws JsonProcessingException {
    String orderAsMessage = objectMapper.writeValueAsString(fileResponseDto);
    kafkaTemplate.send(orderTopic, orderAsMessage);

    log.info("food order produced {}", orderAsMessage);

    return "message sent";
  }
}