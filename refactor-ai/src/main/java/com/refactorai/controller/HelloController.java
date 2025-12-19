package com.refactorai.controller;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.refactorai.analyzer.*;
import com.refactorai.entity.AnalysisHistory;
import com.refactorai.entity.User;
import com.refactorai.model.CodeSmell;
import com.refactorai.repository.AnalysisHistoryRepository;
import com.refactorai.repository.UserRepository;
import com.refactorai.security.JwtUtil;
import com.refactorai.service.DiffService;
import com.refactorai.service.OpenAIService;
import com.refactorai.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HelloController {

    @Autowired
    private ParserService parserService;

    @Autowired
    private LongMethodDetector longMethodDetector;

    @Autowired
    private DeepNestingDetector deepNestingDetector;

    @Autowired
    private UnusedImportDetector unusedImportDetector;

    @Autowired
    private MagicNumberDetector magicNumberDetector;

    @Autowired
    private GodClassDetector godClassDetector;

    @Autowired
    private StringConcatenationInLoopDetector stringConcatenationInLoopDetector;

    @Autowired
    private EmptyCatchBlockDetector emptyCatchBlockDetector;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private DiffService diffService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    public String hello() {
        return "RefactorAI is running! 🚀";
    }

    @PostMapping("/analyze")
    public List<CodeSmell> analyze(@RequestBody String javaCode) {
        Optional<CompilationUnit> cuOpt = parserService.parseCode(javaCode);

        if (cuOpt.isEmpty()) {
            return List.of(new CodeSmell(
                    "Parsing Error",
                    "N/A",
                    "Critical",
                    "Failed to parse Java code. Please ensure the code is valid."
            ));
        }

        CompilationUnit cu = cuOpt.get();
        List<MethodDeclaration> methods = parserService.extractMethods(cu);

        List<CodeSmell> allSmells = new ArrayList<>();

        allSmells.addAll(longMethodDetector.detect(methods));
        allSmells.addAll(deepNestingDetector.detect(methods));
        allSmells.addAll(unusedImportDetector.detect(cu));
        allSmells.addAll(magicNumberDetector.detect(methods));
        allSmells.addAll(godClassDetector.detect(cu));
        allSmells.addAll(stringConcatenationInLoopDetector.detect(methods));
        allSmells.addAll(emptyCatchBlockDetector.detect(methods));

        if (allSmells.isEmpty()) {
            return List.of(new CodeSmell(
                    "No Issues Found",
                    "N/A",
                    "Info",
                    "Great! No code smells detected."
            ));
        }

        return allSmells;
    }

    @PostMapping("/refactor")
    public Map<String, Object> refactor(
            @RequestBody String javaCode,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Map<String, Object> response = new HashMap<>();

        // First analyze the code
        Optional<CompilationUnit> cuOpt = parserService.parseCode(javaCode);

        if (cuOpt.isEmpty()) {
            response.put("error", "Failed to parse Java code");
            return response;
        }

        CompilationUnit cu = cuOpt.get();
        List<MethodDeclaration> methods = parserService.extractMethods(cu);

        List<CodeSmell> allSmells = new ArrayList<>();
        allSmells.addAll(longMethodDetector.detect(methods));
        allSmells.addAll(deepNestingDetector.detect(methods));
        allSmells.addAll(unusedImportDetector.detect(cu));
        allSmells.addAll(magicNumberDetector.detect(methods));
        allSmells.addAll(godClassDetector.detect(cu));
        allSmells.addAll(stringConcatenationInLoopDetector.detect(methods));
        allSmells.addAll(emptyCatchBlockDetector.detect(methods));

        if (allSmells.isEmpty()) {
            response.put("message", "No code smells detected! Code looks good.");
            response.put("originalCode", javaCode);
            return response;
        }

        // Get AI refactoring for first smell
        CodeSmell firstSmell = allSmells.get(0);
        String aiResponse = openAIService.getRefactoringSuggestion(
                javaCode,
                firstSmell.getType(),
                firstSmell.getDescription()
        );

        // Extract refactored code from AI response
        String refactoredCode = diffService.extractJavaCode(aiResponse);

        // Generate diff
        String diff = diffService.generateDiff(javaCode, refactoredCode);

        // Build response
        response.put("originalCode", javaCode);
        response.put("refactoredCode", refactoredCode);
        response.put("diff", diff);
        response.put("detectedSmells", allSmells);
        response.put("explanation", "AI refactored the code to fix: " + firstSmell.getType());

        // Save to database if user is authenticated
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                Optional<User> userOpt = userRepository.findByUsername(username);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // Create smell types string
                    String smellTypes = allSmells.stream()
                            .map(CodeSmell::getType)
                            .distinct()
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("None");

                    // Save analysis
                    AnalysisHistory history = new AnalysisHistory(
                            user,
                            javaCode,
                            refactoredCode,
                            diff,
                            allSmells.size(),
                            smellTypes
                    );
                    analysisHistoryRepository.save(history);

                    response.put("saved", true);
                }
            } catch (Exception e) {
                // Silently fail - analysis works even if save fails
                System.out.println("Failed to save analysis: " + e.getMessage());
                e.printStackTrace();
                response.put("saved", false);
                response.put("saveError", e.getMessage());
            }
        }

        return response;
    }

    @GetMapping("/test-auth")
    public Map<String, Object> testAuth(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null) {
            response.put("error", "No Authorization header");
            return response;
        }

        if (!authHeader.startsWith("Bearer ")) {
            response.put("error", "Authorization header doesn't start with Bearer");
            return response;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isEmpty()) {
                response.put("error", "User not found: " + username);
                return response;
            }

            response.put("success", true);
            response.put("username", username);
            response.put("userId", userOpt.get().getId());

        } catch (Exception e) {
            response.put("error", "Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}