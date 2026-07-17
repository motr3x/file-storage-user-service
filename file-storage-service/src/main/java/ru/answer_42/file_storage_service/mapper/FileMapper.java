package ru.answer_42.file_storage_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.answer_42.file_storage_service.dto.FileOrder;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.RemovedFiles;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    //Позволяет кидать json с незаполненными полями, при этом старые поля остаются неизменными
    //годная вещь для обновления данных пользователя, позволяет изменять только имя, при этом
    //не перезаписывать все остальные данные на нуль
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface FileMapper {

  FileResponseDto toFileResponseDto(File file);

  FileOrder toFileMetadataOrderFromFile(File file);

  FileOrder toFileMetadataOrderFromFileResponseDto(FileResponseDto fileResponseDto);

  FileRequestDto toFileRequestDtoFromFileResponseDto(
      FileResponseDto fileResponseDto);

  File toEntity(FileRequestDto fileDto);

  RemovedFiles toRemovedFileFromEntity(File file);

  void updateEntityFromDto(FileRequestDto fileDto, @MappingTarget File file);
}
