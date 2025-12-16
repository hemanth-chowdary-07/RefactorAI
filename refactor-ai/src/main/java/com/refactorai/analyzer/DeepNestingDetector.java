package com.refactorai.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.refactorai.model.CodeSmell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeepNestingDetector {

    private static final int MAX_NESTING_DEPTH = 3;

    public List<CodeSmell> detect(List<MethodDeclaration> methods) {
        List<CodeSmell> smells = new ArrayList<>();

        for (MethodDeclaration method : methods) {
            int maxDepth = calculateMaxNestingDepth(method);

            if (maxDepth > MAX_NESTING_DEPTH) {
                String methodName = method.getNameAsString();
                int startLine = method.getBegin().get().line;

                CodeSmell smell = new CodeSmell(
                        "Deep Nesting",
                        "Line " + startLine + " (Method: " + methodName + ")",
                        "Medium",
                        "Method has nesting depth of " + maxDepth + ", exceeds maximum of " + MAX_NESTING_DEPTH + ". " +
                                "Deep nesting makes code harder to read and maintain. Consider extracting nested logic into separate methods."
                );

                smells.add(smell);
            }
        }

        return smells;
    }

    private int calculateMaxNestingDepth(MethodDeclaration method) {
        return calculateDepth(method, 0);
    }

    private int calculateDepth(Node node, int currentDepth) {
        int maxDepth = currentDepth;

        // Check if this node increases nesting depth
        if (node instanceof IfStmt ||
                node instanceof ForStmt ||
                node instanceof WhileStmt ||
                node instanceof DoStmt ||
                node instanceof SwitchStmt) {
            currentDepth++;
            maxDepth = currentDepth;
        }

        // Recursively check all child nodes
        for (Node child : node.getChildNodes()) {
            int childDepth = calculateDepth(child, currentDepth);
            maxDepth = Math.max(maxDepth, childDepth);
        }

        return maxDepth;
    }
}