package ru.answer_42.file_storage_service.exception.file;

public class FileSizeLimitExceededException extends RuntimeException {

  public FileSizeLimitExceededException(String message) {
    super(message);
  }
}
