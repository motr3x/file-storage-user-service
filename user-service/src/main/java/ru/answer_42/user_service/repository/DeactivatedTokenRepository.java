package ru.answer_42.user_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.answer_42.user_service.model.DeactivatedToken;
@Repository
public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, UUID> {

}
