package com.homework4.workapi.service;

import com.homework4.workapi.validation.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String IMAGE_PREFIX = "images/";

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public String saveImage(MultipartFile file) {
        String extension = ImageValidator.validateAndGetExtension(file);
        String savedFilename = UUID.randomUUID() + extension;
        String objectKey = IMAGE_PREFIX + savedFilename;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException | S3Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.");
        }

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String objectKey = extractObjectKey(imageUrl);

        try {
            s3Client.deleteObject(request -> request.bucket(bucket).key(objectKey));
        } catch (S3Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");
        }
    }

    private String extractObjectKey(String imageUrl) {
        String path;

        try {
            URI uri = URI.create(imageUrl);
            path = uri.isAbsolute() ? uri.getPath() : imageUrl;
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 이미지 주소입니다.");
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (!path.startsWith(IMAGE_PREFIX) || path.contains("..") || path.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 이미지 경로입니다.");
        }

        return path;
    }
}