package pro.cproject.lkpassengerbackend.admin.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.cproject.lkpassengerbackend.admin.exception.ServerException;
import pro.cproject.lkpassengerbackend.admin.service.S3StorageService;
import pro.cproject.lkpassengerbackend.admin.util.FileUtil;

import java.io.IOException;

@Slf4j
@Service
public class S3StorageServiceImpl implements S3StorageService {

    @Autowired
    AmazonS3Client amazonS3Client;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        if (!FileUtil.isValidFileName(file.getOriginalFilename())) throw new ServerException("Некорректное имя файла");

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            amazonS3Client
                    .putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException ioe) {
            log.error("IOException: " + ioe.getMessage());
        }

        return amazonS3Client.getResourceUrl(bucketName, file.getOriginalFilename());

    }

    @Override
    public void deleteFile(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    @Async
    @Override
    public byte[] getFile(String fileName) {
        byte[] content = null;
        final S3Object s3Object = amazonS3Client.getObject(bucketName, fileName);
        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(stream);
            s3Object.close();
        } catch(final IOException ex) {
            log.info("IO Error Message= " + ex.getMessage());
        }
        return content;
    }
}