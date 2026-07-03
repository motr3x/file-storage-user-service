package ru.answer_42.file_storage_service.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import ru.answer_42.file_storage_service.model.File;

@Repository
public class InMemoryFileRepository {

  private final Map<UUID, File> database = new HashMap<>();

  public UUID save(UUID id, File file) {
    file.setId(id);
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

  public List<File> findAll() {
    List<File> files = new ArrayList<>();
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      files.add(entry.getValue());
    }
    return files;
  }

  public Optional<File> findById(UUID id) {
    return Optional.ofNullable(database.get(id));
  }

  public File deleteById(UUID id) {
    return database.remove(id);
  }

  public File update(File file) {
    file.setUpdateDate(LocalDate.now());
    save(file.getId(), file);
    return file;
  }

  public Optional<File> findByTitle(String name){
    for (Map.Entry<UUID, File> entry : database.entrySet()) {
      if(entry.getValue().getTitle().equals(name)){
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

}
