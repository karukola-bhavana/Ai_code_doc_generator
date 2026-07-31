package com.bhavana.aidoc.controller;

import com.bhavana.aidoc.dto.DocGenerationRequest;
import com.bhavana.aidoc.dto.DocGenerationResponse;
import com.bhavana.aidoc.service.DocumentationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/doc")
public class DocumentationController {

    private final DocumentationService documentationService;

    public DocumentationController(DocumentationService documentationService) {
        this.documentationService = documentationService;
    }

    @PostMapping("/generate")
    public DocGenerationResponse generateDoc(@RequestBody DocGenerationRequest request) throws Exception {
        if (request.getRepoUrl() == null || request.getRepoUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub repository URL is required");
        }
        return documentationService.generateDocumentation(request);
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadMarkdown(@RequestParam(defaultValue = "README.md") String filename,
                                                   @RequestBody String markdownContent) {
        byte[] bytes = markdownContent != null ? markdownContent.getBytes(StandardCharsets.UTF_8) : new byte[0];

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }
}
