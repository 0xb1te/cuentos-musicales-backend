package com.storyteller.platform.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.platform.services.MenuFlatOptionsService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MenuFlatOptionsController {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuFlatOptionsController.class);
    
    @Autowired
    private MenuFlatOptionsService menuFlatOptionsService;
    
    @GetMapping("/menu-options/flat")
    public ResponseEntity<List<Map<String, Object>>> getAllMenuOptionsFlat() {
        logger.info("Getting all menu options in flat format");
        List<Map<String, Object>> flatOptions = menuFlatOptionsService.getAllMenuOptionsFlat();
        return ResponseEntity.ok(flatOptions);
    }
} 