package ru.answer_42.file_storage_service.exception.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import ru.answer_42.file_storage_service.model.Status;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidStatusValidator.class)
@Documented
public @interface ValidStatus {

  Class<? extends Enum<?>> enumClass();

  String message() default "Invalid status. Should be IN_PROCESS/READY/NOT_START/UPLOAD.";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

}