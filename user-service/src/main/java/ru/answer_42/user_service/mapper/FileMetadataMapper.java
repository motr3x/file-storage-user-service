package ru.answer_42.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.answer_42.user_service.dto.FileDownloadDto;
import ru.answer_42.user_service.dto.FileMetadataDto;
import ru.answer_42.user_service.model.FileMetadata;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    //Позволяет кидать json с незаполненными полями, при этом старые поля остаются неизменными
    //годная вещь для обновления данных пользователя, позволяет изменять только имя, при этом
    //не перезаписывать все остальные данные на нуль
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface FileMetadataMapper {
  FileMetadataDto toFileMetadataDto(FileMetadata fileMetadata);
  FileMetadata toEntity(FileMetadataDto fileMetadataDto);
  FileDownloadDto toFileDownloadDto(FileMetadata fileMetadata);
}
