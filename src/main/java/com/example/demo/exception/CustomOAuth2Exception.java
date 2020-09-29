package com.example.demo.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;


import java.io.IOException;


@JsonSerialize(using = com.example.demo.exception.CustomOAuth2ExceptionSerializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {
	private static final long serialVersionUID = -9027004577018591183L;

	public CustomOAuth2Exception(String msg) {
		super(msg);
	}
}

class CustomOAuth2ExceptionSerializer extends StdSerializer<com.example.demo.exception.CustomOAuth2Exception> {
	private static final long serialVersionUID = 2508630095406213903L;

	public CustomOAuth2ExceptionSerializer() {
		super(com.example.demo.exception.CustomOAuth2Exception.class);
	}

	@Override
	public void serialize(com.example.demo.exception.CustomOAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		String errorMessage = value.getLocalizedMessage();
		if (errorMessage.toLowerCase().contains("bad credentials")) {
			errorMessage = "Неверные логин или пароль";
		}
		gen.writeObject(new ErrorResponse(new ApiError(value.getHttpErrorCode(), errorMessage)));
	}
}

