package ru.answer_42.file_storage_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface AntivirusService {
  boolean scan(MultipartFile file);
}
