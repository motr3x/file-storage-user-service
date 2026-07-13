package ru.answer_42.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.answer_42.user_service.model.FileOrder;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

  private final ObjectMapper objectMapper;
  private final FileOrderService fileOrderService;

  private static final String orderTopic = "t.file.order";

  @KafkaListener(topics = orderTopic)
  public void consumeMessage(String message) {
    log.info("message consumed {}", message);

    FileOrder foodOrderDto = objectMapper.readValue(message, FileOrder.class);
    fileOrderService.persistFileOrder(foodOrderDto);
  }

}