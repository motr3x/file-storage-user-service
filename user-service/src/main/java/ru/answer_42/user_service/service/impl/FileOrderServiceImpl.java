package ru.answer_42.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.dto.FileMetadataDto;
import ru.answer_42.user_service.mapper.FileMetadataMapper;
import ru.answer_42.user_service.model.FileMetadata;
import ru.answer_42.user_service.service.FileOrderService;
import ru.answer_42.user_service.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileOrderServiceImpl implements FileOrderService {

  private final FileMetadataMapper fileMetadataMapper;
  private final UserService userService;


  public void persistFileOrder(FileMetadataDto fileMetadataDto) {
    FileMetadata fileMetadata = fileMetadataMapper.toEntity(fileMetadataDto);
    FileMetadata persistedFileMetadataDto = userService.addFileMetadata(fileMetadata);

    log.info("food order persisted {}", persistedFileMetadataDto);
  }

}