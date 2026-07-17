package ru.answer_42.user_service.service.impl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.exception.AccessDeniedException;
import ru.answer_42.user_service.exception.FileSizeLimitExceededException;
import ru.answer_42.user_service.service.FileDownloadService;
import ru.answer_42.user_service.service.FileOrderService;

@Service
@RequiredArgsConstructor
public class FileDownloadServiceImpl implements FileDownloadService {
  private final Long MAX_SIZE = 1024 * 1024 * 1024L;
  private final WebClient webClient;
  private final FileOrderService fileOrderService;

  public CompletableFuture<FileDownloadDto> getFileDownloadDto(UUID userId, UUID fileId){

    if(!fileOrderService.ownerCheck(userId, fileId)){
      throw new AccessDeniedException("User with id: " + userId + " not own file with id: " + fileId);
    }

    CompletableFuture<String> fetchFileDownloadUrl = getFileDownloadUrl(userId, fileId);
    CompletableFuture<Long> fetchFileSize = getFileSize(userId, fileId);

    return CompletableFuture.allOf(fetchFileDownloadUrl, fetchFileSize).thenApplyAsync(ignore -> {
      Long fileSize = fetchFileSize.join();
      if(fileSize > MAX_SIZE){
        throw new FileSizeLimitExceededException("File with id: " + fileId +  " is too large");
      }
      String fileDownloadUrl = fetchFileDownloadUrl.join();
      String title = fileOrderService.findById(fileId).getTitle();
      return new FileDownloadDto(title, fileSize, fileDownloadUrl);
    });
  }

  public CompletableFuture<String> getFileDownloadUrl(final UUID userId, final UUID fileId) {
    return webClient
        .get()
        .uri(String.join("/", "/downloadUrl", userId.toString(), fileId.toString()))
        .retrieve()
        .bodyToMono(String.class)
        .toFuture();
  }
  public CompletableFuture<Long> getFileSize(final UUID userId, final UUID fileId) {
    return webClient
        .get()
        .uri(String.join("/", "/fileSize", userId.toString(), fileId.toString()))
        .retrieve()
        .bodyToMono(Long.class)
        .toFuture();
  }
}


