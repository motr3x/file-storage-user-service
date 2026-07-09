package ru.answer_42.file_storage_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.answer_42.file_storage_service.model.User;
import ru.answer_42.file_storage_service.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
  private final UserRepository userRepository;
  @GetMapping("/u")
  public void saveUser(){
    User user = new User();
    user.setLogin("example1");
    userRepository.save(user);
  }

}
