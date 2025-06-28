package com.storyteller.platform.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
public class FileUploadService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${file.base-url:http://localhost:8080/files}")
    private String baseUrl;

    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // Get original file name and extension
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        
        // Generate hashed file name using SHA-256
        String hashedFileName = generateHashedFileName(originalFileName, file.getSize()) + fileExtension;
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Save the file
        Path targetLocation = uploadPath.resolve(hashedFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the file URL
        return baseUrl + "/" + subDirectory + "/" + hashedFileName;
    }
    
    private String generateHashedFileName(String originalFileName, long fileSize) {
        try {
            // Create a unique string combining filename, size, and current timestamp
            String uniqueString = originalFileName + "_" + fileSize + "_" + Instant.now().toEpochMilli();
            
            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(uniqueString.getBytes("UTF-8"));
            
            // Convert to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // Return first 16 characters of hash for shorter filename
            return hexString.toString().substring(0, 16);
            
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            // Fallback to timestamp-based naming if hashing fails
            return "file_" + System.currentTimeMillis();
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex).toLowerCase();
    }
} 