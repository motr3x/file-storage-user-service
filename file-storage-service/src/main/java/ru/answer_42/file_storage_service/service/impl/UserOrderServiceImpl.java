package ru.answer_42.file_storage_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.model.UserOrder;
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.UserOrderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrderServiceImpl implements UserOrderService {

  private final FileService fileService;

  public void persistUserOrder(UserOrder UserOrder) {
    UserOrder persistedFileMetadataDto = fileService.addFileMetadata(UserOrder);

    log.info("User order persisted {}", persistedFileMetadataDto);
  }

}