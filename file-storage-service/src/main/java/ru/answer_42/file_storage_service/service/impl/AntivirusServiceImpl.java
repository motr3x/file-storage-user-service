package ru.answer_42.file_storage_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.service.AntivirusService;
import ru.answer_42.file_storage_service.service.FileService;

@Service
@RequiredArgsConstructor
public class AntivirusServiceImpl implements AntivirusService {

  private final FileService fileService;
  private final FileMapper fileMapper;

  @Override
  public boolean scan(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    FileResponseDto fileResponseDto = fileService.findByTitle(fileName);
    fileResponseDto.setStatus(Status.IN_PROCESS);
    fileService.update(fileMapper.toFileRequestDto(fileResponseDto));
    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    fileResponseDto.setStatus(Status.READY);
    fileService.update(fileMapper.toFileRequestDto(fileResponseDto));
    return true;
  }
}
