package com.example.demo.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
import com.example.demo.configuration.ErrorTranslator;
import com.example.demo.constant.ApiErrorKeyConstant;

import java.util.Map;

@Configuration
public class DefaultErrorAttributesHandler extends DefaultErrorAttributes {

	private final ErrorTranslator translator;

	public DefaultErrorAttributesHandler(ErrorTranslator translator) {
		this.translator = translator;
	}

	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			@Override
			public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

				Throwable error = getError(webRequest);

				ErrorResponse response;
				if (error == null) {
					int status = Integer.parseInt(errorAttributes.get("status").toString());
					if (status == 401 || status == 403) {
						response = new ErrorResponse(
								new ApiError(status, translator.get(ApiErrorKeyConstant.ACCESS_DENIED)));
					} else if (status == 404) {
						response = new ErrorResponse(
								new ApiError(status, translator.get(ApiErrorKeyConstant.NOT_FOUND)));
					} else {
						response = new ErrorResponse(
								new ApiError(status, errorAttributes.get("error").toString()));
					}
				} else {
					int status = Integer.parseInt(errorAttributes.get("status").toString());
					if (status == 401 || status == 403) {
						response = new ErrorResponse(
								new ApiError(status, error.getMessage()));
					} else {
						// TODO: разобраться с обработкой ошибок. Настроить централизованную обработку
						response = new ErrorResponse(
								new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), translator.get(ApiErrorKeyConstant.INTERNAL_ERROR))
						);
					}
				}
				return new ObjectMapper().convertValue(response, Map.class);
			}
		};
	}
}
