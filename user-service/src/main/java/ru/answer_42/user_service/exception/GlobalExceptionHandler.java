package ru.answer_42.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Response> handleException(ResourceNotFoundException e) {
    Response response = new Response(e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }
}
