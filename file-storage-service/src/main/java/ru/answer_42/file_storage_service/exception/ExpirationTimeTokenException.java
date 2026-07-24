package ru.answer_42.file_storage_service.exception;

public class ExpirationTimeTokenException extends RuntimeException {
  public ExpirationTimeTokenException(String message) {
    super(message);
  }
}
