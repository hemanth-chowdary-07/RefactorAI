package com.refactorai.controller;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.refactorai.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private ParserService parserService;

    @GetMapping("/hello")
    public String hello() {
        return "RefactorAI is running! 🚀";
    }

    @GetMapping("/status")
    public String status() {
        return "RefactorAI backend is operational!";
    }

    @PostMapping("/test-parser")
    public String testParser(@RequestBody String javaCode) {
        // Parse the code
        Optional<CompilationUnit> cuOpt = parserService.parseCode(javaCode);

        if (cuOpt.isEmpty()) {
            return "❌ Parsing failed! Invalid Java code.";
        }

        CompilationUnit cu = cuOpt.get();

        // Extract methods
        List<MethodDeclaration> methods = parserService.extractMethods(cu);

        // Build response
        StringBuilder response = new StringBuilder();
        response.append("✅ Parsing successful!\n\n");
        response.append("Found ").append(methods.size()).append(" method(s):\n\n");

        for (MethodDeclaration method : methods) {
            String methodName = method.getNameAsString();
            int lineCount = parserService.getMethodLineCount(method);

            response.append("- Method: ").append(methodName)
                    .append(" (").append(lineCount).append(" lines)\n");
        }

        return response.toString();
    }
}