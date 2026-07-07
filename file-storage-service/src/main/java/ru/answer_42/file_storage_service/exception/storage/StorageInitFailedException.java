package ru.answer_42.file_storage_service.exception.storage;

public class StorageInitFailedException extends RuntimeException {

  public StorageInitFailedException(String message) {
    super(message);
  }
}
