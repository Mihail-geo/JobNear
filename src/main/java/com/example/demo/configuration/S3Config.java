package com.example.demo.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${s3.access.key}")
    private String accessKey;

    @Value("${s3.access.secret}")
    private String accessSecret;

    @Value("${s3.region}")
    private String region;

    @Bean
    public AmazonS3Client s3client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, accessSecret);

        AmazonS3Client s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        return s3Client;
    }
}
