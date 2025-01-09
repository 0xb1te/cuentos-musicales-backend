package com.storyteller.platform.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.platform.models.Story;
import com.storyteller.platform.services.StoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

	@Autowired
	private StoryService storyService;

	@GetMapping
	public ResponseEntity<List<Story>> getAllStories() {
		// Return all stories
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{slug}")
	public ResponseEntity<Story> getStoryBySlug(@PathVariable String slug) {
		return ResponseEntity.ok(storyService.getStoryBySlug(slug));
	}

	@PostMapping
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<?> createStory(@RequestBody Story story) {
		// Create new story
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<?> updateStory(@PathVariable Long id, @RequestBody Story story) {
		// Update story
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<?> deleteStory(@PathVariable Long id) {
		// Delete story
		return ResponseEntity.ok().build();
	}
}
