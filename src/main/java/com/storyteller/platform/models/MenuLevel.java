package com.storyteller.platform.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "menu_levels")
public class MenuLevel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Type(JsonType.class) // Use JsonType from hibernate-types-60
	@Column(columnDefinition = "jsonb")
	private JsonNode menuStructure;

	private boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}