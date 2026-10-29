package com.careergap;

import com.careergap.dto.*;
import com.careergap.entity.*;
import com.careergap.repository.*;
import com.careergap.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ResumeBuilderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @Autowired
    private UserExperienceRepository userExperienceRepository;

    @Autowired
    private UserCertificationRepository userCertificationRepository;

    @Autowired
    private SkillVerificationRepository skillVerificationRepository;

    @Autowired
    private ResumeVersionRepository resumeVersionRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private LearningRoadmapRepository roadmapRepository;

    @Autowired
    private LearningModuleRepository learningModuleRepository;

    @Autowired
    private UserModuleProgressRepository userModuleProgressRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private MockTestAttemptRepository mockTestAttemptRepository;

    @Autowired
    private SkillAnalysisRepository skillAnalysisRepository;

    @Autowired
    private PracticeSubmissionRepository practiceSubmissionRepository;

    @Autowired
    private AiMentorMessageRepository aiMentorMessageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private String token;

    @BeforeEach
    void setUp() {
        resumeVersionRepository.deleteAll();
        resumeRepository.deleteAll();
        userModuleProgressRepository.deleteAll();
        practiceSubmissionRepository.deleteAll();
        mockTestAttemptRepository.deleteAll();
        mockTestRepository.deleteAll();
        aiMentorMessageRepository.deleteAll();
        learningModuleRepository.deleteAll();
        roadmapRepository.deleteAll();
        skillVerificationRepository.deleteAll();
        skillAnalysisRepository.deleteAll();
        userSkillRepository.deleteAll();
        userProjectRepository.deleteAll();
        userExperienceRepository.deleteAll();
        userCertificationRepository.deleteAll();
        userRepository.deleteAll();

        // Create primary test user
        testUser = new User();
        testUser.setEmail("alex.career@example.com");
        testUser.setPassword(passwordEncoder.encode("Password123!"));
        testUser.setFullName("Alex Morgan");
        testUser.setPhoneNumber("+1-555-0199");
        testUser.setLocation("San Francisco, CA");
        testUser.setDegree("B.S.");
        testUser.setBranch("Computer Science");
        testUser.setCollege("Stanford University");
        testUser.setGraduationYear(2025);
        testUser.setTargetRole("Backend Engineer");
        testUser.setBio("Passionate backend engineer dedicated to building scalable distributed microservices.");
        testUser.setOnboardingCompleted(true);
        testUser = userRepository.save(testUser);

        token = "Bearer " + jwtTokenProvider.generateTokenFromUserId(testUser.getId(), testUser.getEmail());

        // Add skills
        userSkillRepository.save(new UserSkill("Java", "Advanced", "Technical", testUser));
        userSkillRepository.save(new UserSkill("Spring Boot", "Intermediate", "Technical", testUser));
        userSkillRepository.save(new UserSkill("PostgreSQL", "Intermediate", "Technical", testUser));

        // Add project
        UserProject project = new UserProject(
                "E-Commerce Microservices",
                "Built backend microservices using Spring Boot and PostgreSQL. Handled token authentication and order processing.",
                "Java, Spring Boot, PostgreSQL, Docker",
                "https://github.com/alex/ecommerce-microservices",
                "https://ecommerce.alexmorgan.dev"
        );
        project.setUser(testUser);
        userProjectRepository.save(project);

        // Add experience
        UserExperience experience = new UserExperience(
                "Software Engineering Intern",
                "CloudTech Labs",
                "May 2024 - Aug 2024",
                "Worked on REST APIs for cloud metrics. Participated in daily standups and sprint planning."
        );
        experience.setUser(testUser);
        userExperienceRepository.save(experience);

        // Add certification
        UserCertification certification = new UserCertification(
                "AWS Certified Cloud Practitioner",
                "Amazon Web Services",
                2024,
                "https://aws.amazon.com/verification/12345"
        );
        certification.setUser(testUser);
        userCertificationRepository.save(certification);
    }

    @Test
    void testExtractProfileResumeData_IncludesProfileSkillsAndVerification() throws Exception {
        // Mark Java as verified
        SkillVerification verification = new SkillVerification(testUser, "Java", "Backend Engineer");
        verification.setStatus("VERIFIED");
        verification.setVerificationMethod("ASSESSMENT_SCORE");
        verification.setAssessmentScore(92);
        verification.setVerificationBadge("Verified Competency");
        verification.setVerifiedAt(LocalDateTime.now());
        skillVerificationRepository.save(verification);

        MvcResult result = mockMvc.perform(get("/api/resume/extract-profile")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alex Morgan"))
                .andExpect(jsonPath("$.data.email").value("alex.career@example.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+1-555-0199"))
                .andExpect(jsonPath("$.data.skills").isArray())
                .andExpect(jsonPath("$.data.projects").isArray())
                .andExpect(jsonPath("$.data.experiences").isArray())
                .andExpect(jsonPath("$.data.education").isArray())
                .andExpect(jsonPath("$.data.certifications").isArray())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertTrue(json.contains("Stanford University"));
        assertTrue(json.contains("E-Commerce Microservices"));
        assertTrue(json.contains("CloudTech Labs"));
        assertTrue(json.contains("AWS Certified Cloud Practitioner"));
        // Java should be marked verified
        assertTrue(json.contains("\"verified\":true"));
        assertTrue(json.contains("Verified Competency"));
    }

    @Test
    void testPendingSkillUpdates_DistinguishesLearnedAssessedVerified() throws Exception {
        // 1. Learned skill (Module completed)
        LearningRoadmap roadmap = new LearningRoadmap();
        roadmap.setUser(testUser);
        roadmap.setTitle("Backend Engineer Roadmap");
        roadmap.setTargetRole("Backend Engineer");
        roadmap = roadmapRepository.save(roadmap);

        LearningModule module = new LearningModule();
        module.setRoadmap(roadmap);
        module.setSkillName("Docker");
        module.setCategory("DevOps");
        module.setCurrentProficiency("Beginner");
        module.setTargetProficiency("Intermediate");
        module.setEstimatedHours("2 hours");
        module = learningModuleRepository.save(module);

        UserModuleProgress progress = new UserModuleProgress(testUser, module);
        progress.setStatus("COMPLETED");
        progress.setCompletedAt(LocalDateTime.now());
        userModuleProgressRepository.save(progress);

        // 2. Assessed skill (Mock test attempt >= 70%)
        MockTest test = new MockTest();
        test.setUser(testUser);
        test.setTitle("Spring Boot Microservices Evaluation");
        test.setTargetRole("Backend Engineer");
        test.setTotalQuestions(10);
        test = mockTestRepository.save(test);

        MockTestAttempt attempt = new MockTestAttempt();
        attempt.setUser(testUser);
        attempt.setMockTest(test);
        attempt.setScore(85);
        attempt.setTotalQuestions(10);
        attempt.setCorrectCount(8);
        attempt.setAttemptedAt(LocalDateTime.now());
        mockTestAttemptRepository.save(attempt);

        // 3. Verified skill (Rigorous verification)
        SkillVerification verification = new SkillVerification(testUser, "PostgreSQL", "Backend Engineer");
        verification.setStatus("VERIFIED");
        verification.setVerificationMethod("PROJECT_EVIDENCE");
        verification.setProjectTitle("Database Sharding Project");
        verification.setVerificationBadge("Verified Competency");
        verification.setVerifiedAt(LocalDateTime.now());
        skillVerificationRepository.save(verification);

        // Query pending skills endpoint
        MvcResult result = mockMvc.perform(post("/api/resume/pending-skills")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        // Verify 3 distinct skill types exist
        assertTrue(responseBody.contains("\"skillType\":\"VERIFIED\""));
        assertTrue(responseBody.contains("\"skillType\":\"ASSESSED\""));
        assertTrue(responseBody.contains("\"skillType\":\"LEARNED\""));
        // Verify caution note for learned skills that completing a lesson does not imply professional proficiency
        assertTrue(responseBody.contains("Completing a lesson does not imply professional proficiency"));
        assertTrue(responseBody.contains("Verified Competency"));
    }

    @Test
    void testTailorResume_MatchesKeywordsAndSuggestsStrongerBullets() throws Exception {
        ResumeDataDto resume = new ResumeDataDto();
        resume.setFullName("Alex Morgan");
        resume.setEmail("alex.career@example.com");
        resume.setSummary("Backend developer experienced in Java and Spring Boot.");

        ResumeSkillItemDto s1 = new ResumeSkillItemDto("Java", "Technical", "Advanced");
        s1.setVerified(true);
        ResumeSkillItemDto s2 = new ResumeSkillItemDto("Spring Boot", "Technical", "Intermediate");
        resume.getSkills().addAll(List.of(s1, s2));

        ResumeProjectDto project = new ResumeProjectDto(
                "Order Service",
                "Java, Spring Boot",
                "https://github.com/alex/orders",
                "",
                List.of("Worked on REST APIs for order management.")
        );
        resume.getProjects().add(project);

        String jobDesc = "We are seeking a Backend Engineer with strong expertise in Java, Spring Boot, PostgreSQL, Docker, AWS, and Microservices. "
                + "The candidate must design scalable REST APIs and participate in Agile development.";

        ResumeTailorRequest request = new ResumeTailorRequest("Senior Backend Engineer", jobDesc, resume);

        mockMvc.perform(post("/api/resume/tailor")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetKeywords").isArray())
                .andExpect(jsonPath("$.data.matchedKeywords").isArray())
                .andExpect(jsonPath("$.data.missingKeywords").isArray())
                .andExpect(jsonPath("$.data.matchScore").isNumber())
                .andExpect(jsonPath("$.data.bulletImprovements").isArray())
                .andExpect(jsonPath("$.data.keywordRecommendations").isArray());
    }

    @Test
    void testSaveAndRetrieveResumeVersion_PreservesVersions() throws Exception {
        ResumeDataDto resume = new ResumeDataDto();
        resume.setFullName("Alex Morgan");
        resume.setEmail("alex.career@example.com");
        resume.setSummary("Software engineer summary.");

        // Save Version 1
        SaveResumeVersionRequest v1Request = new SaveResumeVersionRequest(
                "Initial Resume",
                "Backend Engineer",
                "Job description text",
                "Initial profile snapshot",
                resume
        );

        mockMvc.perform(post("/api/resume/versions")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(v1Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.versionNumber").value(1))
                .andExpect(jsonPath("$.data.title").value("Initial Resume"));

        // Save Version 2
        resume.setSummary("Updated summary tailored for Fintech systems.");
        SaveResumeVersionRequest v2Request = new SaveResumeVersionRequest(
                "Fintech Tailored Resume",
                "Senior Backend Engineer",
                "Fintech job description",
                "Updated summary and approved verified PostgreSQL badge",
                resume
        );

        MvcResult v2Result = mockMvc.perform(post("/api/resume/versions")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(v2Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.versionNumber").value(2))
                .andExpect(jsonPath("$.data.title").value("Fintech Tailored Resume"))
                .andReturn();

        // Get all versions
        mockMvc.perform(get("/api/resume/versions")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].versionNumber").value(2))
                .andExpect(jsonPath("$.data[1].versionNumber").value(1));

        // Get specific version
        String responseBody = v2Result.getResponse().getContentAsString();
        Long v2Id = objectMapper.readTree(responseBody).path("data").path("id").asLong();

        mockMvc.perform(get("/api/resume/versions/" + v2Id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(v2Id))
                .andExpect(jsonPath("$.data.title").value("Fintech Tailored Resume"));
    }

    @Test
    void testExportPdf_GeneratesValidPdfStream() throws Exception {
        ResumeDataDto resume = new ResumeDataDto();
        resume.setFullName("Alex Morgan");
        resume.setEmail("alex.career@example.com");
        resume.setPhoneNumber("+1 555-0199");
        resume.setLocation("San Francisco, CA");
        resume.setSummary("Motivated software engineer with experience building scalable systems.");

        ResumeSkillItemDto skill1 = new ResumeSkillItemDto("Java", "Technical", "Advanced");
        skill1.setVerified(true);
        skill1.setVerificationBadge("Verified Competency");
        ResumeSkillItemDto skill2 = new ResumeSkillItemDto("Spring Boot", "Technical", "Intermediate");
        resume.setSkills(List.of(skill1, skill2));

        ResumeProjectDto project = new ResumeProjectDto(
                "Distributed Cache",
                "Java, Redis",
                "https://github.com/alex/cache",
                "",
                List.of("Engineered in-memory cache supporting eviction policies.", "Implemented thread-safe concurrency controls.")
        );
        resume.setProjects(List.of(project));

        MvcResult result = mockMvc.perform(post("/api/resume/export-pdf")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resume)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume.pdf\""))
                .andReturn();

        byte[] pdfBytes = result.getResponse().getContentAsByteArray();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF size should be substantial");
 
        // Verify PDF Magic Header bytes "%PDF-"
        String header = new String(pdfBytes, 0, 5);
        assertEquals("%PDF-", header);
    }

    @Test
    void testDownloadVersionPdf_GeneratesPdfFromSavedVersion() throws Exception {
        ResumeDataDto resume = new ResumeDataDto();
        resume.setFullName("Alex Morgan");
        resume.setEmail("alex.career@example.com");
        resume.setSummary("Backend engineer summary");

        SaveResumeVersionRequest saveRequest = new SaveResumeVersionRequest(
                "Downloadable Resume",
                "Backend Engineer",
                "Desc",
                "Test download",
                resume
        );

        MvcResult saveResult = mockMvc.perform(post("/api/resume/versions")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long versionId = objectMapper.readTree(saveResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        MvcResult pdfResult = mockMvc.perform(get("/api/resume/versions/" + versionId + "/pdf")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume-v1.pdf\""))
                .andReturn();

        byte[] pdfBytes = pdfResult.getResponse().getContentAsByteArray();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500);
        assertEquals("%PDF-", new String(pdfBytes, 0, 5));
    }

    @Test
    void testDataIsolation_CannotAccessOtherUserResumeVersion() throws Exception {
        // Create User A version
        ResumeDataDto resume = new ResumeDataDto();
        resume.setFullName("Alex Morgan");
        resume.setEmail("alex.career@example.com");

        SaveResumeVersionRequest saveRequest = new SaveResumeVersionRequest(
                "Alex Confidential Resume",
                "Engineer",
                "",
                "v1",
                resume
        );

        MvcResult saveResult = mockMvc.perform(post("/api/resume/versions")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long alexVersionId = objectMapper.readTree(saveResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Create User B
        User userB = new User();
        userB.setEmail("attacker@example.com");
        userB.setPassword(passwordEncoder.encode("Password123!"));
        userB.setFullName("User B");
        userB.setOnboardingCompleted(true);
        userB = userRepository.save(userB);

        String userBToken = "Bearer " + jwtTokenProvider.generateTokenFromUserId(userB.getId(), userB.getEmail());

        // User B attempts to access User A's resume version
        mockMvc.perform(get("/api/resume/versions/" + alexVersionId)
                        .header(HttpHeaders.AUTHORIZATION, userBToken))
                .andExpect(status().isNotFound());

        // User B attempts to download User A's PDF
        mockMvc.perform(get("/api/resume/versions/" + alexVersionId + "/pdf")
                        .header(HttpHeaders.AUTHORIZATION, userBToken))
                .andExpect(status().isNotFound());
    }
}
