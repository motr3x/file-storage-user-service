package ru.answer_42.file_storage_service.service;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.File;

public interface StorageService {


  FileResponseDto store(UUID userId, MultipartFile file);

  Resource loadAll(UUID userId, List<UUID> filenames);

  Path load(String filename);

  byte[] loadAsResource(UUID userId, String filename);

}
