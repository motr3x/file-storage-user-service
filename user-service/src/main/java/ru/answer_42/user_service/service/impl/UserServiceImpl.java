package ru.answer_42.user_service.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.dto.FileMetadataDto;
import ru.answer_42.user_service.dto.FileMetadataOrder;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.exception.ResourceNotFoundException;
import ru.answer_42.user_service.mapper.FileMetadataMapper;
import ru.answer_42.user_service.mapper.UserMapper;
import ru.answer_42.user_service.model.FileMetadata;
import ru.answer_42.user_service.model.User;
import ru.answer_42.user_service.repository.UserRepository;
import ru.answer_42.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final FileMetadataMapper fileMetadataMapper;
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
    User savedUser = userRepository.save(user);
    return userMapper.toUserResponseDto(savedUser);
  }

  @Override
  public UserResponseDto update(UUID id, UserRequestDto userRequestDto) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

    userMapper.updateEntityFromDto(userRequestDto, existingUser);

    User updateFile = userRepository.save(existingUser);

    return userMapper.toUserResponseDto(updateFile);
  }

  @Override
  public UserResponseDto deleteById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    userRepository.deleteById(id);
    return userMapper.toUserResponseDto(user);
  }

  public FileMetadataOrder addFileMetadata(FileMetadataOrder fileMetadata) {
    String login = fileMetadata.getUserLogin();
    User user = userRepository.findByLogin(fileMetadata.getUserLogin())
        .orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
    user.getFiles().add(fileMetadata);
    userRepository.save(user);
    return fileMetadata;
  }


  @Override
  public List<FileMetadataOrder> findAllFilesByLogin(String login) {
    User user = userRepository.findByLogin(login).
        orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
    ;
    return user.getFiles();
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
