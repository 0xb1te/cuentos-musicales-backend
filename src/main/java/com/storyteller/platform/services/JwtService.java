package com.storyteller.platform.services;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.storyteller.platform.models.MenuLevel;
import com.storyteller.platform.repositories.MenuLevelRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	// Remove the static key and replace with an instance field
	private final Key key;

	@Autowired
	private MenuLevelRepository menuLevelRepository;

	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMs;

	public JwtService(@Value("${app.jwtSecret}") String jwtSecret) {
		// Create the key from the provided JWT secret
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
		logger.info("JWT service initialized with secure key for HS512");
	}

	public String generateToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(username).setIssuedAt(now).setExpiration(expiryDate).signWith(key).compact();
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			logger.debug("Token validado correctamente");
			return true;
		} catch (Exception e) {
			logger.error("Error validando token: {}", e.getMessage());
			return false;
		}
	}

	public List<Map<String, Object>> getMenuOptions() {
		try {
			// Obtener la estructura actual del menú
			MenuLevel menuLevel = menuLevelRepository.findByActiveTrue()
					.orElseThrow(() -> new RuntimeException("No active menu structure found"));

			JsonNode menuStructure = menuLevel.getMenuStructure();
			ArrayNode items = (ArrayNode) menuStructure.get("items");

			// Lista para almacenar todas las opciones de menú aplanadas
			List<Map<String, Object>> flatOptions = new ArrayList<>();

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
	private void flattenMenuStructure(ArrayNode items, List<Map<String, Object>> flatOptions, String prefix,
			int level) {
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
			if (item.has("imageUrl")) {
				option.put("imageUrl", item.get("imageUrl").asText());
			}

			flatOptions.add(option);

			// Procesar hijos recursivamente
			if (item.has("children") && item.get("children").isArray()) {
				flattenMenuStructure((ArrayNode) item.get("children"), flatOptions, prefix + "   ", level + 1);
			}
		}
	}
}