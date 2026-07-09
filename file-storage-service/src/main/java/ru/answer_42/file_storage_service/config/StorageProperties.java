package ru.answer_42.file_storage_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix="storage")
public class StorageProperties {

  private String location;

}