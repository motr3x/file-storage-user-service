package ru.answer_42.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.model.FileOrder;
import ru.answer_42.user_service.service.FileOrderService;
import ru.answer_42.user_service.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileOrderServiceImpl implements FileOrderService {

  private final UserService userService;


  public void persistFileOrder(FileOrder fileOrder) {
    FileOrder persistedFileMetadataDto = userService.addFileMetadata(fileOrder);
    log.info("file order persisted {}", persistedFileMetadataDto);
  }

}