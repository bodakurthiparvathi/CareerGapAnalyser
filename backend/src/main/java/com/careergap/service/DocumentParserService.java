package com.careergap.service;

import com.careergap.exception.AppException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class DocumentParserService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentParserService.class);

    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("Uploaded file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException("Filename is missing");
        }

        String lowerName = originalFilename.toLowerCase();
        try {
            if (lowerName.endsWith(".pdf")) {
                return extractPdfText(file);
            } else if (lowerName.endsWith(".docx")) {
                return extractDocxText(file);
            } else if (lowerName.endsWith(".txt")) {
                return new String(file.getBytes());
            } else {
                throw new AppException("Unsupported file format. Please upload a PDF (.pdf) or Word document (.docx)");
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to extract text from file: {}", originalFilename, e);
            throw new AppException("Failed to parse document: " + e.getMessage());
        }
    }

    private String extractPdfText(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            if (document.isEncrypted()) {
                throw new AppException("The uploaded PDF is password protected and cannot be read.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.trim().isEmpty()) {
                throw new AppException("No readable text found in PDF. Scanned images are not supported yet.");
            }
            return text.trim();
        }
    }

    private String extractDocxText(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            String text = extractor.getText();
            if (text == null || text.trim().isEmpty()) {
                throw new AppException("No readable text found in DOCX file.");
            }
            return text.trim();
        }
    }
}
