package com.careergap.service;

import com.careergap.dto.DashboardStatsDto;
import com.careergap.dto.OnboardingRequest;
import com.careergap.dto.UserProfileDto;
import com.careergap.entity.*;
import com.careergap.repository.SkillAnalysisRepository;
import com.careergap.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SkillAnalysisRepository skillAnalysisRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository,
                       SkillAnalysisRepository skillAnalysisRepository,
                       AuthService authService) {
        this.userRepository = userRepository;
        this.skillAnalysisRepository = skillAnalysisRepository;
        this.authService = authService;
    }

    @Transactional
    public UserProfileDto saveOnboarding(OnboardingRequest request) {
        // Securely resolve user from SecurityContext - never trust frontend ID
        User user = authService.getCurrentUser();

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation().trim());
        }

        // Academic details
        user.setDegree(request.getDegree() != null ? request.getDegree().trim() : null);
        user.setBranch(request.getBranch() != null ? request.getBranch().trim() : null);
        user.setCollege(request.getCollege() != null ? request.getCollege().trim() : null);
        user.setGraduationYear(request.getGraduationYear());
        user.setAcademicStatus(request.getAcademicStatus() != null ? request.getAcademicStatus().trim() : "Student");

        // Set consolidated education string for backward compatibility
        String eduStr = (user.getDegree() != null ? user.getDegree() : "") +
                (user.getBranch() != null ? " in " + user.getBranch() : "") +
                (user.getCollege() != null ? ", " + user.getCollege() : "");
        user.setEducation(eduStr.trim());

        // Career preferences
        if (request.getTargetRole() != null && !request.getTargetRole().isBlank()) {
            user.setTargetRole(request.getTargetRole().trim());
        }
        user.setPreferredIndustry(request.getPreferredIndustry() != null ? request.getPreferredIndustry().trim() : null);
        user.setYearsOfExperience(request.getYearsOfExperience() != null ? request.getYearsOfExperience() : 0);
        user.setBio(request.getBio() != null ? request.getBio().trim() : null);

        // Soft skills
        if (request.getSoftSkills() != null && !request.getSoftSkills().isEmpty()) {
            user.setSoftSkills(String.join(", ", request.getSoftSkills()));
        }

        // Relational collections: replace cleanly with new items
        user.getSkills().clear();
        if (request.getTechnicalSkills() != null) {
            for (OnboardingRequest.SkillDto sDto : request.getTechnicalSkills()) {
                if (sDto.getSkillName() != null && !sDto.getSkillName().isBlank()) {
                    UserSkill skill = new UserSkill(
                            sDto.getSkillName().trim(),
                            sDto.getProficiencyLevel() != null ? sDto.getProficiencyLevel() : "Intermediate",
                            sDto.getCategory() != null ? sDto.getCategory() : "Technical"
                    );
                    user.addSkill(skill);
                }
            }
        }

        user.getProjects().clear();
        if (request.getProjects() != null) {
            for (OnboardingRequest.ProjectDto pDto : request.getProjects()) {
                if (pDto.getTitle() != null && !pDto.getTitle().isBlank()) {
                    UserProject project = new UserProject(
                            pDto.getTitle().trim(),
                            pDto.getDescription(),
                            pDto.getTechnologies(),
                            pDto.getGithubUrl(),
                            pDto.getLiveUrl()
                    );
                    user.addProject(project);
                }
            }
        }

        user.getExperiences().clear();
        if (request.getExperiences() != null) {
            for (OnboardingRequest.ExperienceDto eDto : request.getExperiences()) {
                if (eDto.getTitle() != null && !eDto.getTitle().isBlank()) {
                    UserExperience exp = new UserExperience(
                            eDto.getTitle().trim(),
                            eDto.getCompany(),
                            eDto.getDuration(),
                            eDto.getDescription()
                    );
                    user.addExperience(exp);
                }
            }
        }

        user.getCertifications().clear();
        if (request.getCertifications() != null) {
            for (OnboardingRequest.CertificationDto cDto : request.getCertifications()) {
                if (cDto.getName() != null && !cDto.getName().isBlank()) {
                    UserCertification cert = new UserCertification(
                            cDto.getName().trim(),
                            cDto.getIssuer(),
                            cDto.getIssueYear(),
                            cDto.getCredentialUrl()
                    );
                    user.addCertification(cert);
                }
            }
        }

        user.setOnboardingCompleted(true);
        User savedUser = userRepository.save(user);

        return authService.mapToProfileDto(savedUser);
    }

    @Transactional
    public UserProfileDto updateProfile(UserProfileDto profileDto) {
        User user = authService.getCurrentUser();

        if (profileDto.getFullName() != null) {
            user.setFullName(profileDto.getFullName().trim());
        }
        if (profileDto.getPhoneNumber() != null) {
            user.setPhoneNumber(profileDto.getPhoneNumber().trim());
        }
        if (profileDto.getLocation() != null) {
            user.setLocation(profileDto.getLocation().trim());
        }
        if (profileDto.getDegree() != null) {
            user.setDegree(profileDto.getDegree().trim());
        }
        if (profileDto.getBranch() != null) {
            user.setBranch(profileDto.getBranch().trim());
        }
        if (profileDto.getCollege() != null) {
            user.setCollege(profileDto.getCollege().trim());
        }
        if (profileDto.getGraduationYear() != null) {
            user.setGraduationYear(profileDto.getGraduationYear());
        }
        if (profileDto.getAcademicStatus() != null) {
            user.setAcademicStatus(profileDto.getAcademicStatus().trim());
        }
        if (profileDto.getTargetRole() != null) {
            user.setTargetRole(profileDto.getTargetRole().trim());
        }
        if (profileDto.getPreferredIndustry() != null) {
            user.setPreferredIndustry(profileDto.getPreferredIndustry().trim());
        }
        if (profileDto.getYearsOfExperience() != null) {
            user.setYearsOfExperience(profileDto.getYearsOfExperience());
        }
        if (profileDto.getEducation() != null) {
            user.setEducation(profileDto.getEducation().trim());
        }
        if (profileDto.getBio() != null) {
            user.setBio(profileDto.getBio().trim());
        }
        if (profileDto.getSoftSkills() != null && !profileDto.getSoftSkills().isEmpty()) {
            user.setSoftSkills(String.join(", ", profileDto.getSoftSkills()));
        }

        User updated = userRepository.save(user);
        return authService.mapToProfileDto(updated);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getFullProfile() {
        User user = authService.getCurrentUser();
        return authService.mapToProfileDto(user);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        User user = authService.getCurrentUser();
        List<SkillAnalysis> analyses = skillAnalysisRepository.findByUserOrderByCreatedAtDesc(user);

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalAnalyses(analyses.size());

        if (analyses.isEmpty()) {
            stats.setAverageReadiness(0);
            stats.setSkillsIdentified(0);
            stats.setSkillsGapCount(0);
            stats.setTopTargetRole(user.getTargetRole() != null ? user.getTargetRole() : "Not specified");
            stats.setCurrentReadinessLevel("Getting Started");
            return stats;
        }

        int totalReadiness = 0;
        int totalPresent = 0;
        int totalMissing = 0;

        for (SkillAnalysis analysis : analyses) {
            totalReadiness += analysis.getOverallMatchScore();
            if (analysis.getPresentSkills() != null && !analysis.getPresentSkills().isBlank()) {
                totalPresent += analysis.getPresentSkills().split(",").length;
            }
            if (analysis.getMissingSkills() != null && !analysis.getMissingSkills().isBlank()) {
                totalMissing += analysis.getMissingSkills().split(",").length;
            }
        }

        SkillAnalysis latest = analyses.get(0);
        stats.setAverageReadiness(totalReadiness / analyses.size());
        stats.setSkillsIdentified(totalPresent);
        stats.setSkillsGapCount(totalMissing);
        stats.setTopTargetRole(latest.getTargetRole());
        stats.setCurrentReadinessLevel(latest.getReadinessLevel());

        return stats;
    }
}
