package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3StorageService {

    String uploadFile(MultipartFile file);

    void deleteFile(String url);

    byte[] getFile(String fileName);

}
