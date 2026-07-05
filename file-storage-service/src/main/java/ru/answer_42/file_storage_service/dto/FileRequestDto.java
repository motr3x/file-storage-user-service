package ru.answer_42.file_storage_service.dto;

import lombok.Data;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

@Data
public class FileRequestDto {

  private String title;
  private Type type;
  private Long size;
  private String userLogin;
  private Status status;
  private String downloadUrl;
}
