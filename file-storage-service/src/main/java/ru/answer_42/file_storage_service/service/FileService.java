package ru.answer_42.file_storage_service.service;


import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

public interface FileService {

  UUID save(String login, FileMetadataRequestDto file);

  FileMetadataResponseDto update(UUID id, FileMetadataRequestDto fileDto);

  void updateStatus(UUID id, Status status);

  FileMetadataResponseDto deleteById(UUID id);

  List<String> findAllTitles();

  List<FileMetadataResponseDto> findAll(String login, String name, LocalDate start, LocalDate end,
      Type type);

  FileMetadataResponseDto findById(UUID id);

  FileMetadataResponseDto findByTitle(String name);

  FileMetadataResponseDto multipartFileToFileResponseDto(String login, MultipartFile file,
      Path path);

  File findByPath(Path file);
}
