package com.careergap.controller;

import com.careergap.dto.ApiResponse;
import com.careergap.dto.SkillAnalysisRequest;
import com.careergap.dto.SkillAnalysisResponse;
import com.careergap.entity.Resume;
import com.careergap.service.SkillAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final SkillAnalysisService skillAnalysisService;

    public AnalysisController(SkillAnalysisService skillAnalysisService) {
        this.skillAnalysisService = skillAnalysisService;
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadResume(@RequestParam("file") MultipartFile file) {
        Resume resume = skillAnalysisService.uploadAndParseResume(file);

        Map<String, Object> data = new HashMap<>();
        data.put("resumeId", resume.getId());
        data.put("fileName", resume.getFileName());
        data.put("parsedText", resume.getParsedText());

        return ResponseEntity.ok(ApiResponse.ok("Resume uploaded and parsed successfully", data));
    }

    @PostMapping("/upload-job")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadJobDescription(@RequestParam("file") MultipartFile file) {
        String parsedText = skillAnalysisService.parseJobDescriptionFile(file);

        Map<String, Object> data = new HashMap<>();
        data.put("fileName", file.getOriginalFilename());
        data.put("parsedText", parsedText);

        return ResponseEntity.ok(ApiResponse.ok("Job description uploaded and parsed successfully", data));
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<SkillAnalysisResponse>> analyze(@Valid @RequestBody SkillAnalysisRequest request) {
        SkillAnalysisResponse response = skillAnalysisService.performAnalysis(request);
        return ResponseEntity.ok(ApiResponse.ok("Analysis generated successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SkillAnalysisResponse>>> getHistory() {
        List<SkillAnalysisResponse> history = skillAnalysisService.getUserAnalysisHistory();
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillAnalysisResponse>> getById(@PathVariable Long id) {
        SkillAnalysisResponse response = skillAnalysisService.getAnalysisById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
