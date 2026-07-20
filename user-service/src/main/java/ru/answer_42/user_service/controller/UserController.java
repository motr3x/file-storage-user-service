package ru.answer_42.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
import org.springframework.web.bind.annotation.RestController;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.exception.TimeLimitException;
import ru.answer_42.user_service.model.FileOrder;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.service.FileDownloadService;
import ru.answer_42.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController{
  private final UserService userService;
  private final FileDownloadService fileDownloadService;
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Метаданные файла для скачивания получены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = FileDownloadDto.class))})})
  @Operation(
      summary = "Получить метаданные файла для скачивания",
      description = "В ответе возвращается метаданные файла для скачивания")
  @GetMapping("/downloadUrl/{userId}/{fileId}")
  public ResponseEntity<FileDownloadDto> getFileDownloadDto(
      @Parameter(
          description = "Id пользователя, метаданные файла которого получаются",
          required = true)
      @PathVariable @NotNull UUID userId,
      @Parameter(
          description = "Id файла, метаданные которого получаются",
          required = true)
      @PathVariable @NotNull UUID fileId){
    CompletableFuture<FileDownloadDto> future = fileDownloadService.getFileDownloadDto(userId, fileId);
    try {
      return ResponseEntity.ok(future.get(10, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new TimeLimitException("The waiting time is over");
    }
  }

  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
  })
  @Operation(summary = "Создать пользователя", description = "В ответе возвращается созданный пользователь")
  @PostMapping
  public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestDto userRequestDto)
      throws JsonProcessingException {
    UserResponseDto userResponseDto = userService.save(userRequestDto);
    return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
  }

//  @GetMapping("/{id}")
//  public ResponseEntity<UserResponseDto> readById(@PathVariable UUID id){
//    UserResponseDto userResponseDto = userService.findById(id);
//    return ResponseEntity.ok(userResponseDto);
//  }
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
      @ApiResponse(responseCode = "404", description = "Пользователей нет")
  })
  @Operation(summary = "Получить всех пользователей", description = "В ответе возвращается список пользователей")
  @Tag(name = "get", description = "GET-методы user API")
  @GetMapping()
  public ResponseEntity<List<UserResponseDto>> readAll(){
    List<UserResponseDto> userResponseDtos = userService.findAll();
    return  userResponseDtos != null ? new ResponseEntity<>(userResponseDtos, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список файлов пользователя успешно получен"),
      @ApiResponse(responseCode = "404", description = "Пользователя не существует"),
      @ApiResponse(responseCode = "404", description = "Файлов нет")
  })
  @Operation(summary = "Получить список файлов пользователя", description = "В ответе возвращается список файлов пользователя")
  @Tag(name = "get", description = "GET-методы user API")
  @GetMapping("/{userId}/files")
  public ResponseEntity<List<FileOrder>> readAllFiles(@PathVariable UUID userId){
    List<FileOrder> fileOrders = userService.findAllFilesById(userId);
    return fileOrders != null ? new ResponseEntity<>(fileOrders, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

//  @ApiResponses({
//      @ApiResponse(responseCode = "200", description = "Список ссылок на файлы пользователя успешно получен"),
//      @ApiResponse(responseCode = "404", description = "Пользователя не существует"),
//      @ApiResponse(responseCode = "404", description = "Файлов нет")
//  })
//  @Operation(summary = "Получить список ссылок файлов пользователя", description = "В ответе возвращается список ссылок файлов пользователя")
//  @Tag(name = "get", description = "GET-методы user API")
//  @GetMapping("/{login}/links")
//  public ResponseEntity<List<FileDownloadDto>> readAllLinks(@PathVariable String login){
//    List<FileDownloadDto> fileDownloadDtos = userService.findAllLinksByLogin(login);
//    return fileDownloadDtos != null ? new ResponseEntity<>(fileDownloadDtos, HttpStatus.OK)
//        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пользователей успешно обновлен"),
      @ApiResponse(responseCode = "404", description = "Пользователей нет")
  })
  @Operation(summary = "Обновить данные пользователя", description = "В ответе возвращается обновленный пользователь")
  @PutMapping("/{userId}")
  public ResponseEntity<UserResponseDto> update(@PathVariable UUID userId, @RequestBody UserRequestDto fileRequestDto){
    UserResponseDto userResponseDto = userService.update(userId, fileRequestDto);
    return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
  }

  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
      @ApiResponse(responseCode = "404", description = "Пользователей нет")
  })
  @Operation(summary = "Пользователь удален", description = "В ответе возвращается удаленный пользователь")
  @DeleteMapping("/{userId}")
  public ResponseEntity<UserResponseDto> delete(@PathVariable UUID userId){
    UserResponseDto userResponseDto = userService.deleteById(userId);
    return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
  }
}