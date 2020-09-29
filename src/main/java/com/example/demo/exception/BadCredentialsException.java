package com.example.demo.exception;


public class BadCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 3310148525624870502L;

	public BadCredentialsException(String errorMessage) {
		super(errorMessage);
	}

	public BadCredentialsException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

	public BadCredentialsException(Throwable cause) {
		super(cause);
	}
}
