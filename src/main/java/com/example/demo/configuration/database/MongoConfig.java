package com.example.demo.configuration.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;


@EnableMongoRepositories
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Value("${fileStorage.host}")
	private String host;

	@Value("${fileStorage.port}")
	private String port;

	@Value("${fileStorage.database}")
	private String database;

	@Value("${fileStorage.username}")
	private String username;

	@Value("${fileStorage.password}")
	private String password;

	@Value("${fileStorage.authDatabase}")
	private String authDatabase;

	@Override
	public MongoClient mongoClient() {
		MongoCredential credential = MongoCredential.createCredential(username, authDatabase, password.toCharArray());

		MongoClientSettings settings = MongoClientSettings
				.builder()
				.applyConnectionString(new ConnectionString("mongodb://" + host + ":" + port + "/" + database))
				.applyToClusterSettings(builder -> builder.serverSelectionTimeout(0, TimeUnit.MILLISECONDS))
				.credential(credential)
				.build();

		return MongoClients.create(settings);
	}

	@Override
	protected String getDatabaseName() {
		return database;
	}
}
