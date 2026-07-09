package ru.answer_42.file_storage_service.repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.User;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
  Optional<File> findByUserLoginAndTitle(String login, String title);
  List<File> findAllByUserLogin(String login);
  Optional<File> findByTitle(String title);
  Optional<File> findByDownloadUrl(String url);
  Optional<File> findByUserLoginAndId(String login, UUID id);
//  private final Map<UUID, File> database = new HashMap<>();

//  public UUID save(String login, UUID id, File file);
//  {
//    file.setId(id);
//    file.setUserLogin(login);
//    file.setUserId(findUserIdByLogin(login).orElse(UUID.randomUUID()));
//    database.put(id, file);
//    return id;
//  }

//  public List<String> findAllTitles();
//  {
//    List<String> titles = new ArrayList<>();
//    for (Map.Entry<UUID, File> entry : database.entrySet()) {
//      titles.add(entry.getValue().getTitle());
//    }
//    return titles;
//  }

//  public Optional<User> findUserByUserLogin(String login);
//  {
//    for (Map.Entry<UUID, File> entry : database.entrySet()) {
//      if (entry.getValue().getUserLogin().equals(login)) {
//        return Optional.of(entry.getValue().getUserId());
//      }
//    }
//    return Optional.empty();
//  }

//  public List<File> findAll(String login);
//  {
//    UUID id = findUserIdByLogin(login)
//        .orElseThrow(() -> new ResourceNotFoundException("File not found with login: " + login));
//    ;
//    List<File> files = new ArrayList<>();
//    for (Map.Entry<UUID, File> entry : database.entrySet()) {
//      if (entry.getValue().getUserId().equals(id)) {
//        files.add(entry.getValue());
//      }
//    }
//    return files;
//  }

//  Optional<File> findByPath(Path path);
//  {
//    for (Map.Entry<UUID, File> entry : database.entrySet()) {
//      if (entry.getValue().getDownloadUrl().equals(path.toString())) {
//        return Optional.of(entry.getValue());
//      }
//    }
//    return Optional.empty();
//  }

//  Optional<File> findById(UUID id);
//  {
//    return Optional.ofNullable(database.get(id));
//  }

//  void deleteById(UUID id);
//  {
//    return database.remove(id);
//  }

//  public File update(File file);
//  {
//    file.setUpdateDate(LocalDate.now());
//    save(file.getUserLogin(), file.getId(), file);
//    return file;
//  }

//  public Optional<File> findByTittle(String name);
//  {
//    for (Map.Entry<UUID, File> entry : database.entrySet()) {
//      if (entry.getValue().getTitle().equals(name)) {
//        return Optional.of(entry.getValue());
//      }
//    }
//    return Optional.empty();
//  }

}
