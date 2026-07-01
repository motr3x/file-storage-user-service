package ru.answer_42.file_storage_service.model;

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
public class File {
  private UUID id;
  private String title;
  private Type type;
  private Long size;
  private Status status;
  private UUID userId;
  private List<Comment> comments = new ArrayList<>();
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt = LocalDateTime.now();
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updateDate = LocalDateTime.now();
  /*
  comment
   */
}
