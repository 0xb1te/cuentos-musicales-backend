package com.storyteller.platform.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
