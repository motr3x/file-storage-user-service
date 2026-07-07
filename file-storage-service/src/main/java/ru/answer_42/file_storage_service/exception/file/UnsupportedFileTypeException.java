package ru.answer_42.file_storage_service.exception.file;

public class UnsupportedFileTypeException extends RuntimeException {

  public UnsupportedFileTypeException(String message) {
    super(message);
  }
}
