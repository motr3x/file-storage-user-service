package ru.answer_42.file_storage_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.service.StorageService;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

  private final StorageService storageService;

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Файл успешно сохранён в хранилище"),
      @ApiResponse(responseCode = "400", description = "Указанный тип файла не поддерживается"),
      @ApiResponse(responseCode = "400", description = "Файл пустой"),
      @ApiResponse(responseCode = "409", description = "Файл имеет вирус"),
      @ApiResponse(responseCode = "413", description = "Файл слишком большой"),
      @ApiResponse(responseCode = "500", description = "Файл не сохранён в хранилище")
  })
  @Operation(summary = "Сохранить файл в хранилище", description = "В ответе возвращается метаданные сохраненного файла")
  @PostMapping("/{login}/upload")
  public ResponseEntity<FileResponseDto> handleFileUpload(@Parameter(
          description = "Логин пользователя, который сохраняет файл в хранилище",
          required = true) @PathVariable String login,
      @Parameter(
          description = "Файл, который сохраняется в хранилище",
          required = true) @RequestParam("file") MultipartFile file) {
    FileResponseDto fileResponseDto = storageService.store(login, file);
    return ResponseEntity.ok(fileResponseDto);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Файл успешно получен"),
      @ApiResponse(responseCode = "404", description = "В хранилище нет подходящего файла"),
      @ApiResponse(responseCode = "409", description = "Файл находится под сканированием")
  })
  @Operation(summary = "Получить файл по имени", description = "В ответ возвращается желаемый файл")
  @GetMapping("/{login}/{filename:.+}")
  public ResponseEntity<Resource> serveFile(@Parameter(
      description = "Логин пользователя, который запрашивает файл из хранилища",
      required = true) @PathVariable String login, @Parameter(
      description = "Имя файла, который запрашивают",
      required = true) @PathVariable String filename) {
    byte[] file = storageService.loadAsResource(login, filename);

    if (file == null) {
      return ResponseEntity.notFound().build();
    }

    ByteArrayResource resource = new ByteArrayResource(file);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + filename + "\"").body(resource);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Архив успешно получен"),
      @ApiResponse(responseCode = "403", description = "У пользователя нед доступа к файлам"),
  })
  @Operation(summary = "Получить архив с файлами по их id", description = "В ответе возвращается архив с желаемыми файлами")
  @PostMapping("/{login}")
  public ResponseEntity<Resource> serveFiles(@Parameter(
          description = "Логин пользователя, который сохраняет архив файлов в хранилище",
          required = true) @PathVariable String login,
      @Parameter(
          description = "Список с id файлов, которые пользователь желает сохранить",
          required = true) @RequestBody List<UUID> filesId) {

    Resource resource = storageService.loadAll(login, filesId);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + "file_archive.zip" + "\"").body(resource);
  }

//  @GetMapping()
//  public ResponseEntity<List<Path>> listUploadedFiles() {
//
//    List<Path> list = storageService.loadAll();
//
//    return ResponseEntity.ok(list);
//  }

}
