package ru.answer_42.file_storage_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;

public interface FileMetadataService {

  String createFileOrder(FileMetadataRequestDto fileMetadataRequestDtoDto)
      throws JsonProcessingException;
}
