package ru.answer_42.file_storage_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {

  private UUID id;
  private String title;
  private Type type;
  private Long size;
  private Status status;
  private UUID userId;
  private String userLogin;
  private String downloadUrl;
  private byte[] file;
  private List<Comment> comments = new ArrayList<>();
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt = LocalDate.now();
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate updateDate = LocalDate.now();
}
