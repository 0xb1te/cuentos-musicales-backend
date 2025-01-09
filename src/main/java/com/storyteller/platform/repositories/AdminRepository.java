package com.storyteller.platform.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyteller.platform.models.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	Optional<Admin> findByUsername(String username);
}