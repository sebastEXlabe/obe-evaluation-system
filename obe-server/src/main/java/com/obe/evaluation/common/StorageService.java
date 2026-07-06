package com.obe.evaluation.common;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minioClient;

    /** 上传文件到MinIO，返回存储路径 */
    public String upload(String bucket, MultipartFile file) {
        try {
            ensureBucket(bucket);
            String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")) : "";
            String objectName = UUID.randomUUID().toString() + ext;
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket).object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType()).build());
            return objectName;
        } catch (Exception e) {
            log.error("MinIO upload failed: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /** 下载文件 */
    public InputStream download(String bucket, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket).object(objectName).build());
        } catch (Exception e) {
            log.error("MinIO download failed: {}", e.getMessage());
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /** 删除文件 */
    public void delete(String bucket, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket).object(objectName).build());
        } catch (Exception e) {
            log.warn("MinIO delete failed: {}", e.getMessage());
        }
    }

    private void ensureBucket(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            log.error("MinIO bucket creation failed: {}", e.getMessage());
        }
    }
}
