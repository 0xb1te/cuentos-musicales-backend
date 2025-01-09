package com.storyteller.platform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyteller.platform.models.InteractiveElement;

public interface InteractiveElementRepository extends JpaRepository<InteractiveElement, Long> {
}