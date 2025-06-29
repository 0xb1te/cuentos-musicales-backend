package com.storyteller.platform.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.storyteller.platform.exceptions.ResourceNotFoundException;
import com.storyteller.platform.models.InteractiveElement;
import com.storyteller.platform.models.Story;
import com.storyteller.platform.models.TeachingGuide;
import com.storyteller.platform.repositories.InteractiveElementRepository;
import com.storyteller.platform.repositories.StoryRepository;
import com.storyteller.platform.repositories.TeachingGuideRepository;

import jakarta.transaction.Transactional;

@Service
public class StoryService {

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private TeachingGuideRepository teachingGuideRepository;

	@Autowired
	private InteractiveElementRepository interactiveElementRepository;

	@Autowired
	private MenuService menuService;

	@Transactional
	public Story updateStory(Long id, Story updatedStory) {
		// Find the existing story by ID
		Story existingStory = storyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));

		// Update basic fields
		existingStory.setTitle(updatedStory.getTitle());
		existingStory.setDescription(updatedStory.getDescription());
		existingStory.setContent(updatedStory.getContent());
		existingStory.setPrice(updatedStory.getPrice());
		existingStory.setStripePriceId(updatedStory.getStripePriceId());
		existingStory.setPreviewContent(updatedStory.getPreviewContent());
		existingStory.setCoverImageUrl(updatedStory.getCoverImageUrl());
		existingStory.setImageUrl(updatedStory.getImageUrl());
		existingStory.setHasInteractiveElements(updatedStory.isHasInteractiveElements());
		existingStory.setMenuLevelId(updatedStory.getMenuLevelId()); // Now expects a List<Long>
		existingStory.setCustomPhrase(updatedStory.getCustomPhrase());
		existingStory.setAuthor(updatedStory.getAuthor());
		existingStory.setIsFree(updatedStory.getIsFree());
		existingStory.setAudioPreviewUrl(updatedStory.getAudioPreviewUrl());
		existingStory.setAudioFullUrl(updatedStory.getAudioFullUrl());
		existingStory.setIndicativeImage1(updatedStory.getIndicativeImage1());
		existingStory.setIndicativeImage2(updatedStory.getIndicativeImage2());
		existingStory.setDedicationImageUrl(updatedStory.getDedicationImageUrl());
		existingStory.setPresentationImageUrl(updatedStory.getPresentationImageUrl());
		existingStory.setEmotionalGuideUrl(updatedStory.getEmotionalGuideUrl());
		existingStory.setMusicalGuideUrl(updatedStory.getMusicalGuideUrl());
		existingStory.setAwakeningGuideUrl(updatedStory.getAwakeningGuideUrl());
		existingStory.setDuration(updatedStory.getDuration());
		
		// Update color theme fields
		existingStory.setBackgroundColor(updatedStory.getBackgroundColor());
		existingStory.setButtonsColor(updatedStory.getButtonsColor());
		existingStory.setTextColorButtons(updatedStory.getTextColorButtons());
		existingStory.setTextColor(updatedStory.getTextColor());
		existingStory.setContainerBackgroundColor(updatedStory.getContainerBackgroundColor());

		// Update TeachingGuide if it exists
		if (updatedStory.getTeachingGuide() != null) {
			TeachingGuide updatedTeachingGuide = updatedStory.getTeachingGuide();
			TeachingGuide existingTeachingGuide = existingStory.getTeachingGuide();

			if (existingTeachingGuide != null) {
				// Update existing TeachingGuide
				existingTeachingGuide.setPreview(updatedTeachingGuide.getPreview());
				existingTeachingGuide.setFullContent(updatedTeachingGuide.getFullContent());
				existingTeachingGuide.setDownloadUrl(updatedTeachingGuide.getDownloadUrl());
			} else {
				// Create new TeachingGuide and associate it with the story
				updatedTeachingGuide.setStory(existingStory);
				existingStory.setTeachingGuide(updatedTeachingGuide);
			}
		}

		// Update InteractiveElements if they exist
		if (updatedStory.getInteractiveElements() != null && !updatedStory.getInteractiveElements().isEmpty()) {
			// Delete existing interactive elements
			interactiveElementRepository.deleteById(id);

			// Add new interactive elements
			for (InteractiveElement interactiveElement : updatedStory.getInteractiveElements()) {
				interactiveElement.setStory(existingStory);
				interactiveElementRepository.save(interactiveElement);
			}
		}

		// Save the updated story
		return storyRepository.save(existingStory);
	}

	@Transactional
	public void deleteStory(Long id) {
		// Find the existing story by ID
		Story existingStory = storyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));

		// Delete associated TeachingGuide (if it exists)
		if (existingStory.getTeachingGuide() != null) {
			// Remove the TeachingGuide from the Story to avoid orphaned records
			existingStory.setTeachingGuide(null);
			teachingGuideRepository.deleteById(existingStory.getTeachingGuide().getId());
		}

		// Delete associated InteractiveElements (if they exist)
		if (existingStory.getInteractiveElements() != null && !existingStory.getInteractiveElements().isEmpty()) {
			interactiveElementRepository.deleteAll(existingStory.getInteractiveElements());
		}

		// Delete the story
		storyRepository.delete(existingStory);
	}

	public List<Story> getAllStories() {
		return storyRepository.findAll();
	}

	/**
	 * Get stories by menu_level_id.
	 *
	 * @param menuLevelId The ID of the category (e.g., 211 for "3-5 años").
	 * @return A list of stories filtered by the menu_level_id.
	 */
	public List<Story> getStoriesByMenuLevelId(Long menuLevelId) {
		if (!menuService.isValidMenuLevelId(menuLevelId)) {
			return Collections.emptyList(); // Invalid menu_level_id
		}
		return storyRepository.findByMenuLevelId(menuLevelId);
	}

	public Story getStoryBySlug(String slug) {
		return storyRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Story not found"));
	}

	public Story getStoryById(Long id) {
		return storyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Story not found"));
	}

	public Story createStory(Story story) {
		story.setSlug(generateSlug(story.getTitle()));
		return storyRepository.save(story);
	}

	private String generateSlug(String title) {
		return title.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
	}
}