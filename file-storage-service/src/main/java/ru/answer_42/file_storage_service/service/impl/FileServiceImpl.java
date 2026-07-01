package ru.answer_42.file_storage_service.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.repository.InMemoryFileRepository;
import ru.answer_42.file_storage_service.service.FileService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final InMemoryFileRepository fileRepository;
  private final FileMapper fileMapper;

  @Override
  public FileResponseDto save(FileRequestDto fileRequestDto) {
    File file = fileMapper.toEntity(fileRequestDto);
    File savedFile = fileRepository.save(UUID.randomUUID(), file);
    return fileMapper.toFileResponseDto(savedFile);
  }

  @Override
  public FileResponseDto update(UUID id, FileRequestDto fileRequestDto) {

    File existingFile = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

    fileMapper.updateEntityFromDto(fileRequestDto, existingFile);

    File updateFile = fileRepository.update(existingFile);

    return fileMapper.toFileResponseDto(updateFile);
  }

  @Override
  public List<String> findAllTitles() {
    return fileRepository.findAllTitles();
  }

  @Override
  public FileResponseDto findById(UUID id) {
    File file = fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    return fileMapper.toFileResponseDto(file);
  }


  @Override
  public FileResponseDto deleteById(UUID id) {
    fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    return fileMapper.toFileResponseDto(fileRepository.deleteById(id));
  }
}
