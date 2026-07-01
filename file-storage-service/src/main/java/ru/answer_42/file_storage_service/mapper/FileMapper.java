package ru.answer_42.file_storage_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.answer_42.file_storage_service.dto.FileRequestDto;
import ru.answer_42.file_storage_service.dto.FileResponseDto;
import ru.answer_42.file_storage_service.model.File;

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
  FileRequestDto toFileDto(File file);
  File toEntity(FileRequestDto fileDto);
  void updateEntityFromDto(FileRequestDto fileDto, @MappingTarget File file);
}
