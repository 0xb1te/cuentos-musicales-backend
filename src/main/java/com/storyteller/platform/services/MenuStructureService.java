package com.storyteller.platform.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.storyteller.platform.dtos.MenuStructureDTO;
import com.storyteller.platform.models.MenuLevel;
import com.storyteller.platform.repositories.MenuLevelRepository;

@Service
public class MenuStructureService {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuStructureService.class);
    
    @Autowired
    private MenuLevelRepository menuLevelRepository;
    
    @Transactional(readOnly = true)
    public MenuStructureDTO getActiveMenuStructure() {
        MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
            .orElseThrow(() -> new RuntimeException("No active menu structure found"));
        
        MenuStructureDTO dto = new MenuStructureDTO();
        dto.setId(menuLevel.getId());
        dto.setMenuStructure(menuLevel.getMenuStructure());
        dto.setActive(menuLevel.isActive());
        
        return dto;
    }
    
    @Transactional
    public MenuStructureDTO updateMenuStructure(MenuStructureDTO menuStructureDTO) {
        logger.info("Updating menu structure with id: {}", menuStructureDTO.getId());
        
        Optional<MenuLevel> menuLevelOpt = menuLevelRepository.findById(menuStructureDTO.getId());
        if (!menuLevelOpt.isPresent()) {
            throw new RuntimeException("Menu structure not found with id: " + menuStructureDTO.getId());
        }
        
        MenuLevel menuLevel = menuLevelOpt.get();
        menuLevel.setMenuStructure(menuStructureDTO.getMenuStructure());
        menuLevel.setUpdatedAt(LocalDateTime.now());
        
        MenuLevel savedMenuLevel = menuLevelRepository.save(menuLevel);
        
        MenuStructureDTO savedDto = new MenuStructureDTO();
        savedDto.setId(savedMenuLevel.getId());
        savedDto.setMenuStructure(savedMenuLevel.getMenuStructure());
        savedDto.setActive(savedMenuLevel.isActive());
        
        return savedDto;
    }
} 