package ru.answer_42.file_storage_service.exception.file;

public class FileIsUnderScanException extends RuntimeException {

  public FileIsUnderScanException(String message) {
    super(message);
  }
}
