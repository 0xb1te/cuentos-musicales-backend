package com.storyteller.platform.exceptions;

public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String message) {
		super(message);
	}
}
