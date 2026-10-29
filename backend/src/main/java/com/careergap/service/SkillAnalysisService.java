package com.careergap.service;

import com.careergap.dto.RoadmapDto;
import com.careergap.dto.SkillAnalysisRequest;
import com.careergap.dto.SkillAnalysisResponse;
import com.careergap.dto.SkillDetailDto;
import com.careergap.entity.LearningRoadmap;
import com.careergap.entity.Resume;
import com.careergap.entity.SkillAnalysis;
import com.careergap.entity.User;
import com.careergap.exception.AppException;
import com.careergap.exception.ResourceNotFoundException;
import com.careergap.repository.LearningRoadmapRepository;
import com.careergap.repository.ResumeRepository;
import com.careergap.repository.SkillAnalysisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillAnalysisService {

    private final SkillAnalysisRepository skillAnalysisRepository;
    private final LearningRoadmapRepository learningRoadmapRepository;
    private final ResumeRepository resumeRepository;
    private final AuthService authService;
    private final DocumentParserService documentParserService;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    public SkillAnalysisService(
            SkillAnalysisRepository skillAnalysisRepository,
            LearningRoadmapRepository learningRoadmapRepository,
            ResumeRepository resumeRepository,
            AuthService authService,
            DocumentParserService documentParserService,
            AiService aiService,
            ObjectMapper objectMapper) {
        this.skillAnalysisRepository = skillAnalysisRepository;
        this.learningRoadmapRepository = learningRoadmapRepository;
        this.resumeRepository = resumeRepository;
        this.authService = authService;
        this.documentParserService = documentParserService;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Resume uploadAndParseResume(MultipartFile file) {
        User user = authService.getCurrentUser();
        String parsedText = documentParserService.extractText(file);

        Resume resume = new Resume(
                file.getOriginalFilename(),
                file.getContentType(),
                parsedText,
                user
        );

        return resumeRepository.save(resume);
    }

    public String parseJobDescriptionFile(MultipartFile file) {
        return documentParserService.extractText(file);
    }

    @Transactional
    public SkillAnalysisResponse performAnalysis(SkillAnalysisRequest request) {
        User user = authService.getCurrentUser();

        String resumeText = request.getResumeText();
        if ((resumeText == null || resumeText.isBlank()) && request.getResumeId() != null) {
            Resume resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", request.getResumeId()));
            if (!resume.getUser().getId().equals(user.getId())) {
                throw new AppException("Unauthorized access to requested resume");
            }
            resumeText = resume.getParsedText();
        }

        if (resumeText == null || resumeText.isBlank()) {
            throw new AppException("Please provide resume text or upload a resume file.");
        }

        // Run real algorithmic/AI analysis
        SkillAnalysisResponse response = aiService.analyzeSkillGap(
                request.getTargetRole(),
                request.getJobDescription(),
                resumeText
        );

        // Persist to Database
        SkillAnalysis entity = new SkillAnalysis();
        entity.setUser(user);
        entity.setTargetRole(request.getTargetRole());
        entity.setJobDescription(request.getJobDescription());
        entity.setOverallMatchScore(response.getOverallMatchScore());
        entity.setTechnicalScore(response.getTechnicalScore());
        entity.setSoftSkillsScore(response.getSoftSkillsScore());
        entity.setReadinessLevel(response.getReadinessLevel());
        entity.setPresentSkills(String.join(",", response.getPresentSkills()));
        entity.setMissingSkills(String.join(",", response.getMissingSkillsList()));
        entity.setStrengths(String.join("||", response.getStrengths()));
        entity.setWeaknesses(String.join("||", response.getWeaknesses()));
        entity.setRecommendations(String.join("||", response.getRecommendations()));
        entity.setCalculationCriteria(response.getCalculationCriteria());

        try {
            if (response.getSkills() != null && !response.getSkills().isEmpty()) {
                entity.setSkillsAnalysisJson(objectMapper.writeValueAsString(response.getSkills()));
            }
            if (response.getHighPriorityGaps() != null && !response.getHighPriorityGaps().isEmpty()) {
                entity.setHighPriorityGapsJson(objectMapper.writeValueAsString(response.getHighPriorityGaps()));
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize skill analysis results", e);
        }

        SkillAnalysis savedAnalysis = skillAnalysisRepository.save(entity);
        response.setId(savedAnalysis.getId());
        response.setCreatedAt(savedAnalysis.getCreatedAt());

        // Persist Roadmap
        RoadmapDto roadmapDto = response.getRoadmap();
        if (roadmapDto != null) {
            LearningRoadmap roadmapEntity = new LearningRoadmap();
            roadmapEntity.setUser(user);
            roadmapEntity.setSkillAnalysis(savedAnalysis);
            roadmapEntity.setTitle(roadmapDto.getTitle());
            roadmapEntity.setTargetRole(roadmapDto.getTargetRole());
            roadmapEntity.setEstimatedWeeks(roadmapDto.getEstimatedWeeks());
            try {
                roadmapEntity.setStagesJson(objectMapper.writeValueAsString(roadmapDto.getStages()));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize learning roadmap", e);
            }

            LearningRoadmap savedRoadmap = learningRoadmapRepository.save(roadmapEntity);
            roadmapDto.setId(savedRoadmap.getId());
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<SkillAnalysisResponse> getUserAnalysisHistory() {
        User user = authService.getCurrentUser();
        List<SkillAnalysis> analyses = skillAnalysisRepository.findByUserOrderByCreatedAtDesc(user);

        return analyses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SkillAnalysisResponse getAnalysisById(Long id) {
        User user = authService.getCurrentUser();
        SkillAnalysis analysis = skillAnalysisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SkillAnalysis", "id", id));

        if (!analysis.getUser().getId().equals(user.getId())) {
            throw new AppException("Unauthorized access to this analysis");
        }

        return mapToResponse(analysis);
    }

    private SkillAnalysisResponse mapToResponse(SkillAnalysis entity) {
        SkillAnalysisResponse dto = new SkillAnalysisResponse();
        dto.setId(entity.getId());
        dto.setTargetRole(entity.getTargetRole());
        dto.setOverallMatchScore(entity.getOverallMatchScore());
        dto.setTechnicalScore(entity.getTechnicalScore());
        dto.setSoftSkillsScore(entity.getSoftSkillsScore());
        dto.setReadinessLevel(entity.getReadinessLevel());
        dto.setCalculationCriteria(entity.getCalculationCriteria());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getPresentSkills() != null && !entity.getPresentSkills().isBlank()) {
            dto.setPresentSkills(Arrays.asList(entity.getPresentSkills().split(",")));
        } else {
            dto.setPresentSkills(List.of());
        }

        if (entity.getMissingSkills() != null && !entity.getMissingSkills().isBlank()) {
            dto.setMissingSkillsList(Arrays.asList(entity.getMissingSkills().split(",")));
        } else {
            dto.setMissingSkillsList(List.of());
        }

        if (entity.getStrengths() != null && !entity.getStrengths().isBlank()) {
            dto.setStrengths(Arrays.asList(entity.getStrengths().split("\\|\\|")));
        } else {
            dto.setStrengths(List.of());
        }

        if (entity.getWeaknesses() != null && !entity.getWeaknesses().isBlank()) {
            dto.setWeaknesses(Arrays.asList(entity.getWeaknesses().split("\\|\\|")));
        } else {
            dto.setWeaknesses(List.of());
        }

        if (entity.getRecommendations() != null && !entity.getRecommendations().isBlank()) {
            dto.setRecommendations(Arrays.asList(entity.getRecommendations().split("\\|\\|")));
        } else {
            dto.setRecommendations(List.of());
        }

        // Deserialize detailed skills JSON
        if (entity.getSkillsAnalysisJson() != null && !entity.getSkillsAnalysisJson().isBlank()) {
            try {
                List<SkillDetailDto> skills = objectMapper.readValue(
                        entity.getSkillsAnalysisJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, SkillDetailDto.class)
                );
                dto.setSkills(skills);

                List<SkillDetailDto> strong = skills.stream()
                        .filter(s -> "Strong".equalsIgnoreCase(s.getClassification()))
                        .collect(Collectors.toList());
                List<SkillDetailDto> developing = skills.stream()
                        .filter(s -> "Developing".equalsIgnoreCase(s.getClassification()))
                        .collect(Collectors.toList());
                List<SkillDetailDto> missing = skills.stream()
                        .filter(s -> "Missing".equalsIgnoreCase(s.getClassification()))
                        .collect(Collectors.toList());
                List<SkillDetailDto> optional = skills.stream()
                        .filter(s -> "Optional".equalsIgnoreCase(s.getClassification()))
                        .collect(Collectors.toList());

                dto.setStrongSkills(strong);
                dto.setDevelopingSkills(developing);
                dto.setMissingSkills(missing);
                dto.setOptionalSkills(optional);

                dto.setStrongCount(strong.size());
                dto.setDevelopingCount(developing.size());
                dto.setMissingCount(missing.size());
                dto.setOptionalCount(optional.size());
            } catch (Exception ignored) {
            }
        }

        if (entity.getHighPriorityGapsJson() != null && !entity.getHighPriorityGapsJson().isBlank()) {
            try {
                List<SkillDetailDto> highGaps = objectMapper.readValue(
                        entity.getHighPriorityGapsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, SkillDetailDto.class)
                );
                dto.setHighPriorityGaps(highGaps);
            } catch (Exception ignored) {
            }
        }

        // Fetch associated roadmap if exists
        learningRoadmapRepository.findBySkillAnalysisId(entity.getId()).ifPresent(rm -> {
            try {
                RoadmapDto rDto = new RoadmapDto();
                rDto.setId(rm.getId());
                rDto.setTitle(rm.getTitle());
                rDto.setTargetRole(rm.getTargetRole());
                rDto.setEstimatedWeeks(rm.getEstimatedWeeks());
                if (rm.getStagesJson() != null) {
                    List<RoadmapDto.RoadmapStageDto> stages = objectMapper.readValue(
                            rm.getStagesJson(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, RoadmapDto.RoadmapStageDto.class)
                    );
                    rDto.setStages(stages);
                }
                dto.setRoadmap(rDto);
            } catch (Exception ignored) {
            }
        });

        return dto;
    }
}
