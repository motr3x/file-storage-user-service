package ru.answer_42.file_storage_service.service.impl;


import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.answer_42.file_storage_service.exception.ExpirationTimeTokenException;
import ru.answer_42.file_storage_service.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {

  private final JWEDecrypter jweDecrypter;

  public JwtServiceImpl(@Value("${jwt.cookie-token-key}") String cookieTokenKey)
      throws ParseException, KeyLengthException {
    this.jweDecrypter = new DirectDecrypter(
        OctetSequenceKey.parse(cookieTokenKey)
    );
  }

  public JWTClaimsSet getClaimsSet(String userToken)
      throws Exception {
    EncryptedJWT encryptedJWT = EncryptedJWT.parse(userToken);
    encryptedJWT.decrypt(this.jweDecrypter);
    return encryptedJWT.getJWTClaimsSet();
  }

  public boolean validToken(String userToken) throws Exception {
    JWTClaimsSet claimsSet = getClaimsSet(userToken);
    return claimsSet.getExpirationTime().after(Date.from(Instant.now()));
  }

  @Override
  public UUID getUserIdFromToken(String userToken)
      throws Exception {
    if (validToken(userToken)) {
      throw new ExpirationTimeTokenException("Время жизни токена истекло");
    }
    ;
    JWTClaimsSet claimsSet = getClaimsSet(userToken);
    return UUID.fromString(claimsSet.getSubject());
  }
}
