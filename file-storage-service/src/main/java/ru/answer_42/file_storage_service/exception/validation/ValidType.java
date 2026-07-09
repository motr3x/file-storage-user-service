package ru.answer_42.file_storage_service.exception.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTypeValidator.class)
@Documented
public @interface ValidType {

  Class<? extends Enum<?>> enumClass();

  String message() default "Invalid type. Should be  PDF/JPEG/DOX/TXT/PNG.";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

}