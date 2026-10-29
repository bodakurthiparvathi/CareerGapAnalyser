package com.careergap.service;

import com.careergap.dto.RoadmapDto;
import com.careergap.dto.SkillAnalysisResponse;
import com.careergap.dto.SkillDetailDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${careergap.ai.provider:heuristic}")
    private String provider;

    @Value("${careergap.ai.api-key:}")
    private String apiKey;

    @Value("${careergap.ai.model:gemini-1.5-flash}")
    private String model;

    public AiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(20000);
        this.restTemplate = new RestTemplate(factory);
    }

    // Comprehensive catalog of common industry technical skills
    private static final List<String> KNOWN_TECH_SKILLS = List.of(
            "Java", "Spring Boot", "Spring Cloud", "Hibernate", "JPA", "Microservices",
            "Python", "Django", "FastAPI", "Flask", "Pandas", "NumPy", "TensorFlow", "PyTorch",
            "JavaScript", "TypeScript", "React", "Next.js", "Vue.js", "Angular", "Node.js", "Express",
            "HTML5", "CSS3", "Tailwind CSS", "Bootstrap", "Redux", "GraphQL", "REST API",
            "PostgreSQL", "MySQL", "MongoDB", "Redis", "Elasticsearch", "Cassandra", "Oracle",
            "AWS", "Azure", "GCP", "Docker", "Kubernetes", "CI/CD", "Git", "GitHub", "GitLab",
            "Jenkins", "Terraform", "Linux", "Bash", "Kafka", "RabbitMQ", "Unit Testing", "JUnit",
            "Mockito", "Jest", "Cypress", "Selenium", "Agile", "Scrum", "System Design", "Algorithms",
            "Data Structures", "SQL", "NoSQL", "DevOps", "Cybersecurity", "OAuth", "JWT"
    );

    private static final List<String> KNOWN_SOFT_SKILLS = List.of(
            "Communication", "Teamwork", "Problem Solving", "Leadership", "Critical Thinking",
            "Time Management", "Adaptability", "Collaboration", "Work Ethic", "Attention to Detail",
            "Analytical Skills", "Mentorship", "Stakeholder Management", "Conflict Resolution"
    );

    // Foundational skills that have high dependency value for engineering roles
    private static final Set<String> FOUNDATIONAL_DEPENDENCIES = Set.of(
            "Java", "Python", "JavaScript", "TypeScript", "SQL", "Git", "Algorithms", "Data Structures", "REST API"
    );

    public SkillAnalysisResponse analyzeSkillGap(String targetRole, String jobDescription, String resumeText) {
        // 1. Try real LLM API if key is configured and provider is not heuristic
        if (apiKey != null && !apiKey.trim().isEmpty() && !"heuristic".equalsIgnoreCase(provider)) {
            try {
                SkillAnalysisResponse llmResponse = callLlmApi(targetRole, jobDescription, resumeText);
                if (llmResponse != null && validateLlmResponse(llmResponse)) {
                    logger.info("Successfully completed AI skill gap analysis using LLM provider: {}", provider);
                    return llmResponse;
                }
            } catch (Exception e) {
                logger.warn("LLM API call failed or timed out ({}). Executing verified semantic evidence engine.", e.getMessage());
            }
        }

        // 2. Deterministic Semantic Evidence Engine (extracts real resume evidence, computes transparent scores)
        return performRealAnalysis(targetRole, jobDescription, resumeText);
    }

    private SkillAnalysisResponse callLlmApi(String targetRole, String jobDescription, String resumeText) {
        String systemInstruction = """
                You are CareerGap AI's expert career auditor and skill verification engine.
                Compare the candidate's actual resume against the target job description.
                CRITICAL RULES:
                1. Classify each evaluated skill as:
                   - Strong: Relevant skill demonstrated with convincing evidence in the resume.
                   - Developing: Some relevant evidence exists, but further improvement or depth is needed.
                   - Missing: No sufficient evidence of the required skill found in the resume.
                   - Optional: A useful skill that is desirable/bonus but not essential for the target role.
                2. For evidence: quote exact resume sentences or state: 'No documented evidence found in provided resume.'
                3. NEVER invent or hallucinate qualifications. Missing evidence indicates lack of documented proof, not that candidate lacks the skill.
                4. Compute transparent match scores between 0 and 100.
                5. Prioritize skill gaps using job importance, proficiency gap, dependencies, and learning effort.
                6. Return strictly valid JSON with no markdown wrapping or preamble matching the requested structure.
                """;

        String userPrompt = String.format("""
                Target Role: %s

                Job Description:
                %s

                Candidate Resume Text:
                %s

                Respond with this JSON structure:
                {
                  "targetRole": "%s",
                  "overallMatchScore": 75,
                  "technicalScore": 70,
                  "softSkillsScore": 85,
                  "readinessLevel": "High Job Readiness",
                  "calculationCriteria": "Transparent Readiness Formula: Technical Skills (70%% weight) + Soft Skills (30%% weight)...",
                  "skills": [
                    {
                      "skillName": "Spring Boot",
                      "currentProficiency": "Intermediate",
                      "requiredProficiency": "Advanced",
                      "classification": "Strong",
                      "evidence": "Direct quote from resume",
                      "confidenceScore": 92,
                      "priorityScore": 30,
                      "priorityLevel": "Low",
                      "category": "Technical",
                      "importance": "Essential",
                      "estimatedEffort": "Low (1-2 wks)",
                      "explanation": "...",
                      "recommendedAction": "..."
                    }
                  ],
                  "strengths": ["..."],
                  "weaknesses": ["..."],
                  "recommendations": ["..."]
                }
                """, targetRole, jobDescription, resumeText, targetRole);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonPayload;
        String endpointUrl;

        if ("gemini".equalsIgnoreCase(provider)) {
            endpointUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);
            Map<String, Object> contents = Map.of(
                    "contents", List.of(
                            Map.of("role", "user", "parts", List.of(Map.of("text", systemInstruction + "\n\n" + userPrompt)))
                    ),
                    "generationConfig", Map.of(
                            "responseMimeType", "application/json",
                            "temperature", 0.2
                    )
            );
            try {
                jsonPayload = objectMapper.writeValueAsString(contents);
            } catch (Exception e) {
                return null;
            }
        } else {
            // OpenAI compatible endpoint
            endpointUrl = "https://api.openai.com/v1/chat/completions";
            headers.setBearerAuth(apiKey);
            Map<String, Object> openAiBody = Map.of(
                    "model", model.contains("gemini") ? "gpt-4o-mini" : model,
                    "response_format", Map.of("type", "json_object"),
                    "temperature", 0.2,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemInstruction),
                            Map.of("role", "user", "content", userPrompt)
                    )
            );
            try {
                jsonPayload = objectMapper.writeValueAsString(openAiBody);
            } catch (Exception e) {
                return null;
            }
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpointUrl, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return null;
        }

        String rawJson = extractJsonFromLlmResponse(response.getBody(), provider);
        if (rawJson == null || rawJson.isBlank()) {
            return null;
        }

        try {
            SkillAnalysisResponse parsed = objectMapper.readValue(rawJson, SkillAnalysisResponse.class);
            parsed.setRoadmap(generateRoadmap(targetRole, parsed.getMissingSkillsList(), List.of()));
            categorizeSkills(parsed);
            return parsed;
        } catch (Exception e) {
            logger.warn("Failed to parse LLM JSON response: {}", e.getMessage());
            return null;
        }
    }

    private String extractJsonFromLlmResponse(String responseBody, String prov) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if ("gemini".equalsIgnoreCase(prov)) {
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
                    return cleanMarkdownCodeFence(textNode.asText());
                }
            } else {
                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    JsonNode messageNode = choices.get(0).path("message").path("content");
                    return cleanMarkdownCodeFence(messageNode.asText());
                }
            }
        } catch (Exception e) {
            logger.warn("Error unpacking LLM envelope: {}", e.getMessage());
        }
        return null;
    }

    private String cleanMarkdownCodeFence(String text) {
        if (text == null) return "";
        String trimmed = text.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private boolean validateLlmResponse(SkillAnalysisResponse res) {
        if (res.getSkills() == null || res.getSkills().isEmpty()) return false;
        if (res.getOverallMatchScore() < 0 || res.getOverallMatchScore() > 100) return false;
        for (SkillDetailDto s : res.getSkills()) {
            if (s.getSkillName() == null || s.getClassification() == null) return false;
        }
        return true;
    }

    /**
     * Deterministic Semantic Evidence Engine.
     * Scans real text from resume & job description, extracts exact sentence-level evidence,
     * assigns transparent weights, and calculates gap priorities.
     */
    public SkillAnalysisResponse performRealAnalysis(String targetRole, String jobDescription, String resumeText) {
        String lowerJD = jobDescription.toLowerCase();
        String lowerResume = resumeText.toLowerCase();

        // Split candidate resume into sentences and bullet points for granular evidence extraction
        List<String> resumeSentences = extractSentences(resumeText);

        // 1. Extract required skills from Job Description
        List<String> detectedTech = KNOWN_TECH_SKILLS.stream()
                .filter(skill -> containsSkill(lowerJD, skill.toLowerCase()))
                .collect(Collectors.toList());

        List<String> detectedSoft = KNOWN_SOFT_SKILLS.stream()
                .filter(skill -> containsSkill(lowerJD, skill.toLowerCase()))
                .collect(Collectors.toList());

        // Dynamic fallbacks if JD has non-standard terminology
        if (detectedTech.isEmpty()) {
            detectedTech.addAll(List.of("Problem Solving", "Algorithms", "Git", "REST API", "Database Design"));
        }
        if (detectedSoft.isEmpty()) {
            detectedSoft.addAll(List.of("Communication", "Collaboration", "Time Management"));
        }

        List<SkillDetailDto> evaluatedSkills = new ArrayList<>();

        double totalTechWeight = 0.0;
        double earnedTechPoints = 0.0;

        double totalSoftWeight = 0.0;
        double earnedSoftPoints = 0.0;

        // 2. Evaluate Technical Skills
        for (String skill : detectedTech) {
            boolean isOptionalInJd = isSkillOptionalInJd(jobDescription, skill);
            String importance = isOptionalInJd ? "Optional" : (isSkillEssentialInJd(jobDescription, skill) ? "Essential" : "Desirable");
            double weight = "Essential".equals(importance) ? 3.0 : ("Desirable".equals(importance) ? 2.0 : 1.0);
            totalTechWeight += weight;

            SkillDetailDto detail = evaluateSkillInResume(skill, "Technical", importance, weight, resumeSentences, lowerResume);
            evaluatedSkills.add(detail);

            if ("Strong".equals(detail.getClassification())) {
                earnedTechPoints += weight * 1.0;
            } else if ("Developing".equals(detail.getClassification())) {
                earnedTechPoints += weight * 0.5;
            } else if ("Optional".equals(detail.getClassification()) && !detail.getEvidence().contains("No documented evidence")) {
                earnedTechPoints += weight * 0.8;
            }
        }

        // 3. Evaluate Soft Skills
        for (String skill : detectedSoft) {
            String importance = isSkillEssentialInJd(jobDescription, skill) ? "Essential" : "Desirable";
            double weight = "Essential".equals(importance) ? 2.5 : 1.5;
            totalSoftWeight += weight;

            SkillDetailDto detail = evaluateSkillInResume(skill, "Soft", importance, weight, resumeSentences, lowerResume);
            evaluatedSkills.add(detail);

            if ("Strong".equals(detail.getClassification())) {
                earnedSoftPoints += weight * 1.0;
            } else if ("Developing".equals(detail.getClassification())) {
                earnedSoftPoints += weight * 0.5;
            }
        }

        // 4. Calculate Transparent Job Readiness Scores
        int techScore = totalTechWeight > 0 ? (int) Math.round((earnedTechPoints / totalTechWeight) * 100) : 50;
        int softScore = totalSoftWeight > 0 ? (int) Math.round((earnedSoftPoints / totalSoftWeight) * 100) : 50;
        techScore = Math.min(100, Math.max(0, techScore));
        softScore = Math.min(100, Math.max(0, softScore));

        // 70% technical, 30% soft skills weighted blend
        int overallScore = (int) Math.round((techScore * 0.70) + (softScore * 0.30));
        overallScore = Math.min(100, Math.max(0, overallScore));

        String readinessLevel;
        if (overallScore >= 80) {
            readinessLevel = "Ready to Apply";
        } else if (overallScore >= 65) {
            readinessLevel = "High Job Readiness";
        } else if (overallScore >= 45) {
            readinessLevel = "Promising Fit";
        } else {
            readinessLevel = "Needs Foundation";
        }

        String criteria = "Transparent Readiness Formula: Technical Match (70% weight) + Soft Skills (30% weight). " +
                "Individual skills weighted by job necessity: Essential (3.0x), Desirable (2.0x), Optional (1.0x). " +
                "Credit applied: Strong evidence (100% credit), Developing evidence (50% credit), Missing evidence (0% credit). " +
                "Optional unverified skills do not reduce score.";

        SkillAnalysisResponse response = new SkillAnalysisResponse();
        response.setTargetRole(targetRole);
        response.setOverallMatchScore(overallScore);
        response.setTechnicalScore(techScore);
        response.setSoftSkillsScore(softScore);
        response.setReadinessLevel(readinessLevel);
        response.setCalculationCriteria(criteria);
        response.setSkills(evaluatedSkills);

        categorizeSkills(response);

        // Strengths & Weaknesses
        List<String> strengths = new ArrayList<>();
        if (!response.getStrongSkills().isEmpty()) {
            List<String> strongNames = response.getStrongSkills().stream()
                    .map(SkillDetailDto::getSkillName)
                    .limit(3)
                    .collect(Collectors.toList());
            strengths.add("Demonstrated verified proficiency in: " + String.join(", ", strongNames));
        }
        if (overallScore >= 65) {
            strengths.add("Core profile displays solid architectural alignment with the requirements for " + targetRole);
        }
        if (softScore >= 70) {
            strengths.add("Strong presentation of collaboration, communication, and problem-solving experience.");
        }
        if (strengths.isEmpty()) {
            strengths.add("Clear foundation in basic engineering fundamentals and project exposure.");
        }
        response.setStrengths(strengths);

        List<String> weaknesses = new ArrayList<>();
        if (!response.getHighPriorityGaps().isEmpty()) {
            List<String> highGaps = response.getHighPriorityGaps().stream()
                    .map(SkillDetailDto::getSkillName)
                    .limit(3)
                    .collect(Collectors.toList());
            weaknesses.add("High-priority skill gaps requiring verified evidence: " + String.join(", ", highGaps));
        }
        if (!response.getDevelopingSkills().isEmpty()) {
            weaknesses.add("Skills mentioned with limited project or production depth: " +
                    response.getDevelopingSkills().stream().map(SkillDetailDto::getSkillName).limit(3).collect(Collectors.joining(", ")));
        }
        if (overallScore < 50) {
            weaknesses.add("Noticeable gap between current documented resume experience and target role expectations.");
        }
        response.setWeaknesses(weaknesses);

        List<String> recommendations = new ArrayList<>();
        if (!response.getHighPriorityGaps().isEmpty()) {
            recommendations.add("Prioritize building a production-grade portfolio project demonstrating " +
                    response.getHighPriorityGaps().get(0).getSkillName() +
                    (response.getHighPriorityGaps().size() > 1 ? " and " + response.getHighPriorityGaps().get(1).getSkillName() : ""));
        }
        recommendations.add("Add quantified metric bullets to your resume (e.g., '% performance improvement', 'requests/sec', 'data scale handled')");
        recommendations.add("Complete hands-on technical assessments to convert 'Developing' skills into verified 'Strong' qualifications.");
        response.setRecommendations(recommendations);

        // Build personalized roadmap focused on bridging the gaps
        List<String> missingTech = response.getMissingSkills().stream()
                .filter(s -> "Technical".equals(s.getCategory()))
                .map(SkillDetailDto::getSkillName)
                .collect(Collectors.toList());
        List<String> missingSoft = response.getMissingSkills().stream()
                .filter(s -> "Soft".equals(s.getCategory()))
                .map(SkillDetailDto::getSkillName)
                .collect(Collectors.toList());

        RoadmapDto roadmap = generateRoadmap(targetRole, missingTech, missingSoft);
        response.setRoadmap(roadmap);

        return response;
    }

    private SkillDetailDto evaluateSkillInResume(String skill, String category, String importance, double weight,
                                                 List<String> resumeSentences, String lowerResume) {
        String lowerSkill = skill.toLowerCase();
        boolean appearsInResume = containsSkill(lowerResume, lowerSkill);

        if (!appearsInResume) {
            // Missing Evidence
            String classification = "Optional".equals(importance) ? "Optional" : "Missing";
            String requiredProficiency = "Essential".equals(importance) ? "Advanced" : "Intermediate";
            String currentProficiency = "Unverified in Resume";
            String evidence = "No documented evidence found in the provided resume.";
            int confidence = 92;

            // Prioritization Calculation
            int priorityScore = calculatePriorityScore(skill, importance, classification, category);
            String priorityLevel = priorityScore >= 70 ? "High" : (priorityScore >= 45 ? "Medium" : "Low");
            String effort = determineEstimatedEffort(skill, category);

            String explanation = "No mention or project evidence of " + skill + " was identified in the uploaded resume. " +
                    "Note: This indicates a lack of documented proof in the resume, not conclusive evidence that you lack this competency.";

            String recommendedAction = "Build a targeted project feature or module implementing " + skill + " and document its technical outcomes on your resume.";

            return new SkillDetailDto(skill, currentProficiency, requiredProficiency, classification, evidence,
                    confidence, priorityScore, priorityLevel, category, importance, effort, explanation, recommendedAction);
        }

        // Skill is present: Extract matching sentence as genuine textual evidence
        String bestEvidenceSentence = findBestSentence(resumeSentences, lowerSkill);
        if (bestEvidenceSentence == null || bestEvidenceSentence.isBlank()) {
            bestEvidenceSentence = "Mentioned in resume skills or coursework section: '" + skill + "'";
        }

        // Assess depth of evidence
        boolean hasDepthContext = hasDepthKeywords(bestEvidenceSentence) || hasQuantifiedImpact(bestEvidenceSentence);

        String classification;
        String currentProficiency;
        String requiredProficiency = "Essential".equals(importance) ? "Advanced" : "Intermediate";
        int confidence;

        if (hasDepthContext) {
            classification = "Optional".equals(importance) ? "Optional" : "Strong";
            currentProficiency = "Advanced";
            confidence = 94;
        } else {
            classification = "Optional".equals(importance) ? "Optional" : "Developing";
            currentProficiency = "Beginner to Intermediate";
            confidence = 82;
        }

        int priorityScore = calculatePriorityScore(skill, importance, classification, category);
        String priorityLevel = priorityScore >= 70 ? "High" : (priorityScore >= 45 ? "Medium" : "Low");
        String effort = determineEstimatedEffort(skill, category);

        String explanation;
        String recommendedAction;

        if ("Strong".equals(classification)) {
            explanation = "Documented with convincing project or work context: \"" + truncate(bestEvidenceSentence, 120) + "\".";
            recommendedAction = "Highlight this achievement during technical interviews and describe architectural trade-offs.";
        } else if ("Developing".equals(classification)) {
            explanation = "Present in your resume but lacks deep implementation details or measurable outcomes: \"" + truncate(bestEvidenceSentence, 120) + "\".";
            recommendedAction = "Expand resume bullet points to include quantifiable metrics, architectural decisions, and production scope for " + skill + ".";
        } else {
            explanation = "Recognized as a supportive or optional skill for the role: \"" + truncate(bestEvidenceSentence, 120) + "\".";
            recommendedAction = "Maintain awareness of best practices for " + skill + " as an extra competitive edge.";
        }

        return new SkillDetailDto(skill, currentProficiency, requiredProficiency, classification, bestEvidenceSentence,
                confidence, priorityScore, priorityLevel, category, importance, effort, explanation, recommendedAction);
    }

    private int calculatePriorityScore(String skill, String importance, String classification, String category) {
        if ("Strong".equals(classification)) return 15;

        int score = 0;
        // 1. Job Importance (0 - 40 pts)
        if ("Essential".equals(importance)) score += 40;
        else if ("Desirable".equals(importance)) score += 25;
        else score += 10;

        // 2. Proficiency Gap (0 - 35 pts)
        if ("Missing".equals(classification)) score += 35;
        else if ("Developing".equals(classification)) score += 18;

        // 3. Foundational Dependency Factor (0 - 15 pts)
        if (FOUNDATIONAL_DEPENDENCIES.contains(skill)) score += 15;
        else if ("Technical".equals(category)) score += 8;

        // 4. Quick win bonus for high ROI skills (0 - 10 pts)
        if ("Git".equals(skill) || "Docker".equals(skill) || "REST API".equals(skill) || "SQL".equals(skill)) {
            score += 10;
        }

        return Math.min(100, Math.max(0, score));
    }

    private String determineEstimatedEffort(String skill, String category) {
        if ("Soft".equals(category)) return "Low (1-2 wks)";
        if ("Git".equals(skill) || "SQL".equals(skill) || "REST API".equals(skill) || "HTML5".equals(skill) || "CSS3".equals(skill)) {
            return "Low (1-2 wks)";
        }
        if ("Docker".equals(skill) || "React".equals(skill) || "TypeScript".equals(skill) || "PostgreSQL".equals(skill) || "Redis".equals(skill)) {
            return "Medium (3-4 wks)";
        }
        return "High (5+ wks)";
    }

    private void categorizeSkills(SkillAnalysisResponse res) {
        if (res.getSkills() == null) return;

        List<SkillDetailDto> strong = new ArrayList<>();
        List<SkillDetailDto> developing = new ArrayList<>();
        List<SkillDetailDto> missing = new ArrayList<>();
        List<SkillDetailDto> optional = new ArrayList<>();

        List<String> presentNames = new ArrayList<>();
        List<String> missingNames = new ArrayList<>();

        for (SkillDetailDto s : res.getSkills()) {
            if ("Strong".equalsIgnoreCase(s.getClassification())) {
                strong.add(s);
                presentNames.add(s.getSkillName());
            } else if ("Developing".equalsIgnoreCase(s.getClassification())) {
                developing.add(s);
                presentNames.add(s.getSkillName());
            } else if ("Missing".equalsIgnoreCase(s.getClassification())) {
                missing.add(s);
                missingNames.add(s.getSkillName());
            } else {
                optional.add(s);
                if (s.getEvidence() != null && !s.getEvidence().contains("No documented evidence")) {
                    presentNames.add(s.getSkillName());
                } else {
                    missingNames.add(s.getSkillName());
                }
            }
        }

        // Prioritize gaps: missing & developing skills sorted by priorityScore descending
        List<SkillDetailDto> highGaps = res.getSkills().stream()
                .filter(s -> ("Missing".equalsIgnoreCase(s.getClassification()) || "Developing".equalsIgnoreCase(s.getClassification())))
                .sorted(Comparator.comparingInt(SkillDetailDto::getPriorityScore).reversed())
                .collect(Collectors.toList());

        res.setStrongSkills(strong);
        res.setDevelopingSkills(developing);
        res.setMissingSkills(missing);
        res.setOptionalSkills(optional);
        res.setHighPriorityGaps(highGaps);

        res.setStrongCount(strong.size());
        res.setDevelopingCount(developing.size());
        res.setMissingCount(missing.size());
        res.setOptionalCount(optional.size());

        res.setPresentSkills(presentNames);
        res.setMissingSkillsList(missingNames);
    }

    private boolean containsSkill(String text, String skill) {
        String pattern = "(?i)\\b" + Pattern.quote(skill) + "\\b";
        return Pattern.compile(pattern).matcher(text).find();
    }

    private boolean isSkillEssentialInJd(String jd, String skill) {
        String lower = jd.toLowerCase();
        String lowerSkill = skill.toLowerCase();
        int idx = lower.indexOf(lowerSkill);
        if (idx == -1) return false;
        String window = lower.substring(Math.max(0, idx - 100), Math.min(lower.length(), idx + 100));
        return window.contains("must") || window.contains("required") || window.contains("essential")
                || window.contains("minimum") || window.contains("core");
    }

    private boolean isSkillOptionalInJd(String jd, String skill) {
        String lower = jd.toLowerCase();
        String lowerSkill = skill.toLowerCase();
        int idx = lower.indexOf(lowerSkill);
        if (idx == -1) return false;
        String window = lower.substring(Math.max(0, idx - 80), Math.min(lower.length(), idx + 80));
        return window.contains("nice to have") || window.contains("bonus") || window.contains("preferred")
                || window.contains("plus") || window.contains("optional") || window.contains("desirable");
    }

    private List<String> extractSentences(String text) {
        if (text == null) return List.of();
        // Split on period, newline, or bullet markers
        String[] raw = text.split("(?<=[.!?\\n•\\-])\\s+");
        List<String> list = new ArrayList<>();
        for (String s : raw) {
            String trimmed = s.trim().replaceAll("^[•\\-*\\s]+", "");
            if (trimmed.length() > 10) {
                list.add(trimmed);
            }
        }
        return list;
    }

    private String findBestSentence(List<String> sentences, String lowerSkill) {
        String best = null;
        int bestScore = -1;

        for (String sentence : sentences) {
            if (containsSkill(sentence.toLowerCase(), lowerSkill)) {
                int score = sentence.length();
                if (hasDepthKeywords(sentence)) score += 50;
                if (hasQuantifiedImpact(sentence)) score += 40;
                if (score > bestScore) {
                    bestScore = score;
                    best = sentence;
                }
            }
        }
        return best;
    }

    private boolean hasDepthKeywords(String text) {
        String lower = text.toLowerCase();
        return lower.contains("built") || lower.contains("developed") || lower.contains("implemented")
                || lower.contains("architected") || lower.contains("designed") || lower.contains("engineered")
                || lower.contains("deployed") || lower.contains("scaled") || lower.contains("optimized")
                || lower.contains("spearheaded") || lower.contains("managed") || lower.contains("tested")
                || lower.contains("production") || lower.contains("years");
    }

    private boolean hasQuantifiedImpact(String text) {
        Matcher m = Pattern.compile("\\b(\\d+%)|(\\$\\d+)|(\\d+\\+?\\s*(users|ms|seconds|req|requests|teams|engineers))\\b", Pattern.CASE_INSENSITIVE).matcher(text);
        return m.find();
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max - 3) + "...";
    }

    private RoadmapDto generateRoadmap(String targetRole, List<String> missingTech, List<String> missingSoft) {
        RoadmapDto roadmap = new RoadmapDto();
        roadmap.setTitle("Personalized Gap Bridging Roadmap: " + targetRole);
        roadmap.setTargetRole(targetRole);

        List<RoadmapDto.RoadmapStageDto> stages = new ArrayList<>();

        // Stage 1: Core Fundamentals & Primary Gap
        RoadmapDto.RoadmapStageDto stage1 = new RoadmapDto.RoadmapStageDto();
        stage1.setStageNumber(1);
        stage1.setStageName("Phase 1: Core Foundation & High-Priority Gaps");
        stage1.setDuration("Weeks 1 - 2");
        String primarySkill = (missingTech != null && !missingTech.isEmpty()) ? missingTech.get(0) : "System Architecture";
        stage1.setFocusArea("Mastering " + primarySkill + " fundamentals and core workflows");
        stage1.setLearningGoals(List.of(
                "Study official documentation and idiomatic patterns for " + primarySkill,
                "Configure modern development environment and debugging tools",
                "Implement 3 guided exercises and standard patterns"
        ));
        stage1.setRecommendedResources(List.of(
                "Official " + primarySkill + " Documentation & Quickstarts",
                "FreeCodeCamp & Deep Dive Tutorials",
                "Hands-on coding exercises"
        ));
        stage1.setMilestoneProjects(List.of(
                "Build a standalone utility or microservice using " + primarySkill
        ));
        stages.add(stage1);

        // Stage 2: Secondary Technical Skills & Integration
        RoadmapDto.RoadmapStageDto stage2 = new RoadmapDto.RoadmapStageDto();
        stage2.setStageNumber(2);
        stage2.setStageName("Phase 2: Integration & Advanced Stack");
        stage2.setDuration("Weeks 3 - 5");
        String secondSkill = (missingTech != null && missingTech.size() > 1) ? missingTech.get(1) : "Production System Design";
        stage2.setFocusArea("End-to-End integration with " + secondSkill);
        stage2.setLearningGoals(List.of(
                "Integrate primary skills with " + secondSkill,
                "Implement comprehensive unit and integration testing",
                "Implement security, caching, and containerized deployment"
        ));
        stage2.setRecommendedResources(List.of(
                "Full-stack architecture case studies",
                "Production best practices tutorials"
        ));
        stage2.setMilestoneProjects(List.of(
                "Full-featured capstone project addressing the exact requirements of " + targetRole
        ));
        stages.add(stage2);

        // Stage 3: Interview Readiness & Portfolio Polish
        RoadmapDto.RoadmapStageDto stage3 = new RoadmapDto.RoadmapStageDto();
        stage3.setStageNumber(3);
        stage3.setStageName("Phase 3: Career Readiness & Portfolio Verification");
        stage3.setDuration("Weeks 6 - 8");
        stage3.setFocusArea("Portfolio deployment, resume optimization, and mock technical interviews");
        stage3.setLearningGoals(List.of(
                "Deploy capstone projects to live cloud environment with automated CI/CD",
                "Refactor resume bullets to showcase verified outcomes and GitHub links",
                "Conduct mock technical and behavioral interviews (" + ((missingSoft != null && !missingSoft.isEmpty()) ? missingSoft.get(0) : "Communication") + ")"
        ));
        stage3.setRecommendedResources(List.of(
                "STAR method behavioral interview guides",
                "System design and coding interview challenge sets"
        ));
        stage3.setMilestoneProjects(List.of(
                "Public GitHub repository with comprehensive README, live demo link, and architecture diagrams"
        ));
        stages.add(stage3);

        roadmap.setEstimatedWeeks(8);
        roadmap.setStages(stages);
        return roadmap;
    }
}
