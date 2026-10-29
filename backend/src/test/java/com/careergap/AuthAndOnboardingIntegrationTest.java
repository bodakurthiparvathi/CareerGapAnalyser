package com.careergap;

import com.careergap.dto.*;
import com.careergap.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AuthAndOnboardingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegistration_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "secret123", "Full Stack Developer");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("jane@example.com"))
                .andExpect(jsonPath("$.data.user.fullName").value("Jane Doe"));

        assertTrue(userRepository.existsByEmail("jane@example.com"));
    }

    @Test
    void testRegistration_DuplicateEmail_Rejection() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "secret123", "Full Stack Developer");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try registering again with identical email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already registered. Please login."));
    }

    @Test
    void testRegistration_Validation_ShortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "123", "Full Stack Developer");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testLogin_Success() throws Exception {
        RegisterRequest registerReq = new RegisterRequest("John Doe", "john@example.com", "password123", "Backend Developer");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        AuthRequest loginReq = new AuthRequest("john@example.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("john@example.com"));
    }

    @Test
    void testLogin_BadCredentials_Rejection() throws Exception {
        RegisterRequest registerReq = new RegisterRequest("John Doe", "john@example.com", "password123", "Backend Developer");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        AuthRequest loginReq = new AuthRequest("john@example.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void testCurrentUser_RequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPasswordReset_Workflow() throws Exception {
        RegisterRequest registerReq = new RegisterRequest("Alice Smith", "alice@example.com", "originalPass123", "Data Scientist");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // 1. Forgot password
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest("alice@example.com");
        MvcResult forgotResult = mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resetToken").isNotEmpty())
                .andReturn();

        String responseJson = forgotResult.getResponse().getContentAsString();
        String resetToken = objectMapper.readTree(responseJson).get("data").get("resetToken").asText();
        assertNotNull(resetToken);

        // 2. Reset password
        ResetPasswordRequest resetReq = new ResetPasswordRequest(resetToken, "newSuperSecret456");
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 3. Login with old password fails
        AuthRequest oldLogin = new AuthRequest("alice@example.com", "originalPass123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldLogin)))
                .andExpect(status().isUnauthorized());

        // 4. Login with new password succeeds
        AuthRequest newLogin = new AuthRequest("alice@example.com", "newSuperSecret456");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void testOnboarding_SaveAndRetrieveFullProfile() throws Exception {
        // 1. Register & get token
        RegisterRequest registerReq = new RegisterRequest("Bob Builder", "bob@example.com", "builder123", "Full Stack Engineer");
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .get("data").get("token").asText();

        // 2. Submit complete personalized onboarding payload
        OnboardingRequest onbReq = new OnboardingRequest();
        onbReq.setFullName("Bob Builder");
        onbReq.setPhoneNumber("+1 555 123 4567");
        onbReq.setLocation("Seattle, WA");
        onbReq.setDegree("B.Tech");
        onbReq.setBranch("Computer Science");
        onbReq.setCollege("Stanford Institute");
        onbReq.setGraduationYear(2025);
        onbReq.setAcademicStatus("Final Year Student");
        onbReq.setTargetRole("Senior Java Backend Developer");
        onbReq.setPreferredIndustry("Fintech");
        onbReq.setYearsOfExperience(2);
        onbReq.setBio("Passionate about distributed microservices and clean code.");
        onbReq.setSoftSkills(List.of("Teamwork", "Problem Solving", "Communication"));

        onbReq.setTechnicalSkills(List.of(
                new OnboardingRequest.SkillDto("Java", "Advanced", "Technical"),
                new OnboardingRequest.SkillDto("Spring Boot", "Advanced", "Technical"),
                new OnboardingRequest.SkillDto("PostgreSQL", "Intermediate", "Technical")
        ));

        onbReq.setProjects(List.of(
                new OnboardingRequest.ProjectDto(
                        "CareerGap Platform",
                        "AI powered resume gap analysis platform",
                        "Spring Boot, React, PostgreSQL",
                        "https://github.com/bob/careergap",
                        "https://careergap.ai"
                )
        ));

        onbReq.setExperiences(List.of(
                new OnboardingRequest.ExperienceDto(
                        "Backend Intern",
                        "CloudTech Inc",
                        "May 2024 - Aug 2024",
                        "Built REST APIs using Spring Boot and Hibernate"
                )
        ));

        onbReq.setCertifications(List.of(
                new OnboardingRequest.CertificationDto(
                        "AWS Certified Developer",
                        "Amazon Web Services",
                        2024,
                        "https://aws.cert/123"
                )
        ));

        mockMvc.perform(post("/api/users/onboarding")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onbReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onboardingCompleted").value(true))
                .andExpect(jsonPath("$.data.college").value("Stanford Institute"))
                .andExpect(jsonPath("$.data.preferredIndustry").value("Fintech"))
                .andExpect(jsonPath("$.data.technicalSkills.length()").value(3))
                .andExpect(jsonPath("$.data.projects.length()").value(1))
                .andExpect(jsonPath("$.data.experiences.length()").value(1))
                .andExpect(jsonPath("$.data.certifications.length()").value(1));

        // 3. Retrieve profile to ensure DB persistence
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.degree").value("B.Tech"))
                .andExpect(jsonPath("$.data.graduationYear").value(2025))
                .andExpect(jsonPath("$.data.projects[0].title").value("CareerGap Platform"));
    }

    @Test
    void testAuthorization_AccessWithoutToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }
}
