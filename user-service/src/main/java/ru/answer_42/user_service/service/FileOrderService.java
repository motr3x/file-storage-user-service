package ru.answer_42.user_service.service;

import ru.answer_42.user_service.model.FileOrder;

public interface FileOrderService {
  void persistFileOrder(FileOrder fileOrder);
}
