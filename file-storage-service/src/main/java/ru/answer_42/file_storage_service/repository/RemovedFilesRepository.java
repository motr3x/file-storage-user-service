package ru.answer_42.file_storage_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.answer_42.file_storage_service.model.RemovedFiles;
@Repository
public interface RemovedFilesRepository extends JpaRepository<RemovedFiles, UUID> {

}
