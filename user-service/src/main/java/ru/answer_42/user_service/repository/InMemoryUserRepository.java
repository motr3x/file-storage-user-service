package ru.answer_42.user_service.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import ru.answer_42.user_service.model.User;

@Repository
public class InMemoryUserRepository {

  private final Map<UUID, User> database = new HashMap<>();

  public User save(UUID id, User user){
    user.setId(id);
    database.put(id, user);
    return user;
  }

  public Optional<User> findById(UUID id){
    return Optional.ofNullable(database.get(id));
  }

  public List<User> findAll(){
    List<User> titles = new ArrayList<>();
    for (Map.Entry<UUID, User> entry : database.entrySet()) {
      titles.add(entry.getValue());
    }
    return titles;
  }

  public User deleteById(UUID id){
    return database.remove(id);
  }

  public User update(User user){
    user.setUpdateDate(LocalDateTime.now());
    return user;
  }
}