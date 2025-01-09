package com.storyteller.platform.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "stories")
public class Story {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String slug;
	@Column(columnDefinition = "TEXT")
	private String description;
	@Column(columnDefinition = "TEXT")
	private String content;
	private BigDecimal price;
	private String stripePriceId;
	@Column(columnDefinition = "TEXT")
	private String previewContent;
	private String coverImageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long menuLevelId;
}
