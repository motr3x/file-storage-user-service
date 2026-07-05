package ru.answer_42.user_service.service;

import java.util.List;
import java.util.UUID;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.dto.FileMetadataDto;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.model.FileMetadata;

public interface UserService {

  FileMetadata addFileMetadata(FileMetadata fileMetadata);
  List<UserResponseDto> findAll();
  /**
   * Создает нового юзера
   * @param userDto - юзер для создания
   */
  UserResponseDto save(UserRequestDto userDto);

  /**
   * Обновляет юзера с заданным ID,
   * в соответствии с переданным юзером
   * @param userDto - юзер в соответствии с которым нужно обновить данные
   * @param id - id юзера которой нужно обновить
   * @return - объект UserResponseDto
   */
  UserResponseDto update(UUID id, UserRequestDto userDto);

  /**
   * Удаляет файл с заданным ID
   * @param id - id файла, который нужно удалить
   * @return - объект UserResponseDto
   */
  UserResponseDto deleteById(UUID id);
  /**
   * Возвращает юзера по его ID
   * @param id - ID юзера
   * @return - объект юзера с заданным ID
   */
  UserResponseDto findById(UUID id);

  List<FileMetadataDto> findAllFilesByLogin(String login);

  List<FileDownloadDto> findAllLinksByLogin(String login);
}