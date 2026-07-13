package ru.answer_42.file_storage_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.answer_42.file_storage_service.dto.FileMetadataOrder;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, FileMetadataOrder> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, FileMetadataOrder> tpl = new RedisTemplate<>();
    tpl.setConnectionFactory(cf);
    tpl.setKeySerializer(new StringRedisSerializer());
    tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return tpl;
  }
}
