package ru.answer_42.file_storage_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
  private UUID id;
  private UUID authorId;
  private UUID fileId;
  private String text;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt = LocalDateTime.now();
}
