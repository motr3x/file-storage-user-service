package ru.answer_42.file_storage_service.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.config.StorageProperties;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.exception.FileHasVirusException;
import ru.answer_42.file_storage_service.exception.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.StorageException;
import ru.answer_42.file_storage_service.exception.StorageFileNotFoundException;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.service.AntivirusService;
import ru.answer_42.file_storage_service.service.FileService;
import ru.answer_42.file_storage_service.service.StorageService;

@Service
public class FileSystemStorageService implements StorageService {

  private final static long MAX_SIZE = 15 * 1024 * 1024L;
  private final Path rootLocation;
  private final FileService fileService;
  private final AntivirusService antivirusService;

  @Autowired
  public FileSystemStorageService(StorageProperties properties, FileService fileService,
      AntivirusService antivirusService) {
    this.fileService = fileService;
    this.antivirusService = antivirusService;
    if (properties.getLocation().trim().isEmpty()) {
      throw new StorageException("File upload location can not be Empty.");
    }
    this.rootLocation = Paths.get(properties.getLocation());
  }


  @Override
  public FileResponseDto store(MultipartFile file) {
    Executor executor = Executors.newFixedThreadPool(10);
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file.");
      }
      Path destinationFile = this.rootLocation.resolve(
              Paths.get(file.getOriginalFilename()))
          .normalize().toAbsolutePath();
      if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
        throw new StorageException(
            "Cannot store file outside current directory.");
      }
      if (file.getSize() > MAX_SIZE) {
        throw new FileSizeLimitExceededException("File is too large", file.getSize(), MAX_SIZE);
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile,
            StandardCopyOption.REPLACE_EXISTING);
      }
      FileRequestDto fileEntity = fileService.multipartFileToFileRequestDto(file, destinationFile);
      UUID fileId = fileService.save(fileEntity);
      CompletableFuture.runAsync(() -> {
        fileService.updateStatus(fileId, Status.IN_PROCESS);
        if (!antivirusService.scan(file)) {
          throw new FileHasVirusException("File: " + file.getOriginalFilename() + " - has a virus");
        }
      }, executor).thenRunAsync(() -> {
        fileService.updateStatus(fileId, Status.READY);
      }, CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS, executor));
      return fileService.findById(fileId);
    } catch (IOException e) {
      throw new StorageException("Failed to store file.", e);
    }
  }


  @Override
  public List<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
          .filter(path -> !path.equals(this.rootLocation))
          .map(this.rootLocation::relativize).map(p -> p.getFileName()).toList();
    } catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }

  }

  @Override
  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String filename) {
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        FileResponseDto fileResponseDto = fileService.findByTitle(filename);
        if (fileResponseDto.getStatus().equals(Status.IN_PROCESS)) {
          throw new FileIsUnderScanException(
              "File: " + fileResponseDto.getTitle() + " is under scan");
        }
        return resource;
      } else {
        throw new StorageFileNotFoundException(
            "Could not read file: " + filename);

      }
    } catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
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
      throw new StorageException("Could not initialize storage", e);
    }
  }
}