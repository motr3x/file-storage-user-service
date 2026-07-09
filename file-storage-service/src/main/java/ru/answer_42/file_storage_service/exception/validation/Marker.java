package ru.answer_42.file_storage_service.exception.validation;

public interface Marker {

  interface OnCreate extends jakarta.validation.groups.Default {}

  interface OnUpdate extends jakarta.validation.groups.Default {}

}