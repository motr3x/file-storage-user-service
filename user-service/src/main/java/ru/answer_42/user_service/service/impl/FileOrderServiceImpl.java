package ru.answer_42.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.model.FileOrder;
import ru.answer_42.user_service.repository.FileOrderRepository;
import ru.answer_42.user_service.service.FileOrderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileOrderServiceImpl implements FileOrderService {

  private final FileOrderRepository fileOrderRepository;

  public void persistFileOrder(FileOrder fileOrder) {
    FileOrder persistedFileMetadataDto = addFileMetadata(fileOrder);
    log.info("file order persisted {}", persistedFileMetadataDto);
  }

  public FileOrder addFileMetadata(FileOrder fileOrder) {
    return fileOrderRepository.save(fileOrder);
  }

}