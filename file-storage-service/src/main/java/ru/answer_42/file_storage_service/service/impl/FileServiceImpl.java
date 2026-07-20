package ru.answer_42.file_storage_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileOrder;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.exception.AccessDeniedException;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileContent;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileTypeException;
import ru.answer_42.file_storage_service.mapper.FileMapper;

import ru.answer_42.file_storage_service.dto.FileResponseDto.FileResponseDtoBuilder;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.model.UserOrder;
import ru.answer_42.file_storage_service.repository.FileRepository;
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.Producer;
import ru.answer_42.file_storage_service.service.UserOrderService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;
  private final UserOrderService userOrderService;
  private final FileMapper fileMapper;
  private final Producer producer;

  @Override
  public Long getFileSize(UUID userId, UUID fileId) {
    File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    if(file.getUserId().equals(userId)){
      return file.getSize();
    }
    throw new AccessDeniedException("User with id: " + userId + " hasn't access to file: " + fileId);
  }

  @Override
  public String getFileUrl(UUID userId, UUID fileId) {
    File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    if(file.getUserId().equals(userId)){
      return file.getDownloadUrl();
    }
    throw new AccessDeniedException("User with id: " + userId + " hasn't access to file: " + fileId);
  }

  @Override
  public String createFileOrder(FileOrder fileOrder)
      throws JsonProcessingException {
    return producer.sendMessage(fileOrder);
  }

  @Override
  @Transactional
  public FileResponseDto save(UUID userId, FileRequestDto fileMetadataRequestDto) {
    UserOrder userOrder = userOrderService.findById(userId);
    File file = fileMapper.toEntity(fileMetadataRequestDto);
    file.setUserId(userOrder.getUserId());
    file.setCreatedAt(LocalDate.now());
    file.setUpdateDate(LocalDate.now());
    return fileMapper.toFileResponseDto(fileRepository.save(file));
  }

  public List<FileResponseDto> findAllByFilter(UUID userId, String name, LocalDate start,
      LocalDate end, Type type) {
    String toType = null;
    if(!(type == null)){
      toType = type.name();
    }
    List<File> files = fileRepository.findAllWithFilter(userId, name, start, end, toType);
    List<FileResponseDto> fileResponseDtos = files.stream().map(
        fileMapper::toFileResponseDto).toList();
    return fileResponseDtos;
  }

  @Override
  public void updateStatus(UUID fileId, Status status) {
    File existingFile = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));

    existingFile.setStatus(status);

    fileRepository.save(existingFile);
  }

  @Override
  public List<FileResponseDto> findByUserIdAndFilesId(UUID userId, List<UUID> fileNames) {

    List<FileResponseDto> files = fileRepository.findAll().stream().filter(f -> accessCheck(userId, fileMapper.toFileResponseDto(f)) && fileNames.contains(f.getId())).map(fileMapper::toFileResponseDto).toList();
    return files;
  }

  @Override
  public boolean accessCheck(UUID userId, FileResponseDto responseDto) {
    if (!(responseDto.getUserId().equals(userId))) {
      throw new AccessDeniedException(
          "User with id: " + userId + " hasn't access to file: " + responseDto.getTitle());
    }
    return true;
  }

  @Override
  @Transactional
  public FileResponseDto update(UUID id, FileRequestDto fileMetadataRequestDto) {

    File existingFile = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));

    fileMapper.updateEntityFromDto(fileMetadataRequestDto, existingFile);

    File updateFile = fileRepository.save(existingFile);

    return fileMapper.toFileResponseDto(updateFile);
  }

  @Override
  public List<String> findAllTitles(UUID userId) {
    return fileRepository.findAllByUserId(userId).stream().map(File::getTitle).toList();
  }

  @Override
  public FileResponseDto findByUserIdAndId(UUID userId, UUID id) {
    File file = fileRepository.findByUserIdAndId(userId, id).
        orElseThrow(() -> new ResourceNotFoundException("User with login: " +userId + " not own file with id: " + id + " "));
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileResponseDto findByTitle(String title) {
    File file = fileRepository.findByTitle(title).
        orElseThrow(() -> new ResourceNotFoundException("File not found with title: " + title));
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public UUID getFileIdByUserIdAndTitle(UUID userId, String title) {
    File file = fileRepository.findByUserIdAndTitle(userId, title).
        orElseThrow(() -> new ResourceNotFoundException("File not found with title: " + title + " and user login: " + userId));
    return file.getId();
  }


  @Override
  @Transactional
  public FileResponseDto deleteById(UUID fileId) {
    File file = fileRepository.findById(fileId).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    fileRepository.deleteById(fileId);
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileResponseDto multipartFileToFileResponseDto(UUID userId, MultipartFile file) {
    FileResponseDtoBuilder fileMetadataBuilder = FileResponseDto.builder();
    try {
      fileMetadataBuilder.file(file.getBytes());
    } catch (IOException e) {
      throw new UnsupportedFileContent(
          "File: " + file.getOriginalFilename() + " has unsupported content");
    }
    fileMetadataBuilder
        .size(file.getSize())
        .type(determinateType(file))
        .title(file.getOriginalFilename())
        .status(Status.UPLOAD)
        .userId(userId)
        .createdAt(LocalDate.now())
        .updateDate(LocalDate.now());

    return fileMetadataBuilder.build();
  }

  @Override
  public File findByPath(Path path) {
    return fileRepository.findByDownloadUrl(path.toString()).
        orElseThrow(() -> new ResourceNotFoundException("File not found with path: " + path));
  }

  public Type determinateType(MultipartFile file) {
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

  @Override
  public FileResponseDto setUpDownloadUrl(UUID fileId, Path destinationFile) {
    File existingFile = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    existingFile.setDownloadUrl(destinationFile.resolve(fileId.toString()).toString());
    existingFile = fileRepository.save(existingFile);
    return fileMapper.toFileResponseDto(existingFile);
  }
}