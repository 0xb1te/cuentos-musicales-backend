package com.storyteller.platform.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.storyteller.platform.models.MenuOption;

@Repository
public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
    // Encuentra opciones de menú por menuLevelId
    List<MenuOption> findByMenuLevelId(Long menuLevelId);
    
    // Encuentra opciones de menú ordenadas por el campo 'order'
    List<MenuOption> findByMenuLevelIdOrderByOrderAsc(Long menuLevelId);
    
    // Encuentra opciones de menú activas
    List<MenuOption> findByIsActiveTrue();
} 