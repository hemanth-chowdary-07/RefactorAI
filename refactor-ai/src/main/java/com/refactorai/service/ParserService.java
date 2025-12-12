package com.refactorai.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParserService {

    private final JavaParser javaParser;

    public ParserService() {
        this.javaParser = new JavaParser();
    }

    /**
     * Parse Java code string into AST (Abstract Syntax Tree)
     */
    public Optional<CompilationUnit> parseCode(String javaCode) {
        try {
            ParseResult<CompilationUnit> result = javaParser.parse(javaCode);

            if (result.isSuccessful() && result.getResult().isPresent()) {
                return result.getResult();
            } else {
                System.err.println("Parsing failed: " + result.getProblems());
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("Error parsing code: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Extract all methods from parsed code
     */
    public List<MethodDeclaration> extractMethods(CompilationUnit cu) {
        return cu.findAll(MethodDeclaration.class);
    }

    /**
     * Count lines in a method
     */
    public int getMethodLineCount(MethodDeclaration method) {
        if (method.getBegin().isPresent() && method.getEnd().isPresent()) {
            int startLine = method.getBegin().get().line;
            int endLine = method.getEnd().get().line;
            return endLine - startLine + 1;
        }
        return 0;
    }
}