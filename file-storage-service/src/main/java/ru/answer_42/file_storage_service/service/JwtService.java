package ru.answer_42.file_storage_service.service;

import java.text.ParseException;
import java.util.UUID;

public interface JwtService {
  UUID getUserIdFromToken(String token) throws Exception;
}
