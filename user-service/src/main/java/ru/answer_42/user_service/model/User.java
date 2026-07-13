package ru.answer_42.user_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
  private UUID userId;
  private String login;
  private Role role;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt = LocalDateTime.now();
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updateDate = LocalDateTime.now();
}