package ru.answer_42.file_storage_service.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.config.StorageProperties;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.exception.AccessDeniedException;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.FileHasVirusException;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.storage.StorageInitFailedException;
import ru.answer_42.file_storage_service.exception.storage.StorageLocationEmptyException;
import ru.answer_42.file_storage_service.exception.storage.StorageReadFailedException;
import ru.answer_42.file_storage_service.exception.storage.StorageStoreFailedException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.service.AntivirusService;
import ru.answer_42.file_storage_service.service.FileMetadataService;
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

  private final static long MAX_SIZE = 15 * 1024 * 1024L;
  private final Path rootLocation;
  private final FileService fileService;
  private final AntivirusService antivirusService;
  private final FileMetadataService fileMetadataService;
  private final FileMapper fileMapper;

  @Autowired
  public StorageServiceImpl(StorageProperties properties, FileService fileService,
      AntivirusService antivirusService, FileMetadataService fileMetadataService,
      FileMapper fileMapper) {
    if (properties.getLocation().trim().isEmpty()) {
      throw new StorageLocationEmptyException("File upload location can not be Empty.");
    }
    this.rootLocation = Paths.get(properties.getLocation());
    this.fileService = fileService;
    this.antivirusService = antivirusService;
    this.fileMetadataService = fileMetadataService;
    this.fileMapper = fileMapper;
  }


  @Override
  public FileMetadataResponseDto store(String login, MultipartFile file) {
    Executor executor = Executors.newFixedThreadPool(10);
    try {
      if (file.isEmpty()) {
        throw new FileIsEmptyException("Failed to store empty file.");
      }
//      Path destinationFile = this.rootLocation.resolve(
//              Paths.get(file.getOriginalFilename()))
//          .normalize().toAbsolutePath();
//      if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
//        throw new StorageException(
//            "Cannot store file outside current directory.");
//      }
      Path destinationFile = this.rootLocation.resolve(
              Paths.get(file.getOriginalFilename()))
          .normalize();
      if (file.getSize() > MAX_SIZE) {
        throw new FileSizeLimitExceededException("File is too large", file.getSize(), MAX_SIZE);
      }
//      try (InputStream inputStream = file.getInputStream()) {
//        Files.copy(inputStream, destinationFile,
//            StandardCopyOption.REPLACE_EXISTING);
//      }

      FileMetadataResponseDto fileEntity = fileService.multipartFileToFileResponseDto(login, file,
          destinationFile);
      FileMetadataResponseDto fileMetadataResponseDto = fileService.save(login, fileMapper.toFileRequestDtoFromFileResponseDto(fileEntity));
      UUID fileId = fileService.getFileIdByLoginAndTitle(login, fileMetadataResponseDto.getTitle());
      CompletableFuture future = CompletableFuture.runAsync(() -> {
        fileService.updateStatus(fileId, Status.IN_PROCESS);
        if (!antivirusService.scan(file)) {
          // advi
          throw new RuntimeException();
        }
      }, executor).exceptionally(ex -> {
        fileService.deleteById(fileId);
        throw new FileHasVirusException("File: " + file.getOriginalFilename() + " - has a virus");
      }).thenRunAsync(() -> {
        fileService.updateStatus(fileId, Status.READY);
      }, CompletableFuture.delayedExecutor(15, TimeUnit.SECONDS, executor));
      future.join();
      fileEntity.setStatus(Status.READY);
      fileMetadataService.createFileOrder(
          fileMapper.toFileRequestDtoFromFileResponseDto(fileEntity));
      return fileEntity;
    } catch (IOException e) {
      throw new StorageStoreFailedException("Failed to store file.");
    }
  }

  public boolean accessCheck(String login, FileMetadataResponseDto responseDto) {
    if (!(responseDto.getUserLogin().equals(login))) {
      throw new AccessDeniedException(
          "User with login: " + login + " hasn't access to file: " + responseDto.getTitle());
    }
    return true;
  }

  @Override
  public Resource loadAll(String login, List<UUID> fileNames) {
//    fileNames = fileNames.stream().filter(id -> accessCheck(login, fileService.findById(id))).toList();
    Executor executor = Executors.newFixedThreadPool(10);
    List<CompletableFuture<FileMetadataResponseDto>> futures = fileNames.stream()
        .map(id -> CompletableFuture.supplyAsync(() -> fileService.findByUserLoginAndId(login, id), executor))
        .toList();
    List<FileMetadataResponseDto> desiredFiles = futures.stream().map(CompletableFuture::join)
        .toList();
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
  public List<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
          .filter(path -> !path.equals(this.rootLocation))
          .map(this.rootLocation::relativize).map(Path::getFileName).toList();
    } catch (IOException e) {
      throw new StorageReadFailedException("Failed to read stored files");
    }

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

//    } catch (MalformedURLException e) {
//      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
//    }
//      Resource resource = new UrlResource(file.toUri());
//      if (resource.exists() || resource.isReadable()) {
//        FileResponseDto fileResponseDto = fileService.findByTitle(filename);
//        if (fileResponseDto.getStatus().equals(Status.IN_PROCESS)) {
//          throw new FileIsUnderScanException(
//              "File: " + fileResponseDto.getTitle() + " is under scan");
//        }
//        return resource;
//      } else {
//        throw new StorageFileNotFoundException(
//            "Could not read file: " + filename);
//
//      }
//    } catch (MalformedURLException e) {
//      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
//    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new StorageInitFailedException("Could not initialize storage");
    }
  }
}