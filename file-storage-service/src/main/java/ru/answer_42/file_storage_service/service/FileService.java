package ru.answer_42.file_storage_service.service;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.Type;

public interface FileService {

  FileResponseDto save(FileRequestDto file);

  FileResponseDto update(UUID id, FileRequestDto fileDto);

  FileResponseDto update(FileRequestDto fileDto);

  FileResponseDto deleteById(UUID id);

  List<String> findAllTitles();

  List<FileResponseDto> findAll(String name, LocalDate start, LocalDate end, Type type);

  FileResponseDto findById(UUID id);

  FileResponseDto findByTitle(String name);

}
