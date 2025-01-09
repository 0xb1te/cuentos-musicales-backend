package com.storyteller.platform.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentIntentRequest {
	private Long storyId; // Field to store the story ID
	private BigDecimal amount; // Field to store the payment amount
}