package ru.answer_42.file_storage_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import ru.answer_42.file_storage_service.exception.AccessDeniedException;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.FileHasVirusException;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileTypeException;
import ru.answer_42.file_storage_service.exception.storage.StorageStoreFailedException;
import ru.answer_42.file_storage_service.service.StorageService;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {
  private final StorageService storageService;

  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Файл успешно сохранён в хранилище",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileResponseDto.class))}),
      @ApiResponse(responseCode = "400", description = "Указанный тип файла не поддерживается",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UnsupportedFileTypeException.class))}),
      @ApiResponse(responseCode = "400", description = "Файл пустой",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileIsEmptyException.class))}),
      @ApiResponse(responseCode = "409", description = "Файл имеет вирус",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileHasVirusException.class))}),
      @ApiResponse(responseCode = "413", description = "Файл слишком большой",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileSizeLimitExceededException.class))}),
      @ApiResponse(responseCode = "500", description = "Файл не сохранён в хранилище",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = StorageStoreFailedException.class))})
  })
  @Operation(
      summary = "Сохранить файл в хранилище",
      description = "В ответе возвращается метаданные сохраненного файла")
  @PostMapping("/{userId}/upload")
  public ResponseEntity<FileResponseDto> handleFileUpload(
      @Parameter(
          description = "Логин пользователя, который сохраняет файл в хранилище",
          required = true) @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "Файл, который сохраняется в хранилище",
          required = true) @RequestParam("file") MultipartFile file) {
    FileResponseDto fileResponseDto = storageService.store(userId, file);
    return new ResponseEntity<>(fileResponseDto, HttpStatus.CREATED);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Файл успешно получен",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Resource.class))}),
      @ApiResponse(responseCode = "404", description = "В хранилище нет подходящего файла",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResourceNotFoundException.class))}),
      @ApiResponse(responseCode = "409", description = "Файл находится под сканированием",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileIsUnderScanException.class))})
  })
  @Operation(summary = "Получить файл по имени", description = "В ответ возвращается желаемый файл")
  @GetMapping("/{userId}/{filename:.+}")
  public ResponseEntity<Resource> serveFile(
      @Parameter(
        description = "Логин пользователя, который запрашивает файл из хранилища",
        required = true) @PathVariable @NotNull UUID userId,
      @Parameter(
        description = "Имя файла, который запрашивают",
        required = true) @PathVariable @NotBlank String filename) {
    byte[] file = storageService.loadAsResource(userId, filename);

    if (file == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    ByteArrayResource resource = new ByteArrayResource(file);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + filename + "\"").body(resource);
  }

  //TODO насчёт доступа стоит подумать, стоит ли говорить об этом пользователю
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Архив успешно получен",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Resource.class))}),
      @ApiResponse(responseCode = "403", description = "У пользователя нед доступа к файлам",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = AccessDeniedException.class))}),
  })
  @Operation(summary = "Получить архив с файлами по их id", description = "В ответе возвращается архив с желаемыми файлами")
  @PostMapping("/{userId}")
  public ResponseEntity<Resource> serveFiles(
      @Parameter(
          description = "Логин пользователя, который сохраняет архив файлов в хранилище",
          required = true) @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "Список с id файлов, которые пользователь желает сохранить",
          required = true) @RequestBody @NotEmpty List<UUID> filesId) {

    Resource resource = storageService.loadAll(userId, filesId);

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
