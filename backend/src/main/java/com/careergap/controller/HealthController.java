package com.careergap.controller;

import com.careergap.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        return ResponseEntity.ok(ApiResponse.ok("CareerGap AI backend is healthy and running", Map.of(
                "status", "UP",
                "platform", "CareerGap AI",
                "tagline", "Know what you're missing. Learn what matters. Become job-ready."
        )));
    }
}
