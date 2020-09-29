package com.example.demo.exception;


public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 3015871287723874164L;
	private String resource;

	public NotFoundException(String resource) {
		super(resource);
		this.resource = resource;
	}

	@Override
	public String getMessage() {
		return "Ресурс не найден: " + resource;
	}

	public String getResource() {
		return resource;
	}
}
