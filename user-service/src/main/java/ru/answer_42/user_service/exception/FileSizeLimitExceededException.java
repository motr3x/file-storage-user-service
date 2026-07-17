package ru.answer_42.user_service.exception;

public class FileSizeLimitExceededException extends RuntimeException {

  public FileSizeLimitExceededException(String message) {
    super(message);
  }
}
