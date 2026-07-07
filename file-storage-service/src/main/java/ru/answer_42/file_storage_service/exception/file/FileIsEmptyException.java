package ru.answer_42.file_storage_service.exception.file;

public class FileIsEmptyException extends RuntimeException {

  public FileIsEmptyException(String message) {
    super(message);
  }
}
