package com.andd.DoDangAn.DoDangAn.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        try {
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "analytics", false)); // Tắt analytics toàn cục
            logger.info("Cloudinary initialized successfully with cloud_name: {}", cloudName);
        } catch (Exception e) {
            logger.error("Failed to initialize Cloudinary: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize Cloudinary: " + e.getMessage(), e);
        }
    }

    public Map uploadVideo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.error("File is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        if (file.getSize() > 100 * 1024 * 1024) {
            logger.error("File size exceeds 100MB: {}", file.getSize());
            throw new IllegalArgumentException("File size exceeds 100MB limit");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "video",
                    "access_mode", "public"));
            logger.info("Video uploaded successfully. Result: {}", uploadResult);
            if (uploadResult.containsKey("public_id")) {
                String publicId = (String) uploadResult.get("public_id");
                String url = (String) uploadResult.get("secure_url");
                logger.info("Public ID: {}", publicId);
                logger.info("Video URL: {}", url);
                return uploadResult;
            } else {
                logger.error("Upload failed, no public_id in result: {}", uploadResult);
                throw new IOException("Upload failed: No public_id returned");
            }
        } catch (Exception e) {
            logger.error("Failed to upload video: {}", e.getMessage(), e);
            throw new IOException("Upload failed: " + e.getMessage(), e);
        }
    }

    public String getPublicUrl(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            logger.error("PublicId is null or empty");
            throw new IllegalArgumentException("PublicId cannot be null or empty");
        }
        try {
            logger.info("Attempting to retrieve resource info for publicId: {}", publicId);
            Map<String, Object> resourceInfo = cloudinary.api().resource(publicId, ObjectUtils.asMap(
                    "resource_type", "video",
                    "info", true)); // Lấy thông tin chi tiết
            String version = resourceInfo.get("version") != null ? String.valueOf(resourceInfo.get("version")) : "";
            logger.info("Resource info: version={}, resource_type={}", version, resourceInfo.get("resource_type"));

            String publicUrl = cloudinary.url()
                    .resourceType("video")
                    .publicId(publicId)
                    .format("mp4")
                    .secure(true)
                    .version(version.isEmpty() ? null : version) // Chỉ thêm version nếu có
                    .generate();
            logger.info("Generated public URL: {}", publicUrl);
            return publicUrl;
        } catch (Exception e) {
            logger.error("Detailed error for publicId {}: {}", publicId, e.getMessage(), e);
            // Thử tạo URL mà không cần kiểm tra resource nếu lỗi là NotFound
            try {
                logger.warn("Falling back to generate URL without resource check for publicId: {}", publicId);
                String fallbackUrl = cloudinary.url()
                        .resourceType("video")
                        .publicId(publicId)
                        .format("mp4")
                        .secure(true)
                        .generate();
                logger.info("Fallback URL generated: {}", fallbackUrl);
                return fallbackUrl;
            } catch (Exception e2) {
                logger.error("Fallback URL generation failed for publicId {}: {}", publicId, e2.getMessage(), e2);
                throw new RuntimeException("Failed to generate public URL for publicId " + publicId + ": " + e.getMessage(), e);
            }
        }
    }
}