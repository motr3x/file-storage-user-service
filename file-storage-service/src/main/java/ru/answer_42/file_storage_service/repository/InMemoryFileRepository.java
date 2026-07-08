package ru.answer_42.file_storage_service.repository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import ru.answer_42.file_storage_service.exception.ResourceNotFoundException;
import ru.answer_42.file_storage_service.model.File;

@Repository
public class InMemoryFileRepository {

  private final Map<UUID, File> database = new HashMap<>();

  public UUID save(String login, UUID id, File file) {
    file.setId(id);
    file.setUserLogin(login);
    file.setUserId(findUserIdByLogin(login).orElse(UUID.randomUUID()));
    database.put(id, file);
    return id;
  }

  public List<String> findAllTitles() {
    List<String> titles = new ArrayList<>();
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      titles.add(entry.getValue().getTitle());
    }
    return titles;
  }

  public Optional<UUID> findUserIdByLogin(String login) {
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      if (entry.getValue().getUserLogin().equals(login)) {
        return Optional.of(entry.getValue().getUserId());
      }
    }
    return Optional.empty();
  }

  public List<File> findAll(String login) {
    UUID id = findUserIdByLogin(login)
        .orElseThrow(() -> new ResourceNotFoundException("File not found with login: " + login));
    ;
    List<File> files = new ArrayList<>();
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      if (entry.getValue().getUserId().equals(id)) {
        files.add(entry.getValue());
      }
    }
    return files;
  }

  public Optional<File> findByPath(Path path) {
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      if (entry.getValue().getDownloadUrl().equals(path.toString())) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

  public Optional<File> findById(UUID id) {
    return Optional.ofNullable(database.get(id));
  }

  public File deleteById(UUID id) {
    return database.remove(id);
  }

  public File update(File file) {
    file.setUpdateDate(LocalDate.now());
    save(file.getUserLogin(), file.getId(), file);
    return file;
  }

  public Optional<File> findByTitle(String name) {
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      if (entry.getValue().getTitle().equals(name)) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

}
