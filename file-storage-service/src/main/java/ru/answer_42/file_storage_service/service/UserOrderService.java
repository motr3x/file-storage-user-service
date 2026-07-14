package ru.answer_42.file_storage_service.service;

import java.util.UUID;
import ru.answer_42.file_storage_service.model.UserOrder;

public interface UserOrderService {

  void persistUserOrder(UserOrder userOrder);

  UserOrder findById(UUID userId);

  UserOrder addFileMetadata(UserOrder userOrder);
}
