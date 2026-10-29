package com.careergap.controller;

import com.careergap.dto.ApiResponse;
import com.careergap.dto.MentorChatRequest;
import com.careergap.dto.MentorChatResponse;
import com.careergap.service.MentorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<MentorChatResponse>> chat(@Valid @RequestBody MentorChatRequest request) {
        MentorChatResponse response = mentorService.chat(request);
        return ResponseEntity.ok(ApiResponse.ok("Mentor response received", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getHistory() {
        List<Map<String, Object>> history = mentorService.getChatHistory();
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}

