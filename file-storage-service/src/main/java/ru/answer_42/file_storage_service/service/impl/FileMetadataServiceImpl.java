package ru.answer_42.file_storage_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;
import ru.answer_42.file_storage_service.service.FileMetadataService;
import ru.answer_42.file_storage_service.service.Producer;

@Slf4j
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

  private final Producer producer;

  @Autowired
  public FileMetadataServiceImpl(Producer producer) {
    this.producer = producer;
  }

  public String createFileOrder(FileMetadataRequestDto fileMetadataRequestDtoDto)
      throws JsonProcessingException {
    return producer.sendMessage(fileMetadataRequestDtoDto);
  }
}