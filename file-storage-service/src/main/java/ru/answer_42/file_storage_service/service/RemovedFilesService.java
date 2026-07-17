package ru.answer_42.file_storage_service.service;

import ru.answer_42.file_storage_service.model.RemovedFiles;

public interface RemovedFilesService {
    void save(RemovedFiles removedFiles);
}
