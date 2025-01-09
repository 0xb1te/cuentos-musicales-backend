package com.storyteller.platform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyteller.platform.dtos.PaymentIntentRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@PostMapping("/create-payment-intent")
	public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentIntentRequest request) {
		// Create Stripe payment intent
		return ResponseEntity.ok().build();
	}

	@PostMapping("/webhook")
	public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {
		// Handle Stripe webhook
		return ResponseEntity.ok().build();
	}

	@GetMapping("/sales")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<?> getSalesReport() {
		// Get sales report
		return ResponseEntity.ok().build();
	}
}
