package com.careergap.controller;

import com.careergap.dto.*;
import com.careergap.entity.User;
import com.careergap.service.AuthService;
import com.careergap.service.ResumeBuilderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeBuilderService resumeBuilderService;
    private final AuthService authService;

    public ResumeController(ResumeBuilderService resumeBuilderService, AuthService authService) {
        this.resumeBuilderService = resumeBuilderService;
        this.authService = authService;
    }

    @GetMapping("/extract-profile")
    public ResponseEntity<ApiResponse<ResumeDataDto>> extractFromProfile() {
        User user = authService.getCurrentUser();
        ResumeDataDto data = resumeBuilderService.extractProfileResumeData(user);
        return ResponseEntity.ok(ApiResponse.ok("Profile data extracted", data));
    }

    @PostMapping("/pending-skills")
    public ResponseEntity<ApiResponse<List<PendingSkillUpdateDto>>> getPendingSkills(
            @RequestBody(required = false) ResumeDataDto currentResume) {
        User user = authService.getCurrentUser();
        List<PendingSkillUpdateDto> updates = resumeBuilderService.getPendingSkillUpdates(user, currentResume);
        return ResponseEntity.ok(ApiResponse.ok("Pending skill updates evaluated", updates));
    }

    @PostMapping("/tailor")
    public ResponseEntity<ApiResponse<ResumeTailorResponse>> tailorResume(
            @Valid @RequestBody ResumeTailorRequest request) {
        User user = authService.getCurrentUser();
        ResumeTailorResponse response = resumeBuilderService.tailorResume(user, request);
        return ResponseEntity.ok(ApiResponse.ok("Resume tailored successfully", response));
    }

    @PostMapping("/versions")
    public ResponseEntity<ApiResponse<ResumeVersionDto>> saveVersion(
            @Valid @RequestBody SaveResumeVersionRequest request) {
        User user = authService.getCurrentUser();
        ResumeVersionDto saved = resumeBuilderService.saveVersion(user, request);
        return ResponseEntity.ok(ApiResponse.ok("Resume version saved successfully", saved));
    }

    @GetMapping("/versions")
    public ResponseEntity<ApiResponse<List<ResumeVersionDto>>> getVersions() {
        User user = authService.getCurrentUser();
        List<ResumeVersionDto> versions = resumeBuilderService.getVersions(user);
        return ResponseEntity.ok(ApiResponse.ok("Resume versions retrieved", versions));
    }

    @GetMapping("/versions/{id}")
    public ResponseEntity<ApiResponse<ResumeVersionDto>> getVersion(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        ResumeVersionDto version = resumeBuilderService.getVersion(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Resume version retrieved", version));
    }

    @GetMapping("/versions/{id}/pdf")
    public ResponseEntity<byte[]> downloadVersionPdf(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        ResumeVersionDto version = resumeBuilderService.getVersion(id, user);
        byte[] pdfBytes = resumeBuilderService.generatePdfForVersion(id, user);

        String filename = "resume-v" + version.getVersionNumber() + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdfBytes);
    }

    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody ResumeDataDto resumeData) {
        authService.getCurrentUser(); // ensures authentication
        byte[] pdfBytes = resumeBuilderService.generatePdfFromData(resumeData);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume.pdf\"")
                .body(pdfBytes);
    }
}

