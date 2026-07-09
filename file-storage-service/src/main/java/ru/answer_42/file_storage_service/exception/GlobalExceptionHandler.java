package ru.answer_42.file_storage_service.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.answer_42.file_storage_service.exception.file.FileHasVirusException;
import ru.answer_42.file_storage_service.exception.file.FileIsEmptyException;
import ru.answer_42.file_storage_service.exception.file.FileIsUnderScanException;
import ru.answer_42.file_storage_service.exception.file.UnsupportedFileTypeException;
import ru.answer_42.file_storage_service.exception.storage.StorageInitFailedException;
import ru.answer_42.file_storage_service.exception.storage.StorageLocationEmptyException;
import ru.answer_42.file_storage_service.exception.storage.StorageReadFailedException;
import ru.answer_42.file_storage_service.exception.storage.StorageStoreFailedException;
import ru.answer_42.file_storage_service.exception.validation.ValidationErrorResponse;
import ru.answer_42.file_storage_service.exception.validation.Violation;

@ControllerAdvice
public class GlobalExceptionHandler {
//
//  @ExceptionHandler(Exception.class)
//  public Response handleException(Exception e) {
//    Response response = new Response(e.getMessage());
//    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//  }

//  @ExceptionHandler(HttpMessageConversionException.class)
//  public Response handleException(HttpMessageConversionException e) {
//    if(e.getCause() instanceof HttpMessageNotReadableException){
//
//    }
//    Response response = new Response(e.getMessage());
//    return new ResponseEntity<>(response, HttpStatus.CONTENT_TOO_LARGE);
//  }

  
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationErrorResponse onConstraintValidationException(
      ConstraintViolationException e
  ) {
    final List<Violation> violations = e.getConstraintViolations().stream()
        .map(
            violation -> {
              String violationPath = violation.getPropertyPath().toString();
              String correctField = violationPath.substring(violationPath.lastIndexOf(".")+1);
              return new Violation(
                  correctField,
                  violation.getMessage()
              );
            }
        )
        .collect(Collectors.toList());
    return new ValidationErrorResponse(violations);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse onMethodArgumentNotValidException(
      MethodArgumentNotValidException e
  ) {
    final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
        .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
        .collect(Collectors.toList());
    return new ValidationErrorResponse(violations);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public Response handleException(ResourceNotFoundException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(UnsupportedFileTypeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Response handleException(UnsupportedFileTypeException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(FileSizeLimitExceededException.class)
  @ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
  @ResponseBody
  public Response handleException(FileSizeLimitExceededException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
  @ResponseBody
  public Response handleException(AccessDeniedException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(FileIsUnderScanException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public Response handleException(FileIsUnderScanException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(FileIsEmptyException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Response handleException(FileIsEmptyException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(FileHasVirusException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public Response handleException(FileHasVirusException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(StorageInitFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Response handleException(StorageInitFailedException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(StorageLocationEmptyException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Response handleException(StorageLocationEmptyException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(StorageReadFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Response handleException(StorageReadFailedException e) {
    return new Response(e.getMessage());
  }

  @ExceptionHandler(StorageStoreFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Response handleException(StorageStoreFailedException e) {
    return new Response(e.getMessage());
  }
}
