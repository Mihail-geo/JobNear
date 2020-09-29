package com.example.demo.exception;


public class BadParamException extends RuntimeException {
	private static final long serialVersionUID = 4597239902845069838L;

	public BadParamException(String message) {
		super(message);
	}
}
