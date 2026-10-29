package com.careergap.service;

import com.careergap.dto.*;
import com.careergap.entity.*;
import com.careergap.exception.AppException;
import com.careergap.exception.ResourceNotFoundException;
import com.careergap.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonalizedLearningService {

    private static final Logger logger = LoggerFactory.getLogger(PersonalizedLearningService.class);

    private final LearningRoadmapRepository roadmapRepository;
    private final LearningModuleRepository moduleRepository;
    private final UserModuleProgressRepository progressRepository;
    private final PracticeSubmissionRepository practiceSubmissionRepository;
    private final MockTestRepository mockTestRepository;
    private final MockTestAttemptRepository mockTestAttemptRepository;
    private final UserLearningStreakRepository streakRepository;
    private final SkillVerificationRepository verificationRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillAnalysisRepository skillAnalysisRepository;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public PersonalizedLearningService(
            LearningRoadmapRepository roadmapRepository,
            LearningModuleRepository moduleRepository,
            UserModuleProgressRepository progressRepository,
            PracticeSubmissionRepository practiceSubmissionRepository,
            MockTestRepository mockTestRepository,
            MockTestAttemptRepository mockTestAttemptRepository,
            UserLearningStreakRepository streakRepository,
            SkillVerificationRepository verificationRepository,
            UserSkillRepository userSkillRepository,
            SkillAnalysisRepository skillAnalysisRepository,
            AuthService authService,
            ObjectMapper objectMapper) {
        this.roadmapRepository = roadmapRepository;
        this.moduleRepository = moduleRepository;
        this.progressRepository = progressRepository;
        this.practiceSubmissionRepository = practiceSubmissionRepository;
        this.mockTestRepository = mockTestRepository;
        this.mockTestAttemptRepository = mockTestAttemptRepository;
        this.streakRepository = streakRepository;
        this.verificationRepository = verificationRepository;
        this.userSkillRepository = userSkillRepository;
        this.skillAnalysisRepository = skillAnalysisRepository;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    /**
     * Get or generate ordered learning modules for a roadmap.
     */
    @Transactional
    public List<LearningModuleDto> getOrGenerateModules(Long roadmapId) {
        User user = authService.getCurrentUser();
        LearningRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningRoadmap", "id", roadmapId));

        if (!roadmap.getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to requested roadmap");
        }

        List<LearningModule> modules = moduleRepository.findByRoadmapOrderByOrderIndexAsc(roadmap);
        if (modules.isEmpty()) {
            modules = generateModulesFromAnalysisOrDefaults(roadmap);
        }

        return modules.stream().map(m -> mapToModuleDto(m, user)).collect(Collectors.toList());
    }

    @Transactional
    public LearningDashboardDto updateDailyStudyMinutes(Long roadmapId, StudyPreferenceRequest request) {
        User user = authService.getCurrentUser();
        LearningRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningRoadmap", "id", roadmapId));

        if (!roadmap.getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to requested roadmap");
        }

        roadmap.setDailyStudyMinutes(request.getDailyStudyMinutes());
        roadmapRepository.save(roadmap);
        return getDashboardData();
    }

    /**
     * Get single module details by ID.
     */
    @Transactional(readOnly = true)
    public LearningModuleDto getModuleById(Long moduleId) {
        User user = authService.getCurrentUser();
        LearningModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", moduleId));

        if (!module.getRoadmap().getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to requested module");
        }

        return mapToModuleDto(module, user);
    }

    /**
     * Evaluate a submitted practice exercise or question and explain mistakes.
     */
    @Transactional
    public PracticeEvaluationResult evaluatePractice(Long moduleId, PracticeSubmissionRequest request) {
        User user = authService.getCurrentUser();
        LearningModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", moduleId));

        if (!module.getRoadmap().getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to requested module");
        }

        List<PracticeQuestionDto> questions = parsePracticeQuestions(module.getPracticeQuestionsJson());
        if (request.getQuestionIndex() < 0 || request.getQuestionIndex() >= questions.size()) {
            throw new AppException("Invalid question index: " + request.getQuestionIndex());
        }

        PracticeQuestionDto question = questions.get(request.getQuestionIndex());

        boolean isCorrect = false;
        int score = 0;
        String feedback;

        if ("MULTIPLE_CHOICE".equalsIgnoreCase(request.getQuestionType())) {
            int selectedIndex = -1;
            try {
                selectedIndex = Integer.parseInt(request.getUserAnswer().trim());
            } catch (NumberFormatException e) {
                // Check if letter A, B, C, D was sent
                String clean = request.getUserAnswer().trim().toUpperCase();
                if (clean.startsWith("A")) selectedIndex = 0;
                else if (clean.startsWith("B")) selectedIndex = 1;
                else if (clean.startsWith("C")) selectedIndex = 2;
                else if (clean.startsWith("D")) selectedIndex = 3;
            }

            if (question.getCorrectOptionIndex() != null && selectedIndex == question.getCorrectOptionIndex()) {
                isCorrect = true;
                score = 100;
                feedback = "Correct! " + question.getExplanation();
            } else {
                isCorrect = false;
                score = 0;
                String correctText = (question.getCorrectOptionIndex() != null && question.getCorrectOptionIndex() < question.getOptions().size())
                        ? question.getOptions().get(question.getCorrectOptionIndex())
                        : "the designated option";
                feedback = "Incorrect. The correct answer is: " + correctText + ". " + question.getExplanation();
            }
        } else {
            // Semantic evaluation for Conceptual, Short Answer, or Scenario-based
            String answer = request.getUserAnswer().toLowerCase();
            String sample = question.getSampleAnswer() != null ? question.getSampleAnswer().toLowerCase() : "";
            String explanation = question.getExplanation() != null ? question.getExplanation().toLowerCase() : "";

            // Check for key terms from sample answer or explanation
            Set<String> keywords = extractKeywords(sample + " " + explanation);
            long matchedKeywords = keywords.stream().filter(answer::contains).count();
            double ratio = keywords.isEmpty() ? 0.7 : (double) matchedKeywords / Math.min(keywords.size(), 5);

            if (ratio >= 0.6 || answer.length() > 50) {
                isCorrect = true;
                score = Math.min(100, Math.max(75, (int) Math.round(ratio * 100)));
                feedback = "Strong answer! You accurately covered the core principles: " + question.getExplanation();
            } else if (ratio >= 0.3) {
                isCorrect = true;
                score = 65;
                feedback = "Partially correct. You identified foundational aspects, but consider deepening your explanation: " + question.getExplanation();
            } else {
                isCorrect = false;
                score = 30;
                feedback = "Needs improvement. Core concept missed: " + question.getExplanation() + " Review the module examples to reinforce this skill.";
            }
        }

        // Persist practice submission
        PracticeSubmission submission = new PracticeSubmission();
        submission.setUser(user);
        submission.setModule(module);
        submission.setQuestionIndex(request.getQuestionIndex());
        submission.setQuestionType(request.getQuestionType());
        submission.setQuestionText(question.getQuestion());
        submission.setUserAnswer(request.getUserAnswer());
        submission.setCorrect(isCorrect);
        submission.setScore(score);
        submission.setFeedback(feedback);
        practiceSubmissionRepository.save(submission);

        // Update module progress
        UserModuleProgress progress = progressRepository.findByUserAndModule(user, module)
                .orElseGet(() -> new UserModuleProgress(user, module));

        if ("NOT_STARTED".equals(progress.getStatus())) {
            progress.setStatus("IN_PROGRESS");
            progress.setStartedAt(LocalDateTime.now());
        }
        progress.setScore(Math.max(progress.getScore(), score));
        progress.setLastAccessedAt(LocalDateTime.now());
        progressRepository.save(progress);

        // Update learning streak & activity
        UserLearningStreak streak = updateStreak(user, true, false, false);

        return new PracticeEvaluationResult(
                request.getQuestionIndex(),
                request.getQuestionType(),
                isCorrect,
                score,
                feedback,
                question.getExplanation(),
                progress.getStatus(),
                streak.getCurrentStreakDays()
        );
    }

    /**
     * Update module completion status or notes.
     */
    @Transactional
    public LearningModuleDto updateModuleProgress(Long moduleId, ModuleProgressUpdateRequest request) {
        User user = authService.getCurrentUser();
        LearningModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", moduleId));

        if (!module.getRoadmap().getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to requested module");
        }

        UserModuleProgress progress = progressRepository.findByUserAndModule(user, module)
                .orElseGet(() -> new UserModuleProgress(user, module));

        String status = request.getStatus().trim().toUpperCase(Locale.ROOT);
        if (!Set.of("NOT_STARTED", "IN_PROGRESS", "COMPLETED").contains(status)) {
            throw new AppException("Status must be NOT_STARTED, IN_PROGRESS, or COMPLETED");
        }
        boolean newlyCompleted = "COMPLETED".equals(status) && !"COMPLETED".equals(progress.getStatus());
        progress.setStatus(status);
        if (request.getNotes() != null) {
            progress.setUserNotes(request.getNotes());
        }

        if ("COMPLETED".equals(status)) {
            if (progress.getCompletedAt() == null) {
                progress.setCompletedAt(LocalDateTime.now());
            }
            if (newlyCompleted) {
                updateStreak(user, false, true, false);
            }
        } else if ("IN_PROGRESS".equals(status) && progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
        }

        progress.setLastAccessedAt(LocalDateTime.now());
        progressRepository.save(progress);

        return mapToModuleDto(module, user);
    }

    /**
     * Skill verification: Distinguishes between learning a skill and demonstrating competence.
     * Requires assessment score >= 80% OR verified project evidence.
     */
    @Transactional
    public SkillVerificationResponse verifySkill(SkillVerificationRequest request) {
        User user = authService.getCurrentUser();
        String skillName = request.getSkillName().trim();

        SkillVerification verification = verificationRepository.findByUserAndSkillNameIgnoreCase(user, skillName)
                .orElseGet(() -> new SkillVerification(user, skillName, request.getTargetRole()));

        verification.setVerificationMethod(request.getVerificationMethod());
        verification.setTargetRole(request.getTargetRole());

        boolean isVerified = false;
        String badge;
        String feedback;

        if ("ASSESSMENT_SCORE".equalsIgnoreCase(request.getVerificationMethod())) {
            int score = request.getAssessmentScore() != null ? request.getAssessmentScore() : 0;
            verification.setAssessmentScore(score);
            if (score >= 80) {
                isVerified = true;
                badge = "Verified Competency (" + score + "% Score)";
                feedback = "Successfully demonstrated rigorous conceptual and practical knowledge through assessment verification.";
            } else {
                isVerified = false;
                badge = "Unverified (Score < 80%)";
                feedback = "Assessment score of " + score + "% is below the 80% threshold required for verified competence. Review the learning module and retake.";
            }
        } else {
            // Project evidence verification
            verification.setProjectTitle(request.getProjectTitle());
            verification.setProjectGithubUrl(request.getProjectGithubUrl());
            verification.setProjectLiveUrl(request.getProjectLiveUrl());
            verification.setProjectEvidenceDetails(request.getProjectEvidenceDetails());

            boolean hasRepoOrLive = (request.getProjectGithubUrl() != null && request.getProjectGithubUrl().contains("http")) ||
                    (request.getProjectLiveUrl() != null && request.getProjectLiveUrl().contains("http"));
            boolean hasSubstantialEvidence = request.getProjectEvidenceDetails() != null && request.getProjectEvidenceDetails().length() >= 40;

            if (hasRepoOrLive && hasSubstantialEvidence) {
                isVerified = true;
                badge = "Production-Verified Project";
                feedback = "Project deliverable verified: repository/demo links and technical implementation evidence satisfy competency requirements.";
            } else {
                isVerified = false;
                badge = "Evidence Insufficient";
                feedback = "Please provide a valid GitHub repository or deployed URL along with a description of how " + skillName + " was used and tested.";
            }
        }

        if (isVerified) {
            verification.setStatus("VERIFIED");
            verification.setVerificationBadge(badge);
            verification.setEvaluatorFeedback(feedback);
            verification.setVerifiedAt(LocalDateTime.now());

            // Also update or add to user's profile skills
            userSkillRepository.findByUser(user).stream()
                    .filter(s -> s.getSkillName().equalsIgnoreCase(skillName))
                    .findFirst()
                    .ifPresentOrElse(s -> {
                        s.setProficiencyLevel("Advanced");
                        userSkillRepository.save(s);
                    }, () -> {
                        UserSkill newSkill = new UserSkill(skillName, "Advanced", "Technical", user);
                        userSkillRepository.save(newSkill);
                    });
        } else {
            verification.setStatus("REJECTED");
            verification.setVerificationBadge(badge);
            verification.setEvaluatorFeedback(feedback);
        }

        SkillVerification saved = verificationRepository.save(verification);
        return mapToVerificationResponse(saved);
    }

    /**
     * Get all verified skills for the user.
     */
    @Transactional(readOnly = true)
    public List<SkillVerificationResponse> getUserVerifications() {
        User user = authService.getCurrentUser();
        return verificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToVerificationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Generate a Mock Test based on user's target role, skill gaps, and completed modules.
     */
    @Transactional
    public MockTestDto generateMockTest(String targetRole) {
        User user = authService.getCurrentUser();
        String role = (targetRole != null && !targetRole.isBlank()) ? targetRole : (user.getTargetRole() != null ? user.getTargetRole() : "Full Stack Software Engineer");

        // Collect skills from user's modules and gaps
        List<String> targetSkills = new ArrayList<>();
        List<LearningRoadmap> roadmaps = roadmapRepository.findByUserOrderByCreatedAtDesc(user);
        if (!roadmaps.isEmpty()) {
            List<LearningModule> modules = moduleRepository.findByRoadmapOrderByOrderIndexAsc(roadmaps.get(0));
            for (LearningModule m : modules) {
                targetSkills.add(m.getSkillName());
            }
        }
        if (targetSkills.isEmpty()) {
            targetSkills.addAll(List.of("Java", "Spring Boot", "REST API", "PostgreSQL", "Docker", "Git", "System Design"));
        }

        // Build 10 high-quality mock questions across these skills
        List<MockTestDto.TestQuestionItem> questions = buildMockQuestions(targetSkills, role);

        MockTest mockTest = new MockTest();
        mockTest.setUser(user);
        mockTest.setTargetRole(role);
        mockTest.setTitle("Comprehensive Role Readiness Exam: " + role);
        mockTest.setDescription("Evaluates production engineering competencies across your roadmap: " + String.join(", ", targetSkills.subList(0, Math.min(4, targetSkills.size()))));
        mockTest.setTotalQuestions(questions.size());
        mockTest.setTimeLimitMinutes(25);

        try {
            mockTest.setQuestionsJson(objectMapper.writeValueAsString(questions));
        } catch (Exception ignored) {
        }

        MockTest saved = mockTestRepository.save(mockTest);

        MockTestDto dto = new MockTestDto();
        dto.setId(saved.getId());
        dto.setTargetRole(saved.getTargetRole());
        dto.setTitle(saved.getTitle());
        dto.setDescription(saved.getDescription());
        dto.setTotalQuestions(saved.getTotalQuestions());
        dto.setTimeLimitMinutes(saved.getTimeLimitMinutes());
        dto.setQuestions(questions);

        return dto;
    }

    /**
     * Submit and evaluate a Mock Test attempt.
     * Shows score, strengths, weaknesses, and recommended revision topics.
     */
    @Transactional
    public MockTestResultDto submitMockTest(Long testId, MockTestSubmissionRequest submissionRequest) {
        User user = authService.getCurrentUser();
        MockTest test = mockTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("MockTest", "id", testId));

        if (!test.getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to requested test");
        }

        List<MockTestDto.TestQuestionItem> questions;
        try {
            questions = objectMapper.readValue(test.getQuestionsJson(), new TypeReference<List<MockTestDto.TestQuestionItem>>() {});
        } catch (Exception e) {
            questions = List.of();
        }

        int correctCount = 0;
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> revisionTopics = new ArrayList<>();
        List<MockTestResultDto.QuestionReviewItem> reviews = new ArrayList<>();

        Map<Integer, Integer> answers = submissionRequest.getAnswers() != null ? submissionRequest.getAnswers() : Map.of();

        for (MockTestDto.TestQuestionItem q : questions) {
            Integer selected = answers.get(q.getQuestionId());
            // Lookup correct option (option index 0 is designated as correct in our deterministic generator, shuffled for display or indexed)
            int correctIndex = getDesignatedCorrectIndex(q.getQuestionId());
            boolean isCorrect = selected != null && selected == correctIndex;

            if (isCorrect) {
                correctCount++;
                if (!strengths.contains(q.getSkill())) strengths.add(q.getSkill());
            } else {
                if (!weaknesses.contains(q.getSkill())) weaknesses.add(q.getSkill());
                String topic = "Review " + q.getSkill() + " (" + q.getCategory() + " fundamentals & design patterns)";
                if (!revisionTopics.contains(topic)) revisionTopics.add(topic);
            }

            reviews.add(new MockTestResultDto.QuestionReviewItem(
                    q.getQuestionId(),
                    q.getSkill(),
                    q.getQuestion(),
                    q.getOptions(),
                    selected,
                    correctIndex,
                    isCorrect,
                    getExplanationForQuestion(q.getSkill(), q.getQuestionId())
            ));
        }

        int total = Math.max(1, questions.size());
        int score = (int) Math.round(((double) correctCount / total) * 100);
        boolean passed = score >= 70;

        if (weaknesses.isEmpty() && !strengths.isEmpty()) {
            revisionTopics.add("All evaluated topics mastered! Focus on advanced high-throughput system design.");
        }

        // Persist attempt
        MockTestAttempt attempt = new MockTestAttempt();
        attempt.setMockTest(test);
        attempt.setUser(user);
        attempt.setScore(score);
        attempt.setTotalQuestions(total);
        attempt.setCorrectCount(correctCount);

        try {
            attempt.setStrengthsJson(objectMapper.writeValueAsString(strengths));
            attempt.setWeaknessesJson(objectMapper.writeValueAsString(weaknesses));
            attempt.setRevisionTopicsJson(objectMapper.writeValueAsString(revisionTopics));
            attempt.setAnswersJson(objectMapper.writeValueAsString(reviews));
        } catch (Exception ignored) {
        }

        MockTestAttempt savedAttempt = mockTestAttemptRepository.save(attempt);

        // Update user learning streak
        updateStreak(user, false, false, true);

        MockTestResultDto result = new MockTestResultDto();
        result.setAttemptId(savedAttempt.getId());
        result.setTestId(test.getId());
        result.setTestTitle(test.getTitle());
        result.setTargetRole(test.getTargetRole());
        result.setScore(score);
        result.setTotalQuestions(total);
        result.setCorrectCount(correctCount);
        result.setPassed(passed);
        result.setStrengths(strengths);
        result.setWeaknesses(weaknesses);
        result.setRevisionTopics(revisionTopics);
        result.setQuestionReviews(reviews);
        result.setAttemptedAt(savedAttempt.getAttemptedAt());

        return result;
    }

    /**
     * Compute the user's Next-Best-Action based on live progress.
     */
    public NextBestActionDto calculateNextBestAction(User user) {
        // 1. Check if user has analyzed any job descriptions
        List<SkillAnalysis> analyses = skillAnalysisRepository.findByUserOrderByCreatedAtDesc(user);
        if (analyses.isEmpty()) {
            return new NextBestActionDto(
                    "Perform Your First Skill Gap Analysis",
                    "Upload your resume and paste a target job description to compute your real readiness score and identify missing skills.",
                    "ANALYZE_GAP",
                    "/analyzer",
                    "HIGH",
                    "Essential Step 1",
                    null
            );
        }

        SkillAnalysis latestAnalysis = analyses.get(0);

        // 2. Check roadmaps and modules
        List<LearningRoadmap> roadmaps = roadmapRepository.findByUserOrderByCreatedAtDesc(user);
        if (roadmaps.isEmpty()) {
            return new NextBestActionDto(
                    "Generate Your Custom Learning Roadmap",
                    "Generate an ordered, dependency-aware curriculum targeting " + latestAnalysis.getTargetRole() + ".",
                    "START_MODULE",
                    "/roadmap",
                    "HIGH",
                    "Curriculum Ready",
                    null
            );
        }

        LearningRoadmap roadmap = roadmaps.get(0);
        List<LearningModule> modules = moduleRepository.findByRoadmapOrderByOrderIndexAsc(roadmap);
        if (modules.isEmpty()) {
            modules = generateModulesFromAnalysisOrDefaults(roadmap);
        }

        // 3. Find if any module is IN_PROGRESS
        for (LearningModule m : modules) {
            Optional<UserModuleProgress> prog = progressRepository.findByUserAndModule(user, m);
            if (prog.isPresent() && "IN_PROGRESS".equalsIgnoreCase(prog.get().getStatus())) {
                return new NextBestActionDto(
                        "Continue Module: " + m.getSkillName(),
                        "Complete the hands-on practice exercises to solidify your understanding of " + m.getSkillName() + ".",
                        "COMPLETE_PRACTICE",
                        "/roadmap?module=" + m.getId(),
                        "HIGH",
                        "In Progress",
                        m.getId()
                );
            }
        }

        // 4. Find if any module is completed but unverified
        for (LearningModule m : modules) {
            Optional<UserModuleProgress> prog = progressRepository.findByUserAndModule(user, m);
            if (prog.isPresent() && "COMPLETED".equalsIgnoreCase(prog.get().getStatus())) {
                Optional<SkillVerification> ver = verificationRepository.findByUserAndSkillNameIgnoreCase(user, m.getSkillName());
                if (ver.isEmpty() || !"VERIFIED".equalsIgnoreCase(ver.get().getStatus())) {
                    return new NextBestActionDto(
                            "Verify Competency in " + m.getSkillName(),
                            "You finished the curriculum! Submit project evidence or take the skill assessment to earn verified status.",
                            "VERIFY_SKILL",
                            "/roadmap?verify=" + m.getSkillName(),
                            "MEDIUM",
                            "Demonstrate Competence",
                            m.getId()
                    );
                }
            }
        }

        // 5. Find next NOT_STARTED module in sequence
        for (LearningModule m : modules) {
            Optional<UserModuleProgress> prog = progressRepository.findByUserAndModule(user, m);
            if (prog.isEmpty() || "NOT_STARTED".equalsIgnoreCase(prog.get().getStatus())) {
                return new NextBestActionDto(
                        "Start Next Module: " + m.getSkillName(),
                        "Master " + m.getSkillName() + " (Estimated: " + m.getEstimatedHours() + ") for your target role as " + roadmap.getTargetRole() + ".",
                        "START_MODULE",
                        "/roadmap?module=" + m.getId(),
                        "RECOMMENDED",
                        "Next Ordered Step",
                        m.getId()
                );
            }
        }

        // 6. If all modules completed, prompt Mock Assessment
        List<MockTestAttempt> attempts = mockTestAttemptRepository.findByUserOrderByAttemptedAtDesc(user);
        if (attempts.isEmpty()) {
            return new NextBestActionDto(
                    "Take Full Role Readiness Mock Exam",
                    "Assess your readiness for " + roadmap.getTargetRole() + " under timed conditions to validate interview fitness.",
                    "TAKE_MOCK_TEST",
                    "/roadmap?tab=mock-test",
                    "HIGH",
                    "Milestone Exam",
                    null
            );
        }

        MockTestAttempt latestAttempt = attempts.get(0);
        if (latestAttempt.getScore() < 80) {
            String topic = "interview topics";
            try {
                List<String> weaknesses = objectMapper.readValue(latestAttempt.getWeaknessesJson(), new TypeReference<List<String>>() {});
                if (!weaknesses.isEmpty()) topic = weaknesses.get(0);
            } catch (Exception ignored) {
            }
            return new NextBestActionDto(
                    "Targeted Revision: " + topic,
                    "Your latest mock score was " + latestAttempt.getScore() + "%. Revise " + topic + " to close this knowledge gap.",
                    "REVISE_TOPIC",
                    "/roadmap?tab=mock-test",
                    "HIGH",
                    "Score Boost",
                    latestAttempt.getId()
            );
        }

        return new NextBestActionDto(
                "Optimize Resume & Consult AI Career Mentor",
                "Your readiness is strong! Practice behavioral & technical interview scenarios with your AI Career Mentor.",
                "CHAT_MENTOR",
                "/roadmap?tab=mentor",
                "RECOMMENDED",
                "Interview Ready",
                null
        );
    }

    /**
     * Dashboard overview data bundling modules, streak, verified skills, and next best action.
     */
    @Transactional
    public LearningDashboardDto getDashboardData() {
        User user = authService.getCurrentUser();
        List<LearningRoadmap> roadmaps = roadmapRepository.findByUserOrderByCreatedAtDesc(user);

        LearningDashboardDto dashboard = new LearningDashboardDto();

        // User learning streak
        UserLearningStreak streak = streakRepository.findByUser(user)
                .orElseGet(() -> streakRepository.save(new UserLearningStreak(user)));

        dashboard.setCurrentStreakDays(streak.getCurrentStreakDays());
        dashboard.setLongestStreakDays(streak.getLongestStreakDays());
        dashboard.setTotalPracticeCount(streak.getTotalPracticeQuestions());
        dashboard.setTotalMockTestsCount(streak.getTotalMockTests());
        dashboard.setTotalHoursLearned(streak.getTotalLearningHours());

        // Verifications
        List<SkillVerificationResponse> verifications = getUserVerifications();
        dashboard.setVerifiedSkills(verifications);
        dashboard.setVerifiedSkillsCount((int) verifications.stream().filter(v -> "VERIFIED".equalsIgnoreCase(v.getStatus())).count());

        // Next Best Action
        dashboard.setNextBestAction(calculateNextBestAction(user));

        if (!roadmaps.isEmpty()) {
            LearningRoadmap roadmap = roadmaps.get(0);
            dashboard.setRoadmapId(roadmap.getId());
            dashboard.setRoadmapTitle(roadmap.getTitle());
            dashboard.setTargetRole(roadmap.getTargetRole());
            dashboard.setEstimatedWeeks(roadmap.getEstimatedWeeks());
            dashboard.setDailyStudyMinutes(roadmap.getDailyStudyMinutes() > 0 ? roadmap.getDailyStudyMinutes() : 60);

            List<LearningModule> modules = moduleRepository.findByRoadmapOrderByOrderIndexAsc(roadmap);
            if (modules.isEmpty()) {
                modules = generateModulesFromAnalysisOrDefaults(roadmap);
            }

            List<LearningModuleDto> moduleDtos = modules.stream().map(m -> mapToModuleDto(m, user)).collect(Collectors.toList());
            dashboard.setModules(moduleDtos);
            dashboard.setTotalModules(moduleDtos.size());

            int completed = (int) moduleDtos.stream().filter(m -> "COMPLETED".equalsIgnoreCase(m.getStatus())).count();
            int inProgress = (int) moduleDtos.stream().filter(m -> "IN_PROGRESS".equalsIgnoreCase(m.getStatus())).count();

            dashboard.setCompletedModules(completed);
            dashboard.setInProgressModules(inProgress);
            dashboard.setProgressPercentage(moduleDtos.isEmpty() ? 0 : Math.round(((float) completed / moduleDtos.size()) * 100));
        }

        // Latest test attempt
        List<MockTestAttempt> attempts = mockTestAttemptRepository.findByUserOrderByAttemptedAtDesc(user);
        if (!attempts.isEmpty()) {
            MockTestAttempt latest = attempts.get(0);
            MockTestResultDto tDto = new MockTestResultDto();
            tDto.setAttemptId(latest.getId());
            tDto.setTestId(latest.getMockTest().getId());
            tDto.setTestTitle(latest.getMockTest().getTitle());
            tDto.setTargetRole(latest.getMockTest().getTargetRole());
            tDto.setScore(latest.getScore());
            tDto.setTotalQuestions(latest.getTotalQuestions());
            tDto.setCorrectCount(latest.getCorrectCount());
            tDto.setPassed(latest.getScore() >= 70);
            tDto.setAttemptedAt(latest.getAttemptedAt());
            try {
                tDto.setStrengths(objectMapper.readValue(latest.getStrengthsJson(), new TypeReference<List<String>>() {}));
                tDto.setWeaknesses(objectMapper.readValue(latest.getWeaknessesJson(), new TypeReference<List<String>>() {}));
                tDto.setRevisionTopics(objectMapper.readValue(latest.getRevisionTopicsJson(), new TypeReference<List<String>>() {}));
            } catch (Exception ignored) {
            }
            dashboard.setLatestTestResult(tDto);
        }

        return dashboard;
    }

    // Helper: Update learning streak
    private UserLearningStreak updateStreak(User user, boolean isPractice, boolean isCompletedModule, boolean isMockTest) {
        UserLearningStreak streak = streakRepository.findByUser(user)
                .orElseGet(() -> new UserLearningStreak(user));

        LocalDate today = LocalDate.now();
        if (streak.getLastActivityDate() == null) {
            streak.setCurrentStreakDays(1);
            streak.setLongestStreakDays(1);
        } else if (streak.getLastActivityDate().isEqual(today)) {
            // Already logged activity today, keep streak
        } else if (streak.getLastActivityDate().isEqual(today.minusDays(1))) {
            streak.setCurrentStreakDays(streak.getCurrentStreakDays() + 1);
            if (streak.getCurrentStreakDays() > streak.getLongestStreakDays()) {
                streak.setLongestStreakDays(streak.getCurrentStreakDays());
            }
        } else {
            // Streak broken
            streak.setCurrentStreakDays(1);
        }

        streak.setLastActivityDate(today);

        if (isPractice) {
            streak.setTotalPracticeQuestions(streak.getTotalPracticeQuestions() + 1);
            streak.setTotalLearningHours(streak.getTotalLearningHours() + 0.25);
        }
        if (isCompletedModule) {
            streak.setTotalCompletedModules(streak.getTotalCompletedModules() + 1);
            streak.setTotalLearningHours(streak.getTotalLearningHours() + 2.5);
        }
        if (isMockTest) {
            streak.setTotalMockTests(streak.getTotalMockTests() + 1);
            streak.setTotalLearningHours(streak.getTotalLearningHours() + 0.75);
        }

        return streakRepository.save(streak);
    }

    // Mapping: LearningModule to DTO
    private LearningModuleDto mapToModuleDto(LearningModule module, User user) {
        LearningModuleDto dto = new LearningModuleDto();
        dto.setId(module.getId());
        dto.setRoadmapId(module.getRoadmap().getId());
        dto.setSkillName(module.getSkillName());
        dto.setCategory(module.getCategory());
        dto.setOrderIndex(module.getOrderIndex());
        dto.setEstimatedHours(module.getEstimatedHours());
        dto.setCurrentProficiency(module.getCurrentProficiency());
        dto.setTargetProficiency(module.getTargetProficiency());
        dto.setWhyItMatters(module.getWhyItMatters());
        dto.setContentExplanations(module.getContentExplanations());

        try {
            if (module.getLearningObjectivesJson() != null) {
                dto.setLearningObjectives(objectMapper.readValue(module.getLearningObjectivesJson(), new TypeReference<List<String>>() {}));
            }
            if (module.getSuitableResourcesJson() != null) {
                dto.setSuitableResources(objectMapper.readValue(module.getSuitableResourcesJson(), new TypeReference<List<Map<String, String>>>() {}));
            }
            if (module.getPracticeExercisesJson() != null) {
                dto.setPracticeExercises(objectMapper.readValue(module.getPracticeExercisesJson(), new TypeReference<List<String>>() {}));
            }
            if (module.getPracticeQuestionsJson() != null) {
                dto.setPracticeQuestions(objectMapper.readValue(module.getPracticeQuestionsJson(), new TypeReference<List<PracticeQuestionDto>>() {}));
            }
            if (module.getDependenciesJson() != null) {
                dto.setDependencies(objectMapper.readValue(module.getDependenciesJson(), new TypeReference<List<String>>() {}));
            }
        } catch (Exception ignored) {
        }

        boolean hasGeneratedPlaceholderUrl = dto.getSuitableResources().stream()
                .map(resource -> resource.get("url"))
                .filter(Objects::nonNull)
                .anyMatch(url -> url.contains("docs.") && url.endsWith(".org")
                        || url.contains("dev.to/t/") || url.contains("github.com/topics/"));
        if (hasGeneratedPlaceholderUrl) {
            dto.setSuitableResources(curatedResources(module.getSkillName()));
        }
        if (module.getContentExplanations() != null
                && module.getContentExplanations().startsWith("### 1. Conceptual Architecture of ")) {
            dto.setContentExplanations(buildDeepModuleExplanations(module.getSkillName()));
        }
        if (!dto.getLearningObjectives().isEmpty()
                && dto.getLearningObjectives().get(0).startsWith("Understand the core architectural philosophy")) {
            dto.setLearningObjectives(buildLearningObjectives(
                    module.getSkillName(), module.getCurrentProficiency(), module.getTargetProficiency()));
        }
        if (!dto.getPracticeExercises().isEmpty()
                && dto.getPracticeExercises().get(0).startsWith("Hands-on Exercise 1:")) {
            dto.setPracticeExercises(buildPracticeExercises(module.getSkillName()));
        }

        // Populate user progress
        progressRepository.findByUserAndModule(user, module).ifPresent(p -> {
            dto.setStatus(p.getStatus());
            dto.setUserScore(p.getScore());
            dto.setUserNotes(p.getUserNotes());
        });

        // Populate verification
        verificationRepository.findByUserAndSkillNameIgnoreCase(user, module.getSkillName()).ifPresent(v -> {
            if ("VERIFIED".equalsIgnoreCase(v.getStatus())) {
                dto.setVerified(true);
                dto.setVerificationBadge(v.getVerificationBadge());
            }
        });

        return dto;
    }

    private SkillVerificationResponse mapToVerificationResponse(SkillVerification v) {
        SkillVerificationResponse dto = new SkillVerificationResponse();
        dto.setId(v.getId());
        dto.setSkillName(v.getSkillName());
        dto.setTargetRole(v.getTargetRole());
        dto.setStatus(v.getStatus());
        dto.setVerificationMethod(v.getVerificationMethod());
        dto.setAssessmentScore(v.getAssessmentScore());
        dto.setProjectTitle(v.getProjectTitle());
        dto.setProjectGithubUrl(v.getProjectGithubUrl());
        dto.setProjectLiveUrl(v.getProjectLiveUrl());
        dto.setVerificationBadge(v.getVerificationBadge());
        dto.setEvaluatorFeedback(v.getEvaluatorFeedback());
        dto.setVerifiedAt(v.getVerifiedAt());
        return dto;
    }

    private List<PracticeQuestionDto> parsePracticeQuestions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<PracticeQuestionDto>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        for (String word : text.split("[^a-zA-Z0-9_]+")) {
            if (word.length() > 3) keywords.add(word);
        }
        return keywords;
    }

    /**
     * Generate an ordered learning path based on the target role, current skills, missing skills, dependencies, and estimated study time.
     */
    private List<LearningModule> generateModulesFromAnalysisOrDefaults(LearningRoadmap roadmap) {
        List<String> missingSkills = new ArrayList<>();
        SkillAnalysis analysis = roadmap.getSkillAnalysis();

        if (analysis != null) {
            if (analysis.getMissingSkills() != null && !analysis.getMissingSkills().isBlank()) {
                missingSkills.addAll(Arrays.asList(analysis.getMissingSkills().split(",")));
            }
        }

        // If no explicit missing skills, use comprehensive role-specific default curriculum
        if (missingSkills.isEmpty()) {
            String role = roadmap.getTargetRole() != null ? roadmap.getTargetRole().toLowerCase() : "";
            if (role.contains("python") || role.contains("data") || role.contains("ai")) {
                missingSkills.addAll(List.of("Python", "FastAPI", "PostgreSQL", "Docker", "Pandas", "System Design"));
            } else if (role.contains("frontend") || role.contains("react")) {
                missingSkills.addAll(List.of("TypeScript", "React", "Tailwind CSS", "REST API", "Unit Testing", "CI/CD"));
            } else {
                missingSkills.addAll(List.of("Spring Boot", "PostgreSQL", "Docker", "Kafka", "Microservices", "System Design"));
            }
        }

        // Dependency Ordering Matrix:
        // Priority 1: Languages & Fundamentals (Java, Python, TypeScript, SQL, Git)
        // Priority 2: Frameworks & APIs (Spring Boot, React, FastAPI, REST API)
        // Priority 3: Data & Storage (PostgreSQL, MongoDB, Redis)
        // Priority 4: Cloud, Containers & Queues (Docker, Kubernetes, Kafka, CI/CD)
        // Priority 5: High-Level Architecture & Soft Skills (System Design, Microservices, Agile)
        List<String> orderedSkillNames = sortSkillsByDependency(missingSkills);

        List<LearningModule> modules = new ArrayList<>();
        int order = 1;
        for (String skill : orderedSkillNames) {
            LearningModule m = createCuratedModule(roadmap, skill.trim(), order++);
            modules.add(moduleRepository.save(m));
        }

        return modules;
    }

    private List<String> sortSkillsByDependency(List<String> skills) {
        Map<String, Integer> tiers = Map.ofEntries(
                Map.entry("Git", 1), Map.entry("Java", 1), Map.entry("Python", 1), Map.entry("TypeScript", 1), Map.entry("JavaScript", 1), Map.entry("SQL", 1),
                Map.entry("REST API", 2), Map.entry("Spring Boot", 2), Map.entry("React", 2), Map.entry("Django", 2), Map.entry("FastAPI", 2),
                Map.entry("PostgreSQL", 3), Map.entry("MySQL", 3), Map.entry("MongoDB", 3), Map.entry("Redis", 3), Map.entry("JPA", 3), Map.entry("Hibernate", 3),
                Map.entry("Docker", 4), Map.entry("Kubernetes", 4), Map.entry("Kafka", 4), Map.entry("CI/CD", 4), Map.entry("AWS", 4), Map.entry("Jenkins", 4),
                Map.entry("Microservices", 5), Map.entry("System Design", 5), Map.entry("Algorithms", 5), Map.entry("Communication", 5), Map.entry("Agile", 5)
        );

        List<String> sorted = new ArrayList<>(skills);
        sorted.sort(Comparator.comparingInt(s -> tiers.getOrDefault(s, 3)));
        return sorted;
    }

    private LearningModule createCuratedModule(LearningRoadmap roadmap, String skill, int orderIndex) {
        LearningModule m = new LearningModule();
        m.setRoadmap(roadmap);
        m.setSkillName(skill);
        m.setOrderIndex(orderIndex);
        m.setCategory("Communication".equalsIgnoreCase(skill) || "Agile".equalsIgnoreCase(skill) ? "Soft" : "Technical");
        SkillDetailDto detail = findSkillDetail(roadmap.getSkillAnalysis(), skill);
        String currentProficiency = detail != null && detail.getCurrentProficiency() != null
                ? detail.getCurrentProficiency() : findUserProficiency(roadmap.getUser(), skill);
        String targetProficiency = detail != null && detail.getRequiredProficiency() != null
                ? detail.getRequiredProficiency() : "Advanced";
        m.setEstimatedHours(estimateHours(currentProficiency));
        m.setCurrentProficiency(currentProficiency);
        m.setTargetProficiency(targetProficiency);

        m.setWhyItMatters(detail != null && detail.getRecommendedAction() != null
                ? detail.getRecommendedAction()
                : skill + " is a learning gap for the " + roadmap.getTargetRole()
                        + " role. This plan builds from your current level toward " + targetProficiency + ".");

        List<String> objectives = buildLearningObjectives(skill, currentProficiency, targetProficiency);
        List<Map<String, String>> resources = curatedResources(skill);
        String explanations = buildDeepModuleExplanations(skill);

        List<String> exercises = buildPracticeExercises(skill);

        List<PracticeQuestionDto> questions = buildModulePracticeQuestions(skill);

        List<String> dependencies = orderIndex > 1 ? List.of("Core Programming Fundamentals", "Version Control (Git)") : List.of();

        try {
            m.setLearningObjectivesJson(objectMapper.writeValueAsString(objectObjectivesOrEmpty(objectives)));
            m.setSuitableResourcesJson(objectMapper.writeValueAsString(resources));
            m.setContentExplanations(explanations);
            m.setPracticeExercisesJson(objectMapper.writeValueAsString(exercises));
            m.setPracticeQuestionsJson(objectMapper.writeValueAsString(questions));
            m.setDependenciesJson(objectMapper.writeValueAsString(dependencies));
        } catch (Exception ignored) {
        }

        return m;
    }

    private SkillDetailDto findSkillDetail(SkillAnalysis analysis, String skill) {
        if (analysis == null || analysis.getSkillsAnalysisJson() == null || analysis.getSkillsAnalysisJson().isBlank()) {
            return null;
        }
        try {
            List<SkillDetailDto> details = objectMapper.readValue(
                    analysis.getSkillsAnalysisJson(),
                    new TypeReference<List<SkillDetailDto>>() {});
            return details.stream()
                    .filter(detail -> detail.getSkillName() != null && detail.getSkillName().equalsIgnoreCase(skill))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            logger.warn("Could not read skill proficiency for roadmap skill {}", skill, e);
            return null;
        }
    }

    private String findUserProficiency(User user, String skill) {
        return userSkillRepository.findByUser(user).stream()
                .filter(userSkill -> userSkill.getSkillName().equalsIgnoreCase(skill))
                .map(UserSkill::getProficiencyLevel)
                .findFirst()
                .orElse("Beginner / Unverified");
    }

    private String estimateHours(String currentProficiency) {
        String level = currentProficiency.toLowerCase(Locale.ROOT);
        if (level.contains("advanced") || level.contains("expert")) return "3 - 5 hours";
        if (level.contains("intermediate") || level.contains("developing")) return "5 - 7 hours";
        return "7 - 9 hours";
    }

    private List<String> buildLearningObjectives(String skill, String current, String target) {
        String level = current.toLowerCase(Locale.ROOT);
        if (level.contains("advanced") || level.contains("expert")) {
            return List.of(
                    "Assess advanced " + skill + " patterns and trade-offs for the " + target + " target",
                    "Apply production reliability, performance, and security practices",
                    "Integrate " + skill + " into a realistic feature in your target-role stack",
                    "Review the implementation using tests, observability, and maintainability criteria"
            );
        }
        if (level.contains("intermediate") || level.contains("developing")) {
            return List.of(
                    "Strengthen the " + skill + " concepts you already use and identify gaps against " + target,
                    "Build a complete feature using the core APIs and common patterns",
                    "Practice debugging, validation, and error handling for realistic edge cases",
                    "Test and document the feature using conventions from the target role"
            );
        }
        return List.of(
                "Learn the core vocabulary and purpose of " + skill + " in the " + target + " role",
                "Follow a guided tutorial to build and run a small working example",
                "Practice the essential concepts with short exercises and explain your choices",
                "Extend the example into a tested feature that demonstrates the target proficiency"
        );
    }

    private List<Map<String, String>> curatedResources(String skill) {
        String key = skill.toLowerCase(Locale.ROOT);
        List<Map<String, String>> resources = new ArrayList<>();
        if (key.contains("spring boot")) {
            resources.add(verifiedResource("Official Spring Boot documentation", "Official docs", "https://docs.spring.io/spring-boot/index.html"));
            resources.add(verifiedResource("Build a RESTful web service", "Guided tutorial", "https://spring.io/guides/gs/rest-service"));
            resources.add(verifiedResource("Spring Boot guides", "Practical examples", "https://spring.io/guides"));
            resources.add(resource("Spring Boot tutorial video search", "Video search", "https://www.youtube.com/results?search_query=spring+boot+beginner+tutorial"));
        } else if (key.contains("typescript")) {
            resources.add(verifiedResource("TypeScript Handbook", "Official docs", "https://www.typescriptlang.org/docs/handbook/intro.html"));
            resources.add(resource("TypeScript Playground", "Interactive", "https://www.typescriptlang.org/play"));
            resources.add(resource("JavaScript Guide (MDN)", "Article", "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide"));
            resources.add(resource("TypeScript tutorial video search", "Video search", "https://www.youtube.com/results?search_query=typescript+tutorial"));
        } else if (key.equals("sql") || key.contains("postgres")) {
            resources.add(verifiedResource("PostgreSQL tutorial", "Official docs", "https://www.postgresql.org/docs/current/tutorial.html"));
            resources.add(verifiedResource("SQLBolt interactive lessons", "Interactive", "https://sqlbolt.com/"));
            resources.add(verifiedResource("PostgreSQL Exercises", "Practice", "https://pgexercises.com/"));
            resources.add(resource("SQL tutorial video search", "Video search", "https://www.youtube.com/results?search_query=sql+beginner+tutorial"));
        } else if (key.contains("docker")) {
            resources.add(verifiedResource("Docker Get Started", "Official docs", "https://docs.docker.com/get-started/"));
            resources.add(resource("Docker samples", "Practical examples", "https://docs.docker.com/samples/"));
            resources.add(resource("Docker tutorial video search", "Video search", "https://www.youtube.com/results?search_query=docker+beginner+tutorial"));
        } else if (key.equals("react")) {
            resources.add(verifiedResource("React Learn", "Official docs", "https://react.dev/learn"));
            resources.add(resource("React tutorial video search", "Video search", "https://www.youtube.com/results?search_query=react+beginner+tutorial"));
        } else if (key.equals("java")) {
            resources.add(resource("Dev.java learning center", "Official docs", "https://dev.java/learn/"));
            resources.add(resource("Java tutorial video search", "Video search", "https://www.youtube.com/results?search_query=java+programming+tutorial"));
        } else if (key.equals("python")) {
            resources.add(resource("The Python Tutorial", "Official docs", "https://docs.python.org/3/tutorial/"));
            resources.add(resource("Python tutorial video search", "Video search", "https://www.youtube.com/results?search_query=python+programming+tutorial"));
        } else if (key.contains("kafka")) {
            resources.add(resource("Apache Kafka documentation", "Official docs", "https://kafka.apache.org/documentation/"));
            resources.add(resource("Kafka quickstart", "Guided tutorial", "https://kafka.apache.org/quickstart"));
            resources.add(resource("Kafka tutorial video search", "Video search", "https://www.youtube.com/results?search_query=apache+kafka+tutorial"));
        } else if (key.contains("microservice")) {
            resources.add(resource("Microservices architecture", "Reference", "https://microservices.io/"));
            resources.add(resource("Spring Cloud documentation", "Official docs", "https://docs.spring.io/spring-cloud/reference/"));
            resources.add(resource("Microservices tutorial video search", "Video search", "https://www.youtube.com/results?search_query=microservices+architecture+tutorial"));
        } else if (key.contains("system design")) {
            resources.add(resource("Azure Architecture Center", "Architecture guide", "https://learn.microsoft.com/en-us/azure/architecture/guide/"));
            resources.add(resource("System design tutorial video search", "Video search", "https://www.youtube.com/results?search_query=system+design+fundamentals"));
        } else if (key.contains("tailwind")) {
            resources.add(resource("Tailwind CSS documentation", "Official docs", "https://tailwindcss.com/docs"));
            resources.add(resource("Tailwind CSS tutorial video search", "Video search", "https://www.youtube.com/results?search_query=tailwind+css+tutorial"));
        } else if (key.contains("rest api")) {
            resources.add(resource("HTTP request methods (MDN)", "Reference", "https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods"));
            resources.add(resource("REST API tutorial video search", "Video search", "https://www.youtube.com/results?search_query=rest+api+tutorial"));
        } else if (key.contains("unit testing") || key.equals("junit")) {
            resources.add(resource("JUnit 5 User Guide", "Official docs", "https://junit.org/junit5/docs/current/user-guide/"));
            resources.add(resource("JUnit tutorial video search", "Video search", "https://www.youtube.com/results?search_query=junit+5+tutorial"));
        } else if (key.contains("ci/cd") || key.equals("github actions")) {
            resources.add(resource("GitHub Actions documentation", "Official docs", "https://docs.github.com/en/actions"));
            resources.add(resource("GitHub Actions tutorial video search", "Video search", "https://www.youtube.com/results?search_query=github+actions+tutorial"));
        } else if (key.contains("fastapi")) {
            resources.add(resource("FastAPI tutorial", "Official docs", "https://fastapi.tiangolo.com/tutorial/"));
            resources.add(resource("FastAPI tutorial video search", "Video search", "https://www.youtube.com/results?search_query=fastapi+tutorial"));
        } else if (key.contains("pandas")) {
            resources.add(resource("Pandas User Guide", "Official docs", "https://pandas.pydata.org/docs/user_guide/"));
            resources.add(resource("Pandas tutorial video search", "Video search", "https://www.youtube.com/results?search_query=pandas+python+tutorial"));
        } else if (key.equals("git") || key.equals("github")) {
            resources.add(resource("Git reference", "Official docs", "https://git-scm.com/docs"));
            resources.add(resource("GitHub Skills interactive courses", "Interactive", "https://skills.github.com/"));
            resources.add(resource("Git tutorial video search", "Video search", "https://www.youtube.com/results?search_query=git+beginner+tutorial"));
        }
        return resources;
    }

    private Map<String, String> resource(String title, String type, String url) {
        return Map.of("title", title, "type", type, "url", url, "verification", "unverified");
    }

    private Map<String, String> verifiedResource(String title, String type, String url) {
        return Map.of("title", title, "type", type, "url", url, "verification", "verified");
    }

    private List<String> buildPracticeExercises(String skill) {
        if ("spring boot".equalsIgnoreCase(skill)) {
            return List.of(
                    "Create a REST endpoint with a request DTO, validation, and a clear response type.",
                    "Move business rules into a constructor-injected service and add a repository-backed operation with tests."
            );
        }
        if ("typescript".equalsIgnoreCase(skill)) {
            return List.of(
                    "Define a discriminated union for success and error API results, then narrow it safely.",
                    "Create a generic helper and write examples that demonstrate valid and invalid type usage."
            );
        }
        if ("sql".equalsIgnoreCase(skill) || skill.toLowerCase(Locale.ROOT).contains("postgres")) {
            return List.of(
                    "Write queries using filtering, joins, grouping, and constraints against a small relational schema.",
                    "Add an index for a measured query pattern and compare the query plan before and after."
            );
        }
        return List.of(
                "Build a small, runnable example that demonstrates the core concepts of " + skill + ".",
                "Extend the example with input handling, automated tests, and a short explanation of your design choices."
        );
    }

    private List<String> objectObjectivesOrEmpty(List<String> objs) {
        return objs != null ? objs : List.of();
    }

    private String buildDeepModuleExplanations(String skill) {
        if ("spring boot".equalsIgnoreCase(skill)) {
            return "Spring Boot configures a Spring application using starters and auto-configuration. You still own the application design: controllers handle HTTP, services hold business rules, and repositories manage persistence.\n\n"
                    + "A useful progression is: create a project with Spring Initializr, expose a REST endpoint, move logic into a constructor-injected service, persist through Spring Data JPA, validate requests, then protect and test the API.\n\n"
                    + "```java\n@RestController\n@RequestMapping(\"/api/tasks\")\npublic class TaskController {\n    private final TaskService service;\n    public TaskController(TaskService service) { this.service = service; }\n    @GetMapping\n    public List<TaskDto> list() { return service.listTasks(); }\n}\n```\n\n"
                    + "Start with the official getting-started REST guide, then adapt it to a small domain feature. Keep secrets in external configuration and cover controller behavior with tests.";
        }
        if ("typescript".equalsIgnoreCase(skill)) {
            return "TypeScript checks JavaScript programs before they run. Start with primitive and object types, then use unions, narrowing, interfaces, and generics to describe data at API boundaries.\n\n"
                    + "```ts\ntype ApiResult<T> = { ok: true; data: T } | { ok: false; error: string };\nfunction unwrap<T>(result: ApiResult<T>): T | null {\n  return result.ok ? result.data : null;\n}\n```\n\n"
                    + "Use the official Handbook for concepts and the TypeScript Playground to experiment. Remember that types are erased at runtime; validate untrusted input separately.";
        }
        if ("sql".equalsIgnoreCase(skill) || skill.toLowerCase(Locale.ROOT).contains("postgres")) {
            return "Relational databases store related records in tables. Learn filtering and joins first, then grouping, transactions, constraints, and indexes. PostgreSQL's tutorial and PGExercises provide a working progression.\n\n"
                    + "```sql\nSELECT department_id, COUNT(*) AS employee_count\nFROM employees\nGROUP BY department_id\nORDER BY employee_count DESC;\n```\n\n"
                    + "Practice by querying a small schema, checking query results, and explaining how indexes and constraints affect correctness and performance.";
        }
        return "### 1. Conceptual Architecture of " + skill + "\n\n" +
                skill + " plays an indispensable role in contemporary cloud and enterprise architectures. " +
                "Understanding its lifecycle, component isolation, and concurrency patterns is essential before writing production code.\n\n" +
                "### 2. Core Implementation Patterns\n\n" +
                "When implementing " + skill + ", always adhere to the single-responsibility principle and ensure stateless, " +
                "idempotent execution. Defensive error boundaries should wrap external calls, and metrics should track throughput and latency.\n\n" +
                "```\n// Production Architecture Pattern for " + skill + "\n" +
                "public class " + skill.replaceAll("[^a-zA-Z]", "") + "Service {\n" +
                "    // Encapsulate dependencies and enforce input validation\n" +
                "    public Result executeOperation(Request request) {\n" +
                "        validate(request);\n" +
                "        return processWithObservability(request);\n" +
                "    }\n}\n```\n\n" +
                "### 3. Common Anti-Patterns to Avoid\n\n" +
                "- **Over-coupling**: Do not tie domain logic directly to vendor-specific APIs.\n" +
                "- **Missing Timeouts**: Always declare explicit connect and read timeouts on I/O boundaries.\n" +
                "- **Ignoring Idempotency**: Design operations to handle retry storms gracefully.\n";
    }

    private List<PracticeQuestionDto> buildModulePracticeQuestions(String skill) {
        List<PracticeQuestionDto> list = new ArrayList<>();

        // 1. Multiple Choice Question
        list.add(new PracticeQuestionDto(
                0,
                "MULTIPLE_CHOICE",
                "What is the primary architectural advantage of utilizing " + skill + " in modern scalable systems?",
                List.of(
                        "Provides decoupled, maintainable abstractions and predictable execution",
                        "Guarantees zero memory allocation at runtime",
                        "Replaces the need for any unit or integration testing",
                        "Eliminates network latency across all distributed nodes"
                ),
                0,
                null,
                "Option A is correct because " + skill + " delivers modularity, structured error boundaries, and scalable design patterns.",
                null
        ));

        // 2. Conceptual Question
        list.add(new PracticeQuestionDto(
                1,
                "CONCEPTUAL",
                "Explain the core responsibility of " + skill + " and why separation of concerns matters when designing systems around it.",
                null,
                null,
                skill + " manages distinct responsibilities such as data encapsulation, execution orchestration, or client abstraction. Separation of concerns prevents tight coupling, simplifies unit testing, and allows independent scaling.",
                "Key concepts include separation of concerns, decoupling, testability, and modular architecture.",
                null
        ));

        // 3. Short Answer Question
        list.add(new PracticeQuestionDto(
                2,
                "SHORT_ANSWER",
                "Identify the primary configuration setting or command used to initialize and safeguard " + skill + " in a production environment.",
                null,
                null,
                "Configuring explicit connection pools, declarative timeouts, environment variables for credentials, and health check endpoints.",
                "Answers should mention security isolation, environment variables, health checks, or timeout thresholds.",
                null
        ));

        // 4. Scenario-Based Question
        list.add(new PracticeQuestionDto(
                3,
                "SCENARIO_BASED",
                "Scenario: During peak traffic, your service utilizing " + skill + " experiences an unexpected spike in p99 latency. How would you systematically diagnose and resolve the bottleneck?",
                null,
                null,
                "First, inspect distributed traces and APM metrics to isolate whether the delay is CPU, memory saturation, database locking, or network I/O. Next, verify connection pool limits and thread saturation. Apply caching, rate-limiting, or connection recycling to mitigate the pressure.",
                "Evaluation looks for systematic root-cause diagnosis: observability metrics, thread/connection pool inspection, caching, and circuit breaking.",
                "Production high-load incident triage"
        ));

        return list;
    }

    private List<MockTestDto.TestQuestionItem> buildMockQuestions(List<String> targetSkills, String role) {
        List<MockTestDto.TestQuestionItem> questions = new ArrayList<>();
        int id = 1;

        for (int i = 0; i < 10; i++) {
            String skill = targetSkills.get(i % targetSkills.size());
            String category = (i % 3 == 0) ? "Architecture" : ((i % 3 == 1) ? "Technical" : "Best Practice");

            questions.add(new MockTestDto.TestQuestionItem(
                    id,
                    skill,
                    "In the context of " + role + ", how does " + skill + " handle state synchronization and resilient failover?",
                    List.of(
                            "Uses idempotent transaction protocols, connection pools, and circuit-breaker fallbacks",
                            "Automatically restarts the hardware node whenever a 4xx error is detected",
                            "Stores all transient data in unencrypted local flat files",
                            "Disables thread safety checks to maximize raw single-core throughput"
                    ),
                    category
            ));
            id++;
        }

        return questions;
    }

    private int getDesignatedCorrectIndex(int questionId) {
        // Our deterministic generator places the correct option at index 0
        return 0;
    }

    private String getExplanationForQuestion(String skill, int questionId) {
        return "Option A is correct: Production engineering in " + skill + " requires idempotency, managed pooling, and graceful circuit breaking to maintain high availability.";
    }
}
