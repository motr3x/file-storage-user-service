package ru.answer_42.file_storage_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.service.FileService;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
public class FileMetadataController {

  //404 - если объект с id не найден
  //400 - неправильные данные

  private final FileService fileService;

  //TODO
  // После валидации дтошки дописать документацию по исключениям связанных с валидацией
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Метаданные файла успешно сохранён",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileResponseDto.class))})})
  @Operation(
      summary = "Создать объект с метаданными переданного файла",
      description = "В ответе возвращается созданные метаданными файла")
  @PostMapping("/{userId}")
  public ResponseEntity<FileResponseDto> create(
      @Parameter(
          description = "Id пользователя, метаданными о файле по которому сохраняются",
          required = true)
      @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "Метаданными о файле, который сохраняют",
          required = true)
      @RequestBody @NotNull @Valid FileRequestDto fileMetadataRequestDto) {
    FileResponseDto fileResponseDto = fileService.save(userId, fileMetadataRequestDto);
    return new ResponseEntity<>(fileResponseDto, HttpStatus.CREATED);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Метаданные файла успешно получены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileResponseDto.class))}),
      @ApiResponse(responseCode = "404", description = "Метаданные файла не найдены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResourceNotFoundException.class))})})
  @Operation(
      summary = "Получить метаданные файла по id",
      description = "В ответе возвращается желаемые метаданные файла")
  @Tag(name = "get", description = "GET-методы file API")
  @GetMapping("/{userId}/{fileId}")
  public ResponseEntity<FileResponseDto> readById(
      @Parameter(
          description = "Id пользователя, метаданными о файле по которому сохраняются",
          required = true)
      @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "ID метаданных файла, по которым идет запрос",
          required = true)
      @PathVariable @NotNull UUID fileId) {
    FileResponseDto fileResponseDto = fileService.findByUserIdAndId(userId, fileId);
    return ResponseEntity.ok(fileResponseDto);
  }

  //TODO
  // После валидации дтошки дописать документацию по исключениям связанных с валидацией
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Метаданные файла успешно обновлёны",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileResponseDto.class))}),
      @ApiResponse(responseCode = "404", description = "Метаданные файла не найдены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResourceNotFoundException.class))})})
  @Operation(
      summary = "Обновить метаданные файла с определенным id",
      description = "В ответе возвращается обновленные метаданные файла")
  @PutMapping("/{userId}/{fileId}")
  public ResponseEntity<FileResponseDto> update(
      @Parameter(
          description = "Id пользователя, метаданными о файле по которому обновляются",
          required = true)
      @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "ID метаданных файла, данные которого обновляются",
          required = true)
      @PathVariable @NotNull UUID fileId,
      @Parameter(
          description = "Файл с полями, которые стоит обновить у заданного файла",
          required = true)
      @RequestBody @Valid FileRequestDto fileMetadataRequestDto) {
    fileService.findByUserIdAndId(userId, fileId);
    FileResponseDto fileResponseDto = fileService.update(fileId,
        fileMetadataRequestDto);
    return ResponseEntity.ok(fileResponseDto);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Метаданные файла успешно удалёны",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileResponseDto.class))}),
      @ApiResponse(responseCode = "404", description = "Метаданные файла не найдены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResourceNotFoundException.class))})
  })
  @Operation(
      summary = "Удалить метаданные о файле по id",
      description = "В ответе возвращается метаданные о файле, которые были удалены")
  @DeleteMapping("/{userId}/{fileId}")
  public ResponseEntity<FileResponseDto> delete(
      @Parameter(
          description = "Id пользователя, метаданными о файле по которому удаляются",
          required = true)
      @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "ID метаданных файла, который удаляется",
          required = true)
      @PathVariable @NotNull UUID fileId) {
    fileService.findByUserIdAndId(userId, fileId);
    FileResponseDto fileResponseDto = fileService.deleteById(fileId);
    return ResponseEntity.ok(fileResponseDto);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список имен файлов успешно получен",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(
                  type = "array",
                  implementation = String.class))}),
      @ApiResponse(responseCode = "404", description = "В хранилище нет файлов",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResourceNotFoundException.class))})
  })
  @Operation(summary = "Получить названия всех файлов", description = "В ответе возвращается список названий файлов")
  @Tag(name = "get", description = "GET-методы file API")
  @GetMapping("/{userId}/titles")
  public ResponseEntity<List<String>> getTitles(
      @Parameter(
          description = "Id пользователя, метаданными о файле по которому запрашиваются",
          required = true)
      @PathVariable @NotNull UUID userId
  ) {
    final List<String> titles = fileService.findAllTitles(userId);
    return titles != null ? new ResponseEntity<>(titles, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список методанных о файлах успешно получен",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(
                  type = "array",
                  implementation = FileResponseDto.class))}),
      @ApiResponse(responseCode = "404", description = "В хранилище нет подходящих файлов",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResourceNotFoundException.class))})
  })
  @Operation(summary = "Получить метаданные о файлах с возможностью фильтрации", description = "В ответе возвращается список метаданными о файлах, прошедших фильтр ")
  @Tag(name = "get", description = "GET-методы file API")
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<FileResponseDto>> getUserFilesByFilter(
      @Parameter(
          description = "Id пользователя, данные о файле по которому запрашиваются",
          required = true)
      @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "Фильтр по имени(возможно частичное вхождение)",
          required = false)
      @RequestParam(required = false)  String name,
      @Parameter(
          description = "Фильтр по дате изменения(диапазон от)",
          required = false)
      @RequestParam(required = false) LocalDate start,
      @Parameter(
          description = "Фильтр по дате изменения(диапазон до)",
          required = false)
      @RequestParam(required = false) LocalDate end,
      @Parameter(
          description = "Фильтр по типу (без указания - все, при указании - список переданных типов)",
          required = false)
      @RequestParam(required = false) Type type) {
    final List<FileResponseDto> files = fileService.findAllByFilter(userId, name, start, end, type);
    return files != null ? new ResponseEntity<>(files, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
