package ru.answer_42.file_storage_service.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.answer_42.file_storage_service.exception.file.FileHasVirusException;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileTypeException;
import ru.answer_42.file_storage_service.exception.storage.StorageInitFailedException;
import ru.answer_42.file_storage_service.exception.storage.StorageLocationEmptyException;
import ru.answer_42.file_storage_service.exception.storage.StorageReadFailedException;
import ru.answer_42.file_storage_service.exception.storage.StorageStoreFailedException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Response> handleException(ResourceNotFoundException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnsupportedFileTypeException.class)
  public ResponseEntity<Response> handleException(UnsupportedFileTypeException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FileSizeLimitExceededException.class)
  public ResponseEntity<Response> handleException(FileSizeLimitExceededException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.CONTENT_TOO_LARGE);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Response> handleException(AccessDeniedException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.CONTENT_TOO_LARGE);
  }

  @ExceptionHandler(FileIsUnderScanException.class)
  public ResponseEntity<Response> handleException(FileIsUnderScanException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(FileIsEmptyException.class)
  public ResponseEntity<Response> handleException(FileIsEmptyException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FileHasVirusException.class)
  public ResponseEntity<Response> handleException(FileHasVirusException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(StorageInitFailedException.class)
  public ResponseEntity<Response> handleException(StorageInitFailedException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StorageLocationEmptyException.class)
  public ResponseEntity<Response> handleException(StorageLocationEmptyException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StorageReadFailedException.class)
  public ResponseEntity<Response> handleException(StorageReadFailedException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StorageStoreFailedException.class)
  public ResponseEntity<Response> handleException(StorageStoreFailedException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
