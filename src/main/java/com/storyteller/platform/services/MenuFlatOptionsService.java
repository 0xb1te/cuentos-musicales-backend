package com.storyteller.platform.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.storyteller.platform.models.MenuLevel;
import com.storyteller.platform.repositories.MenuLevelRepository;

@Service
public class MenuFlatOptionsService {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuFlatOptionsService.class);
    
    @Autowired
    private MenuLevelRepository menuLevelRepository;
    
    public List<Map<String, Object>> getAllMenuOptionsFlat() {
        try {
            // Obtener la estructura actual del menú
            MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active menu structure found"));
            
            JsonNode menuStructure = menuLevel.getMenuStructure();
            ArrayNode items = (ArrayNode) menuStructure.get("items");
            
            // Lista para almacenar todas las opciones de menú aplanadas
            List<Map<String, Object>> flatOptions = new ArrayList<>();
            
            // Opción "Ninguno" para el nivel principal
            Map<String, Object> rootOption = new HashMap<>();
            rootOption.put("id", 0);
            rootOption.put("title", "-- Nivel Principal --");
            rootOption.put("level", 0);
            flatOptions.add(rootOption);
            
            // Aplanar recursivamente la estructura del menú
            flattenMenuStructure(items, flatOptions, "", 0);
            
            logger.debug("Retrieved {} menu options", flatOptions.size());
            return flatOptions;
        } catch (Exception e) {
            logger.error("Error getting menu options", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Método recursivo para aplanar la estructura del menú
     */
    private void flattenMenuStructure(ArrayNode items, List<Map<String, Object>> flatOptions, String prefix, int level) {
        for (JsonNode item : items) {
            Map<String, Object> option = new HashMap<>();
            int id = item.get("id").asInt();
            String title = item.get("title").asText();
            
            // Agregar indentación para mejor visualización en el dropdown
            String displayTitle = prefix + title;
            
            option.put("id", id);
            option.put("title", displayTitle);
            option.put("level", level);
            
            // Añadir propiedades adicionales si existen
            if (item.has("route")) {
                option.put("route", item.get("route").asText());
            }
            
            // Handle images array
            if (item.has("images") && item.get("images").isArray()) {
                // Convert JsonNode array to Java array
                ArrayNode imagesNode = (ArrayNode) item.get("images");
                String[] imagesArray = new String[imagesNode.size()];
                for (int i = 0; i < imagesNode.size(); i++) {
                    imagesArray[i] = imagesNode.get(i).asText();
                }
                option.put("images", imagesArray);
            } else if (item.has("imageUrl")) {
                // Legacy support for single imageUrl field
                String[] imagesArray = new String[] { item.get("imageUrl").asText() };
                option.put("images", imagesArray);
            }
            
            flatOptions.add(option);
            
            // Procesar hijos recursivamente
            if (item.has("children") && item.get("children").isArray()) {
                flattenMenuStructure((ArrayNode) item.get("children"), flatOptions, prefix + "   ", level + 1);
            }
        }
    }
} 