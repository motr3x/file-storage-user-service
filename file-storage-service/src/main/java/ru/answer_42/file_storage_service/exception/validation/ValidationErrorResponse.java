package ru.answer_42.file_storage_service.exception.validation;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
  private final List<Violation> violations;
}
