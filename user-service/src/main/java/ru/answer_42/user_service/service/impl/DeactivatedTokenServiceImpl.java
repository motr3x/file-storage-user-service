package ru.answer_42.user_service.service.impl;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.quota.ClientQuotaAlteration.Op;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.answer_42.user_service.exception.ResourceNotFoundException;
import ru.answer_42.user_service.model.DeactivatedToken;
import ru.answer_42.user_service.repository.DeactivatedTokenRepository;
import ru.answer_42.user_service.service.DeactivatedTokenService;

@Service
@RequiredArgsConstructor
public class DeactivatedTokenServiceImpl implements DeactivatedTokenService {

  private final DeactivatedTokenRepository deactivatedTokenRepository;


  @Override
  public DeactivatedToken findById(UUID id) {
    DeactivatedToken deactivatedToken = deactivatedTokenRepository.findById(id)
        .orElse(null);
    return deactivatedToken;
  }

  @Override
  public void update(UUID id, Date from) {
    DeactivatedToken deactivatedToken = deactivatedTokenRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Deactivated token not found with id: " + id));
    deactivatedToken.setKeepUntil(from);
    deactivatedTokenRepository.save(deactivatedToken);
  }
}
