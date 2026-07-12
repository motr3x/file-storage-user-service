package ru.answer_42.file_storage_service.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletionException;
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
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.config.StorageProperties;
import ru.answer_42.file_storage_service.dto.FileMetadataRequestDto;
import ru.answer_42.file_storage_service.dto.FileMetadataResponseDto;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileSizeLimitExceededException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.service.AntivirusService;
import ru.answer_42.file_storage_service.service.FileService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StorageServiceImplTest {

  public final static Long MIN_SIZE = 1L;
  public final static Long MAX_SIZE = 15 * 1024 * 1024L;
  private static final String USER_LOGIN = "testUserLogin";
  private static final Long TOO_LARGE_SIZE = MAX_SIZE + 1L;
  private static final String FILE_NAME = "testFileName";
  private static final Boolean VIRUS_FLAG = Boolean.TRUE;
  private static final String TEST_TITLE = "testTitle";
  private static final UUID RANDOM_FILE_ID = UUID.randomUUID();

  @Mock
  private FileService fileService;
  @Mock
  private AntivirusService antivirusService;
  @Mock
  private FileMapper fileMapper;
  @Spy
  private StorageProperties properties = new StorageProperties();
  @Spy
  private static Path rootLocation = Path.of("storage");
  @InjectMocks
  private StorageServiceImpl storageService;
  @Mock
  private MultipartFile multipartFile;
  @Mock
  private FileMetadataResponseDto fileResponseDto;
  @Mock
  private FileMetadataRequestDto fileRequestDto;

  @BeforeEach
  void init() {
    Path destinationFile = this.rootLocation.resolve(
            USER_LOGIN).resolve(Paths.get(FILE_NAME))
        .normalize();
    when(multipartFile.isEmpty()).thenReturn(!VIRUS_FLAG);
    when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);
    when(multipartFile.getSize()).thenReturn(MAX_SIZE);
    when(antivirusService.scan(multipartFile)).thenReturn(VIRUS_FLAG);
    when(fileService.getFileIdByLoginAndTitle(USER_LOGIN, TEST_TITLE)).thenReturn(RANDOM_FILE_ID);
    when(fileService.multipartFileToFileResponseDto(USER_LOGIN, multipartFile,
        destinationFile)).thenReturn(fileResponseDto);
    when(fileMapper.toFileRequestDtoFromFileResponseDto(fileResponseDto)).thenReturn(
        fileRequestDto);
    when(fileService.save(USER_LOGIN, fileRequestDto)).thenReturn(fileResponseDto);
    when(fileResponseDto.getTitle()).thenReturn(TEST_TITLE);
  }

  @Test
  @DisplayName("В случае, когда файл с вирусом, выбрасывается корректное исключение")
  void thrown_correct_exception_when_file_has_virus() {
    //Given
    String expectedMessage = FILE_NAME + " - has a virus";
    when(antivirusService.scan(multipartFile)).thenReturn(!VIRUS_FLAG);

    //When
    CompletionException thrown = assertThrows(CompletionException.class,
        () -> {
          storageService.store(USER_LOGIN, multipartFile);
        });

    //Then
    String actualMessage = thrown.getMessage();
    String clearMessage = actualMessage.substring(actualMessage.lastIndexOf(":") + 1).trim();
    assertThat(clearMessage).isEqualTo(expectedMessage);
    verify(antivirusService, times(1))
        .scan(multipartFile);
  }

  @Test
  @DisplayName("В случае, когда файл слишком большой, выбрасывается корректное исключение")
  void thrown_correct_exception_when_file_is_too_large() {
    //Given
    String expectedMessage = "File is too large";
    when(multipartFile.getSize()).thenReturn(TOO_LARGE_SIZE);

    //When
    FileSizeLimitExceededException thrown = assertThrows(FileSizeLimitExceededException.class,
        () -> {
          storageService.store(USER_LOGIN, multipartFile);
        });

    //Then
    String actualMessage = thrown.getMessage();
    assertThat(actualMessage).isEqualTo(expectedMessage);
  }

  @Test
  @DisplayName("В случае, когда файл пустой, выбрасывается корректное исключение")
  void thrown_correct_exception_when_file_is_empty() {
    //Given
    String expectedMessage = "Failed to store empty file.";
    when(multipartFile.isEmpty()).thenReturn(VIRUS_FLAG);

    //When
    FileIsEmptyException thrown = assertThrows(FileIsEmptyException.class, () -> {
      storageService.store(USER_LOGIN, multipartFile);
    });

    //Then
    String actualMessage = thrown.getMessage();
    assertThat(actualMessage).isEqualTo(expectedMessage);
  }
}