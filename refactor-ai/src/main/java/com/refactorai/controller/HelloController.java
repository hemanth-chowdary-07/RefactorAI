package com.refactorai.controller;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.refactorai.analyzer.*;
import com.refactorai.model.CodeSmell;
import com.refactorai.service.DiffService;
import com.refactorai.service.OpenAIService;
import com.refactorai.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
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
    public Map<String, Object> refactor(@RequestBody String javaCode) {
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

        return response;
    }
}