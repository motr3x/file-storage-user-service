package ru.answer_42.file_storage_service.service;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;

public interface StorageService {

  void init();

  FileMetadataResponseDto store(String userLogin, MultipartFile file);

  List<Path> loadAll();

  Resource loadAll(String userLogin, List<UUID> filenames);

  Path load(String filename);

  byte[] loadAsResource(String userLogin, String filename);

  void deleteAll();
}
