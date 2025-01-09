package com.storyteller.platform.exceptions;

/**
 * Custom exception for payment processing errors.
 */
public class PaymentProcessingException extends Exception {

	/**
	 * Constructs a new PaymentProcessingException with the specified detail
	 * message.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                getMessage() method).
	 */
	public PaymentProcessingException(String message) {
		super(message);
	}

	/**
	 * Constructs a new PaymentProcessingException with the specified detail message
	 * and cause.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                getMessage() method).
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                getCause() method).
	 */
	public PaymentProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}