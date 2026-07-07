package ru.answer_42.file_storage_service.exception.storage;

public class StorageStoreFailedException extends RuntimeException {

  public StorageStoreFailedException(String message) {
    super(message);
  }
}
