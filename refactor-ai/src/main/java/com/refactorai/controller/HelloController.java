package com.refactorai.controller;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.refactorai.analyzer.DeepNestingDetector;
import com.refactorai.analyzer.GodClassDetector;
import com.refactorai.analyzer.LongMethodDetector;
import com.refactorai.analyzer.MagicNumberDetector;
import com.refactorai.analyzer.UnusedImportDetector;
import com.refactorai.model.CodeSmell;
import com.refactorai.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}