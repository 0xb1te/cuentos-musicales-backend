package com.storyteller.platform.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionDTO {
    private Long id;
    private String title;
    private String description;
    private String iconUrl;
    private String backgroundImageUrl;
    private String route;
    private Integer order;
    private Boolean isActive;
    private Long menuLevelId;
} 