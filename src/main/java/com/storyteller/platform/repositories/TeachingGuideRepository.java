package com.storyteller.platform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyteller.platform.models.TeachingGuide;

public interface TeachingGuideRepository extends JpaRepository<TeachingGuide, Long> {
}