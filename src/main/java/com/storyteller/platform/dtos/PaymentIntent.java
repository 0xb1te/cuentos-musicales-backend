package com.storyteller.platform.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntent {
	private String id;
	private String clientSecret;
	private BigDecimal amount;
	private String currency;
}