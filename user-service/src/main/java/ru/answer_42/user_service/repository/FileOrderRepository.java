package ru.answer_42.user_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.answer_42.user_service.model.FileOrder;

public interface FileOrderRepository extends JpaRepository<FileOrder, UUID> {

}
