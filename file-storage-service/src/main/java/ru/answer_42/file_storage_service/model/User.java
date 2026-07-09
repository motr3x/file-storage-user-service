package ru.answer_42.file_storage_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CUSTOM_USER")
public class User {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;
  private String login;
  private Role role;
  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
  private List<File> files = new ArrayList<>();
}
