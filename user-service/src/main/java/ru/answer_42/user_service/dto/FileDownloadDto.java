package ru.answer_42.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadDto {
  private String fileName;
  private Long size;
  private String downloadUrl;
}
