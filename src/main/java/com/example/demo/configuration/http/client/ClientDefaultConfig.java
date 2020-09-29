package com.example.demo.configuration.http.client;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;


@Slf4j
@Configuration
public class ClientDefaultConfig {
	@Bean
	public Logger logger() {
		return new ClientDefaultLogger();
	}

	@Bean
	public RequestInterceptor baseRequestInterceptor() {
		return new ClientBaseRequestInterceptor();
	}

	private static class ClientBaseRequestInterceptor implements RequestInterceptor {
		@Override
		public void apply(RequestTemplate template) {
			template.header(HttpHeaders.ACCEPT_LANGUAGE, LocaleContextHolder.getLocale().getLanguage());
			template.header(HttpHeaders.CONNECTION, "keep-alive");
		}
	}
}
