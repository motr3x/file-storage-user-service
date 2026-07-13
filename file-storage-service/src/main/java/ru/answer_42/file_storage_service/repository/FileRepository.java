package ru.answer_42.file_storage_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.answer_42.file_storage_service.model.File;
import ru.answer_42.file_storage_service.model.Type;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

  Optional<File> findByUserIdAndTitle(UUID userId, String title);

  List<File> findAllByUserId(UUID userId);

  Optional<File> findByTitle(String title);

  Optional<File> findByDownloadUrl(String url);

  Optional<File> findByUserIdAndId(UUID userId, UUID fileId);

  @Query("SELECT f FROM File f WHERE "
      + "(f.userId = :#{#requestUserId}) "
      + "AND  ((:#{#requestTitle} IS NULL) OR f.title LIKE CONCAT('%',:#{#requestTitle},'%')) "
      + "AND (:#{#startDate} IS NULL OR f.createdAt > :#{#startDate}) "
      + "AND (:#{#endData} IS NULL OR f.createdAt < :#{#endData}) "
      + "AND (:#{#requestType} IS NULL OR f.type = :#{#requestType})")
  List<File> findAllWithFilter(
      @Param("requestUserId") UUID userId,
      @Param("requestTitle") String title,
      @Param("startDate")LocalDate start,
      @Param("endData")LocalDate end,
      @Param("requestType") String type);
}
