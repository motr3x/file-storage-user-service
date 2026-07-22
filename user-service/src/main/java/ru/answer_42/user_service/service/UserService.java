package ru.answer_42.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.dto.UserDetailsDto;
import ru.answer_42.user_service.dto.UserOrder;
import ru.answer_42.user_service.model.FileOrder;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;

public interface UserService {

  List<UserResponseDto> findAll();

  UserResponseDto save(UserRequestDto userDto) throws JsonProcessingException;

  UserResponseDto update(UUID id, UserRequestDto userDto);

  UserResponseDto deleteById(UUID id);

  List<FileOrder> findAllFilesById(UUID userId);

   String createUserOrder(UserOrder userOrder) throws JsonProcessingException;

  UserDetailsDto findByLogin(String login);
}