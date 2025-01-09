package com.storyteller.platform.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.storyteller.platform.exceptions.ResourceNotFoundException;
import com.storyteller.platform.models.MenuLevel;
import com.storyteller.platform.models.MenuStructure;
import com.storyteller.platform.repositories.MenuLevelRepository;

@Service
public class MenuService {

	@Autowired
	private MenuLevelRepository menuLevelRepository;

	public MenuLevel getActiveMenu() {
		return menuLevelRepository.findByActiveTrue()
				.orElseThrow(() -> new ResourceNotFoundException("No active menu found"));
	}

	public MenuLevel updateMenu(Long id, MenuStructure menuStructure) {
		MenuLevel menuLevel = menuLevelRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

		menuLevel.setMenuStructure(menuStructure.getItems());
		menuLevel.setUpdatedAt(LocalDateTime.now());
		return menuLevelRepository.save(menuLevel);
	}

	/**
	 * Check if a menu_level_id exists in the menu_structure JSON.
	 *
	 * @param menuLevelId The ID of the category (e.g., 211 for "3-5 años").
	 * @return True if the menu_level_id exists, false otherwise.
	 */
	public boolean isValidMenuLevelId(Long menuLevelId) {
		List<MenuLevel> menuLevels = menuLevelRepository.findAll();
		for (MenuLevel menuLevel : menuLevels) {
			JsonNode menuStructure = menuLevel.getMenuStructure();
			JsonNode items = menuStructure.path("items");
			if (findMenuLevelId(items, menuLevelId)) {
				return true;
			}
		}
		return false;
	}

	private boolean findMenuLevelId(JsonNode items, Long menuLevelId) {
		for (JsonNode item : items) {
			if (item.path("id").asLong() == menuLevelId) {
				return true;
			}
			if (item.has("children")) {
				if (findMenuLevelId(item.path("children"), menuLevelId)) {
					return true;
				}
			}
		}
		return false;
	}
}
