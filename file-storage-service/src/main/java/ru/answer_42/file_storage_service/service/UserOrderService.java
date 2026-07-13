package ru.answer_42.file_storage_service.service;

import ru.answer_42.file_storage_service.model.UserOrder;

public interface UserOrderService {
  void persistUserOrder(UserOrder userOrder);
}
