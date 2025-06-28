package com.storyteller.platform.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.storyteller.platform.models.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {
	Optional<Story> findBySlug(String slug);

	@Query(value = "SELECT * FROM stories WHERE :menuLevelId = ANY(menu_level_id::bigint[])", nativeQuery = true)
	List<Story> findByMenuLevelId(@Param("menuLevelId") Long menuLevelId);
}