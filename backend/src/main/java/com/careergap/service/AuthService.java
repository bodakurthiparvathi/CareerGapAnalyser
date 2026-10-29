package com.careergap.service;

import com.careergap.dto.*;
import com.careergap.entity.User;
import com.careergap.entity.UserSkill;
import com.careergap.exception.AppException;
import com.careergap.repository.UserRepository;
import com.careergap.security.JwtTokenProvider;
import com.careergap.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email is already registered. Please login.");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setFullName(request.getFullName().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setTargetRole(request.getTargetRole() != null ? request.getTargetRole().trim() : "Software Engineer");
        user.setOnboardingCompleted(false);

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenFromUserId(savedUser.getId(), savedUser.getEmail());
        return new AuthResponse(token, mapToProfileDto(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new AppException("User not found"));

        return new AuthResponse(token, mapToProfileDto(user));
    }

    @Transactional
    public String requestPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new AppException("No account found with email: " + request.getEmail()));

        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        return resetToken;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new AppException("Invalid or expired password reset token."));

        if (user.getResetPasswordExpiry() == null || LocalDateTime.now().isAfter(user.getResetPasswordExpiry())) {
            throw new AppException("Password reset token has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AppException("No authenticated user found in session");
        }

        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new AppException("User not found: " + principal.getId()));
    }

    public UserProfileDto getCurrentUserProfile() {
        return mapToProfileDto(getCurrentUser());
    }

    public UserProfileDto mapToProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setLocation(user.getLocation());
        dto.setDegree(user.getDegree());
        dto.setBranch(user.getBranch());
        dto.setCollege(user.getCollege());
        dto.setGraduationYear(user.getGraduationYear());
        dto.setAcademicStatus(user.getAcademicStatus());
        dto.setTargetRole(user.getTargetRole());
        dto.setPreferredIndustry(user.getPreferredIndustry());
        dto.setYearsOfExperience(user.getYearsOfExperience());
        dto.setBio(user.getBio());
        dto.setEducation(user.getEducation());
        dto.setOnboardingCompleted(user.isOnboardingCompleted());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getSoftSkills() != null && !user.getSoftSkills().isBlank()) {
            dto.setSoftSkills(Arrays.stream(user.getSoftSkills().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
        } else {
            dto.setSoftSkills(new ArrayList<>());
        }

        if (user.getSkills() != null) {
            dto.setTechnicalSkills(user.getSkills().stream()
                    .map(s -> new OnboardingRequest.SkillDto(s.getSkillName(), s.getProficiencyLevel(), s.getCategory()))
                    .collect(Collectors.toList()));
        }

        if (user.getProjects() != null) {
            dto.setProjects(user.getProjects().stream()
                    .map(p -> new OnboardingRequest.ProjectDto(p.getTitle(), p.getDescription(), p.getTechnologies(), p.getGithubUrl(), p.getLiveUrl()))
                    .collect(Collectors.toList()));
        }

        if (user.getExperiences() != null) {
            dto.setExperiences(user.getExperiences().stream()
                    .map(e -> new OnboardingRequest.ExperienceDto(e.getTitle(), e.getCompany(), e.getDuration(), e.getDescription()))
                    .collect(Collectors.toList()));
        }

        if (user.getCertifications() != null) {
            dto.setCertifications(user.getCertifications().stream()
                    .map(c -> new OnboardingRequest.CertificationDto(c.getName(), c.getIssuer(), c.getIssueYear(), c.getCredentialUrl()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
