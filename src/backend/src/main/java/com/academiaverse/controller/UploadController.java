package com.academiaverse.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        byte[] data = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }
}