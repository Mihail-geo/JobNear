package com.example.demo.configuration.http.client.nbs;

import com.google.common.base.Charsets;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * @author vladi_geras on 12.01.2020
 */
@Slf4j
public class NbsClientErrorDecoder implements ErrorDecoder {
	@Override
	public Exception decode(String methodKey, Response response) {
		Response.Body body = response.body();

		String errorBody = null;
		if (body != null) {
			try {
				errorBody = IOUtils.toString(body.asInputStream(), Charsets.UTF_8.toString());
			} catch (IOException e) {
				return defaultHandler(methodKey, response);
			}
		}

		int status = response.status();
		return new NbsErrorParser().parse(status, errorBody == null ? response.reason() : errorBody);
	}

	private Exception defaultHandler(String methodKey, Response response) {
		return FeignException.errorStatus(methodKey, response);
	}
}
