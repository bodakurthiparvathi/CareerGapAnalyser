package com.careergap;

import com.careergap.dto.*;
import com.careergap.entity.*;
import com.careergap.repository.*;
import com.careergap.service.AuthService;
import com.careergap.service.PersonalizedLearningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class PersonalizedLearningIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LearningRoadmapRepository roadmapRepository;

    @Autowired
    private SkillAnalysisRepository skillAnalysisRepository;

    @Autowired
    private LearningModuleRepository moduleRepository;

    @Autowired
    private UserModuleProgressRepository progressRepository;

    @Autowired
    private SkillVerificationRepository verificationRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private MockTestAttemptRepository mockTestAttemptRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PersonalizedLearningService learningService;

    private String userToken;
    private User testUser;
    private LearningRoadmap testRoadmap;

    @BeforeEach
    void setUp() {
        mockTestAttemptRepository.deleteAll();
        mockTestRepository.deleteAll();
        verificationRepository.deleteAll();
        progressRepository.deleteAll();
        moduleRepository.deleteAll();
        roadmapRepository.deleteAll();
        skillAnalysisRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User("learn.tester@careergap.ai", passwordEncoder.encode("SecurePass123!"), "Learning Candidate", "Full Stack Java Developer");
        testUser.setDegree("B.Tech Computer Science");
        testUser = userRepository.save(testUser);

        // Login to get token
        AuthRequest loginReq = new AuthRequest("learn.tester@careergap.ai", "SecurePass123!");
        AuthResponse loginRes = authService.login(loginReq);
        userToken = loginRes.getToken();

        // Create test SkillAnalysis and Roadmap
        SkillAnalysis analysis = new SkillAnalysis();
        analysis.setUser(testUser);
        analysis.setTargetRole("Full Stack Java Developer");
        analysis.setJobDescription("Looking for a Full Stack Java Developer with Spring Boot, PostgreSQL, Docker, and Kafka.");
        analysis.setOverallMatchScore(65);
        analysis.setTechnicalScore(60);
        analysis.setSoftSkillsScore(80);
        analysis.setReadinessLevel("High Job Readiness");
        analysis.setMissingSkills("Docker,Kafka,PostgreSQL");
        analysis.setPresentSkills("Java,Spring Boot,Git");
        SkillAnalysis savedAnalysis = skillAnalysisRepository.save(analysis);

        LearningRoadmap roadmap = new LearningRoadmap();
        roadmap.setUser(testUser);
        roadmap.setSkillAnalysis(savedAnalysis);
        roadmap.setTitle("Personalized Roadmap for Full Stack Java Developer");
        roadmap.setTargetRole("Full Stack Java Developer");
        roadmap.setEstimatedWeeks(8);
        testRoadmap = roadmapRepository.save(roadmap);
    }

    @Test
    void testGetAndGenerateRoadmapModules() throws Exception {
        mockMvc.perform(get("/api/learning/roadmaps/" + testRoadmap.getId() + "/modules")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].skillName").exists())
                .andExpect(jsonPath("$.data[0].whyItMatters").exists())
                .andExpect(jsonPath("$.data[0].learningObjectives").isArray())
                .andExpect(jsonPath("$.data[0].practiceQuestions").isArray());

        List<LearningModule> modules = moduleRepository.findByRoadmapOrderByOrderIndexAsc(testRoadmap);
        assertFalse(modules.isEmpty(), "Modules should be persisted in database");
    }

    @Test
    void testEvaluatePracticeQuestionAndTrackStreak() throws Exception {
        // Generate modules first
        List<LearningModuleDto> moduleDtos = learningService.getOrGenerateModules(testRoadmap.getId());
        Long moduleId = moduleDtos.get(0).getId();

        // 1. Submit correct Multiple Choice Answer (index 0)
        PracticeSubmissionRequest mcqReq = new PracticeSubmissionRequest(0, "MULTIPLE_CHOICE", "0");

        mockMvc.perform(post("/api/learning/modules/" + moduleId + "/practice/evaluate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mcqReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.correct").value(true))
                .andExpect(jsonPath("$.data.score").value(100))
                .andExpect(jsonPath("$.data.streakDays").value(1));

        // 2. Submit Conceptual Question
        PracticeSubmissionRequest conceptReq = new PracticeSubmissionRequest(1, "CONCEPTUAL",
                "It manages separation of concerns, modular architecture, and decoupling dependencies to enable testability.");

        mockMvc.perform(post("/api/learning/modules/" + moduleId + "/practice/evaluate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conceptReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(true))
                .andExpect(jsonPath("$.data.score").isNumber());
    }

    @Test
    void testUpdateModuleProgress() throws Exception {
        List<LearningModuleDto> moduleDtos = learningService.getOrGenerateModules(testRoadmap.getId());
        Long moduleId = moduleDtos.get(0).getId();

        ModuleProgressUpdateRequest req = new ModuleProgressUpdateRequest("COMPLETED", "Mastered connection pooling and configuration");

        mockMvc.perform(post("/api/learning/modules/" + moduleId + "/progress")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.userNotes").value("Mastered connection pooling and configuration"));
    }

    @Test
    void testDailyStudyPreferenceIsSavedOnRoadmap() throws Exception {
        mockMvc.perform(put("/api/learning/roadmaps/" + testRoadmap.getId() + "/study-preferences")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dailyStudyMinutes\":45}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyStudyMinutes").value(45));

        mockMvc.perform(get("/api/learning/dashboard")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyStudyMinutes").value(45));

        mockMvc.perform(put("/api/learning/roadmaps/" + testRoadmap.getId() + "/study-preferences")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dailyStudyMinutes\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGeneratedLearningResourcesAreCuratedAndProgressRemainsPersistent() throws Exception {
        List<LearningModuleDto> modules = learningService.getOrGenerateModules(testRoadmap.getId());
        assertFalse(modules.isEmpty());
        LearningModuleDto postgresModule = modules.stream()
                .filter(module -> module.getSkillName().equalsIgnoreCase("PostgreSQL"))
                .findFirst()
                .orElseThrow();
        assertTrue(postgresModule.getSuitableResources().stream()
                .anyMatch(resource -> "https://www.postgresql.org/docs/current/tutorial.html".equals(resource.get("url"))));
        assertTrue(postgresModule.getLearningObjectives().size() >= 3);
        assertFalse(postgresModule.getPracticeExercises().isEmpty());

        mockMvc.perform(post("/api/learning/modules/" + postgresModule.getId() + "/progress")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        mockMvc.perform(get("/api/learning/modules/" + postgresModule.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void testSkillVerificationFlow() throws Exception {
        // 1. Verification with Project Evidence
        SkillVerificationRequest projReq = new SkillVerificationRequest();
        projReq.setSkillName("Docker");
        projReq.setTargetRole("Full Stack Java Developer");
        projReq.setVerificationMethod("PROJECT_EVIDENCE");
        projReq.setProjectTitle("Multi-Container Microservices Deployment");
        projReq.setProjectGithubUrl("https://github.com/learn-tester/docker-microservices");
        projReq.setProjectLiveUrl("https://docker-demo.careergap.ai");
        projReq.setProjectEvidenceDetails("Containerized Spring Boot and PostgreSQL backend with custom multi-stage Dockerfile and healthcheck parameters.");

        mockMvc.perform(post("/api/learning/verify-skill")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("VERIFIED"))
                .andExpect(jsonPath("$.data.verificationBadge").exists());

        // 2. Fetch User Verifications
        mockMvc.perform(get("/api/learning/verifications")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].skillName").value("Docker"))
                .andExpect(jsonPath("$.data[0].status").value("VERIFIED"));
    }

    @Test
    void testMockTestGenerationAndSubmission() throws Exception {
        // 1. Generate Mock Test
        String resStr = mockMvc.perform(post("/api/learning/mock-tests/generate?targetRole=Full+Stack+Java+Developer")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalQuestions").value(10))
                .andReturn().getResponse().getContentAsString();

        Long testId = objectMapper.readTree(resStr).path("data").path("id").asLong();

        // 2. Submit Mock Test
        MockTestSubmissionRequest subReq = new MockTestSubmissionRequest();
        // Answer first 8 questions with correct option 0, and last 2 with wrong option 1
        subReq.setAnswers(Map.of(
                1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 1, 10, 1
        ));

        mockMvc.perform(post("/api/learning/mock-tests/" + testId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(80))
                .andExpect(jsonPath("$.data.passed").value(true))
                .andExpect(jsonPath("$.data.strengths").isArray())
                .andExpect(jsonPath("$.data.weaknesses").isArray())
                .andExpect(jsonPath("$.data.revisionTopics").isArray());
    }

    @Test
    void testAiMentorChatAndHistory() throws Exception {
        MentorChatRequest chatReq = new MentorChatRequest("What should I learn next to improve my chances for a Java developer job?");

        mockMvc.perform(post("/api/mentor/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.response").exists())
                .andExpect(jsonPath("$.data.suggestedNextActions").isArray());

        // Check history
        mockMvc.perform(get("/api/mentor/history")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].sender").exists());
    }

    @Test
    void testNextBestActionRecommendation() throws Exception {
        mockMvc.perform(get("/api/learning/next-best-action")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.actionType").exists())
                .andExpect(jsonPath("$.data.targetUrl").exists());
    }

    @Test
    void testCompleteDashboardAggregation() throws Exception {
        mockMvc.perform(get("/api/learning/dashboard")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.modules").isArray())
                .andExpect(jsonPath("$.data.nextBestAction").exists())
                .andExpect(jsonPath("$.data.currentStreakDays").isNumber());
    }
}
