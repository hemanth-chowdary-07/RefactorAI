package com.refactorai.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.refactorai.model.CodeSmell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UnusedImportDetector {

    public List<CodeSmell> detect(CompilationUnit cu) {
        List<CodeSmell> smells = new ArrayList<>();
        List<ImportDeclaration> imports = cu.getImports();
        String sourceCode = cu.toString();

        for (ImportDeclaration importDecl : imports) {
            String importName = importDecl.getNameAsString();

            // Get the simple class name (last part after the last dot)
            String simpleClassName = getSimpleClassName(importName);

            // Check if this class name is used anywhere in the code
            // (excluding the import statement itself)
            String codeWithoutImports = removeImports(sourceCode);

            if (!isClassUsedInCode(simpleClassName, codeWithoutImports)) {
                int line = importDecl.getBegin().get().line;

                CodeSmell smell = new CodeSmell(
                        "Unused Import",
                        "Line " + line,
                        "Low",
                        "Import '" + importName + "' is declared but never used. " +
                                "Unused imports clutter code and should be removed."
                );

                smells.add(smell);
            }
        }

        return smells;
    }

    private String getSimpleClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        if (lastDot > 0) {
            return fullClassName.substring(lastDot + 1);
        }
        return fullClassName;
    }

    private String removeImports(String sourceCode) {
        // Remove all import statements to avoid false positives
        String[] lines = sourceCode.split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (!line.trim().startsWith("import ")) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

    private boolean isClassUsedInCode(String className, String code) {
        // Check if the class name appears as a standalone word in the code
        // Use word boundaries to avoid partial matches
        String regex = "\\b" + className + "\\b";
        return code.matches("(?s).*" + regex + ".*");
    }
}