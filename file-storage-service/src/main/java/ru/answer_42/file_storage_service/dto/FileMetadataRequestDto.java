package ru.answer_42.file_storage_service.dto;

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
public class FileMetadataRequestDto {

  private String title;
  private Type type;
  private Long size;
  private Status status;
  private String userLogin;
  private String downloadUrl;
  private byte[] file;
}
