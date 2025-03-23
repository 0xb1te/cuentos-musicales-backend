package com.storyteller.platform.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storyteller.platform.dtos.MenuOptionDTO;
import com.storyteller.platform.models.MenuOption;
import com.storyteller.platform.repositories.MenuOptionRepository;

@Service
public class MenuOptionService {
    
    @Autowired
    private MenuOptionRepository menuOptionRepository;
    
    @Transactional(readOnly = true)
    public List<MenuOptionDTO> getAllMenuOptions() {
        return menuOptionRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<MenuOptionDTO> getMenuOptionById(Long id) {
        return menuOptionRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public List<MenuOptionDTO> getMenuOptionsByMenuLevelId(Long menuLevelId) {
        return menuOptionRepository.findByMenuLevelIdOrderByOrderAsc(menuLevelId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public MenuOptionDTO createMenuOption(MenuOptionDTO menuOptionDTO) {
        MenuOption menuOption = convertToEntity(menuOptionDTO);
        MenuOption savedMenuOption = menuOptionRepository.save(menuOption);
        return convertToDTO(savedMenuOption);
    }
    
    @Transactional
    public Optional<MenuOptionDTO> updateMenuOption(Long id, MenuOptionDTO menuOptionDTO) {
        if (!menuOptionRepository.existsById(id)) {
            return Optional.empty();
        }
        
        MenuOption menuOption = convertToEntity(menuOptionDTO);
        menuOption.setId(id);
        MenuOption savedMenuOption = menuOptionRepository.save(menuOption);
        return Optional.of(convertToDTO(savedMenuOption));
    }
    
    @Transactional
    public boolean deleteMenuOption(Long id) {
        if (!menuOptionRepository.existsById(id)) {
            return false;
        }
        
        menuOptionRepository.deleteById(id);
        return true;
    }
    
    private MenuOptionDTO convertToDTO(MenuOption menuOption) {
        MenuOptionDTO menuOptionDTO = new MenuOptionDTO();
        BeanUtils.copyProperties(menuOption, menuOptionDTO);
        return menuOptionDTO;
    }
    
    private MenuOption convertToEntity(MenuOptionDTO menuOptionDTO) {
        MenuOption menuOption = new MenuOption();
        BeanUtils.copyProperties(menuOptionDTO, menuOption);
        return menuOption;
    }
} 