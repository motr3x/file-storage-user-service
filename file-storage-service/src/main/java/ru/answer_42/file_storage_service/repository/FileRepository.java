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

  Optional<File> findByUserLoginAndTitle(String login, String title);

  List<File> findAllByUserLogin(String login);

  Optional<File> findByTitle(String title);

  Optional<File> findByDownloadUrl(String url);

  Optional<File> findByUserLoginAndId(String login, UUID id);

  @Query("SELECT f FROM File f WHERE "
      + "(f.userLogin = :#{#requestLogin}) "
      + "AND  ((:#{#requestTitle} IS NULL) OR f.title LIKE CONCAT('%',:#{#requestTitle},'%')) "
      + "AND (:#{#startDate} IS NULL OR f.createdAt > :#{#startDate}) "
      + "AND (:#{#endData} IS NULL OR f.createdAt < :#{#endData}) "
      + "AND (:#{#requestType} IS NULL OR CAST(f.type AS STRING) = :#{#requestType})")
  List<File> findAllWithFilter(
      @Param("requestLogin") String login,
      @Param("requestTitle") String title,
      @Param("startDate")LocalDate start,
      @Param("endData")LocalDate end,
      @Param("requestType") String type);
}
