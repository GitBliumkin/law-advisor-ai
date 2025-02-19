package com.shared.basecrud.tables;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public class BaseTable {
  @Id
  @Column(columnDefinition = "UUID", nullable = false, updatable = false)
  private String id;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdOn;

  @Column(columnDefinition = "UUID", nullable = false, updatable = false)
  private String createdBy = new UUID(0L, 0L).toString();

  @Column private LocalDateTime updatedOn;

  @Column(columnDefinition = "UUID")
  private String updatedBy;

  @Column private LocalDateTime deletedOn;

  @Column(columnDefinition = "UUID")
  private String deletedBy;

  @PrePersist
  public void prePersist() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }

    LocalDateTime currentTime = LocalDateTime.now();
    if (createdOn == null) {
      createdOn = currentTime;
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public LocalDateTime getDeletedOn() {
    return deletedOn;
  }

  public void setDeletedOn(LocalDateTime deletedOn) {
    this.deletedOn = deletedOn;
  }

  public String getDeletedBy() {
    return deletedBy;
  }

  public void setDeletedBy(String deletedBy) {
    this.deletedBy = deletedBy;
  }
}
