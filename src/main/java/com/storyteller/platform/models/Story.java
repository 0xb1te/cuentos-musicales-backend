package com.storyteller.platform.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference; // Import JsonManagedReference

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	private String imageUrl;

	private boolean hasInteractiveElements;

	@OneToOne(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference // Marks this as the "parent" side of the relationship
	private TeachingGuide teachingGuide;

	@OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore // Ignore this field during serialization
	private List<InteractiveElement> interactiveElements;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Long menuLevelId;
}