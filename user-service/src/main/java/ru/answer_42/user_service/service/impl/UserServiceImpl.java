package ru.answer_42.user_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.dto.UserOrder;
import ru.answer_42.user_service.model.FileOrder;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.exception.ResourceNotFoundException;
import ru.answer_42.user_service.mapper.UserMapper;
import ru.answer_42.user_service.model.User;
import ru.answer_42.user_service.repository.FileOrderRepository;
import ru.answer_42.user_service.repository.UserRepository;
import ru.answer_42.user_service.service.Producer;
import ru.answer_42.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final Producer producer;
  private final FileOrderRepository fileOrderRepository;


  @Override
  public String createUserOrder(UserOrder userOrder)
      throws JsonProcessingException {
    return producer.sendMessage(userOrder);
  }

  @Override
  public List<UserResponseDto> findAll() {
    List<UserResponseDto> userRequestDtos = userRepository.findAll().stream().map(
        userMapper::toUserResponseDto).toList();
    return userRequestDtos;
  }

  @Override
  public UserResponseDto save(UserRequestDto userRequestDto) throws JsonProcessingException {
    User user = userMapper.toEntity(userRequestDto);
    User savedUser = userRepository.save(user);
    createUserOrder(userMapper.fromEntityToUserOrder(user));
    return userMapper.toUserResponseDto(savedUser);
  }

  @Override
  public UserResponseDto update(UUID userId, UserRequestDto userRequestDto) {
    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    userMapper.updateEntityFromDto(userRequestDto, existingUser);

    User updateFile = userRepository.save(existingUser);

    return userMapper.toUserResponseDto(updateFile);
  }

  @Override
  public UserResponseDto deleteById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    userRepository.deleteById(userId);
    return userMapper.toUserResponseDto(user);
  }

  public FileOrder addFileMetadata(FileOrder fileOrder) {
    return fileOrderRepository.save(fileOrder);
  }


  @Override
  public List<FileOrder> findAllFilesByLogin(UUID userId) {
    User user = userRepository.findById(userId).
        orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    List<FileOrder> fileOrders = fileOrderRepository.findAll().stream().filter(f -> f.getUserId().equals(userId)).toList();
    return fileOrders;
  }

//  @Override
//  public List<FileDownloadDto> findAllLinksByLogin(String login) {
//    User user = userRepository.findByLogin(login).
//        orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
//    ;
//    return user.getFiles().stream().map(FileMetadata::getDownloadUrl).map(FileDownloadDto::new)
//        .toList();
//  }
}
