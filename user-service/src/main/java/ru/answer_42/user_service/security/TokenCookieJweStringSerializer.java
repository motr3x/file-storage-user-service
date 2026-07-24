package ru.answer_42.user_service.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Setter
public class TokenCookieJweStringSerializer implements Function<Token, String> {

  private final JWEEncrypter jweEncrypter;

  private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

  private EncryptionMethod encryptionMethod = EncryptionMethod.A128GCM;


  @SneakyThrows
  @Override
  public String apply(Token token) {
    var jweHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
        .keyID(token.id().toString())
        .build();
    var claimsSet = new JWTClaimsSet.Builder()
        .jwtID(token.id().toString())
        .subject(token.subject())
        .issueTime(Date.from(token.createdAt()))
        .expirationTime(Date.from(token.expiresAt()))
        .claim("authorities", token.authorities())
        .build();
    var encryptedJWT = new EncryptedJWT(jweHeader, claimsSet);
    try {
      encryptedJWT.encrypt(this.jweEncrypter);

      return encryptedJWT.serialize();
    } catch (JOSEException exception) {
      log.error(exception.getMessage());
    }

    return null;
  }
}
