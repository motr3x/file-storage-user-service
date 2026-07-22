package ru.answer_42.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.answer_42.user_service.dto.UserDetailsDto;
import ru.answer_42.user_service.dto.UserOrder;
import ru.answer_42.user_service.dto.UserRequestDto;
import ru.answer_42.user_service.dto.UserResponseDto;
import ru.answer_42.user_service.model.User;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    //Позволяет кидать json с незаполненными полями, при этом старые поля остаются неизменными
    //годная вещь для обновления данных пользователя, позволяет изменять только имя, при этом
    //не перезаписывать все остальные данные на нуль
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface UserMapper {
  UserResponseDto toUserResponseDto(User user);
  User toEntity(UserRequestDto userDto);
  void updateEntityFromDto(UserRequestDto userDto, @MappingTarget User user);
  UserOrder fromEntityToUserOrder(User user);
  UserDetailsDto fromEntityToUserDetailsDto(User user);
}