package com.careergap.service;

import com.careergap.dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfResumeGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfResumeGeneratorService.class);

    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth(); // 612
    private static final float PAGE_HEIGHT = PDRectangle.LETTER.getHeight(); // 792
    private static final float MARGIN_LEFT = 45f;
    private static final float MARGIN_RIGHT = 45f;
    private static final float USABLE_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT; // 522
    private static final float MARGIN_TOP = 45f;
    private static final float MARGIN_BOTTOM = 45f;

    public byte[] generateResumePdf(ResumeDataDto resume) {
        if (resume == null) {
            resume = new ResumeDataDto();
        }

        try (PDDocument document = new PDDocument()) {
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            PdfContext ctx = new PdfContext(document, fontRegular, fontBold, fontOblique);
            ctx.newPage();

            // 1. Header (Name, Contact Information)
            renderHeader(ctx, resume);

            // 2. Professional Summary
            if (resume.getSummary() != null && !resume.getSummary().trim().isEmpty()) {
                renderSectionHeading(ctx, "PROFESSIONAL SUMMARY");
                renderParagraph(ctx, resume.getSummary().trim(), 10f, 13f, ctx.fontRegular, Color.DARK_GRAY);
                ctx.y -= 10f;
            }

            // 3. Technical Skills
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                renderSectionHeading(ctx, "TECHNICAL & VERIFIED SKILLS");
                renderSkills(ctx, resume.getSkills());
                ctx.y -= 10f;
            }

            // 4. Experience
            if (resume.getExperiences() != null && !resume.getExperiences().isEmpty()) {
                renderSectionHeading(ctx, "PROFESSIONAL EXPERIENCE");
                renderExperiences(ctx, resume.getExperiences());
                ctx.y -= 10f;
            }

            // 5. Projects
            if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                renderSectionHeading(ctx, "PROJECTS");
                renderProjects(ctx, resume.getProjects());
                ctx.y -= 10f;
            }

            // 6. Education
            if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
                renderSectionHeading(ctx, "EDUCATION");
                renderEducation(ctx, resume.getEducation());
                ctx.y -= 10f;
            }

            // 7. Certifications
            if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
                renderSectionHeading(ctx, "CERTIFICATIONS & CREDENTIALS");
                renderCertifications(ctx, resume.getCertifications());
            }

            ctx.closeCurrentStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            logger.error("Failed to generate resume PDF", e);
            throw new RuntimeException("Error generating resume PDF: " + e.getMessage(), e);
        }
    }

    private void renderHeader(PdfContext ctx, ResumeDataDto resume) throws IOException {
        String name = sanitize(resume.getFullName() != null && !resume.getFullName().trim().isEmpty()
                ? resume.getFullName() : "Candidate Name");

        ctx.ensureSpace(60f);

        // Center Candidate Name
        float nameSize = 19f;
        float nameWidth = ctx.fontBold.getStringWidth(name) / 1000f * nameSize;
        float nameX = MARGIN_LEFT + (USABLE_WIDTH - nameWidth) / 2f;
        if (nameX < MARGIN_LEFT) nameX = MARGIN_LEFT;

        ctx.stream.beginText();
        ctx.stream.setFont(ctx.fontBold, nameSize);
        ctx.stream.setNonStrokingColor(new Color(24, 43, 73)); // Navy dark
        ctx.stream.newLineAtOffset(nameX, ctx.y);
        ctx.stream.showText(name);
        ctx.stream.endText();
        ctx.y -= 16f;

        // Contact details line (Email | Phone | Location | LinkedIn | GitHub)
        List<String> contactParts = new ArrayList<>();
        if (resume.getEmail() != null && !resume.getEmail().isBlank()) contactParts.add(resume.getEmail().trim());
        if (resume.getPhoneNumber() != null && !resume.getPhoneNumber().isBlank()) contactParts.add(resume.getPhoneNumber().trim());
        if (resume.getLocation() != null && !resume.getLocation().isBlank()) contactParts.add(resume.getLocation().trim());
        if (resume.getLinkedinUrl() != null && !resume.getLinkedinUrl().isBlank()) contactParts.add(resume.getLinkedinUrl().trim());
        if (resume.getGithubUrl() != null && !resume.getGithubUrl().isBlank()) contactParts.add(resume.getGithubUrl().trim());
        if (resume.getPortfolioUrl() != null && !resume.getPortfolioUrl().isBlank()) contactParts.add(resume.getPortfolioUrl().trim());

        String contactLine = sanitize(String.join("  |  ", contactParts));
        if (!contactLine.isEmpty()) {
            float contactSize = 9f;
            float contactWidth = ctx.fontRegular.getStringWidth(contactLine) / 1000f * contactSize;
            float contactX = MARGIN_LEFT + (USABLE_WIDTH - contactWidth) / 2f;
            if (contactX < MARGIN_LEFT) {
                // If it exceeds one line, wrap it
                renderParagraph(ctx, contactLine, 9f, 12f, ctx.fontRegular, Color.GRAY);
            } else {
                ctx.stream.beginText();
                ctx.stream.setFont(ctx.fontRegular, contactSize);
                ctx.stream.setNonStrokingColor(Color.GRAY);
                ctx.stream.newLineAtOffset(contactX, ctx.y);
                ctx.stream.showText(contactLine);
                ctx.stream.endText();
                ctx.y -= 12f;
            }
        }

        // Horizontal divider line
        ctx.y -= 6f;
        ctx.stream.setStrokingColor(new Color(200, 210, 225));
        ctx.stream.setLineWidth(1.0f);
        ctx.stream.moveTo(MARGIN_LEFT, ctx.y);
        ctx.stream.lineTo(PAGE_WIDTH - MARGIN_RIGHT, ctx.y);
        ctx.stream.stroke();
        ctx.y -= 14f;
    }

    private void renderSectionHeading(PdfContext ctx, String heading) throws IOException {
        ctx.ensureSpace(35f);
        heading = sanitize(heading.toUpperCase());

        ctx.stream.beginText();
        ctx.stream.setFont(ctx.fontBold, 11f);
        ctx.stream.setNonStrokingColor(new Color(20, 50, 95));
        ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
        ctx.stream.showText(heading);
        ctx.stream.endText();

        ctx.y -= 4f;
        ctx.stream.setStrokingColor(new Color(40, 90, 160));
        ctx.stream.setLineWidth(1.2f);
        ctx.stream.moveTo(MARGIN_LEFT, ctx.y);
        ctx.stream.lineTo(PAGE_WIDTH - MARGIN_RIGHT, ctx.y);
        ctx.stream.stroke();

        ctx.y -= 12f;
    }

    private void renderSkills(PdfContext ctx, List<ResumeSkillItemDto> skills) throws IOException {
        // Group skills by category if present
        java.util.Map<String, List<ResumeSkillItemDto>> grouped = new java.util.LinkedHashMap<>();
        for (ResumeSkillItemDto s : skills) {
            String cat = s.getCategory() != null && !s.getCategory().isBlank() ? s.getCategory().trim() : "Core Competencies";
            grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(s);
        }

        for (java.util.Map.Entry<String, List<ResumeSkillItemDto>> entry : grouped.entrySet()) {
            ctx.ensureSpace(20f);
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey()).append(": ");
            List<String> skillItems = new ArrayList<>();
            for (ResumeSkillItemDto item : entry.getValue()) {
                String label = item.getSkillName();
                if (item.isVerified()) {
                    label += " [Verified]";
                } else if ("ASSESSED".equalsIgnoreCase(item.getSkillType())) {
                    label += " [Assessed]";
                } else if ("LEARNED".equalsIgnoreCase(item.getSkillType())) {
                    label += " [Learned]";
                }
                skillItems.add(label);
            }
            sb.append(String.join(", ", skillItems));

            renderParagraph(ctx, sb.toString(), 9.5f, 13f, ctx.fontRegular, new Color(30, 30, 30));
            ctx.y -= 3f;
        }
    }

    private void renderExperiences(PdfContext ctx, List<ResumeExperienceDto> experiences) throws IOException {
        for (ResumeExperienceDto exp : experiences) {
            ctx.ensureSpace(40f);

            // Job Title (Bold) & Duration (Right aligned)
            String role = sanitize(exp.getRole() != null ? exp.getRole() : "Role");
            String duration = sanitize(exp.getDuration() != null ? exp.getDuration() : "");

            ctx.stream.beginText();
            ctx.stream.setFont(ctx.fontBold, 10f);
            ctx.stream.setNonStrokingColor(Color.BLACK);
            ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
            ctx.stream.showText(role);
            ctx.stream.endText();

            if (!duration.isEmpty()) {
                float durWidth = ctx.fontRegular.getStringWidth(duration) / 1000f * 9.5f;
                float durX = PAGE_WIDTH - MARGIN_RIGHT - durWidth;
                ctx.stream.beginText();
                ctx.stream.setFont(ctx.fontRegular, 9.5f);
                ctx.stream.setNonStrokingColor(Color.DARK_GRAY);
                ctx.stream.newLineAtOffset(durX, ctx.y);
                ctx.stream.showText(duration);
                ctx.stream.endText();
            }
            ctx.y -= 12f;

            // Company & Location (Oblique/Gray)
            String companyLoc = sanitize((exp.getCompany() != null ? exp.getCompany() : "")
                    + (exp.getLocation() != null && !exp.getLocation().isBlank() ? " - " + exp.getLocation() : ""));
            if (!companyLoc.isEmpty()) {
                ctx.stream.beginText();
                ctx.stream.setFont(ctx.fontOblique, 9.5f);
                ctx.stream.setNonStrokingColor(new Color(60, 60, 60));
                ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
                ctx.stream.showText(companyLoc);
                ctx.stream.endText();
                ctx.y -= 12f;
            }

            // Bullets
            if (exp.getBullets() != null) {
                for (String bullet : exp.getBullets()) {
                    if (bullet != null && !bullet.isBlank()) {
                        renderBullet(ctx, bullet.trim());
                    }
                }
            }
            ctx.y -= 6f;
        }
    }

    private void renderProjects(PdfContext ctx, List<ResumeProjectDto> projects) throws IOException {
        for (ResumeProjectDto proj : projects) {
            ctx.ensureSpace(40f);

            String title = sanitize(proj.getTitle() != null ? proj.getTitle() : "Project");
            String tech = sanitize(proj.getTechnologies() != null && !proj.getTechnologies().isBlank()
                    ? " | Tech Stack: " + proj.getTechnologies() : "");

            ctx.stream.beginText();
            ctx.stream.setFont(ctx.fontBold, 10f);
            ctx.stream.setNonStrokingColor(Color.BLACK);
            ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
            ctx.stream.showText(title);

            if (!tech.isEmpty()) {
                ctx.stream.setFont(ctx.fontOblique, 9.5f);
                ctx.stream.setNonStrokingColor(new Color(70, 70, 70));
                ctx.stream.showText(tech);
            }
            ctx.stream.endText();

            // URLs on right if available
            String links = sanitize((proj.getGithubUrl() != null && !proj.getGithubUrl().isBlank() ? proj.getGithubUrl() : "")
                    + (proj.getLiveUrl() != null && !proj.getLiveUrl().isBlank() ? " " + proj.getLiveUrl() : ""));
            if (!links.isEmpty()) {
                float linkWidth = ctx.fontRegular.getStringWidth(links) / 1000f * 8.5f;
                if (linkWidth < 180f) {
                    ctx.stream.beginText();
                    ctx.stream.setFont(ctx.fontRegular, 8.5f);
                    ctx.stream.setNonStrokingColor(new Color(0, 102, 204));
                    ctx.stream.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - linkWidth, ctx.y);
                    ctx.stream.showText(links);
                    ctx.stream.endText();
                }
            }
            ctx.y -= 13f;

            if (proj.getBullets() != null) {
                for (String bullet : proj.getBullets()) {
                    if (bullet != null && !bullet.isBlank()) {
                        renderBullet(ctx, bullet.trim());
                    }
                }
            }
            ctx.y -= 6f;
        }
    }

    private void renderEducation(PdfContext ctx, List<ResumeEducationDto> educationList) throws IOException {
        for (ResumeEducationDto edu : educationList) {
            ctx.ensureSpace(30f);

            String degree = sanitize((edu.getDegree() != null ? edu.getDegree() : "")
                    + (edu.getBranch() != null && !edu.getBranch().isBlank() ? " in " + edu.getBranch() : ""));
            String year = sanitize(edu.getGraduationYear() != null ? edu.getGraduationYear() : "");

            ctx.stream.beginText();
            ctx.stream.setFont(ctx.fontBold, 10f);
            ctx.stream.setNonStrokingColor(Color.BLACK);
            ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
            ctx.stream.showText(degree);
            ctx.stream.endText();

            if (!year.isEmpty()) {
                float yrWidth = ctx.fontRegular.getStringWidth(year) / 1000f * 9.5f;
                ctx.stream.beginText();
                ctx.stream.setFont(ctx.fontRegular, 9.5f);
                ctx.stream.setNonStrokingColor(Color.DARK_GRAY);
                ctx.stream.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - yrWidth, ctx.y);
                ctx.stream.showText(year);
                ctx.stream.endText();
            }
            ctx.y -= 12f;

            String inst = sanitize((edu.getInstitution() != null ? edu.getInstitution() : "")
                    + (edu.getGradeGpa() != null && !edu.getGradeGpa().isBlank() ? " | GPA / Grade: " + edu.getGradeGpa() : ""));
            if (!inst.isEmpty()) {
                ctx.stream.beginText();
                ctx.stream.setFont(ctx.fontRegular, 9.5f);
                ctx.stream.setNonStrokingColor(new Color(70, 70, 70));
                ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
                ctx.stream.showText(inst);
                ctx.stream.endText();
                ctx.y -= 12f;
            }
            ctx.y -= 4f;
        }
    }

    private void renderCertifications(PdfContext ctx, List<ResumeCertificationDto> certifications) throws IOException {
        for (ResumeCertificationDto cert : certifications) {
            ctx.ensureSpace(20f);
            String name = sanitize(cert.getName() != null ? cert.getName() : "Certification");
            String issuer = sanitize(cert.getIssuer() != null ? cert.getIssuer() : "");
            String yr = sanitize(cert.getIssueYear() != null ? cert.getIssueYear() : "");

            StringBuilder sb = new StringBuilder();
            sb.append("- ").append(name);
            if (!issuer.isEmpty()) sb.append(" - ").append(issuer);
            if (!yr.isEmpty()) sb.append(" (").append(yr).append(")");

            renderParagraph(ctx, sb.toString(), 9.5f, 13f, ctx.fontRegular, new Color(30, 30, 30));
            ctx.y -= 2f;
        }
    }

    private void renderBullet(PdfContext ctx, String bulletText) throws IOException {
        bulletText = sanitize(bulletText);
        float fontSize = 9.5f;
        float leading = 12.5f;
        float bulletIndent = 12f;
        float bulletWidth = USABLE_WIDTH - bulletIndent;

        List<String> lines = wrapText(bulletText, ctx.fontRegular, fontSize, bulletWidth);

        for (int i = 0; i < lines.size(); i++) {
            ctx.ensureSpace(leading);
            ctx.stream.beginText();
            ctx.stream.setFont(ctx.fontRegular, fontSize);
            ctx.stream.setNonStrokingColor(new Color(40, 40, 40));

            if (i == 0) {
                // Draw bullet symbol
                ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
                ctx.stream.showText("-");
                ctx.stream.endText();

                ctx.stream.beginText();
                ctx.stream.setFont(ctx.fontRegular, fontSize);
                ctx.stream.setNonStrokingColor(new Color(40, 40, 40));
                ctx.stream.newLineAtOffset(MARGIN_LEFT + bulletIndent, ctx.y);
                ctx.stream.showText(lines.get(i));
                ctx.stream.endText();
            } else {
                ctx.stream.newLineAtOffset(MARGIN_LEFT + bulletIndent, ctx.y);
                ctx.stream.showText(lines.get(i));
                ctx.stream.endText();
            }
            ctx.y -= leading;
        }
        ctx.y -= 2f;
    }

    private void renderParagraph(PdfContext ctx, String text, float fontSize, float leading, PDType1Font font, Color color) throws IOException {
        text = sanitize(text);
        List<String> lines = wrapText(text, font, fontSize, USABLE_WIDTH);
        for (String line : lines) {
            ctx.ensureSpace(leading);
            ctx.stream.beginText();
            ctx.stream.setFont(font, fontSize);
            ctx.stream.setNonStrokingColor(color);
            ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.y);
            ctx.stream.showText(line);
            ctx.stream.endText();
            ctx.y -= leading;
        }
    }

    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }

        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
            float width = font.getStringWidth(candidate) / 1000f * fontSize;

            if (width <= maxWidth) {
                currentLine = new StringBuilder(candidate);
            } else {
                if (currentLine.length() > 0) {
                    result.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Single word exceeds width, force break
                    result.add(word);
                    currentLine = new StringBuilder();
                }
            }
        }
        if (currentLine.length() > 0) {
            result.add(currentLine.toString());
        }

        return result;
    }

    private String sanitize(String input) {
        if (input == null) return "";
        return input
                .replace("\u2018", "'")
                .replace("\u2019", "'")
                .replace("\u201C", "\"")
                .replace("\u201D", "\"")
                .replace("\u2013", "-")
                .replace("\u2014", "-")
                .replace("\u2022", "*")
                .replace("\u00A0", " ")
                .replaceAll("[^\\x00-\\x7E]", " ")
                .trim();
    }

    private static class PdfContext {
        final PDDocument document;
        final PDType1Font fontRegular;
        final PDType1Font fontBold;
        final PDType1Font fontOblique;
        PDPageContentStream stream;
        float y;

        PdfContext(PDDocument document, PDType1Font fontRegular, PDType1Font fontBold, PDType1Font fontOblique) {
            this.document = document;
            this.fontRegular = fontRegular;
            this.fontBold = fontBold;
            this.fontOblique = fontOblique;
        }

        void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = PAGE_HEIGHT - MARGIN_TOP;
        }

        void ensureSpace(float requiredHeight) throws IOException {
            if (y - requiredHeight < MARGIN_BOTTOM) {
                newPage();
            }
        }

        void closeCurrentStream() throws IOException {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        }
    }
}
