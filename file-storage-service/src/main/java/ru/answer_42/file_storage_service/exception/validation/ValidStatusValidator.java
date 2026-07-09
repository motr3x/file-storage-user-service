package ru.answer_42.file_storage_service.exception.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.answer_42.file_storage_service.model.Status;
import ru.answer_42.file_storage_service.model.Type;

public class ValidStatusValidator implements ConstraintValidator<ValidStatus, String> {

  @Override
  public boolean isValid(String status, ConstraintValidatorContext constraintValidatorContext) {
    if(status == null){
      return true;
    }
    try{
      Status.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e){
      return false;
    }
    return true;
  }
}