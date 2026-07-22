package ru.answer_42.user_service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.dto.UserDetailsDto;
import ru.answer_42.user_service.service.UserService;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserService userService;
  @Override
  public UserDetails loadUserByUsername(String login)  {
    UserDetailsDto myUser= userService.findByLogin(login);
    return User.builder()
        .username(myUser.getLogin())
        .password(myUser.getPassword())
        .roles(myUser.getRole())
        .build();
  }
}