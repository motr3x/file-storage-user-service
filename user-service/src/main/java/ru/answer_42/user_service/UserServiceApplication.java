package ru.answer_42.user_service;

import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import java.text.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import ru.answer_42.user_service.model.DeactivatedToken;
import ru.answer_42.user_service.security.GetCsrfTokenFilter;
import ru.answer_42.user_service.security.TokenCookieAuthenticationConfigurer;
import ru.answer_42.user_service.security.TokenCookieJweStringDeserializer;
import ru.answer_42.user_service.security.TokenCookieJweStringSerializer;
import ru.answer_42.user_service.security.TokenCookieSessionAuthenticationStrategy;
import ru.answer_42.user_service.service.DeactivatedTokenService;
import ru.answer_42.user_service.service.impl.CustomUserDetailsServiceImpl;
import ru.answer_42.user_service.service.impl.DeactivatedTokenServiceImpl;

@SpringBootApplication
@EnableJpaRepositories("ru.answer_42.user_service.*")
@ComponentScan(basePackages = { "ru.answer_42.user_service.*" })
@EntityScan("ru.answer_42.user_service.*")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}


	@Bean
	public TokenCookieJweStringSerializer tokenCookieJweStringSerializer(
			@Value("${jwt.cookie-token-key}") String cookieTokenKey
	) throws Exception {
		return new TokenCookieJweStringSerializer(new DirectEncrypter(
				OctetSequenceKey.parse(cookieTokenKey)
		));
	}

	@Bean
	public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
			@Value("${jwt.cookie-token-key}") String cookieTokenKey,
			DeactivatedTokenService deactivatedTokenService) throws Exception {
		return new TokenCookieAuthenticationConfigurer()
				.tokenCookieStringDeserializer(new TokenCookieJweStringDeserializer(
						new DirectDecrypter(
								OctetSequenceKey.parse(cookieTokenKey)
						)
				))
				.deactivatedTokenService(deactivatedTokenService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer,
			TokenCookieJweStringSerializer tokenCookieJweStringSerializer) throws Exception {

		var tokenCookieSessionAuthenticationStrategy = new TokenCookieSessionAuthenticationStrategy();
		tokenCookieSessionAuthenticationStrategy.setTokenStringSerializer(tokenCookieJweStringSerializer);

		http.httpBasic(Customizer.withDefaults())
				.addFilterAfter(new GetCsrfTokenFilter(), ExceptionTranslationFilter.class)
				.authorizeHttpRequests(authorizeHttpRequest -> {
					authorizeHttpRequest
							.requestMatchers("/error", "/test", "/test/v2").hasRole("USER")
							.requestMatchers("/swagger-ui/**", "/v3/api-docs*/**").permitAll();

				})
				.sessionManagement(sessionManagement -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						.sessionAuthenticationStrategy(tokenCookieSessionAuthenticationStrategy))
				.csrf(csrf -> {
					csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
							.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
							.sessionAuthenticationStrategy(((authentication, request, response) -> {}));
				});
		http.apply(tokenCookieAuthenticationConfigurer);
		return http.build();
	}

}
