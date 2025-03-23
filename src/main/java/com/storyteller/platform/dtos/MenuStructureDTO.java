package com.storyteller.platform.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuStructureDTO {
    private Long id;
    private JsonNode menuStructure;
    private Boolean active;
} 