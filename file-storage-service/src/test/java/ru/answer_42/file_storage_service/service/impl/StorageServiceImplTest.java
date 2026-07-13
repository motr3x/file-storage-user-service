package ru.answer_42.file_storage_service.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.config.StorageProperties;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.file.FileSizeLimitExceededException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.service.AntivirusService;
import ru.answer_42.file_storage_service.service.FileService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StorageServiceImplTest {
//
//  public final static Long MIN_SIZE = 1L;
//  public final static Long MAX_SIZE = 15 * 1024 * 1024L;
//  private static final String USER_LOGIN = "testUserLogin";
//  private static final String NON_ACCESS_USER_LOGIN = "nonAccessUserLogin";
//  private static final Long TOO_LARGE_SIZE = MAX_SIZE + 1L;
//  private static final String FILE_TITLE = "testFileName";
//  private static final Boolean VIRUS_FLAG = Boolean.TRUE;
//  private static final UUID RANDOM_FILE_ID = UUID.randomUUID();
//
//  @Mock
//  private FileService fileService;
//  @Mock
//  private AntivirusService antivirusService;
//  @Mock
//  private FileMapper fileMapper;
//  @Spy
//  private StorageProperties properties = new StorageProperties();
//  @Spy
//  private static Path rootLocation = Path.of("storage");
//  @InjectMocks
//  private StorageServiceImpl storageService;
//  @Mock
//  private MultipartFile multipartFile;
//  @Spy
//  private FileResponseDto fileResponseDto;
//  @Mock
//  private FileRequestDto fileRequestDto;
//  @Mock
//  private File file;
//  private byte[] bytes;
//
//  @BeforeEach
//  void init() {
//    bytes = new byte[]{};
//    Path destinationFile = this.rootLocation.resolve(
//            USER_LOGIN).resolve(Paths.get(FILE_TITLE))
//        .normalize();
//    when(multipartFile.isEmpty()).thenReturn(!VIRUS_FLAG);
//    when(multipartFile.getOriginalFilename()).thenReturn(FILE_TITLE);
//    when(multipartFile.getSize()).thenReturn(MAX_SIZE);
//    when(antivirusService.scan(multipartFile)).thenReturn(VIRUS_FLAG);
//    when(fileService.getFileIdByLoginAndTitle(USER_LOGIN, FILE_TITLE)).thenReturn(RANDOM_FILE_ID);
//    when(fileService.multipartFileToFileResponseDto(USER_LOGIN, multipartFile,
//        destinationFile)).thenReturn(fileResponseDto);
//    when(fileMapper.toFileRequestDtoFromFileResponseDto(fileResponseDto)).thenReturn(
//        fileRequestDto);
//    when(fileService.save(USER_LOGIN, fileRequestDto)).thenReturn(fileResponseDto);
//    when(fileResponseDto.getTitle()).thenReturn(FILE_TITLE);
//    when(file.getFile()).thenReturn(bytes);
//    when(file.getTitle()).thenReturn(FILE_TITLE);
//    when(file.getUserLogin()).thenReturn(USER_LOGIN);
//    when(fileService.findByPath(any())).thenReturn(file);
//  }
//
//  @Test
//  @DisplayName("Успешная выгрузка архива файлов")
//  void success_load_all() throws IOException {
//    //Given
//    List<UUID> filesId = List.of(UUID.randomUUID(), UUID.randomUUID());
//    List<FileResponseDto> desiredFiles = List.of(fileResponseDto);
//    String result = null;
//
//    //When
//    when(fileService.findByLoginAndFilesId(USER_LOGIN, filesId)).thenReturn(desiredFiles);
//    Resource resource = storageService.loadAll(USER_LOGIN, filesId);
//    FileUtils.writeByteArrayToFile(new java.io.File("test.zip"), resource.getContentAsByteArray());
//    ZipFile zipFile = new ZipFile("test.zip");
//
//    Enumeration enumeration = zipFile.entries();
//    while (enumeration.hasMoreElements()) {
//      ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
//      result = zipEntry.getName();
//    }
//
//    FileUtils.delete(new java.io.File("test.zip"));
//
//    //Then
//    assertThat(FILE_TITLE).isEqualTo(result);
//  }
//
//  @Test
//  @DisplayName("Успешное сохранение файла")
//  void success_store_file() {
//    //When
//    FileResponseDto fileResponseDto = storageService.store(USER_LOGIN,
//        multipartFile);
//
//    //Then
//    assertThat(fileResponseDto).isEqualTo(this.fileResponseDto);
//  }
//
//  @Test
//  @DisplayName("Корректный вывод пути к корня хранилища")
//  void correct_load_path_to_root_location() {
//    //Given
//    Path expectedPath = rootLocation.resolve(FILE_TITLE);
//    //When
//    Path actualPath = storageService.load(FILE_TITLE);
//    //Then
//    assertThat(actualPath).isEqualTo(expectedPath);
//  }
//
//  @Test
//  @DisplayName("Успешная загрузка файла с ресурса")
//  void success_load_as_resource() {
//    //Given
//    bytes = new byte[]{1};
//    byte[] expectedResult = bytes;
//
//    //When
//    when(file.getFile()).thenReturn(bytes);
//    when(file.getStatus()).thenReturn(Status.READY);
//    byte[] actualResult = storageService.loadAsResource(USER_LOGIN, FILE_TITLE);
//
//    //Then
//    assertThat(actualResult).isEqualTo(expectedResult);
//  }
//
//  @Test
//  @DisplayName("При получении, если файл находится под сканированием, выбрасывается корректное исключение")
//  void thrown_correct_exception_when_file_under_scan() {
//    //Given
//    String expectedMessage = "File: " + FILE_TITLE + " is under scan";
//    bytes = new byte[]{1};
//
//    //When
//    when(file.getFile()).thenReturn(bytes);
//    when(file.getStatus()).thenReturn(Status.IN_PROCESS);
//    FileIsUnderScanException thrown = assertThrows(FileIsUnderScanException.class, () -> {
//      storageService.loadAsResource(USER_LOGIN, FILE_TITLE);
//    });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("При получении, если у файл владелец не заданный юзер, выбрасывается корректное исключение")
//  void thrown_correct_exception_when_user_not_own_file() {
//    //Given
//    String expectedMessage = "File: " + FILE_TITLE + " isn't own " + NON_ACCESS_USER_LOGIN;
//    bytes = new byte[]{1};
//
//    //When
//    when(file.getFile()).thenReturn(bytes);
//    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
//      storageService.loadAsResource(NON_ACCESS_USER_LOGIN, FILE_TITLE);
//    });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("При получении, если файл пустой, выбрасывается корректное исключение")
//  void thrown_correct_exception_when_file_field_could_not_read() {
//    //Given
//    String expectedMessage = "Could not read file: " + FILE_TITLE;
//
//    //When
//    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
//      storageService.loadAsResource(USER_LOGIN, FILE_TITLE);
//    });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("При сохранение, если файл с вирусом, выбрасывается корректное исключение")
//  void thrown_correct_exception_when_file_has_virus() {
//    //Given
//    String expectedMessage = FILE_TITLE + " - has a virus";
//    when(antivirusService.scan(multipartFile)).thenReturn(!VIRUS_FLAG);
//
//    //When
//    CompletionException thrown = assertThrows(CompletionException.class,
//        () -> {
//          storageService.store(USER_LOGIN, multipartFile);
//        });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    String clearMessage = actualMessage.substring(actualMessage.lastIndexOf(":") + 1).trim();
//    assertThat(clearMessage).isEqualTo(expectedMessage);
//    verify(antivirusService, times(1))
//        .scan(multipartFile);
//  }
//
//  @Test
//  @DisplayName("При сохранение, если слишком большой, выбрасывается корректное исключение")
//  void thrown_correct_exception_when_file_is_too_large() {
//    //Given
//    String expectedMessage = "File is too large";
//    when(multipartFile.getSize()).thenReturn(TOO_LARGE_SIZE);
//
//    //When
//    FileSizeLimitExceededException thrown = assertThrows(FileSizeLimitExceededException.class,
//        () -> {
//          storageService.store(USER_LOGIN, multipartFile);
//        });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("При сохранение, если файл пустой, выбрасывается корректное исключение")
//  void thrown_correct_exception_when_file_is_empty() {
//    //Given
//    String expectedMessage = "Failed to store empty file.";
//    when(multipartFile.isEmpty()).thenReturn(VIRUS_FLAG);
//
//    //When
//    FileIsEmptyException thrown = assertThrows(FileIsEmptyException.class, () -> {
//      storageService.store(USER_LOGIN, multipartFile);
//    });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
}