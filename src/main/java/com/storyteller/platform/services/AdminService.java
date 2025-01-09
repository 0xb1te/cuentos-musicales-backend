package com.storyteller.platform.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.storyteller.platform.dtos.LoginRequest;
import com.storyteller.platform.dtos.LoginResponse;
import com.storyteller.platform.exceptions.UnauthorizedException;
import com.storyteller.platform.models.Admin;
import com.storyteller.platform.repositories.AdminRepository;

@Service
public class AdminService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AdminRepository adminRepository;

	public LoginResponse login(LoginRequest loginRequest) {
		Admin admin = adminRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

		if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
			throw new UnauthorizedException("Invalid credentials");
		}

		String token = jwtService.generateToken(admin.getUsername());
		return new LoginResponse(token);
	}
}
