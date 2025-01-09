package com.storyteller.platform.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.storyteller.platform.exceptions.ResourceNotFoundException;
import com.storyteller.platform.models.Story;
import com.storyteller.platform.repositories.StoryRepository;

@Service
public class StoryService {

	@Autowired
	private StoryRepository storyRepository;

	public List<Story> getAllStories() {
		return storyRepository.findAll();
	}

	public Story getStoryBySlug(String slug) {
		return storyRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Story not found"));
	}

	public Story createStory(Story story) {
		story.setSlug(generateSlug(story.getTitle()));
		return storyRepository.save(story);
	}

	private String generateSlug(String title) {
		return title.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
	}
}
