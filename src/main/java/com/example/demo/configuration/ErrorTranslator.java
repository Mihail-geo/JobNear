package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;


@Component
public class ErrorTranslator {
	private final MessageSource messageSource;

	public ErrorTranslator(@Qualifier("errorMessageSource") MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String get(String code, Locale locale) {
		if (locale == null) return get(code);
		return messageSource.getMessage(code, new Object[]{""}, locale);
	}

	public String get(String code) {
		return messageSource.getMessage(code, new Object[]{""}, LocaleContextHolder.getLocale());
	}

	public String get(String code, Object[] args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}
