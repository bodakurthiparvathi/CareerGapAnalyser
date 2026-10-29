package com.careergap.service;

import com.careergap.dto.*;
import com.careergap.entity.*;
import com.careergap.exception.ResourceNotFoundException;
import com.careergap.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeBuilderService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeBuilderService.class);

    private final ResumeVersionRepository resumeVersionRepository;
    private final SkillVerificationRepository skillVerificationRepository;
    private final UserModuleProgressRepository userModuleProgressRepository;
    private final MockTestAttemptRepository mockTestAttemptRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserExperienceRepository userExperienceRepository;
    private final UserCertificationRepository userCertificationRepository;
    private final PdfResumeGeneratorService pdfResumeGeneratorService;
    private final ObjectMapper objectMapper;

    private static final List<String> COMMON_INDUSTRY_KEYWORDS = List.of(
            "Java", "Spring Boot", "Spring Cloud", "Hibernate", "JPA", "Microservices",
            "Python", "Django", "FastAPI", "Pandas", "NumPy", "TensorFlow", "PyTorch",
            "JavaScript", "TypeScript", "React", "Next.js", "Vue", "Angular", "Node.js", "Express",
            "HTML5", "CSS3", "Tailwind CSS", "REST API", "GraphQL", "PostgreSQL", "MySQL",
            "MongoDB", "Redis", "Elasticsearch", "AWS", "Azure", "GCP", "Docker", "Kubernetes",
            "CI/CD", "Git", "GitHub", "Linux", "Kafka", "RabbitMQ", "JUnit", "Unit Testing",
            "Agile", "Scrum", "System Design", "SQL", "DevOps", "OAuth", "JWT"
    );

    public ResumeBuilderService(ResumeVersionRepository resumeVersionRepository,
                                SkillVerificationRepository skillVerificationRepository,
                                UserModuleProgressRepository userModuleProgressRepository,
                                MockTestAttemptRepository mockTestAttemptRepository,
                                UserSkillRepository userSkillRepository,
                                UserProjectRepository userProjectRepository,
                                UserExperienceRepository userExperienceRepository,
                                UserCertificationRepository userCertificationRepository,
                                PdfResumeGeneratorService pdfResumeGeneratorService,
                                ObjectMapper objectMapper) {
        this.resumeVersionRepository = resumeVersionRepository;
        this.skillVerificationRepository = skillVerificationRepository;
        this.userModuleProgressRepository = userModuleProgressRepository;
        this.mockTestAttemptRepository = mockTestAttemptRepository;
        this.userSkillRepository = userSkillRepository;
        this.userProjectRepository = userProjectRepository;
        this.userExperienceRepository = userExperienceRepository;
        this.userCertificationRepository = userCertificationRepository;
        this.pdfResumeGeneratorService = pdfResumeGeneratorService;
        this.objectMapper = objectMapper;
    }

    /**
     * Extracts existing resume information from user profile, skills, projects,
     * experiences, certifications, and verified credentials.
     */
    @Transactional(readOnly = true)
    public ResumeDataDto extractProfileResumeData(User user) {
        ResumeDataDto dto = new ResumeDataDto();

        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setLocation(user.getLocation());

        // Construct professional summary
        if (user.getBio() != null && !user.getBio().isBlank()) {
            dto.setSummary(user.getBio());
        } else {
            String role = user.getTargetRole() != null ? user.getTargetRole() : "Software Professional";
            String background = user.getDegree() != null ? user.getDegree() : "Engineering";
            if (user.getBranch() != null) {
                background += " in " + user.getBranch();
            }
            dto.setSummary("Goal-driven " + role + " with foundational background in " + background
                    + ". Dedicated to building high-quality, production-ready software solutions with clean architecture and continuous learning.");
        }

        // Education
        if (user.getCollege() != null || user.getDegree() != null) {
            ResumeEducationDto edu = new ResumeEducationDto();
            edu.setInstitution(user.getCollege() != null ? user.getCollege() : "University");
            edu.setDegree(user.getDegree() != null ? user.getDegree() : "Bachelor of Technology");
            edu.setBranch(user.getBranch() != null ? user.getBranch() : "Computer Science");
            edu.setGraduationYear(user.getGraduationYear() != null ? String.valueOf(user.getGraduationYear()) : "");
            edu.setGradeGpa("");
            dto.getEducation().add(edu);
        }

        // Skills (cross-reference with SkillVerifications)
        Map<String, SkillVerification> verifiedSkillsMap = skillVerificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(v -> "VERIFIED".equalsIgnoreCase(v.getStatus()))
                .collect(Collectors.toMap(
                        v -> v.getSkillName().toLowerCase(),
                        v -> v,
                        (existing, replacement) -> existing
                ));

        List<UserSkill> userSkills = userSkillRepository.findByUser(user);
        Set<String> processedSkills = new HashSet<>();

        for (UserSkill us : userSkills) {
            String lowerName = us.getSkillName().toLowerCase();
            processedSkills.add(lowerName);

            ResumeSkillItemDto skillItem = new ResumeSkillItemDto();
            skillItem.setSkillName(us.getSkillName());
            skillItem.setCategory(us.getCategory() != null ? us.getCategory() : "Technical");
            skillItem.setProficiencyLevel(us.getProficiencyLevel() != null ? us.getProficiencyLevel() : "Intermediate");

            if (verifiedSkillsMap.containsKey(lowerName)) {
                skillItem.setVerified(true);
                skillItem.setVerificationBadge("Verified Competency");
                skillItem.setSkillType("VERIFIED");
            } else {
                skillItem.setVerified(false);
                skillItem.setSkillType("CORE");
            }
            dto.getSkills().add(skillItem);
        }

        // Add any verified skills not yet present in UserSkills
        for (SkillVerification sv : verifiedSkillsMap.values()) {
            if (!processedSkills.contains(sv.getSkillName().toLowerCase())) {
                ResumeSkillItemDto skillItem = new ResumeSkillItemDto();
                skillItem.setSkillName(sv.getSkillName());
                skillItem.setCategory("Technical");
                skillItem.setProficiencyLevel("Advanced");
                skillItem.setVerified(true);
                skillItem.setVerificationBadge(sv.getVerificationBadge() != null ? sv.getVerificationBadge() : "Verified Competency");
                skillItem.setSkillType("VERIFIED");
                dto.getSkills().add(skillItem);
                processedSkills.add(sv.getSkillName().toLowerCase());
            }
        }

        // Experiences
        List<UserExperience> experiences = userExperienceRepository.findByUser(user);
        for (UserExperience ue : experiences) {
            ResumeExperienceDto exp = new ResumeExperienceDto();
            exp.setRole(ue.getTitle());
            exp.setCompany(ue.getCompany());
            exp.setDuration(ue.getDuration() != null ? ue.getDuration() : "");
            exp.setBullets(splitToBullets(ue.getDescription()));
            dto.getExperiences().add(exp);
        }

        // Projects
        List<UserProject> projects = userProjectRepository.findByUser(user);
        for (UserProject up : projects) {
            ResumeProjectDto proj = new ResumeProjectDto();
            proj.setTitle(up.getTitle());
            proj.setTechnologies(up.getTechnologies() != null ? up.getTechnologies() : "");
            proj.setGithubUrl(up.getGithubUrl() != null ? up.getGithubUrl() : "");
            proj.setLiveUrl(up.getLiveUrl() != null ? up.getLiveUrl() : "");
            proj.setBullets(splitToBullets(up.getDescription()));
            dto.getProjects().add(proj);
        }

        // Certifications
        List<UserCertification> certifications = userCertificationRepository.findByUser(user);
        for (UserCertification uc : certifications) {
            ResumeCertificationDto cert = new ResumeCertificationDto();
            cert.setName(uc.getName());
            cert.setIssuer(uc.getIssuer());
            cert.setIssueYear(uc.getIssueYear() != null ? String.valueOf(uc.getIssueYear()) : "");
            cert.setCredentialUrl(uc.getCredentialUrl() != null ? uc.getCredentialUrl() : "");
            dto.getCertifications().add(cert);
        }

        return dto;
    }

    /**
     * Distinguish between LEARNED, ASSESSED, and VERIFIED skills.
     * Completing a lesson does NOT automatically imply professional proficiency.
     * Generates pending updates that require candidate review and approval.
     */
    @Transactional(readOnly = true)
    public List<PendingSkillUpdateDto> getPendingSkillUpdates(User user, ResumeDataDto currentResume) {
        List<PendingSkillUpdateDto> updates = new ArrayList<>();
        Set<String> resumeSkillNames = currentResume != null && currentResume.getSkills() != null
                ? currentResume.getSkills().stream()
                        .map(ResumeSkillItemDto::getSkillName)
                        .filter(Objects::nonNull)
                        .map(skillName -> skillName.toLowerCase(Locale.ROOT))
                        .collect(Collectors.toSet())
                : new HashSet<>();

        // 1. Verified skills (Highest confidence: Verified via assessment & project audit)
        List<SkillVerification> verified = skillVerificationRepository.findByUserOrderByCreatedAtDesc(user);
        for (SkillVerification sv : verified) {
            if ("VERIFIED".equalsIgnoreCase(sv.getStatus())) {
                boolean inResume = resumeSkillNames.contains(sv.getSkillName().toLowerCase());
                String badge = sv.getVerificationBadge() != null ? sv.getVerificationBadge() : "Verified Competency";
                String source = sv.getVerificationMethod() != null ? "Verified: " + sv.getVerificationMethod() : "Skill Verification Assessment";

                String bullet = "Demonstrated verified competency in " + sv.getSkillName()
                        + (sv.getProjectTitle() != null ? " by architecting " + sv.getProjectTitle() : "") + ".";

                updates.add(new PendingSkillUpdateDto(
                        sv.getSkillName(),
                        "Technical",
                        "VERIFIED",
                        "Advanced",
                        source,
                        bullet,
                        badge,
                        inResume,
                        "Verified by rigorous technical evaluation. Candidate approved addition recommended."
                ));
            }
        }

        // 2. Assessed skills (Medium confidence: passed mock test or practice assessment)
        List<MockTestAttempt> testAttempts = mockTestAttemptRepository.findByUserOrderByAttemptedAtDesc(user);
        Set<String> assessedSkillsSeen = new HashSet<>();
        for (MockTestAttempt attempt : testAttempts) {
            if (attempt.getScore() >= 70) {
                String role = attempt.getMockTest() != null ? attempt.getMockTest().getTargetRole() : "Role Assessment";
                String testTitle = attempt.getMockTest() != null ? attempt.getMockTest().getTitle() : "Technical Assessment";

                // Extract core skill from test title or role
                String skillName = extractSkillFromTestTitle(testTitle, role);
                if (skillName != null && !assessedSkillsSeen.contains(skillName.toLowerCase())) {
                    assessedSkillsSeen.add(skillName.toLowerCase());
                    boolean inResume = resumeSkillNames.contains(skillName.toLowerCase());

                    updates.add(new PendingSkillUpdateDto(
                            skillName,
                            "Technical",
                            "ASSESSED",
                            "Intermediate",
                            "Mock Test: " + testTitle + " (" + attempt.getScore() + "% score)",
                            "Demonstrated problem-solving competence in " + skillName + " on timed role-aligned evaluation (" + attempt.getScore() + "%).",
                            "Assessed Competency",
                            inResume,
                            "Evaluated via technical test. Review and approve before including on resume."
                    ));
                }
            }
        }

        // 3. Learned skills (Completed lessons/modules - NOT automatically professional proficiency)
        List<UserModuleProgress> moduleProgresses = userModuleProgressRepository.findByUser(user);
        Set<String> learnedSkillsSeen = new HashSet<>();
        for (UserModuleProgress mp : moduleProgresses) {
            if ("COMPLETED".equalsIgnoreCase(mp.getStatus()) && mp.getModule() != null) {
                String skillName = mp.getModule().getSkillName();
                if (skillName != null && !assessedSkillsSeen.contains(skillName.toLowerCase())
                        && !learnedSkillsSeen.contains(skillName.toLowerCase())) {
                    learnedSkillsSeen.add(skillName.toLowerCase());
                    boolean inResume = resumeSkillNames.contains(skillName.toLowerCase());

                    updates.add(new PendingSkillUpdateDto(
                            skillName,
                            "Technical",
                            "LEARNED",
                            "Beginner",
                            "Completed Module: " + skillName,
                            "Completed coursework and hands-on laboratory exercises in " + skillName + ".",
                            "Learned Concept",
                            inResume,
                            "Foundational theory completed. Note: Completing a lesson does not imply professional proficiency. Candidate must review carefully."
                    ));
                }
            }
        }

        // Sort: VERIFIED first, then ASSESSED, then LEARNED
        updates.sort((a, b) -> {
            int scoreA = "VERIFIED".equalsIgnoreCase(a.getSkillType()) ? 3 : ("ASSESSED".equalsIgnoreCase(a.getSkillType()) ? 2 : 1);
            int scoreB = "VERIFIED".equalsIgnoreCase(b.getSkillType()) ? 3 : ("ASSESSED".equalsIgnoreCase(b.getSkillType()) ? 2 : 1);
            return Integer.compare(scoreB, scoreA);
        });

        return updates;
    }

    /**
     * Tailor resume to a target job description.
     * Identifies matched keywords, missing keywords, and recommends stronger ATS-friendly project bullets
     * WITHOUT inventing experience or false qualifications.
     */
    public ResumeTailorResponse tailorResume(User user, ResumeTailorRequest request) {
        ResumeTailorResponse response = new ResumeTailorResponse();
        ResumeDataDto resume = request.getResumeData() != null ? request.getResumeData() : extractProfileResumeData(user);
        String jobDesc = request.getTargetJobDescription();
        String jobTitle = request.getTargetJobTitle() != null ? request.getTargetJobTitle() : "Target Position";

        // 1. Keyword extraction from Job Description
        List<String> targetKeywords = extractKeywordsFromText(jobDesc);
        if (targetKeywords.isEmpty()) {
            targetKeywords = new ArrayList<>(List.of("REST API", "Database", "Git", "Problem Solving"));
        }
        response.setTargetKeywords(targetKeywords);

        // 2. Identify candidate's existing keywords
        String resumeFullText = buildResumeFullText(resume).toLowerCase();
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String kw : targetKeywords) {
            if (resumeFullText.contains(kw.toLowerCase())) {
                matched.add(kw);
            } else {
                missing.add(kw);
            }
        }

        response.setMatchedKeywords(matched);
        response.setMissingKeywords(missing);

        // 3. Match score calculation (realistic transparent formula)
        int matchScore = targetKeywords.isEmpty() ? 50 : Math.min(95, Math.max(30, (int) Math.round((double) matched.size() / targetKeywords.size() * 100.0)));
        response.setMatchScore(matchScore);

        // 4. Keyword recommendations
        List<String> recommendations = new ArrayList<>();
        if (!missing.isEmpty()) {
            List<String> topMissing = missing.stream().limit(5).collect(Collectors.toList());
            recommendations.add("Consider highlighting coursework, side projects, or verified experience with: " + String.join(", ", topMissing) + ".");
        }
        recommendations.add("Keep section headings standard (SUMMARY, TECHNICAL SKILLS, EXPERIENCE, PROJECTS, EDUCATION) for maximum ATS readability.");
        if (matched.size() < 4) {
            recommendations.add("Incorporate relevant core terminology in your project descriptions to demonstrate practical usage.");
        }
        response.setKeywordRecommendations(recommendations);

        // 5. Actionable bullet improvements for projects and experiences (STAR method / strong active verbs)
        // Crucial: Never invent achievements, numbers, or qualifications.
        List<BulletImprovementDto> bulletImprovements = new ArrayList<>();

        if (resume.getProjects() != null) {
            for (ResumeProjectDto proj : resume.getProjects()) {
                if (proj.getBullets() != null) {
                    for (String originalBullet : proj.getBullets()) {
                        String improved = suggestStrongerBullet(originalBullet);
                        if (improved != null && !improved.equalsIgnoreCase(originalBullet)) {
                            bulletImprovements.add(new BulletImprovementDto(
                                    "PROJECTS",
                                    proj.getTitle(),
                                    originalBullet,
                                    improved,
                                    "Upgrades passive phrasing to strong action verbs and emphasizes architectural implementation without fabricating claims."
                            ));
                        }
                    }
                }
            }
        }

        if (resume.getExperiences() != null) {
            for (ResumeExperienceDto exp : resume.getExperiences()) {
                if (exp.getBullets() != null) {
                    for (String originalBullet : exp.getBullets()) {
                        String improved = suggestStrongerBullet(originalBullet);
                        if (improved != null && !improved.equalsIgnoreCase(originalBullet)) {
                            bulletImprovements.add(new BulletImprovementDto(
                                    "EXPERIENCE",
                                    exp.getRole() + " at " + exp.getCompany(),
                                    originalBullet,
                                    improved,
                                    "Enhances ATS scanning by emphasizing technical execution and structured responsibilities."
                            ));
                        }
                    }
                }
            }
        }

        response.setBulletImprovements(bulletImprovements);

        // Tailored resume clone
        ResumeDataDto tailoredResume = cloneResumeData(resume);
        if (jobTitle != null && !jobTitle.isBlank()) {
            String existingSummary = tailoredResume.getSummary() != null ? tailoredResume.getSummary() : "";
            if (!existingSummary.toLowerCase().contains(jobTitle.toLowerCase())) {
                String alignment = matched.isEmpty()
                        ? "Relevant experience is documented in the resume."
                        : "Documented skills aligned to the role include "
                                + String.join(", ", matched.stream().limit(3).collect(Collectors.toList())) + ".";
                tailoredResume.setSummary("Targeting " + jobTitle + ". " + alignment
                        + (existingSummary.isBlank() ? "" : " " + existingSummary));
            }
        }
        response.setTailoredResume(tailoredResume);
        response.setAlignmentSummary(String.format("Found %d matching keywords and %d potential alignment opportunities for '%s'.",
                matched.size(), missing.size(), jobTitle));

        return response;
    }

    /**
     * Save a new resume version.
     */
    @Transactional
    public ResumeVersionDto saveVersion(User user, SaveResumeVersionRequest request) {
        int nextVersionNumber = 1;
        Optional<ResumeVersion> topVersion = resumeVersionRepository.findTopByUserOrderByVersionNumberDesc(user);
        if (topVersion.isPresent()) {
            nextVersionNumber = topVersion.get().getVersionNumber() + 1;
        }

        ResumeVersion version = new ResumeVersion();
        version.setUser(user);
        version.setVersionNumber(nextVersionNumber);
        version.setTitle(request.getTitle() != null && !request.getTitle().isBlank() ? request.getTitle() : "Resume v" + nextVersionNumber);
        version.setTargetJobTitle(request.getTargetJobTitle());
        version.setTargetJobDescription(request.getTargetJobDescription());
        version.setChangeSummary(request.getChangeSummary() != null && !request.getChangeSummary().isBlank()
                ? request.getChangeSummary() : "Saved version " + nextVersionNumber);

        try {
            version.setResumeDataJson(objectMapper.writeValueAsString(request.getResumeData()));
        } catch (Exception e) {
            logger.error("Failed to serialize resume data to JSON", e);
            throw new RuntimeException("Failed to serialize resume data: " + e.getMessage());
        }

        ResumeVersion saved = resumeVersionRepository.save(version);
        return mapToDto(saved);
    }

    /**
     * Retrieve all saved resume versions for a user.
     */
    @Transactional(readOnly = true)
    public List<ResumeVersionDto> getVersions(User user) {
        return resumeVersionRepository.findByUserOrderByVersionNumberDesc(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific resume version by ID, verifying user ownership.
     */
    @Transactional(readOnly = true)
    public ResumeVersionDto getVersion(Long id, User user) {
        ResumeVersion version = resumeVersionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("ResumeVersion", "id", id));
        return mapToDto(version);
    }

    /**
     * Generate PDF bytes for an existing saved version.
     */
    @Transactional(readOnly = true)
    public byte[] generatePdfForVersion(Long id, User user) {
        ResumeVersion version = resumeVersionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("ResumeVersion", "id", id));
        ResumeDataDto data = deserializeResumeData(version.getResumeDataJson());
        return pdfResumeGeneratorService.generateResumePdf(data);
    }

    /**
     * Generate PDF bytes directly from live editor data.
     */
    public byte[] generatePdfFromData(ResumeDataDto resumeData) {
        return pdfResumeGeneratorService.generateResumePdf(resumeData);
    }

    // --- Private Helper Methods ---

    private List<String> splitToBullets(String text) {
        List<String> bullets = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return bullets;
        }
        String[] lines = text.split("\\r?\\n|\\u2022|(?<=\\.)\\s+(?=[A-Z])");
        for (String line : lines) {
            String trimmed = line.replaceFirst("^[-*•\\s]+", "").trim();
            if (!trimmed.isEmpty()) {
                bullets.add(trimmed);
            }
        }
        return bullets;
    }

    private String extractSkillFromTestTitle(String title, String role) {
        for (String skill : COMMON_INDUSTRY_KEYWORDS) {
            if (title.toLowerCase().contains(skill.toLowerCase()) || (role != null && role.toLowerCase().contains(skill.toLowerCase()))) {
                return skill;
            }
        }
        return "Core Architecture";
    }

    private List<String> extractKeywordsFromText(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        Set<String> matched = new LinkedHashSet<>();
        String lower = text.toLowerCase();

        for (String kw : COMMON_INDUSTRY_KEYWORDS) {
            // Match whole word or exact pattern
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(kw.toLowerCase()) + "\\b", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(lower).find()) {
                matched.add(kw);
            }
        }
        return new ArrayList<>(matched);
    }

    private String buildResumeFullText(ResumeDataDto resume) {
        StringBuilder sb = new StringBuilder();
        if (resume.getSummary() != null) sb.append(resume.getSummary()).append(" ");
        if (resume.getSkills() != null) {
            for (ResumeSkillItemDto s : resume.getSkills()) {
                sb.append(s.getSkillName()).append(" ");
            }
        }
        if (resume.getExperiences() != null) {
            for (ResumeExperienceDto e : resume.getExperiences()) {
                sb.append(e.getRole()).append(" ").append(e.getCompany()).append(" ");
                if (e.getBullets() != null) sb.append(String.join(" ", e.getBullets())).append(" ");
            }
        }
        if (resume.getProjects() != null) {
            for (ResumeProjectDto p : resume.getProjects()) {
                sb.append(p.getTitle()).append(" ").append(p.getTechnologies()).append(" ");
                if (p.getBullets() != null) sb.append(String.join(" ", p.getBullets())).append(" ");
            }
        }
        return sb.toString();
    }

    private String suggestStrongerBullet(String bullet) {
        if (bullet == null || bullet.isBlank()) return bullet;
        String trimmed = bullet.trim();

        // Check if bullet starts with weak/passive verbs
        String lower = trimmed.toLowerCase();
        if (lower.startsWith("worked on") || lower.startsWith("helped") || lower.startsWith("responsible for")
                || lower.startsWith("made") || lower.startsWith("did") || lower.startsWith("created a basic")
                || lower.startsWith("built a small")) {

            String rest = trimmed.replaceFirst("^(?i)(worked on|helped with|responsible for|made|did|created a basic|built a small)\\s*", "");
            if (rest.length() > 0) {
                // Capitalize first letter
                rest = Character.toUpperCase(rest.charAt(0)) + rest.substring(1);
            }

            // Pick an active verb based on tech or context
            String verb = lower.startsWith("helped") ? "Supported"
                    : (lower.startsWith("made") || lower.startsWith("did")
                            || lower.startsWith("created a basic") || lower.startsWith("built a small"))
                                    ? "Developed" : "Contributed to";
            String improved = verb + " " + rest;
            return improved.endsWith(".") ? improved : improved + ".";
        }

        // If bullet doesn't end with a period, ensure standard ATS punctuation
        if (!trimmed.endsWith(".")) {
            return trimmed + ".";
        }

        return trimmed;
    }

    private ResumeDataDto cloneResumeData(ResumeDataDto original) {
        try {
            String json = objectMapper.writeValueAsString(original);
            return objectMapper.readValue(json, ResumeDataDto.class);
        } catch (Exception e) {
            logger.warn("Clone failed, returning original", e);
            return original;
        }
    }

    private ResumeDataDto deserializeResumeData(String json) {
        if (json == null || json.isBlank()) return new ResumeDataDto();
        try {
            return objectMapper.readValue(json, ResumeDataDto.class);
        } catch (Exception e) {
            logger.error("Failed to parse resume data JSON", e);
            return new ResumeDataDto();
        }
    }

    private ResumeVersionDto mapToDto(ResumeVersion entity) {
        ResumeVersionDto dto = new ResumeVersionDto();
        dto.setId(entity.getId());
        dto.setVersionNumber(entity.getVersionNumber());
        dto.setTitle(entity.getTitle());
        dto.setTargetJobTitle(entity.getTargetJobTitle());
        dto.setTargetJobDescription(entity.getTargetJobDescription());
        dto.setChangeSummary(entity.getChangeSummary());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setResumeData(deserializeResumeData(entity.getResumeDataJson()));

        if (entity.getTailoredKeywordsJson() != null && !entity.getTailoredKeywordsJson().isBlank()) {
            try {
                List<String> kw = objectMapper.readValue(entity.getTailoredKeywordsJson(), new TypeReference<List<String>>() {});
                dto.setTailoredKeywords(kw);
            } catch (Exception ignored) {
            }
        }
        return dto;
    }
}
