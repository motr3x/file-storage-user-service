package ru.answer_42.file_storage_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "FILES")
public class File {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "file_id")
  private UUID id;
  private String title;
  private Type type;
  private Long size;
  private Status status;
  private UUID userId;
  private String downloadUrl;
  private byte[] file;
  @OneToMany(mappedBy = "file")
  private List<Comment> comments = new ArrayList<>();
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt = LocalDate.now();
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate updateDate = LocalDate.now();
}
