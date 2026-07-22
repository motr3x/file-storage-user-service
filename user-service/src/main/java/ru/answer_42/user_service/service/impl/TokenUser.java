package ru.answer_42.user_service.service.impl;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.answer_42.user_service.security.Token;

public class TokenUser extends User {

  private final Token token;

  public TokenUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Token token) {
    super(username, password, authorities);
    this.token = token;
  }

  public TokenUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Token token) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}