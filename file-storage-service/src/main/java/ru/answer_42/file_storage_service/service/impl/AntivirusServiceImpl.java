package ru.answer_42.file_storage_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.service.AntivirusService;

@Service
@RequiredArgsConstructor
public class AntivirusServiceImpl implements AntivirusService {

  @Override
  public boolean scan(MultipartFile file) {
    return true;
  }
}
