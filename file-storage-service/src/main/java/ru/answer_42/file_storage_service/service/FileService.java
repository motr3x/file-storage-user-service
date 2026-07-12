package ru.answer_42.file_storage_service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.answer_42.file_storage_service.repository.FileRepository;

public interface FileService {

  boolean accessCheck(String login, FileMetadataResponseDto responseDto);

  FileMetadataResponseDto save(String login, FileMetadataRequestDto file);

  FileMetadataResponseDto update(UUID id, FileMetadataRequestDto fileDto);

  void updateStatus(UUID id, Status status);

  FileMetadataResponseDto deleteById(UUID id);

  List<String> findAllTitles(String login);

  List<FileMetadataResponseDto> findAll(String login, String name, LocalDate start, LocalDate end,
      Type type);

  String createFileOrder(FileMetadataRequestDto fileMetadataRequestDtoDto)
      throws JsonProcessingException;

  FileMetadataResponseDto findByTitle(String name);

  FileMetadataResponseDto multipartFileToFileResponseDto(String login, MultipartFile file,
      Path path);

  FileMetadataResponseDto findByUserLoginAndId(String login, UUID id);

  File findByPath(Path file);

  UUID getFileIdByLoginAndTitle(String login, String title);

  List<FileMetadataResponseDto> findByLoginAndFilesId(String login, List<UUID> fileNames);
}
