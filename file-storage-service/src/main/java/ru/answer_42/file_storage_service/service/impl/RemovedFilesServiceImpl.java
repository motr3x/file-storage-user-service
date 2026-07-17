package ru.answer_42.file_storage_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.model.RemovedFiles;
import ru.answer_42.file_storage_service.repository.RemovedFilesRepository;
import ru.answer_42.file_storage_service.service.RemovedFilesService;
@RequiredArgsConstructor
@Service
public class RemovedFilesServiceImpl implements RemovedFilesService {

  private final RemovedFilesRepository removedFilesRepository;

  @Override
  public void save(RemovedFiles removedFiles){
    removedFilesRepository.save(removedFiles);
  }
}
