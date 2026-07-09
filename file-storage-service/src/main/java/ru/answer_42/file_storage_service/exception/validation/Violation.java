package ru.answer_42.file_storage_service.exception.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Violation {

  private final String fieldName;
  private final String message;

}