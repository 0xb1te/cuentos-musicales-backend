package com.storyteller.platform.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.storyteller.platform.dtos.LoginRequest;
import com.storyteller.platform.dtos.LoginResponse;
import com.storyteller.platform.models.Admin;
import com.storyteller.platform.repositories.AdminRepository;

@Service
public class AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AdminRepository adminRepository;

	@Value("${admin.username}")
	private String adminUsername;

	@Value("${admin.password}")
	private String adminPassword;

	public LoginResponse login(LoginRequest loginRequest) {
		// Los logs aquí
		logger.debug("Intento de login para el usuario: {}", loginRequest.getUsername());

		// Check hardcoded admin credentials from env properties
		if (adminUsername.equals(loginRequest.getUsername()) && adminPassword.equals(loginRequest.getPassword())) {

			// Generate JWT token
			String token = jwtService.generateToken(adminUsername);

			return new LoginResponse(token, 86400000); // 24 hours
		} else {
			// Check against database
			Admin admin = adminRepository.findByUsername(loginRequest.getUsername())
					.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (!encoder.matches(loginRequest.getPassword(), admin.getPassword())) {
				throw new BadCredentialsException("Invalid username or password");
			}

			// Más logs donde se genera el token
			String token = jwtService.generateToken(admin.getUsername());
			logger.debug("Token JWT generado correctamente para: {}", admin.getUsername());

			return new LoginResponse(token, 86400000); // 24 hours
		}
	}
}
