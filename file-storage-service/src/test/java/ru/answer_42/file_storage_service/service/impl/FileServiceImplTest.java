package ru.answer_42.file_storage_service.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.exception.AccessDeniedException;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.mapper.FileMapper;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;
import ru.answer_42.file_storage_service.model.UserOrder;
import ru.answer_42.file_storage_service.repository.FileRepository;
import ru.answer_42.file_storage_service.repository.UserOrderRepository;
import ru.answer_42.file_storage_service.service.Producer;
import ru.answer_42.file_storage_service.service.UserOrderService;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTest {

//  private static final String USER_LOGIN = "userLogin";
//  private static final String NON_ACCESS_USER_LOGIN = "nonAccessUserLogin";
//  private static final String FILE_TITLE = "testTitle";
//  private static final Long DEFAULT_SIZE = 10L;
//  private static final UUID RANDOM_FILE_ID = UUID.randomUUID();
//  private static final UUID RANDOM_USER_ID = UUID.randomUUID();
//  private static final UUID NON_ACCESS_RANDOM_USER_ID = UUID.randomUUID();
//  private static final Path PATH = Path.of("test");
//  private static final Type CORRECT_TYPE = Type.DOX;
//  public final static Long MIN_SIZE = 1L;
//  public final static Long MAX_SIZE = 15 * 1024 * 1024L;
//  private static final Long TOO_LARGE_SIZE = MAX_SIZE + 1L;
//  private static final Boolean VIRUS_FLAG = Boolean.TRUE;
//  private static final UUID NON_ACCESS_USER_ID = UUID.randomUUID();
////  private final FileRepository fileRepository;
////  private final UserOrderService userOrderService;
////  private final FileMapper fileMapper;
////  private final Producer producer;
//
//  @Mock
//  private FileRepository fileRepository;
//  @Mock
//  private UserOrderService userOrderService;
//  @Mock
//  private FileMapper fileMapper;
//  @Mock
//  private Producer producer;
//  @InjectMocks
//  private FileServiceImpl fileService;
//  @Mock
//  private MultipartFile multipartFile;
//
//  private static FileRequestDto fileDto;
//  private static File file;
//  private static FileResponseDto fileResponseDto;
//  private static byte[] bytes;
//  private static UserOrder userOrder;
//  private static List<UUID> filesUUID;
//  private static List<File> files = new ArrayList<>();
//
//  @BeforeEach()
//  void init() {
//    when(multipartFile.isEmpty()).thenReturn(!VIRUS_FLAG);
//    when(multipartFile.getOriginalFilename()).thenReturn(FILE_TITLE);
//    when(multipartFile.getSize()).thenReturn(MAX_SIZE);
//    when(fileService.getFileIdByUserIdAndTitle(RANDOM_USER_ID, FILE_TITLE)).thenReturn(RANDOM_FILE_ID);
//    when(fileResponseDto.getTitle()).thenReturn(FILE_TITLE);
//    when(file.getFile()).thenReturn(bytes);
//    when(file.getTitle()).thenReturn(FILE_TITLE);
//    when(file.getUserId()).thenReturn(RANDOM_USER_ID);
//    when(fileService.findByPath(any())).thenReturn(file);
//  }
//
//  @Test
//  @DisplayName("Если пользователя с таким логином и файлом не найден, выбрасывается исключение")
//  void thrown_correct_exception_when_user_with_file_not_found() {
//    //Given
//    String expectedMessage =
//        "User with login: " + RANDOM_USER_ID + " not own file with id: " + RANDOM_FILE_ID + " ";
//    when(fileRepository.findByUserIdAndId(RANDOM_USER_ID, RANDOM_FILE_ID)).thenReturn(
//        Optional.empty());
//
//    //When
//    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
//        () -> {
//          fileService.findByUserIdAndId(RANDOM_USER_ID, RANDOM_FILE_ID);
//        });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("Если файл с таким путем не найден, выбрасывается исключение")
//  void thrown_correct_exception_when_file_not_found() {
//    //Given
//    String expectedMessage = "File not found with path: " + PATH;
//    when(fileRepository.findByDownloadUrl(PATH.toString())).thenReturn(Optional.empty());
//
//    //When
//    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
//        () -> {
//          fileService.findByPath(PATH);
//        });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("При нехватке прав пользователя, выбрасывается исключение")
//  void success_access_check() {
//
//    String expectedMessage =
//        "User with id: " + NON_ACCESS_RANDOM_USER_ID + " hasn't access to file: " + FILE_TITLE;
//
//    fileService.accessCheck(RANDOM_USER_ID, fileResponseDto);
//    //When
//    AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
//        () -> {
//          fileService.accessCheck(NON_ACCESS_RANDOM_USER_ID, fileResponseDto);
//        });
//
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
//
//  @Test
//  @DisplayName("Статус файла не обновлен, т.е файл не найден.")
//  void file_not_found_to_update_status() {
//    //Given
//    Status status = Status.UPLOAD;
//    String expectedMessage = "File not found with id: " + RANDOM_FILE_ID;
//    when(fileRepository.findById(RANDOM_FILE_ID)).thenReturn(Optional.empty());
//
//    //When
//    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
//      fileService.updateStatus(RANDOM_FILE_ID, status);
//    });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//    verify(fileRepository, times(1))
//        .findById(RANDOM_FILE_ID);
//  }
//
//  @Test
//  @DisplayName("Статус файла успешно обновляется.")
//  void success_update_status() {
//    //Given
//    Status status = Status.UPLOAD;
//    File file = new File();
//    when(fileRepository.findById(RANDOM_FILE_ID)).thenReturn(Optional.of(file));
//    file.setStatus(status);
//    var captor = ArgumentCaptor.forClass(File.class);
//    when(fileRepository.save(captor.capture())).thenReturn(file);
//
//    //When
//    fileService.updateStatus(RANDOM_FILE_ID, status);
//
//    //Then
//    assertThat(captor.getValue().getStatus()).isEqualTo(status);
//    verify(fileRepository, times(1))
//        .findById(RANDOM_FILE_ID);
//    verify(fileRepository, times(1))
//        .save(captor.getValue());
//  }
//
//  @Test
//  @DisplayName("Файл успешно сохраняется с корректными полями.")
//  void success_save_file_with_correct_fields() {
//    //Given
//    var captor = ArgumentCaptor.forClass(File.class);
//    when(userOrderRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.of(userOrder));
//    when(fileMapper.toEntity(fileDto)).thenReturn(file);
//    when(fileRepository.save(captor.capture())).thenReturn(file);
//    when(fileMapper.toFileResponseDto(file)).thenReturn(fileResponseDto);
//
//    file.setCreatedAt(LocalDate.now());
//    file.setUpdateDate(LocalDate.now());
//
//    //When
//    fileService.save(RANDOM_USER_ID, fileDto);
//
//    //Then
//    assertThat(file).isEqualTo(captor.getValue());
//    verify(fileRepository, times(1)).save(captor.getValue());
//  }
//
//  @Test
//  @DisplayName("Не сохраняет файл для пользователя, которого не существует и выкидывает исключение.")
//  void cannot_save_file_with_unknown_user_throw_exception() {
//    //Given
//    String expectedMessage = "User not found with login: " + RANDOM_USER_ID;
//    FileRequestDto fileMetadataRequestDto = new FileRequestDto();
//    when(userOrderRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.empty());
//
//    //When
//    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
//      fileService.save(RANDOM_USER_ID, fileMetadataRequestDto);
//    });
//
//    //Then
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//    verify(userOrderRepository, times(1))
//        .findById(RANDOM_USER_ID);
//  }
//
//  @Test
//  @DisplayName("Тип файла успешно определён")
//  void success_determinate_file_type() {
//    when(multipartFile.getOriginalFilename()).thenReturn(FILE_TITLE + ".dox");
//    Type type = fileService.determinateType(multipartFile);
//    assertThat(type).isEqualTo(CORRECT_TYPE);
//  }
//
//  @Test
//  @DisplayName("Если тип файла не поддерживается, то выбрасывается исключение")
//  void thrown_correct_exception_when_type_unsupported() {
//    when(multipartFile.getOriginalFilename()).thenReturn(FILE_TITLE);
//    String expectedMessage = "Invalid filename";
//    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
//        () -> {
//          fileService.determinateType(multipartFile);
//        });
//    String actualMessage = thrown.getMessage();
//    assertThat(actualMessage).isEqualTo(expectedMessage);
//  }
}
