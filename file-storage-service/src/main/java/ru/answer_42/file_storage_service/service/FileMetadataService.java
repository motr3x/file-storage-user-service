package ru.answer_42.file_storage_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.answer_42.file_storage_service.dto.FileResponseDto;

public interface FileMetadataService {
  String createFileOrder(FileResponseDto fileResponseDto) throws JsonProcessingException;
}
