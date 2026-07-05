package ru.answer_42.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.answer_42.user_service.model.Status;
import ru.answer_42.user_service.model.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDto {
  private String title;
  private Type type;
  private Long size;
  private String userLogin;
  private Status status;
  private String downloadUrl;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate updateDate;
}
