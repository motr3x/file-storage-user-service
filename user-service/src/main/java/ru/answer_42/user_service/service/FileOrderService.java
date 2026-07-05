package ru.answer_42.user_service.service;

import ru.answer_42.user_service.dto.FileMetadataDto;

public interface FileOrderService {
  void persistFileOrder(FileMetadataDto fileMetadataDto);
}
