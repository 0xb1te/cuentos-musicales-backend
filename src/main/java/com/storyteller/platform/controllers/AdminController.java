package com.storyteller.platform.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.platform.dtos.LoginRequest;
import com.storyteller.platform.dtos.LoginResponse;
import com.storyteller.platform.services.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(adminService.login(loginRequest));
	}
}
