package com.careergap.service;

import com.careergap.dto.MentorChatRequest;
import com.careergap.dto.MentorChatResponse;
import com.careergap.dto.NextBestActionDto;
import com.careergap.entity.*;
import com.careergap.repository.AiMentorMessageRepository;
import com.careergap.repository.LearningRoadmapRepository;
import com.careergap.repository.SkillAnalysisRepository;
import com.careergap.repository.SkillVerificationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MentorService {

    private static final Logger logger = LoggerFactory.getLogger(MentorService.class);

    private final AiMentorMessageRepository mentorMessageRepository;
    private final SkillAnalysisRepository skillAnalysisRepository;
    private final LearningRoadmapRepository roadmapRepository;
    private final SkillVerificationRepository verificationRepository;
    private final PersonalizedLearningService learningService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${careergap.ai.provider:heuristic}")
    private String provider;

    @Value("${careergap.ai.api-key:}")
    private String apiKey;

    @Value("${careergap.ai.model:gemini-1.5-flash}")
    private String model;

    public MentorService(
            AiMentorMessageRepository mentorMessageRepository,
            SkillAnalysisRepository skillAnalysisRepository,
            LearningRoadmapRepository roadmapRepository,
            SkillVerificationRepository verificationRepository,
            PersonalizedLearningService learningService,
            AuthService authService,
            ObjectMapper objectMapper) {
        this.mentorMessageRepository = mentorMessageRepository;
        this.skillAnalysisRepository = skillAnalysisRepository;
        this.roadmapRepository = roadmapRepository;
        this.verificationRepository = verificationRepository;
        this.learningService = learningService;
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send message to personalized AI Career Mentor and receive context-aware response.
     */
    @Transactional
    public MentorChatResponse chat(MentorChatRequest request) {
        User user = authService.getCurrentUser();
        String userQuery = request.getMessage().trim();

        // 1. Persist user message
        AiMentorMessage userMsg = new AiMentorMessage(user, "USER", userQuery);
        mentorMessageRepository.save(userMsg);

        // 2. Gather user context
        String targetRole = user.getTargetRole() != null ? user.getTargetRole() : "Software Engineer";
        List<SkillAnalysis> analyses = skillAnalysisRepository.findByUserOrderByCreatedAtDesc(user);
        SkillAnalysis latestAnalysis = analyses.isEmpty() ? null : analyses.get(0);

        List<SkillVerification> verifications = verificationRepository.findByUserOrderByCreatedAtDesc(user);
        List<String> verifiedSkills = verifications.stream()
                .filter(v -> "VERIFIED".equalsIgnoreCase(v.getStatus()))
                .map(SkillVerification::getSkillName)
                .collect(Collectors.toList());

        NextBestActionDto nextAction = learningService.calculateNextBestAction(user);

        // 3. Generate response using LLM if configured, otherwise deterministic mentor engine
        String replyText = null;
        if (apiKey != null && !apiKey.trim().isEmpty() && !"heuristic".equalsIgnoreCase(provider)) {
            try {
                replyText = callLlmMentor(user, targetRole, latestAnalysis, verifiedSkills, userQuery);
            } catch (Exception e) {
                logger.warn("LLM mentor call failed: {}. Executing local mentor engine.", e.getMessage());
            }
        }

        if (replyText == null || replyText.isBlank()) {
            replyText = generateDeterministicMentorResponse(user, targetRole, latestAnalysis, verifiedSkills, nextAction, userQuery);
        }

        // Suggested next actions for quick follow-up
        List<String> suggested = generateSuggestedFollowUps(nextAction, userQuery);

        // 4. Persist mentor response
        AiMentorMessage mentorMsg = new AiMentorMessage(user, "MENTOR", replyText);
        try {
            mentorMsg.setSuggestedActionsJson(objectMapper.writeValueAsString(suggested));
        } catch (Exception ignored) {
        }
        mentorMessageRepository.save(mentorMsg);

        return new MentorChatResponse(replyText, suggested);
    }

    /**
     * Retrieve chat history for the user.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getChatHistory() {
        User user = authService.getCurrentUser();
        List<AiMentorMessage> history = mentorMessageRepository.findTop30ByUserOrderByCreatedAtAsc(user);

        return history.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("sender", m.getSender());
            map.put("message", m.getMessage());
            map.put("timestamp", m.getCreatedAt());
            if (m.getSuggestedActionsJson() != null) {
                try {
                    map.put("suggestedActions", objectMapper.readValue(m.getSuggestedActionsJson(), new TypeReference<List<String>>() {}));
                } catch (Exception ignored) {
                }
            }
            return map;
        }).collect(Collectors.toList());
    }

    private String callLlmMentor(User user, String targetRole, SkillAnalysis analysis, List<String> verified, String query) {
        String profileContext = String.format("""
                Candidate Name: %s
                Target Role: %s
                Education: %s (%s)
                Years of Experience: %d
                Readiness Score: %s
                Demonstrated Strengths: %s
                Verified Skills: %s
                Missing Skill Gaps: %s
                """,
                user.getFullName(),
                targetRole,
                user.getDegree() != null ? user.getDegree() : "Computer Science",
                user.getCollege() != null ? user.getCollege() : "University",
                user.getYearsOfExperience() != null ? user.getYearsOfExperience() : 0,
                analysis != null ? (analysis.getOverallMatchScore() + "% (" + analysis.getReadinessLevel() + ")") : "No analysis run yet",
                analysis != null ? analysis.getPresentSkills() : "None yet",
                String.join(", ", verified),
                analysis != null ? analysis.getMissingSkills() : "None yet"
        );

        String systemPrompt = "You are CareerGap AI's dedicated Senior Engineering Career Mentor and Staff Tech Lead. " +
                "You provide empathetic, encouraging, technically rigorous, and deeply personalized career guidance.\n" +
                "Always refer to the candidate's exact target role, verified skills, and identified gaps.\n" +
                "Give concrete, actionable advice with examples. Avoid generic platitudes.\n\n" +
                "CANDIDATE PROFILE & GAP DATA:\n" + profileContext;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String endpoint;
        String payload;

        if ("gemini".equalsIgnoreCase(provider)) {
            endpoint = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);
            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("role", "user", "parts", List.of(Map.of("text", systemPrompt + "\n\nUser Question:\n" + query)))
                    )
            );
            try {
                payload = objectMapper.writeValueAsString(body);
            } catch (Exception e) {
                return null;
            }
        } else {
            endpoint = "https://api.openai.com/v1/chat/completions";
            headers.setBearerAuth(apiKey);
            Map<String, Object> body = Map.of(
                    "model", model.contains("gemini") ? "gpt-4o-mini" : model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", query)
                    )
            );
            try {
                payload = objectMapper.writeValueAsString(body);
            } catch (Exception e) {
                return null;
            }
        }

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                var node = objectMapper.readTree(response.getBody());
                if ("gemini".equalsIgnoreCase(provider)) {
                    return node.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                } else {
                    return node.path("choices").get(0).path("message").path("content").asText();
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String generateDeterministicMentorResponse(User user, String targetRole, SkillAnalysis analysis,
                                                       List<String> verified, NextBestActionDto nextAction, String query) {
        String lower = query.toLowerCase();

        // 1. Next steps / What to learn
        if (lower.contains("next") || lower.contains("learn") || lower.contains("start") || lower.contains("what should i do")) {
            return String.format(
                    "Hello %s! Based on your target role as **%s** and current readiness level%s, " +
                    "your highest-impact next step is: **%s**.\n\n" +
                    "Why this matters: %s\n\n" +
                    "Once you complete this, you'll be one step closer to closing the gap and demonstrating verified competency.",
                    user.getFullName(),
                    targetRole,
                    analysis != null ? " (" + analysis.getOverallMatchScore() + "% readiness)" : "",
                    nextAction.getTitle(),
                    nextAction.getDescription()
            );
        }

        // 2. Interview preparation
        if (lower.contains("interview") || lower.contains("behavioral") || lower.contains("system design") || lower.contains("questions")) {
            return String.format(
                    "For technical interviews targeting **%s** positions, hiring managers evaluate 3 key pillars:\n\n" +
                    "1. **Architectural Trade-offs**: Be ready to explain *why* you chose a specific technology over an alternative (e.g., PostgreSQL relational ACID vs Redis caching vs Kafka asynchronous messaging).\n" +
                    "2. **Production Incident Stories (STAR Method)**: Prepare 2-3 stories where you diagnosed high latency, handled concurrency, or resolved memory leaks.\n" +
                    "3. **Verified Skills**: Highlight your verified competencies (%s) and describe the exact metrics or project scope.\n\n" +
                    "Tip: Take our timed Mock Assessment to test your answers against production scenarios!",
                    targetRole,
                    verified.isEmpty() ? "your current projects" : String.join(", ", verified)
            );
        }

        // 3. Resume & project evidence advice
        if (lower.contains("resume") || lower.contains("project") || lower.contains("github") || lower.contains("portfolio")) {
            return String.format(
                    "To make your resume stand out for **%s** openings:\n\n" +
                    "- **Quantify Outcomes**: Instead of 'Built REST APIs in Spring Boot', write 'Architected 12 REST endpoints in Spring Boot with JPA connection pooling, serving 500+ req/sec with <45ms p95 latency.'\n" +
                    "- **Verifiable GitHub Links**: Include clean GitHub repos with comprehensive READMEs, architectural diagrams, and Docker Compose files.\n" +
                    "- **Close Remaining Gaps**: %s",
                    targetRole,
                    analysis != null && analysis.getMissingSkills() != null
                            ? "Prioritize building projects that demonstrate: " + analysis.getMissingSkills()
                            : "Focus on building end-to-end cloud deployments."
            );
        }

        // 4. Specific technologies (Docker, Spring Boot, React, Kafka, PostgreSQL, etc.)
        if (lower.contains("docker") || lower.contains("spring") || lower.contains("react") || lower.contains("kafka") || lower.contains("sql") || lower.contains("database")) {
            return String.format(
                    "Focusing on core engineering tools is a great priority for %s. " +
                    "In our curriculum, we break down each skill into its architectural rationale, core execution patterns, and scenario exercises. " +
                    "Make sure to not just watch tutorials, but build a small working microservice and submit it for **Skill Verification** once done!",
                    targetRole
            );
        }

        // Default supportive mentor greeting
        return String.format(
                "Welcome back, %s! I'm your CareerGap AI Mentor tracking your journey toward becoming a **%s**.\n\n" +
                "Current Status:\n" +
                "- Job Readiness: %s\n" +
                "- Verified Competencies: %s\n" +
                "- Recommended Action: %s\n\n" +
                "Feel free to ask me anything about technical interview prep, system design trade-offs, how to close specific skill gaps, or how to polish your resume bullets!",
                user.getFullName(),
                targetRole,
                analysis != null ? (analysis.getOverallMatchScore() + "% (" + analysis.getReadinessLevel() + ")") : "Run your first analysis to calibrate",
                verified.isEmpty() ? "None verified yet (complete modules to verify)" : String.join(", ", verified),
                nextAction.getTitle()
        );
    }

    private List<String> generateSuggestedFollowUps(NextBestActionDto nextAction, String query) {
        List<String> list = new ArrayList<>();
        list.add("What should I learn next?");
        list.add("How do I prepare for technical interviews?");
        list.add("How can I improve my resume bullet points?");
        if (nextAction != null && nextAction.getTitle() != null) {
            list.add("Tell me more about " + nextAction.getTitle());
        }
        return list;
    }
}

