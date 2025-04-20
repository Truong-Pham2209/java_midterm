package com.pht.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.pht.service.MetafileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/files")
public class MetafileApi {
	MetafileService service;

    @GetMapping("/")
    public ResponseEntity<StreamingResponseBody> getById(@RequestParam("id") UUID fileId) throws IOException {
        Path filePath = service.getById(fileId);
        return streamFile(filePath);
    }

    private ResponseEntity<StreamingResponseBody> streamFile(Path filePath) throws IOException {
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy file");
        }

        String contentType = Files.probeContentType(filePath);
        MediaType mediaType = MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream");

        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        };

        String contentDisposition = "inline; filename=\"" + filePath.getFileName().toString() + "\"";
        log.info("Content Disposition: {}", contentDisposition);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(mediaType)
                .body(stream);
    }
}
