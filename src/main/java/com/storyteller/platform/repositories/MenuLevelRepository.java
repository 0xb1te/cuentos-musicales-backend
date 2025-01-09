package com.storyteller.platform.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyteller.platform.models.MenuLevel;

public interface MenuLevelRepository extends JpaRepository<MenuLevel, Long> {
	Optional<MenuLevel> findByActiveTrue();
}