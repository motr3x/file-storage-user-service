package ru.answer_42.user_service.service.impl;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.security.Token;
import ru.answer_42.user_service.service.DeactivatedTokenService;

@Service
public class TokenAuthenticationUserDetailsService implements
    AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  @Autowired
  private final DeactivatedTokenService deactivatedTokenService;

  public TokenAuthenticationUserDetailsService(DeactivatedTokenService deactivatedTokenService) {
    this.deactivatedTokenService = deactivatedTokenService;
  }

  @Override
  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken)
      throws UsernameNotFoundException {
    if(authenticationToken.getPrincipal() instanceof Token token) {
      // todo create deactivated token table
      return new TokenUser(token.subject(), "nopassword", true, true,
          (deactivatedTokenService.findById(token.id())==null) && token.expiresAt().isAfter(Instant.now()), true,
          token.authorities().stream().map(SimpleGrantedAuthority::new)
              .toList(), token);
    }
      throw new UsernameNotFoundException("Principal must me of type Token");
  }
}
