package ru.answer_42.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.answer_42.user_service.dto.FileMetadataDto;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

  private final ObjectMapper objectMapper;
  private final FileOrderService fileOrderService;

  @KafkaListener(topics = "${topic.name}")
  public void consumeMessage(String message) throws JsonProcessingException {
    log.info("message consumed {}", message);

    FileMetadataDto foodOrderDto = objectMapper.readValue(message, FileMetadataDto.class);
    fileOrderService.persistFileOrder(foodOrderDto);
  }

}