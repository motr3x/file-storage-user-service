package ru.answer_42.file_storage_service.exception;

public class UnsupportedFileTypeException extends RuntimeException {

  public UnsupportedFileTypeException(String message) {
    super(message);
  }
}
