package ru.answer_42.user_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {
  private UUID id;
  private String title;
  private Type type;
  private Long size;
  private String userLogin;
  private UUID userId;
  private Status status;
  private String downloadUrl;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate updateDate;
}
