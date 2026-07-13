package ru.answer_42.file_storage_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.answer_42.file_storage_service.model.UserOrder;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {


  private final ObjectMapper objectMapper;
  private final UserOrderService userOrderService;

  private static final String orderTopic = "t.user.order";

  @KafkaListener(topics = orderTopic)
  public void consumeMessage(String message) {
    log.info("message consumed {}", message);

    UserOrder userOrder = objectMapper.readValue(message, UserOrder.class);
    userOrderService.persistUserOrder(userOrder);
  }

}