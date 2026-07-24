package ru.answer_42.user_service.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Setter
public class TokenCookieSessionAuthenticationStrategy implements SessionAuthenticationStrategy {

  private Function<Authentication, Token> tokenCookieFactory = new DefaultTokenCookieFactory();

  // По дефолту просто перевод токена в строку, но в application переопределён
  private Function<Token, String> tokenStringSerializer = Objects::toString;


  // Действия, которые происходят при удачной аутентификации
  @Override
  public void onAuthentication(Authentication authentication, HttpServletRequest request,
      HttpServletResponse response) throws SessionAuthenticationException {
    // проверка нужна для того, что бы токен генерился только в том случае, когда пользователь
    // кидает запрос с логином/паролем -> создается UsernamePasswordAuthenticationToken
    // А когда уже токен есть и пользователь передает его через куки -> JwtAuthenticationToken
    // соответственно токен не обновляется
    if(authentication instanceof UsernamePasswordAuthenticationToken) {
      // мы берем объект authentication, который в себе содержит логин/пароль юзера и отдаем на
      // фабрику токенов, что бы логин/пароль превратить в токен
      var token = this.tokenCookieFactory.apply(authentication);
      // переводим
      var tokesString = this.tokenStringSerializer.apply(token);
      var cookie = new Cookie("__Host-auth-token", tokesString);
      cookie.setPath("/");
      cookie.setDomain(null);
      cookie.setSecure(true);
      //Только сервер имеет доступ к куке
      cookie.setHttpOnly(true);
      //Время хранение куки
      cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));
      response.addCookie(cookie);
    }

  }

}
