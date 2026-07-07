package ru.answer_42.file_storage_service.exception.storage;

public class StorageReadFailedException extends RuntimeException {

  public StorageReadFailedException(String message) {
    super(message);
  }
}
