package com.storyteller.platform.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "http://localhost:4200")
public class StoryController {

	@Autowired
	private StoryService storyService;

	@GetMapping
	public ResponseEntity<List<Story>> getAllStories() {
		return ResponseEntity.ok(storyService.getAllStories());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Story> getStoryById(@PathVariable Long id) {
		return ResponseEntity.ok(storyService.getStoryById(id));
	}

	@PostMapping
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<Story> createStory(@RequestBody Story story) {
		Story createdStory = storyService.createStory(story);
		return ResponseEntity.ok(createdStory);
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<Story> updateStory(@PathVariable Long id, @RequestBody Story story) {
		Story updatedStory = storyService.updateStory(id, story);
		return ResponseEntity.ok(updatedStory);
	}

	@DeleteMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
		storyService.deleteStory(id);
		return ResponseEntity.noContent().build();
	}
}