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
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto.FileMetadataResponseDtoBuilder;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileContent;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileTypeException;
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
  public UUID save(String login, FileMetadataRequestDto fileMetadataRequestDto) {
    File file = fileMapper.toEntity(fileMetadataRequestDto);
    file.setCreatedAt(LocalDate.now());
    UUID savedFileId = fileRepository.save(login, UUID.randomUUID(), file);
    return savedFileId;
  }

  public void updateStatus(UUID id, Status status) {
    File file = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    file.setStatus(status);
    fileRepository.update(file);
  }

  public List<FileMetadataResponseDto> findAll(String login, String name, LocalDate start,
      LocalDate end, Type type) {
    Stream<File> files = fileRepository.findAll(login).stream();
    if (name != null) {
      files = files.filter(file -> file.getTitle().contains(name));
    }
    if (start != null) {
      files = files.filter(file -> file.getCreatedAt().isAfter(start));
    }
    if (end != null) {
      files = files.filter(file -> file.getCreatedAt().isBefore(end));
    }
    if (type != null) {
      files = files.filter(file -> file.getType().equals(type));
    }
    List<FileMetadataResponseDto> fileMetadataResponseDtos = files.map(
        fileMapper::toFileResponseDto).toList();
    return fileMetadataResponseDtos;
  }

  @Override
  public FileMetadataResponseDto update(UUID id, FileMetadataRequestDto fileMetadataRequestDto) {

    File existingFile = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));

    fileMapper.updateEntityFromDto(fileMetadataRequestDto, existingFile);

    File updateFile = fileRepository.update(existingFile);

    return fileMapper.toFileResponseDto(updateFile);
  }

  @Override
  public List<String> findAllTitles() {
    return fileRepository.findAllTitles();
  }

  @Override
  public FileMetadataResponseDto findById(UUID id) {
    File file = fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileMetadataResponseDto findByTitle(String name) {
    File file = fileRepository.findByTitle(name).
        orElseThrow(() -> new ResourceNotFoundException("File not found with name: " + name));
    return fileMapper.toFileResponseDto(file);
  }


  @Override
  public FileMetadataResponseDto deleteById(UUID id) {
    fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    return fileMapper.toFileResponseDto(fileRepository.deleteById(id));
  }

  @Override
  public FileMetadataResponseDto multipartFileToFileResponseDto(String login, MultipartFile file,
      Path destinationFile) {
//    FileRequestDtoBuilder fileRequestDtoBuilder = FileRequestDto.builder()
//        .file()
//    FileBuilder fileEntity = File.builder()
//        .userLogin(login)
//        .title(file.getOriginalFilename())
//        .size(file.getSize())
//        .type(determinateType(file))
//        .downloadUrl(destinationFile.toString())
//        .status(Status.UPLOAD)
//        .createdAt(LocalDate.now())
//        .updateDate(LocalDate.now());
//    try {
//      fileEntity.file(file.getBytes());
//    } catch (IOException e){
//      throw new UnsupportedFileContent("File: " + file.getOriginalFilename() + " has unsupported content");
//    }
    FileMetadataResponseDtoBuilder fileMetadataBuilder = FileMetadataResponseDto.builder();
    try {
      fileMetadataBuilder.file(file.getBytes());
    } catch (IOException e) {
      throw new UnsupportedFileContent(
          "File: " + file.getOriginalFilename() + " has unsupported content");
    }
    fileMetadataBuilder
        .size(file.getSize())
        .downloadUrl(destinationFile.toString())
        .type(determinateType(file))
        .title(file.getOriginalFilename())
        .status(Status.UPLOAD)
        .userLogin(login)
        .createdAt(LocalDate.now())
        .updateDate(LocalDate.now());

    return fileMetadataBuilder.build();
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