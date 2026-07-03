package ru.answer_42.file_storage_service.exception;

public class FileHasVirusException extends RuntimeException {

  public FileHasVirusException(String message) {
    super(message);
  }
}
