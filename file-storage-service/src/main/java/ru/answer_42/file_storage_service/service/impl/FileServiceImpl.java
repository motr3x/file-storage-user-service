package ru.answer_42.file_storage_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileOrder;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
=======
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileMetadataOrder;
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto.FileMetadataResponseDtoBuilder;
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
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
import ru.answer_42.file_storage_service.repository.UserOrderRepository;
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.Producer;
import ru.answer_42.file_storage_service.service.UserOrderService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;
<<<<<<< HEAD
<<<<<<< HEAD
  private final UserOrderService userOrderService;
=======
  private final UserOrderRepository userOrderRepository;
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
=======
  private final UserOrderService userOrderService;
>>>>>>> d41d28f (fix(file-storage): fix redis behavior)
  private final FileMapper fileMapper;
  private final Producer producer;
  private final RedisTemplate<String, FileMetadataOrder> redisTemplate;

  private static final String CACHE_KEY_PREFIX = "file:";
  private static final Duration TTL = Duration.ofMinutes(10);


  @Override
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> d41d28f (fix(file-storage): fix redis behavior)
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
=======
  public UserOrder addFileMetadata(UserOrder userOrder) {
    String userLogin = userOrder.getLogin();
    UserOrder user = new UserOrder();
    user.setLogin(userLogin);
    userOrderRepository.save(user);
    return null;
  }

  public FileMetadataOrder getById(UUID id){
    String cacheKey = CACHE_KEY_PREFIX + id;
    FileMetadataOrder cached = redisTemplate.opsForValue().get(cacheKey);
    if (cached != null) {
      return cached;
    }
    FileMetadataOrder fromDb = fileMapper.toFileMetadataOrderFromFile(fileRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found: " + id)));

    // Кэшируем объект с TTL
    redisTemplate.opsForValue().set(cacheKey, fromDb, TTL);

    return fromDb;
  }
  @Override
  public String createFileOrder(FileMetadataOrder fileMetadataOrder)
      throws JsonProcessingException {
    return producer.sendMessage(fileMetadataOrder);
  }

  @Override
  public FileMetadataResponseDto save(String login, FileMetadataRequestDto fileMetadataRequestDto) {
    UserOrder userOrder = userOrderRepository.findByLogin(login)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
    File file = fileMapper.toEntity(fileMetadataRequestDto);
    file.setUserOrder(userOrder);
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
    file.setCreatedAt(LocalDate.now());
    file.setUpdateDate(LocalDate.now());
    return fileMapper.toFileResponseDto(fileRepository.save(file));
  }

  public List<FileResponseDto> findAll(UUID userId, String name, LocalDate start,
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
<<<<<<< HEAD
  public List<FileResponseDto> findByUserIdAndFilesId(UUID userId, List<UUID> fileNames) {

    List<FileResponseDto> files = fileRepository.findAll().stream().filter(f -> accessCheck(userId, fileMapper.toFileResponseDto(f)) && fileNames.contains(f.getId())).map(fileMapper::toFileResponseDto).toList();
=======
  public List<FileMetadataResponseDto> findByLoginAndFilesId(String login, List<UUID> fileNames) {

    List<FileMetadataResponseDto> files = fileRepository.findAll().stream().filter(f -> accessCheck(f.getUserLogin(), fileMapper.toFileResponseDto(f)) && fileNames.contains(f.getId())).map(fileMapper::toFileResponseDto).toList();
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
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

    String cacheKey = CACHE_KEY_PREFIX + id;
    redisTemplate.delete(cacheKey);

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
<<<<<<< HEAD
  public FileResponseDto findByTitle(String title) {
=======
  public FileMetadataResponseDto findByTitle(String title) {
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
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
<<<<<<< HEAD
  @Transactional
  public FileResponseDto deleteById(UUID fileId) {
    File file = fileRepository.findById(fileId).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    fileRepository.deleteById(fileId);
=======
  public FileMetadataResponseDto deleteById(UUID id) {
    File file = fileRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    String cacheKey = CACHE_KEY_PREFIX + id;
    redisTemplate.delete(cacheKey);
    fileRepository.deleteById(id);
>>>>>>> 53ad478 (feat(file-storage): refactor kafka service & add demo redis service)
    return fileMapper.toFileResponseDto(file);
  }

  @Override
  public FileResponseDto multipartFileToFileResponseDto(UUID userId, MultipartFile file,
      Path destinationFile) {
    FileResponseDtoBuilder fileMetadataBuilder = FileResponseDto.builder();
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
}