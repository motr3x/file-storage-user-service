package ru.answer_42.file_storage_service.dto;

import static ru.answer_42.file_storage_service.service.impl.StorageServiceImpl.MAX_SIZE;
import static ru.answer_42.file_storage_service.service.impl.StorageServiceImpl.MIN_SIZE;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.answer_42.file_storage_service.exception.validation.ValidStatus;
import ru.answer_42.file_storage_service.exception.validation.ValidType;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileRequestDto {

  @NotBlank(message = "File should has title")
  @Size(max=255, message = "file title should be no more then 255")
  private String title;
  @ValidType(enumClass = Type.class)
  @NotNull(message = "File should has type")
  private String type;
  @Min(value = MIN_SIZE, message = "File shouldn't be empty")
  @Max(value = MAX_SIZE, message = "File is too large")
  @NotNull(message = "File shouldn't be empty")
  private Long size;
  @ValidStatus(enumClass = Status.class)
  @NotNull(message = "File should has a status")
  private String status;
  @NotBlank(message = "File should has an owner")
  private UUID userId;
  @NotEmpty(message = "File should be")
  private byte[] file;
}
