package com.example.demo.exception;


public class UnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = 3310148525624870502L;

	public UnauthorizedException(String errorMessage) {
		super(errorMessage);
	}

	public UnauthorizedException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}
}
