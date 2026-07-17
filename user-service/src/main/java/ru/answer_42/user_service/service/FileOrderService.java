package ru.answer_42.user_service.service;

import java.util.UUID;
import ru.answer_42.user_service.model.FileOrder;

public interface FileOrderService {
  void persistFileOrder(FileOrder fileOrder);

  boolean ownerCheck(UUID userId, UUID fileId);

  FileOrder findById(UUID fileId);
}
