package com.careergap.controller;

import com.careergap.dto.ApiResponse;
import com.careergap.dto.DashboardStatsDto;
import com.careergap.dto.OnboardingRequest;
import com.careergap.dto.UserProfileDto;
import com.careergap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<UserProfileDto>> completeOnboarding(@Valid @RequestBody OnboardingRequest request) {
        UserProfileDto profile = userService.saveOnboarding(request);
        return ResponseEntity.ok(ApiResponse.ok("Onboarding profile saved successfully", profile));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile() {
        UserProfileDto profile = userService.getFullProfile();
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@RequestBody UserProfileDto profileDto) {
        UserProfileDto updated = userService.updateProfile(profileDto);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = userService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
