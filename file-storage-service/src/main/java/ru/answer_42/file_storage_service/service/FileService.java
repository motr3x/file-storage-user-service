package ru.answer_42.file_storage_service.service;


import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

public interface FileService {

  UUID save(FileRequestDto file);

  FileResponseDto update(UUID id, FileRequestDto fileDto);

  void updateStatus(UUID id, Status status);

  FileResponseDto update(FileRequestDto fileDto);

  FileResponseDto deleteById(UUID id);

  List<String> findAllTitles();

  List<FileResponseDto> findAll(String name, LocalDate start, LocalDate end, Type type);

  FileResponseDto findById(UUID id);

  FileResponseDto findByTitle(String name);

  FileRequestDto multipartFileToFileRequestDto(MultipartFile file, Path path);
}
