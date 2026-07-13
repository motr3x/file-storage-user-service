package ru.answer_42.user_service.service;

import java.util.List;
import java.util.UUID;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.dto.FileMetadataDto;
import ru.answer_42.user_service.dto.FileMetadataOrder;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.model.FileMetadata;

public interface UserService {

  FileMetadataOrder addFileMetadata(FileMetadataOrder fileMetadataOrder);

  List<UserResponseDto> findAll();

  UserResponseDto save(UserRequestDto userDto);

  UserResponseDto update(UUID id, UserRequestDto userDto);

  UserResponseDto deleteById(UUID id);

  List<FileMetadataOrder> findAllFilesByLogin(String login);

//  List<FileDownloadDto> findAllLinksByLogin(String login);
}