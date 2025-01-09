package com.storyteller.platform.models;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class MenuStructure {
	private JsonNode items;
}
