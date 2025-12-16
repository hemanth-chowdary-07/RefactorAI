package com.refactorai.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.refactorai.model.CodeSmell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GodClassDetector {

    private static final int MAX_LINES = 500;
    private static final int MAX_METHODS = 10;

    public List<CodeSmell> detect(CompilationUnit cu) {
        List<CodeSmell> smells = new ArrayList<>();

        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classDecl : classes) {
            String className = classDecl.getNameAsString();
            int lineCount = getClassLineCount(classDecl);
            int methodCount = classDecl.getMethods().size();

            List<String> violations = new ArrayList<>();

            if (lineCount > MAX_LINES) {
                violations.add(lineCount + " lines (exceeds " + MAX_LINES + ")");
            }

            if (methodCount > MAX_METHODS) {
                violations.add(methodCount + " methods (exceeds " + MAX_METHODS + ")");
            }

            if (!violations.isEmpty()) {
                int startLine = classDecl.getBegin().get().line;

                CodeSmell smell = new CodeSmell(
                        "God Class",
                        "Line " + startLine + " (Class: " + className + ")",
                        "High",
                        "Class '" + className + "' is too large: " + String.join(", ", violations) + ". " +
                                "God classes violate the Single Responsibility Principle and are hard to maintain. " +
                                "Consider splitting into smaller, focused classes."
                );

                smells.add(smell);
            }
        }

        return smells;
    }

    private int getClassLineCount(ClassOrInterfaceDeclaration classDecl) {
        if (classDecl.getBegin().isPresent() && classDecl.getEnd().isPresent()) {
            int startLine = classDecl.getBegin().get().line;
            int endLine = classDecl.getEnd().get().line;
            return endLine - startLine + 1;
        }
        return 0;
    }
}