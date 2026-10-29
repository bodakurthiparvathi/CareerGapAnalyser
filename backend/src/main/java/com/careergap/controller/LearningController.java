package com.careergap.controller;

import com.careergap.dto.*;
import com.careergap.entity.User;
import com.careergap.service.AuthService;
import com.careergap.service.PersonalizedLearningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    private final PersonalizedLearningService learningService;
    private final AuthService authService;

    public LearningController(PersonalizedLearningService learningService, AuthService authService) {
        this.learningService = learningService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<LearningDashboardDto>> getDashboard() {
        LearningDashboardDto data = learningService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/roadmaps/{roadmapId}/modules")
    public ResponseEntity<ApiResponse<List<LearningModuleDto>>> getModulesForRoadmap(@PathVariable Long roadmapId) {
        List<LearningModuleDto> modules = learningService.getOrGenerateModules(roadmapId);
        return ResponseEntity.ok(ApiResponse.ok(modules));
    }

    @PutMapping("/roadmaps/{roadmapId}/study-preferences")
    public ResponseEntity<ApiResponse<LearningDashboardDto>> updateStudyPreference(
            @PathVariable Long roadmapId,
            @Valid @RequestBody StudyPreferenceRequest request) {
        LearningDashboardDto data = learningService.updateDailyStudyMinutes(roadmapId, request);
        return ResponseEntity.ok(ApiResponse.ok("Study preference saved", data));
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponse<LearningModuleDto>> getModuleById(@PathVariable Long moduleId) {
        LearningModuleDto module = learningService.getModuleById(moduleId);
        return ResponseEntity.ok(ApiResponse.ok(module));
    }

    @PostMapping("/modules/{moduleId}/practice/evaluate")
    public ResponseEntity<ApiResponse<PracticeEvaluationResult>> evaluatePractice(
            @PathVariable Long moduleId,
            @Valid @RequestBody PracticeSubmissionRequest request) {
        PracticeEvaluationResult result = learningService.evaluatePractice(moduleId, request);
        return ResponseEntity.ok(ApiResponse.ok("Practice exercise evaluated", result));
    }

    @PostMapping("/modules/{moduleId}/progress")
    public ResponseEntity<ApiResponse<LearningModuleDto>> updateProgress(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleProgressUpdateRequest request) {
        LearningModuleDto updated = learningService.updateModuleProgress(moduleId, request);
        return ResponseEntity.ok(ApiResponse.ok("Progress updated", updated));
    }

    @PostMapping("/verify-skill")
    public ResponseEntity<ApiResponse<SkillVerificationResponse>> verifySkill(
            @Valid @RequestBody SkillVerificationRequest request) {
        SkillVerificationResponse response = learningService.verifySkill(request);
        return ResponseEntity.ok(ApiResponse.ok("Skill verification processed", response));
    }

    @GetMapping("/verifications")
    public ResponseEntity<ApiResponse<List<SkillVerificationResponse>>> getVerifications() {
        List<SkillVerificationResponse> verifications = learningService.getUserVerifications();
        return ResponseEntity.ok(ApiResponse.ok(verifications));
    }

    @PostMapping("/mock-tests/generate")
    public ResponseEntity<ApiResponse<MockTestDto>> generateMockTest(
            @RequestParam(value = "targetRole", required = false) String targetRole) {
        MockTestDto test = learningService.generateMockTest(targetRole);
        return ResponseEntity.ok(ApiResponse.ok("Mock test generated", test));
    }

    @PostMapping("/mock-tests/{testId}/submit")
    public ResponseEntity<ApiResponse<MockTestResultDto>> submitMockTest(
            @PathVariable Long testId,
            @RequestBody MockTestSubmissionRequest request) {
        MockTestResultDto result = learningService.submitMockTest(testId, request);
        return ResponseEntity.ok(ApiResponse.ok("Mock test evaluated", result));
    }

    @GetMapping("/next-best-action")
    public ResponseEntity<ApiResponse<NextBestActionDto>> getNextBestAction() {
        User user = authService.getCurrentUser();
        NextBestActionDto nextAction = learningService.calculateNextBestAction(user);
        return ResponseEntity.ok(ApiResponse.ok(nextAction));
    }
}
