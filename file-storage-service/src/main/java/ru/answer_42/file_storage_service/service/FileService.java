package ru.answer_42.file_storage_service.service;


import java.util.List;
import java.util.UUID;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;

public interface FileService {

  /**
   * Создает новый файл
   * @param file - файл для создания
   */
  FileResponseDto save(FileRequestDto file);

  /**
   * Обновляет файл с заданным ID,
   * в соответствии с переданным файлом
   * @param fileDto - файл в соответствии с которым нужно обновить данные
   * @param id - id файла которой нужно обновить
   * @return - true если данные были обновлены, иначе false
   */
  FileResponseDto update(UUID id, FileRequestDto fileDto);

  /**
   * Удаляет файл с заданным ID
   * @param id - id файла, который нужно удалить
   * @return - true если файл был удален, иначе false
   */
  FileResponseDto deleteById(UUID id);

  /**
   * Возвращает список всех имеющихся файлов
   * @return список файлов
   */
  List<String> findAllTitles();

  /**
   * Возвращает файл по его ID
   * @param id - ID файла
   * @return - объект файла с заданным ID
   */
  FileResponseDto findById(UUID id);

}
