package ru.answer_42.user_service.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.answer_42.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByLogin(String login);

//  private final Map<UUID, User> database = new HashMap<>();
//
//  public User save(UUID id, User user){
//    user.setId(id);
//    database.put(id, user);
//    return user;
//  }
//
//  public Optional<User> findByLogin(String login){
//    for (Map.Entry<UUID, User> entry : database.entrySet()) {
//      if(entry.getValue().getLogin().equals(login)){
//        return Optional.of(entry.getValue());
//      }
//    }
//    return Optional.empty();
//  }
//
//  public Optional<User> findById(UUID id){
//    return Optional.ofNullable(database.get(id));
//  }
//
//  public List<User> findAll(){
//    List<User> titles = new ArrayList<>();
//    for (Map.Entry<UUID, User> entry : database.entrySet()) {
//      titles.add(entry.getValue());
//    }
//    return titles;
//  }
//
//  public User deleteById(UUID id){
//    return database.remove(id);
//  }
//
//  public FileMetadata addFileMetadata(User user, FileMetadata fileMetadata){
//      user.getFiles().add(fileMetadata);
//      update(user);
//      return fileMetadata;
//  }
//
//  public User update(User user){
//    user.setUpdateDate(LocalDateTime.now());
//    save(user.getId(), user);
//    return user;
//  }
}