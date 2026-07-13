package ru.answer_42.file_storage_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDto {

  private String title;
  private Type type;
  private Long size;
  private Status status;
  private UUID userId;
  private String downloadUrl;
  private byte[] file;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate updateDate;
}
