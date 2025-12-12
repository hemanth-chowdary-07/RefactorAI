package com.refactorai.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.refactorai.model.CodeSmell;
import com.refactorai.service.ParserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LongMethodDetector {

    private static final int MAX_LINES = 30;
    private final ParserService parserService;

    public LongMethodDetector(ParserService parserService) {
        this.parserService = parserService;
    }

    public List<CodeSmell> detect(List<MethodDeclaration> methods) {
        List<CodeSmell> smells = new ArrayList<>();

        for (MethodDeclaration method : methods) {
            int lineCount = parserService.getMethodLineCount(method);

            if (lineCount > MAX_LINES) {
                String methodName = method.getNameAsString();
                int startLine = method.getBegin().get().line;

                CodeSmell smell = new CodeSmell(
                        "Long Method",
                        "Line " + startLine + " (Method: " + methodName + ")",
                        "Medium",
                        "Method has " + lineCount + " lines, exceeds maximum of " + MAX_LINES + " lines. " +
                                "Long methods are harder to understand and maintain. Consider extracting smaller methods."
                );

                smells.add(smell);
            }
        }

        return smells;
    }
}