package ru.answer_42.file_storage_service.exception.storage;

public class StorageLocationEmptyException extends RuntimeException {

  public StorageLocationEmptyException(String message) {
    super(message);
  }
}
