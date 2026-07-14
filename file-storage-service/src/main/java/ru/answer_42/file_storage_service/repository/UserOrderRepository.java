package ru.answer_42.file_storage_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.answer_42.file_storage_service.model.UserOrder;

public interface UserOrderRepository extends JpaRepository<UserOrder, UUID> {

}
