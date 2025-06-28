package com.storyteller.platform.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.storyteller.platform.services.FileUploadService;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminFileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(AdminFileUploadController.class);
    
    // Allowed file types
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml"
    );
    
    private static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList(
        "audio/mpeg", "audio/mp3", "audio/wav", "audio/wave", "audio/ogg", "audio/mp4", "audio/m4a", 
        "audio/webm", "audio/flac", "audio/aac", "audio/x-wav", "audio/x-ms-wma", "audio/3gpp", "audio/amr"
    );
    
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "application/rtf"
    );

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAdminFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", defaultValue = "uploads") String fileType) {
        
        logger.info("=== Admin File Upload Request ===");
        logger.info("File name: {}", file.getOriginalFilename());
        logger.info("File size: {} bytes", file.getSize());
        logger.info("File type: {}", file.getContentType());
        logger.info("Target directory: {}", fileType);
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                logger.error("File is empty");
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file type based on target directory
            if (!isValidFileType(file, fileType)) {
                logger.error("Invalid file type {} for directory {}. Allowed types for {}: {}", 
                    file.getContentType(), fileType, fileType, getAllowedTypesForDirectory(fileType));
                response.put("error", "Invalid file type '" + file.getContentType() + "' for category '" + fileType + "'. Allowed types: " + String.join(", ", getAllowedTypesForDirectory(fileType)));
                return ResponseEntity.badRequest().body(response);
            }
            
            // Store the file
            String fileUrl = fileUploadService.storeFile(file, fileType);
            
            response.put("url", fileUrl);
            response.put("message", "File uploaded successfully");
            response.put("originalName", file.getOriginalFilename());
            response.put("size", String.valueOf(file.getSize()));
            
            logger.info("File uploaded successfully: {}", fileUrl);
            logger.info("=== Upload Complete ===");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error uploading admin file: {}", e.getMessage(), e);
            response.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private boolean isValidFileType(MultipartFile file, String fileType) {
        String contentType = file.getContentType();
        
        if (contentType == null) {
            logger.warn("File content type is null, allowing upload");
            return true; // Allow if content type cannot be determined
        }
        
        switch (fileType.toLowerCase()) {
            case "images":
                return ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
            case "audio":
                return ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase());
            case "documents":
                return ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase());
            case "uploads":
            default:
                // Allow all types for general uploads
                return true;
        }
    }
    
    private List<String> getAllowedTypesForDirectory(String fileType) {
        switch (fileType.toLowerCase()) {
            case "images":
                return ALLOWED_IMAGE_TYPES;
            case "audio":
                return ALLOWED_AUDIO_TYPES;
            case "documents":
                return ALLOWED_DOCUMENT_TYPES;
            case "uploads":
            default:
                return Arrays.asList("All file types allowed");
        }
    }
} 