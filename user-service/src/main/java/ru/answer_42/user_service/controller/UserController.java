package ru.answer_42.user_service.controller;

import java.util.List;
import java.util.UUID;
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
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController{
  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestDto userRequestDto){
    UserResponseDto userResponseDto = userService.save(userRequestDto);
    return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> readById(@PathVariable UUID id){
    UserResponseDto userResponseDto = userService.findById(id);
    return ResponseEntity.ok(userResponseDto);
  }

  @GetMapping()
  public ResponseEntity<List<UserResponseDto>> readAll(){
    List<UserResponseDto> userResponseDto = userService.findAll();
    return ResponseEntity.ok(userResponseDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDto> update(@PathVariable UUID id, @RequestBody UserRequestDto fileRequestDto){
    UserResponseDto userResponseDto = userService.update(id, fileRequestDto);
    return ResponseEntity.ok(userResponseDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<UserResponseDto> delete(@PathVariable UUID id){
    UserResponseDto userResponseDto = userService.deleteById(id);
    return ResponseEntity.ok(userResponseDto);
  }
}