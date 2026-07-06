package ru.answer_42.file_storage_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.service.FileService;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;


  @PostMapping("/{login}")
  public ResponseEntity<FileResponseDto> create(@PathVariable String login,
      @RequestBody FileRequestDto fileRequestDto) {
    UUID fileId = fileService.save(login, fileRequestDto);
    return new ResponseEntity<>(fileService.findById(fileId), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileResponseDto> readById(@PathVariable UUID id) {
    FileResponseDto fileResponseDto = fileService.findById(id);
    return ResponseEntity.ok(fileResponseDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<FileResponseDto> update(@PathVariable UUID id,
      @RequestBody FileRequestDto fileRequestDto) {
    FileResponseDto fileResponseDto = fileService.update(id, fileRequestDto);
    return ResponseEntity.ok(fileResponseDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<FileResponseDto> delete(@PathVariable UUID id) {
    FileResponseDto fileResponseDto = fileService.deleteById(id);
    return ResponseEntity.ok(fileResponseDto);
  }

  @GetMapping("/titles")
  public ResponseEntity<List<String>> readTitles() {
    final List<String> titles = fileService.findAllTitles();
    return titles != null ? new ResponseEntity<>(titles, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/user/{login}")
  public ResponseEntity<List<FileResponseDto>> readAll(@PathVariable String login,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) LocalDate start,
      @RequestParam(required = false) LocalDate end,
      @RequestParam(required = false) Type type) {
    final List<FileResponseDto> files = fileService.findAll(login, name, start, end, type);
    return files != null ? new ResponseEntity<>(files, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

}
