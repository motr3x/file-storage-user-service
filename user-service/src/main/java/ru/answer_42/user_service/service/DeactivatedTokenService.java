package ru.answer_42.user_service.service;

import java.util.Date;
import java.util.UUID;
import ru.answer_42.user_service.model.DeactivatedToken;

public interface DeactivatedTokenService {
  DeactivatedToken findById(UUID id);

  void update(UUID id, Date from);
}
