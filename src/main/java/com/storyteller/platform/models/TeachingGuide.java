package com.storyteller.platform.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference; // Import JsonBackReference

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "teaching_guides")
public class TeachingGuide {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "story_id", nullable = false)
	@JsonBackReference // Marks this as the "child" side of the relationship
	private Story story;

	@Column(columnDefinition = "TEXT")
	private String preview;

	@Column(columnDefinition = "TEXT")
	private String fullContent;

	private String downloadUrl;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}