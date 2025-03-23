package com.storyteller.platform.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.platform.dtos.MenuStructureDTO;
import com.storyteller.platform.services.MenuStructureService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200") 
public class MenuStructureController {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuStructureController.class);
    
    @Autowired
    private MenuStructureService menuStructureService;
    
    @GetMapping("/menu-structure")
    public ResponseEntity<MenuStructureDTO> getMenuStructure() {
        logger.info("Fetching active menu structure");
        return ResponseEntity.ok(menuStructureService.getActiveMenuStructure());
    }
    
    @PutMapping("/admin/menu-structure")
    public ResponseEntity<MenuStructureDTO> updateMenuStructure(@RequestBody MenuStructureDTO menuStructureDTO) {
        logger.info("Updating menu structure: {}", menuStructureDTO);
        MenuStructureDTO updatedStructure = menuStructureService.updateMenuStructure(menuStructureDTO);
        return ResponseEntity.ok(updatedStructure);
    }
} 