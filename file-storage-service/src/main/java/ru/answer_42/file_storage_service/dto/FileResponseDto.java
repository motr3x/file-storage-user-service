package ru.answer_42.file_storage_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

@Data
public class FileResponseDto {

  private UUID id;
  private String title;
  private Type type;
  private Long size;
  private Status status;
  private UUID userId;
  private String userLogin;
  private String downloadUrl;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate updateDate;
}
