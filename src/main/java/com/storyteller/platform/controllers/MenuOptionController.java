package com.storyteller.platform.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.storyteller.platform.models.MenuLevel;
import com.storyteller.platform.repositories.MenuLevelRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MenuOptionController {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuOptionController.class);
    
    @Autowired
    private MenuLevelRepository menuLevelRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @GetMapping("/admin/menu-options/all")
    public ResponseEntity<List<Map<String, Object>>> getAllMenuOptionsFlat() {
        try {
            // Obtener la estructura actual del menú
            MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active menu structure found"));
            
            JsonNode currentStructure = menuLevel.getMenuStructure();
            
            // Obtener el array de items
            ArrayNode items = (ArrayNode) currentStructure.get("items");
            
            // Lista para almacenar todas las opciones aplanadas
            List<Map<String, Object>> flatOptions = new ArrayList<>();
            
            // Aplanar recursivamente la estructura del menú
            flattenMenuOptions(items, flatOptions, "", 0);
            
            return ResponseEntity.ok(flatOptions);
        } catch (Exception e) {
            logger.error("Error getting all menu options", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Método para aplanar recursivamente la estructura del menú
     */
    private void flattenMenuOptions(ArrayNode items, List<Map<String, Object>> flatOptions, String prefix, int level) {
        for (JsonNode item : items) {
            Map<String, Object> option = new HashMap<>();
            int id = item.get("id").asInt();
            String title = item.get("title").asText();
            
            // Agregar indentación según el nivel para mejor visualización en el dropdown
            String displayTitle = prefix + title;
            
            option.put("id", id);
            option.put("title", displayTitle);
            option.put("level", level);
            
            // Agregar otras propiedades relevantes
            if (item.has("route")) {
                option.put("route", item.get("route").asText());
            }
            if (item.has("imageUrl")) {
                option.put("imageUrl", item.get("imageUrl").asText());
            }
            
            flatOptions.add(option);
            
            // Procesar hijos recursivamente si existen
            if (item.has("children") && item.get("children").isArray()) {
                ArrayNode children = (ArrayNode) item.get("children");
                flattenMenuOptions(children, flatOptions, prefix + "   ", level + 1);
            }
        }
    }
    
    @PostMapping("/admin/menu-option")
    public ResponseEntity<JsonNode> addMenuOption(@RequestBody Map<String, Object> menuOption) {
        try {
            logger.info("Adding menu option: {}", menuOption);
            
            // Obtener la estructura actual del menú
            MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active menu structure found"));
            
            JsonNode currentStructure = menuLevel.getMenuStructure();
            
            // Convertir la estructura a un ObjectNode mutable
            ObjectNode menuStructure = currentStructure.deepCopy();
            
            // Obtener el array de items
            ArrayNode items = (ArrayNode) menuStructure.get("items");
            
            // Generar un nuevo ID único basado en los existentes
            int newId = generateNewId(items);
            
            // Crear el nuevo objeto de menú
            ObjectNode newOption = objectMapper.createObjectNode();
            newOption.put("id", newId);
            
            // Copiar propiedades del menuOption al newOption
            for (Map.Entry<String, Object> entry : menuOption.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                // Ignorar parentId que se maneja separadamente
                if (key.equals("parentId")) {
                    continue;
                }
                
                if (value instanceof String) {
                    newOption.put(key, (String) value);
                } else if (value instanceof Number) {
                    if (value instanceof Integer) {
                        newOption.put(key, (Integer) value);
                    } else if (value instanceof Long) {
                        newOption.put(key, (Long) value);
                    } else if (value instanceof Double) {
                        newOption.put(key, (Double) value);
                    }
                } else if (value instanceof Boolean) {
                    newOption.put(key, (Boolean) value);
                }
            }
            
            // Verificar si hay un parentId especificado
            Integer parentId = null;
            if (menuOption.containsKey("parentId") && menuOption.get("parentId") != null) {
                if (menuOption.get("parentId") instanceof Integer) {
                    parentId = (Integer) menuOption.get("parentId");
                } else if (menuOption.get("parentId") instanceof String) {
                    try {
                        parentId = Integer.parseInt((String) menuOption.get("parentId"));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid parentId format: {}", menuOption.get("parentId"));
                    }
                }
            }
            
            // Si se especificó un parentId, agregar como hijo
            if (parentId != null && parentId > 0) {
                logger.info("Adding menu option as child of parent with ID: {}", parentId);
                boolean added = addToParent(items, parentId, newOption);
                if (!added) {
                    logger.warn("Parent with ID {} not found, adding to root level", parentId);
                    items.add(newOption);
                }
            } else {
                // Agregar al nivel superior
                logger.info("Adding menu option to root level");
                items.add(newOption);
            }
            
            // Actualizar la estructura del menú
            menuLevel.setMenuStructure(menuStructure);
            menuLevel.setUpdatedAt(LocalDateTime.now());
            
            // Guardar la estructura actualizada
            MenuLevel savedMenuLevel = menuLevelRepository.save(menuLevel);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMenuLevel.getMenuStructure());
        } catch (Exception e) {
            logger.error("Error adding menu option", e);
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Método recursivo para agregar un elemento a un padre específico en la estructura del menú
     */
    private boolean addToParent(ArrayNode items, int parentId, ObjectNode newOption) {
        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            if (item.get("id").asInt() == parentId) {
                // Encontró el padre, agregar como hijo
                if (item.has("children") && item.get("children").isArray()) {
                    // El padre ya tiene hijos, añadir a la lista existente
                    ArrayNode children = ((ObjectNode) item).withArray("children");
                    children.add(newOption);
                } else {
                    // El padre no tiene hijos, crear un nuevo array y añadir
                    ArrayNode children = objectMapper.createArrayNode();
                    children.add(newOption);
                    ((ObjectNode) item).set("children", children);
                }
                return true;
            }
            
            // Buscar recursivamente en los hijos
            if (item.has("children") && item.get("children").isArray()) {
                ArrayNode children = (ArrayNode) item.get("children");
                boolean added = addToParent(children, parentId, newOption);
                if (added) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private int generateNewId(ArrayNode items) {
        int maxId = 0;
        
        // Función recursiva para encontrar el ID máximo
        maxId = findMaxId(items, maxId);
        
        // Retornar el siguiente ID
        return maxId + 1;
    }
    
    private int findMaxId(ArrayNode items, int currentMax) {
        for (JsonNode item : items) {
            int itemId = item.get("id").asInt();
            if (itemId > currentMax) {
                currentMax = itemId;
            }
            
            // Si el elemento tiene hijos, buscar recursivamente
            if (item.has("children") && item.get("children").isArray()) {
                currentMax = findMaxId((ArrayNode) item.get("children"), currentMax);
            }
        }
        
        return currentMax;
    }
    
    @PutMapping("/admin/menu-option/{id}")
    public ResponseEntity<JsonNode> updateMenuOption(@PathVariable("id") int id, @RequestBody Map<String, Object> menuOption) {
        try {
            logger.info("Updating menu option with ID {}: {}", id, menuOption);
            
            // Obtener la estructura actual del menú
            MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active menu structure found"));
            
            JsonNode currentStructure = menuLevel.getMenuStructure();
            
            // Convertir la estructura a un ObjectNode mutable
            ObjectNode menuStructure = currentStructure.deepCopy();
            
            // Obtener el array de items
            ArrayNode items = (ArrayNode) menuStructure.get("items");
            
            // Verificar si hay un parentId especificado
            Integer parentId = null;
            if (menuOption.containsKey("parentId") && menuOption.get("parentId") != null) {
                if (menuOption.get("parentId") instanceof Integer) {
                    parentId = (Integer) menuOption.get("parentId");
                } else if (menuOption.get("parentId") instanceof String) {
                    try {
                        parentId = Integer.parseInt((String) menuOption.get("parentId"));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid parentId format: {}", menuOption.get("parentId"));
                    }
                }
            }
            
            // Si el parentId cambió, necesitamos mover la opción
            if (parentId != null && parentId > 0) {
                // Primero encontramos y guardamos los hijos y propiedades actuales
                ObjectNode nodeToUpdate = findAndRemoveNode(items, id);
                if (nodeToUpdate == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        objectMapper.createObjectNode().put("error", "Menu option with ID " + id + " not found")
                    );
                }
                
                // Actualizar propiedades del nodo
                for (Map.Entry<String, Object> entry : menuOption.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    // Ignorar parentId que se maneja separadamente
                    if (key.equals("parentId")) {
                        continue;
                    }
                    
                    if (value instanceof String) {
                        nodeToUpdate.put(key, (String) value);
                    } else if (value instanceof Number) {
                        if (value instanceof Integer) {
                            nodeToUpdate.put(key, (Integer) value);
                        } else if (value instanceof Long) {
                            nodeToUpdate.put(key, (Long) value);
                        } else if (value instanceof Double) {
                            nodeToUpdate.put(key, (Double) value);
                        }
                    } else if (value instanceof Boolean) {
                        nodeToUpdate.put(key, (Boolean) value);
                    }
                }
                
                // Añadir el nodo actualizado a su nuevo padre
                boolean added = addToParent(items, parentId, nodeToUpdate);
                if (!added) {
                    logger.warn("Parent with ID {} not found, adding to root level", parentId);
                    items.add(nodeToUpdate);
                }
            } else {
                // Si no cambió el parentId, actualizar in situ
                boolean updated = updateNodeInPlace(items, id, menuOption);
                if (!updated) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        objectMapper.createObjectNode().put("error", "Menu option with ID " + id + " not found")
                    );
                }
            }
            
            // Actualizar la estructura del menú
            menuLevel.setMenuStructure(menuStructure);
            menuLevel.setUpdatedAt(LocalDateTime.now());
            
            // Guardar la estructura actualizada
            MenuLevel savedMenuLevel = menuLevelRepository.save(menuLevel);
            
            return ResponseEntity.ok(savedMenuLevel.getMenuStructure());
        } catch (Exception e) {
            logger.error("Error updating menu option", e);
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Método para actualizar una opción de menú en su lugar, preservando sus hijos
     */
    private boolean updateNodeInPlace(ArrayNode items, int id, Map<String, Object> newValues) {
        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            
            if (item.get("id").asInt() == id) {
                // Encontró el elemento, actualizarlo
                ObjectNode nodeToUpdate = (ObjectNode) item;
                
                // Actualizar todas las propiedades excepto id y children
                for (Map.Entry<String, Object> entry : newValues.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    // Mantener id y parentId, y preservar children
                    if (key.equals("id") || key.equals("parentId") || key.equals("children")) {
                        continue;
                    }
                    
                    if (value instanceof String) {
                        nodeToUpdate.put(key, (String) value);
                    } else if (value instanceof Number) {
                        if (value instanceof Integer) {
                            nodeToUpdate.put(key, (Integer) value);
                        } else if (value instanceof Long) {
                            nodeToUpdate.put(key, (Long) value);
                        } else if (value instanceof Double) {
                            nodeToUpdate.put(key, (Double) value);
                        }
                    } else if (value instanceof Boolean) {
                        nodeToUpdate.put(key, (Boolean) value);
                    }
                }
                
                return true;
            }
            
            // Buscar recursivamente en los hijos
            if (item.has("children") && item.get("children").isArray()) {
                ArrayNode children = (ArrayNode) item.get("children");
                boolean updated = updateNodeInPlace(children, id, newValues);
                if (updated) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Método para encontrar y eliminar un nodo por su ID, retornando el nodo eliminado
     */
    private ObjectNode findAndRemoveNode(ArrayNode items, int id) {
        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            
            if (item.get("id").asInt() == id) {
                // Encontró el elemento, eliminarlo y retornarlo
                ObjectNode nodeToReturn = (ObjectNode) item.deepCopy();
                items.remove(i);
                return nodeToReturn;
            }
            
            // Buscar recursivamente en los hijos
            if (item.has("children") && item.get("children").isArray()) {
                ArrayNode children = (ArrayNode) item.get("children");
                ObjectNode found = findAndRemoveNode(children, id);
                if (found != null) {
                    // Si el elemento fue eliminado y no quedan más hijos, eliminar el array de hijos
                    if (children.size() == 0) {
                        ((ObjectNode) item).remove("children");
                    }
                    return found;
                }
            }
        }
        
        return null;
    }
    
    @DeleteMapping("/admin/menu-option/{id}")
    public ResponseEntity<JsonNode> deleteMenuOption(@PathVariable("id") int id) {
        try {
            logger.info("Deleting menu option with ID: {}", id);
            
            // Obtener la estructura actual del menú
            MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active menu structure found"));
            
            JsonNode currentStructure = menuLevel.getMenuStructure();
            
            // Convertir la estructura a un ObjectNode mutable
            ObjectNode menuStructure = currentStructure.deepCopy();
            
            // Obtener el array de items
            ArrayNode items = (ArrayNode) menuStructure.get("items");
            
            // Eliminar la opción del menú
            boolean removed = removeMenuOption(items, id);
            if (!removed) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    objectMapper.createObjectNode().put("error", "Menu option with ID " + id + " not found")
                );
            }
            
            // Actualizar la estructura del menú
            menuLevel.setMenuStructure(menuStructure);
            menuLevel.setUpdatedAt(LocalDateTime.now());
            
            // Guardar la estructura actualizada
            MenuLevel savedMenuLevel = menuLevelRepository.save(menuLevel);
            
            return ResponseEntity.ok(savedMenuLevel.getMenuStructure());
        } catch (Exception e) {
            logger.error("Error deleting menu option", e);
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Método recursivo para eliminar una opción de menú por su ID
     */
    private boolean removeMenuOption(ArrayNode items, int id) {
        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            
            if (item.get("id").asInt() == id) {
                // Encontró el elemento, eliminarlo
                items.remove(i);
                return true;
            }
            
            // Buscar recursivamente en los hijos
            if (item.has("children") && item.get("children").isArray()) {
                ArrayNode children = (ArrayNode) item.get("children");
                boolean removed = removeMenuOption(children, id);
                if (removed) {
                    // Si el elemento fue eliminado y no quedan más hijos, eliminar el array de hijos
                    if (children.size() == 0) {
                        ((ObjectNode) item).remove("children");
                    }
                    return true;
                }
            }
        }
        
        return false;
    }
} 