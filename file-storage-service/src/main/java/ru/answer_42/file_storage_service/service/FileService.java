package ru.answer_42.file_storage_service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileOrder;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.model.UserOrder;

public interface FileService {

  boolean accessCheck(UUID userId, FileResponseDto responseDto);

  FileResponseDto save(UUID userId, FileRequestDto file);

  FileResponseDto update(UUID id, FileRequestDto fileDto);

  void updateStatus(UUID id, Status status);

  FileResponseDto deleteById(UUID id);

  List<String> findAllTitles(UUID userId);

  List<FileResponseDto> findAll(UUID userId, String name, LocalDate start, LocalDate end,
      Type type);

  String createFileOrder(FileOrder fileOrder)
      throws JsonProcessingException;

  FileResponseDto findByTitle(String name);

  FileResponseDto multipartFileToFileResponseDto(UUID userId, MultipartFile file,
      Path path);

  FileResponseDto findByUserIdAndId(UUID userId, UUID id);

  File findByPath(Path file);

  UUID getFileIdByUserIdAndTitle(UUID userId, String title);

  List<FileResponseDto> findByUserIdAndFilesId(UUID userId, List<UUID> fileNames);

  Type determinateType(MultipartFile file);

  UserOrder addFileMetadata(UserOrder userOrder);
}
