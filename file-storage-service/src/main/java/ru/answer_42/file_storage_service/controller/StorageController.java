package ru.answer_42.file_storage_service.controller;

import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.service.StorageService;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

  private final StorageService storageService;

  @PostMapping("/{userLogin}/upload")
  public ResponseEntity<FileResponseDto> handleFileUpload(@PathVariable String userLogin,
      @RequestParam("file") MultipartFile file) {
    FileResponseDto fileResponseDto = storageService.store(userLogin, file);
    return ResponseEntity.ok(fileResponseDto);
  }

  @GetMapping("/{filename:.+}")
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    byte[] file = storageService.loadAsResource(filename);

    if (file == null) {
      return ResponseEntity.notFound().build();
    }

    ByteArrayResource resource = new ByteArrayResource(file);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + filename + "\"").body(resource);
  }

  @GetMapping()
  public ResponseEntity<List<Path>> listUploadedFiles() {

    List<Path> list = storageService.loadAll();

    return ResponseEntity.ok(list);
  }

}
