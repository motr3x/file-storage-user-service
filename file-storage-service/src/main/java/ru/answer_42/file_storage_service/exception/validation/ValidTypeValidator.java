package ru.answer_42.file_storage_service.exception.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.answer_42.file_storage_service.model.Type;

public class ValidTypeValidator implements ConstraintValidator<ValidType, String> {


  @Override
  public boolean isValid(String type, ConstraintValidatorContext constraintValidatorContext) {
    if(type == null){
      return true;
    }
    try{
      Type.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e){
      return false;
    }
    return true;
  }
}
