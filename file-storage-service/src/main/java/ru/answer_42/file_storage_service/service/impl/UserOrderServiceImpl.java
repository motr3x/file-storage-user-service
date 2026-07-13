package ru.answer_42.file_storage_service.service.impl;

<<<<<<< HEAD
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.model.UserOrder;
import ru.answer_42.file_storage_service.repository.UserOrderRepository;
=======
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.model.UserOrder;
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.UserOrderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrderServiceImpl implements UserOrderService {

<<<<<<< HEAD
  private final UserOrderRepository userOrderRepository;

  public void persistUserOrder(UserOrder UserOrder) {
    UserOrder persistedUserOrder = addFileMetadata(UserOrder);

    log.info("User order persisted {}", persistedUserOrder);
  }

  @Override
  @Cacheable(value = "user:byId", key = "#userId", sync = true)
  public UserOrder findById(UUID userId){
    return userOrderRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
  }

  @Override
  public UserOrder addFileMetadata(UserOrder userOrder) {
    return userOrderRepository.save(userOrder);
=======
  private final FileService fileService;

  public void persistUserOrder(UserOrder UserOrder) {
    UserOrder persistedFileMetadataDto = fileService.addFileMetadata(UserOrder);

    log.info("User order persisted {}", persistedFileMetadataDto);
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
  }

}