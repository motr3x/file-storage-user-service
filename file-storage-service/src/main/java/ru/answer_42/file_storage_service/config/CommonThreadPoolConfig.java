package ru.answer_42.file_storage_service.config;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonThreadPoolConfig {
  @Bean(name = "commonExecutor")
  public ExecutorService commonExecutor() {
    return Executors.newFixedThreadPool(10);
  }
  @PreDestroy
  public void shutdownExecutor() {
    ExecutorService executor = commonExecutor();
    if (executor != null) {
      executor.shutdown();
    }
  }
}
