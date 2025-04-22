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

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminFileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(AdminFileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAdminFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", defaultValue = "image") String fileType) {
        
        logger.info("Admin file upload request received for file type: {}", fileType);
        
        try {
            String fileUrl = fileUploadService.storeFile(file, fileType);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("message", "File uploaded successfully");
            
            logger.info("Admin file uploaded successfully: {}", fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error uploading admin file: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 