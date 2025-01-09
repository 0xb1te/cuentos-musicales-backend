package com.storyteller.platform.services;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storyteller.platform.dtos.PaymentIntent;
import com.storyteller.platform.dtos.PaymentIntentRequest;
import com.storyteller.platform.exceptions.PaymentProcessingException;
import com.storyteller.platform.models.Sales;
import com.storyteller.platform.repositories.SalesRepository;

@Service
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private SalesRepository salesRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public PaymentIntent createPaymentIntent(PaymentIntentRequest request) {
		String paymentIntentId = "pi_" + UUID.randomUUID().toString();
		logger.info("Creating payment intent with ID: {}", paymentIntentId);

		return PaymentIntent.builder().id(paymentIntentId).clientSecret(paymentIntentId + "_secret")
				.amount(request.getAmount()).currency("usd").build();
	}

	public void handleWebhook(String payload, String signature) throws PaymentProcessingException {
		try {
			JsonNode event = objectMapper.readTree(payload);
			logger.debug("Received webhook event: {}", event);

			if (!event.has("type") || !event.has("data") || !event.get("data").has("object")) {
				throw new PaymentProcessingException("Invalid webhook payload");
			}

			String type = event.get("type").asText();
			logger.info("Processing webhook event of type: {}", type);

			if ("payment_intent.succeeded".equals(type)) {
				String paymentIntentId = event.get("data").get("object").get("id").asText();
				Long storyId = event.get("data").get("object").get("metadata").get("storyId").asLong(); // Extract
																										// storyId from
																										// metadata
				logger.info("Processing successful payment intent: {}", paymentIntentId);
				processSale(paymentIntentId, storyId); // Pass both paymentIntentId and storyId
			}
		} catch (IOException e) {
			logger.error("Failed to process webhook payload", e);
			throw new PaymentProcessingException("Failed to process webhook payload", e);
		}
	}

	private void processSale(String paymentIntentId, Long storyId) {
		Sales sale = new Sales();
		sale.setStripePaymentId(paymentIntentId);
		sale.setStatus("completed");
		sale.setId(storyId); // Set the story ID
		salesRepository.save(sale);
		logger.info("Processed sale for payment intent: {}", paymentIntentId);
	}
}