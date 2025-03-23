package com.storyteller.platform.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
	private String token;
	private long expiresIn;
	
	// Constructor with just token for backward compatibility if needed
	public LoginResponse(String token) {
		this.token = token;
		this.expiresIn = 86400000; // Default to 24 hours
	}
}