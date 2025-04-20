package com.pht.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "metafiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetafileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@Column(nullable = false)
	@NotBlank(message = "Original file name size can not be blank")
	String originalFileName;

	@Column(nullable = false)
	@NotBlank(message = "Stored file name can not be blank")
	String storedFileName;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "File path can not be blank")
	String filePath;

	@Column(nullable = false)
	@NotBlank(message = "File content type (MIME type) can not be blank")
	String contentType;
}