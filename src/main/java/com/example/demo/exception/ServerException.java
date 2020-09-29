package com.example.demo.exception;


public class ServerException extends RuntimeException {
	private static final long serialVersionUID = -8168000218027432150L;
	private Integer statusCode;

	public ServerException(String serverError) {
		super(serverError);
	}

	public ServerException(String serverError, int statusCode) {
		super(serverError);
		this.statusCode = statusCode;
	}

	public ServerException(String serverError, Throwable cause) {
		super(serverError, cause);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}

	public Integer getStatusCode() {
		return statusCode;
	}
}
