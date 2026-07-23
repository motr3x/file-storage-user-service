package ru.answer_42.user_service.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import ru.answer_42.user_service.dto.FileDownloadDto;

public interface FileDownloadService {
    CompletableFuture<FileDownloadDto> getFileDownloadDto(UUID userId, UUID fileId);
}
