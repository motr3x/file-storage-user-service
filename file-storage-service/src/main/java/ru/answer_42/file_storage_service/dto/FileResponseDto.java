package ru.answer_42.file_storage_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updateDate;
}
