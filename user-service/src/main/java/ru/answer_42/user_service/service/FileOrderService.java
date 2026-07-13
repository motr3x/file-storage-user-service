package ru.answer_42.user_service.service;

import ru.answer_42.user_service.dto.FileMetadataDto;
import ru.answer_42.user_service.dto.FileMetadataOrder;

public interface FileOrderService {
  void persistFileOrder(FileMetadataOrder fileMetadataOrder);
}
