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
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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


	private final static String USER_API = "/api/users";
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

		// Внедряется бин который сериализует экземпляр объекта класса токен в JWE токен
		var tokenCookieSessionAuthenticationStrategy = new TokenCookieSessionAuthenticationStrategy();
		tokenCookieSessionAuthenticationStrategy.setTokenStringSerializer(tokenCookieJweStringSerializer);

		// басик реализация аутентификации
		http.httpBasic(Customizer.withDefaults())
				//Добавление фильтра для получение csrf токена
				.addFilterAfter(new GetCsrfTokenFilter(), ExceptionTranslationFilter.class)
				.authorizeHttpRequests(authorizeHttpRequest -> {
					authorizeHttpRequest
							.requestMatchers(HttpMethod.GET, USER_API + "/downloadUrl/**").hasAnyRole("USER", "ADMIN")
							.requestMatchers(HttpMethod.POST, USER_API + "/create").hasRole("ADMIN")
							.requestMatchers(HttpMethod.GET, USER_API).hasRole("ADMIN")
							.requestMatchers(HttpMethod.PUT, USER_API).hasAnyRole("USER", "ADMIN")
							.requestMatchers(HttpMethod.DELETE, USER_API+"/{file_id}").hasRole("ADMIN")
							.requestMatchers(HttpMethod.GET, USER_API + "/files").hasAnyRole("USER", "ADMIN")
							.requestMatchers(HttpMethod.GET, USER_API + "/token").hasAnyRole("USER", "ADMIN")
							.requestMatchers("/swagger-ui/**", "/v3/api-docs*/**").permitAll()
							.anyRequest().authenticated();

				})
				.sessionManagement(sessionManagement -> sessionManagement
						// отключение сессии, что бы при каждом запросе клиент отправлял токен
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						// Установка стратегии для аутентификации
						.sessionAuthenticationStrategy(tokenCookieSessionAuthenticationStrategy))
				.csrf(csrf -> {
					// по умолчанию csrf токен привязан к сессий, здесь меняем на cookie repository
					csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
							// Ищет csrf токен в атрибутах запросу
							.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
							// стратегия не обновляет csrf токен
							.sessionAuthenticationStrategy(((authentication, request, response) -> {}));
				});
		http.apply(tokenCookieAuthenticationConfigurer);
		return http.build();
	}


}
