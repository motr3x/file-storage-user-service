package ru.answer_42.file_storage_service.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.config.StorageProperties;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.FileHasVirusException;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.file.FileSizeLimitExceededException;
import ru.answer_42.file_storage_service.exception.storage.StorageLocationEmptyException;
import ru.answer_42.file_storage_service.exception.storage.StorageStoreFailedException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.service.AntivirusService;
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

  public final static long MIN_SIZE = 1L;
  public final static long MAX_SIZE = 15 * 1024 * 1024L;
  private final Path rootLocation;
  private final FileService fileService;
  private final AntivirusService antivirusService;
  private final FileMapper fileMapper;

  @Autowired
  public StorageServiceImpl(StorageProperties properties, FileService fileService,
      AntivirusService antivirusService, FileMapper fileMapper) {
    if (properties.getLocation().trim().isEmpty()) {
      throw new StorageLocationEmptyException("File upload location can not be Empty.");
    }
    this.rootLocation = Paths.get(properties.getLocation());
    this.fileService = fileService;
    this.antivirusService = antivirusService;
    this.fileMapper = fileMapper;
  }


  @Override
  public FileMetadataResponseDto store(String login, MultipartFile file) {
    Executor executor = Executors.newFixedThreadPool(10);
    try {
      if (file.isEmpty()) {
        throw new FileIsEmptyException("Failed to store empty file.");
      }
      Path destinationFile = this.rootLocation.resolve(
              login).resolve(Paths.get(file.getOriginalFilename()))
          .normalize();
      if (file.getSize() > MAX_SIZE) {
        throw new FileSizeLimitExceededException("File is too large");
      }

      FileMetadataResponseDto fileEntity = fileService.multipartFileToFileResponseDto(login, file,
          destinationFile);
      FileMetadataResponseDto fileMetadataResponseDto = fileService.save(login,
          fileMapper.toFileRequestDtoFromFileResponseDto(fileEntity));
      UUID fileId = fileService.getFileIdByLoginAndTitle(login, fileMetadataResponseDto.getTitle());
      CompletableFuture future = CompletableFuture.runAsync(() -> {
        fileService.updateStatus(fileId, Status.IN_PROCESS);
        if (!antivirusService.scan(file)) {
          throw new RuntimeException();
        }
      }, executor).exceptionally(ex -> {
        fileService.deleteById(fileId);
        throw new FileHasVirusException("File: " + file.getOriginalFilename() + " - has a virus");
      }).thenRunAsync(() -> {
        fileService.updateStatus(fileId, Status.READY);
      }, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS, executor));
      future.join();
      fileEntity.setStatus(Status.READY);
      fileService.createFileOrder(
          fileMapper.toFileMetadataOrderFromFileResponseDto(fileEntity));
      return fileEntity;
    } catch (IOException e) {
      throw new StorageStoreFailedException("Failed to store file.");
    }
  }

  @Override
  public Resource loadAll(String login, List<UUID> fileNames) {
    Executor executor = Executors.newFixedThreadPool(10);
    CompletableFuture<List<FileMetadataResponseDto>> futures = CompletableFuture.supplyAsync(
        () -> fileService.findByLoginAndFilesId(login, fileNames), executor);
    List<FileMetadataResponseDto> desiredFiles = futures.join();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zout = new ZipOutputStream(baos)) {
      for (FileMetadataResponseDto file : desiredFiles) {
        ZipEntry entry1 = new ZipEntry(file.getTitle());
        zout.putNextEntry(entry1);
        // считываем содержимое файла в массив byte
        byte[] buffer = file.getFile();
        // добавляем содержимое к архиву
        zout.write(buffer);
        // закрываем текущую запись для новой записи
        zout.closeEntry();

      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    ByteArrayResource byteArrayResource = new ByteArrayResource(baos.toByteArray());
    return byteArrayResource;
  }

  @Override
  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public byte[] loadAsResource(String userLogin, String filename) {

    Path file = load(filename);
    File resultFile = fileService.findByPath(file);
    if (!ArrayUtils.isEmpty(resultFile.getFile())) {
      if (!resultFile.getUserLogin().equals(userLogin)) {
        throw new ResourceNotFoundException(
            "File: " + resultFile.getTitle() + " isn't own " + userLogin);
      }
      if (resultFile.getStatus().equals(Status.IN_PROCESS)) {
        throw new FileIsUnderScanException(
            "File: " + resultFile.getTitle() + " is under scan");
      }
      return resultFile.getFile();
    } else {
      throw new ResourceNotFoundException(
          "Could not read file: " + filename);
    }
  }
}