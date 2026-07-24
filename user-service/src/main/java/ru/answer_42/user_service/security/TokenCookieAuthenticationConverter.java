package ru.answer_42.user_service.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class TokenCookieAuthenticationConverter implements AuthenticationConverter {

  private final Function<String, Token> tokenCookieStringDeserializer;

  public TokenCookieAuthenticationConverter(Function<String, Token> tokenCookieStringDeserializer) {
    this.tokenCookieStringDeserializer = tokenCookieStringDeserializer;
  }

  @Override
  public @Nullable Authentication convert(HttpServletRequest request) {
    if(request.getCookies() != null){
      return Stream.of(request.getCookies())
          .filter(cookie -> cookie.getName().equals("__Host-auth-token"))
          .findFirst()
          .map(cookie -> {
            var token = this.tokenCookieStringDeserializer.apply(cookie.getValue());
            return new PreAuthenticatedAuthenticationToken(token, cookie.getValue());
          }).orElse(null);
    }
    return null;
  }
}
