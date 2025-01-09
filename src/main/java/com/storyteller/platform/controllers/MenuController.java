package com.storyteller.platform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.platform.models.MenuStructure;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

	@GetMapping
	public ResponseEntity<?> getMenuStructure() {
		// Return active menu structure
		return ResponseEntity.ok().build();
	}

	@PostMapping
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<?> createMenu(@RequestBody MenuStructure menu) {
		// Create new menu structure
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<?> updateMenu(@PathVariable Long id, @RequestBody MenuStructure menu) {
		// Update menu structure
		return ResponseEntity.ok().build();
	}
}