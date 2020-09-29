package com.example.demo.configuration.http.client.nbs;

import feign.Client;
import feign.codec.ErrorDecoder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author vladi_geras on 12.01.2020
 */
public class NbsClientConfig {
	@Bean
	public ErrorDecoder nbsClientErrorDecoder() {
		return new NbsClientErrorDecoder();
	}

	@Bean
	public Client nbsFeignClient() {
		return new Client.Default(getSSLSocketFactory(), new NoopHostnameVerifier());
	}

	private SSLSocketFactory getSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();
			return sslContext.getSocketFactory();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}