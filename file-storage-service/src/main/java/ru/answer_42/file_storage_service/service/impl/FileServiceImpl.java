package ru.answer_42.file_storage_service.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.webresources.FileResource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto.FileMetadataResponseDtoBuilder;
import ru.answer_42.file_storage_service.exception.AccessDeniedException;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileContent;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileTypeException;
import ru.answer_42.file_storage_service.exception.validation.Marker;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.model.User;
import ru.answer_42.file_storage_service.repository.FileRepository;
import ru.answer_42.file_storage_service.repository.UserRepository;
import ru.answer_42.file_storage_service.service.FileService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;
  private final UserRepository userRepository;
  private final FileMapper fileMapper;

  @Override
  public FileMetadataResponseDto save(String login, FileMetadataRequestDto fileMetadataRequestDto) {
    User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
    File file = fileMapper.toEntity(fileMetadataRequestDto);
    file.setUserLogin(login);
    file.setUser(user);
    file.setCreatedAt(LocalDate.now());
    file.setUpdateDate(LocalDate.now());
    fileRepository.save(file);
    return fileMapper.toFileResponseDto(fileRepository.save(file));
  }

  public List<FileMetadataResponseDto> findAll(String login, String name, LocalDate start,
      LocalDate end, Type type) {
    String toType = null;
    if(!(type == null)){
      toType = type.name();
    }
    List<File> files = fileRepository.findAllWithFilter(login, name, start, end, toType);
    List<FileMetadataResponseDto> fileMetadataResponseDtos = files.stream().map(
        fileMapper::toFileResponseDto).toList();
    return fileMetadataResponseDtos;
  }

  @Override
  public void updateStatus(UUID id, Status status) {
    File existingFile = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));

    existingFile.setStatus(status);

    fileRepository.save(existingFile);
  }

  @Override
  public List<FileMetadataResponseDto> findByFileNamesId(String login, List<UUID> fileNames) {
    List<FileMetadataResponseDto> files = fileRepository.findAll().stream().filter(f -> accessCheck(f.getUserLogin(), fileMapper.toFileResponseDto(f)) && fileNames.contains(f.getId())).map(fileMapper::toFileResponseDto).toList();
    return files;
  }

  private boolean accessCheck(String login, FileMetadataResponseDto responseDto) {
    if (!(responseDto.getUserLogin().equals(login))) {
      throw new AccessDeniedException(
          "User with login: " + login + " hasn't access to file: " + responseDto.getTitle());
    }
    return true;
  }
  @Override
  public FileMetadataResponseDto update(UUID id, FileMetadataRequestDto fileMetadataRequestDto) {

    File existingFile = fileRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));

    fileMapper.updateEntityFromDto(fileMetadataRequestDto, existingFile);

    File updateFile = fileRepository.save(existingFile);

    return fileMapper.toFileResponseDto(updateFile);
  }

  @Override
  public List<String> findAllTitles(String login) {
    return fileRepository.findAllByUserLogin(login).stream().map(File::getTitle).toList();
  }

  @Override
  public FileMetadataResponseDto findByUserLoginAndId(String login, UUID id) {
    File file = fileRepository.findByUserLoginAndId(login, id).
        orElseThrow(() -> new ResourceNotFoundException("User with login: " +login + " not own file with id: " + id + " "));
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileMetadataResponseDto findByTitle(String name) {
    File file = fileRepository.findByTitle(name).
        orElseThrow(() -> new ResourceNotFoundException("File not found with title: " + name));
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public UUID getFileIdByLoginAndTitle(String login, String title) {
    File file = fileRepository.findByUserLoginAndTitle(login, title).
        orElseThrow(() -> new ResourceNotFoundException("File not found with title: " + title + " and user login: " + login));
    return file.getId();
  }


  @Override
  public FileMetadataResponseDto deleteById(UUID id) {
    File file = fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    fileRepository.deleteById(id);
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileMetadataResponseDto multipartFileToFileResponseDto(String login, MultipartFile file,
      Path destinationFile) {
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
  public File findByPath(Path path) {
    return fileRepository.findByDownloadUrl(path.toString()).
        orElseThrow(() -> new ResourceNotFoundException("File not found with path: " + path));
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