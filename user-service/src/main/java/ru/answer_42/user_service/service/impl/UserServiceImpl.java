package ru.answer_42.user_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.exception.ResourceNotFoundException;
import ru.answer_42.user_service.mapper.UserMapper;
import ru.answer_42.user_service.model.User;
import ru.answer_42.user_service.repository.InMemoryUserRepository;
import ru.answer_42.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final InMemoryUserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public List<UserResponseDto> findAll() {
    List<UserResponseDto> userRequestDtos = userRepository.findAll().stream().map(
        userMapper::toUserResponseDto).toList();
    return userRequestDtos;
  }

  @Override
  public UserResponseDto save(UserRequestDto userRequestDto) {
    User user = userMapper.toEntity(userRequestDto);
    User savedUser = userRepository.save(UUID.randomUUID(), user);
    return userMapper.toUserResponseDto(savedUser);
  }

  @Override
  public UserResponseDto update(UUID id, UserRequestDto userRequestDto) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

    userMapper.updateEntityFromDto(userRequestDto, existingUser);

    User updateFile = userRepository.update(existingUser);

    return userMapper.toUserResponseDto(updateFile);
  }

  @Override
  public UserResponseDto deleteById(UUID id) {
    findById(id);
    return userMapper.toUserResponseDto(userRepository.deleteById(id));
  }


  @Override
  public UserResponseDto findById(UUID id) {
    User user = userRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    return userMapper.toUserResponseDto(user);
  }
}
