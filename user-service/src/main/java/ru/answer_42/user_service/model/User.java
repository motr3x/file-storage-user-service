package ru.answer_42.user_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private UUID id;
  private String login;
  private Role role;
  private List<FileMetadata> files = new ArrayList<>();
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt = LocalDateTime.now();
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updateDate = LocalDateTime.now();
}