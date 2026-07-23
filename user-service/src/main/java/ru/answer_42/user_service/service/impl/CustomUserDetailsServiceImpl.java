package ru.answer_42.user_service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.dto.UserDetailsDto;
import ru.answer_42.user_service.exception.ResourceNotFoundException;
import ru.answer_42.user_service.model.User;
import ru.answer_42.user_service.repository.UserRepository;
import ru.answer_42.user_service.service.UserService;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;
  @Override
  public UserDetails loadUserByUsername(String login)  {
      User user = userRepository.findByLogin(login).
          orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
      return user;
    }
}