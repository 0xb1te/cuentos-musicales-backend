package com.storyteller.platform.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyteller.platform.models.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {
	Optional<Story> findBySlug(String slug);
}