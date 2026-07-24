package ru.answer_42.user_service.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.function.Function;
import lombok.Builder;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import ru.answer_42.user_service.service.DeactivatedTokenService;
import ru.answer_42.user_service.service.impl.TokenAuthenticationUserDetailsService;
import ru.answer_42.user_service.service.impl.TokenUser;


public class TokenCookieAuthenticationConfigurer
    extends AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer, HttpSecurity> {

  private DeactivatedTokenService deactivatedTokenService;
  private Function<String,Token> tokenCookieStringDeserializer;

  @Override
  public void init(HttpSecurity builder) {
    builder.logout(logout -> logout
        .addLogoutHandler(new CookieClearingLogoutHandler("__Host-auth-token"))
        .addLogoutHandler((request, response, authentication) -> {
          if(authentication != null &&
              authentication.getPrincipal() instanceof TokenUser tokenUser){
            this.deactivatedTokenService.update(tokenUser.getToken().id(), Date.from(tokenUser.getToken().expiresAt()));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
          }
        }));
  }

  @Override
  public void configure(HttpSecurity builder) {
    var cookieAuthenticationFilter = new AuthenticationFilter(
        builder.getSharedObject(AuthenticationManager.class),
        new TokenCookieAuthenticationConverter(this.tokenCookieStringDeserializer));
      cookieAuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {}));
      cookieAuthenticationFilter.setFailureHandler(
          new AuthenticationEntryPointFailureHandler(
              new Http403ForbiddenEntryPoint()
          )
      );

      var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
      authenticationProvider.setPreAuthenticatedUserDetailsService(new TokenAuthenticationUserDetailsService(deactivatedTokenService));

      builder.addFilterAfter(cookieAuthenticationFilter, CsrfFilter.class)
          .authenticationProvider(authenticationProvider);

  }

  public TokenCookieAuthenticationConfigurer tokenCookieStringDeserializer(
      Function<String, Token> tokenCookieStringDeserializer) {
    this.tokenCookieStringDeserializer = tokenCookieStringDeserializer;
    return this;
  }

  public TokenCookieAuthenticationConfigurer deactivatedTokenService(
      DeactivatedTokenService deactivatedTokenService) {
    this.deactivatedTokenService = deactivatedTokenService;
    return this;
  }
}
