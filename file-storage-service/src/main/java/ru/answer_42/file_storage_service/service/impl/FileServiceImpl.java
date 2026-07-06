package ru.answer_42.file_storage_service.service.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.UnsupportedFileTypeException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.repository.InMemoryFileRepository;
import ru.answer_42.file_storage_service.service.FileService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final InMemoryFileRepository fileRepository;
  private final FileMapper fileMapper;

  @Override
  public UUID save(String login, FileRequestDto fileRequestDto) {
    File file = fileMapper.toEntity(fileRequestDto);
    UUID savedFileId = fileRepository.save(login, UUID.randomUUID(), file);
    return savedFileId;
  }

  public void updateStatus(UUID id, Status status){
    File file = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    file.setStatus(status);
    fileRepository.update(file);
  }

  public List<FileResponseDto> findAll(String login, String name, LocalDate start, LocalDate end, Type type){
    Stream<File> files = fileRepository.findAll(login).stream();
    if(name != null) {
      files = files.filter(file -> file.getTitle().contains(name));
    }
    if(start != null){
      files = files.filter(file -> file.getCreatedAt().isAfter(start));
    }
    if(end != null){
      files = files.filter(file -> file.getCreatedAt().isBefore(end));
    }
    if(type != null) {
      files = files.filter(file -> file.getType().equals(type));
    }
    List<FileResponseDto> fileResponseDtos = files.map(fileMapper::toFileResponseDto).toList();
    return fileResponseDtos;
  }

  @Override
  public FileResponseDto update(UUID id, FileRequestDto fileRequestDto) {

    File existingFile = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));

    fileMapper.updateEntityFromDto(fileRequestDto, existingFile);

    File updateFile = fileRepository.update(existingFile);

    return fileMapper.toFileResponseDto(updateFile);
  }

  @Override
  public FileResponseDto update(FileRequestDto fileRequestDto) {
    String title = fileRequestDto.getTitle();
    File existingFile = fileRepository.findByTitle(title)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + title));
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
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileResponseDto findByTitle(String name) {
    File file = fileRepository.findByTitle(name).
        orElseThrow(() -> new ResourceNotFoundException("File not found with name: " + name));
    return fileMapper.toFileResponseDto(file);
  }


  @Override
  public FileResponseDto deleteById(UUID id) {
    fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    return fileMapper.toFileResponseDto(fileRepository.deleteById(id));
  }

  @Override
  public FileRequestDto multipartFileToFileRequestDto(MultipartFile file, Path destinationFile) {
    File fileEntity = new File();
    fileEntity.setTitle(file.getOriginalFilename());
    fileEntity.setSize(file.getSize());
    fileEntity.setType(determinateType(file));
    fileEntity.setDownloadUrl(destinationFile.toString());
    fileEntity.setStatus(Status.UPLOAD);
    try {
      fileEntity.setFile(file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileMapper.toFileRequestDto(fileEntity);
  }

  @Override
  public File findByPath(Path file) {
    return fileRepository.findByPath(file).
        orElseThrow(() -> new ResourceNotFoundException("File not found with path: " + file));
  }

  private Type determinateType(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    if (fileName == null || !fileName.contains(".")) {
      throw new IllegalArgumentException("Invalid filename");
    }
    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
    try {
      return Type.valueOf(fileType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new UnsupportedFileTypeException(fileType);
    }
  }
}