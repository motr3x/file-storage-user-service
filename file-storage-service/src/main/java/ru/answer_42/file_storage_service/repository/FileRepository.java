package ru.answer_42.file_storage_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.answer_42.file_storage_service.model.File;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

  Optional<File> findByUserLoginAndTitle(String login, String title);

  List<File> findAllByUserLogin(String login);

  Optional<File> findByTitle(String title);

  Optional<File> findByDownloadUrl(String url);

  Optional<File> findByUserLoginAndId(String login, UUID id);
}
