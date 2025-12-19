package com.refactorai.service;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DiffService {

    public String generateDiff(String originalCode, String refactoredCode) {
        try {
            // Split code into lines
            List<String> originalLines = Arrays.asList(originalCode.split("\n"));
            List<String> refactoredLines = Arrays.asList(refactoredCode.split("\n"));

            // Generate patch (differences)
            Patch<String> patch = DiffUtils.diff(originalLines, refactoredLines);

            // Generate unified diff format
            List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
                    "Original Code",
                    "Refactored Code",
                    originalLines,
                    patch,
                    3 // Context lines
            );

            // Join diff lines into single string
            return String.join("\n", unifiedDiff);

        } catch (Exception e) {
            return "Error generating diff: " + e.getMessage();
        }
    }

    public String extractJavaCode(String aiResponse) {
        // Remove markdown code blocks if present
        String cleaned = aiResponse.trim();

        // Remove ```java and ``` markers
        if (cleaned.startsWith("```java")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }
}