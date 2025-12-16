package com.refactorai.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.refactorai.model.CodeSmell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StringConcatenationInLoopDetector {

    public List<CodeSmell> detect(List<MethodDeclaration> methods) {
        List<CodeSmell> smells = new ArrayList<>();

        for (MethodDeclaration method : methods) {
            detectInMethod(method, smells);
        }

        return smells;
    }

    private void detectInMethod(Node node, List<CodeSmell> smells) {
        // Check if we're inside a loop
        if (node instanceof ForStmt || node instanceof WhileStmt || node instanceof DoStmt) {
            // Now check for string concatenation inside this loop
            checkForStringConcatenation(node, smells);
        }

        // Recursively check child nodes
        for (Node child : node.getChildNodes()) {
            detectInMethod(child, smells);
        }
    }

    private void checkForStringConcatenation(Node loopNode, List<CodeSmell> smells) {
        // Look for += operations on strings
        List<AssignExpr> assignments = loopNode.findAll(AssignExpr.class);

        for (AssignExpr assign : assignments) {
            if (assign.getOperator() == AssignExpr.Operator.PLUS) {
                // Found += operator in loop
                int line = assign.getBegin().get().line;

                CodeSmell smell = new CodeSmell(
                        "String Concatenation in Loop",
                        "Line " + line,
                        "Medium",
                        "String concatenation using '+=' inside a loop is inefficient. " +
                                "Each concatenation creates a new String object, leading to O(n²) performance. " +
                                "Use StringBuilder for better performance: StringBuilder.append() is O(n)."
                );

                smells.add(smell);
            }
        }

        // Also check for regular + concatenation in loops
        List<BinaryExpr> binaryExprs = loopNode.findAll(BinaryExpr.class);

        for (BinaryExpr binary : binaryExprs) {
            if (binary.getOperator() == BinaryExpr.Operator.PLUS) {
                // Check if it involves string operations
                String exprStr = binary.toString();
                if (exprStr.contains("\"") || exprStr.toLowerCase().contains("string")) {
                    int line = binary.getBegin().get().line;

                    CodeSmell smell = new CodeSmell(
                            "String Concatenation in Loop",
                            "Line " + line,
                            "Medium",
                            "String concatenation using '+' inside a loop may be inefficient. " +
                                    "Consider using StringBuilder.append() for better performance."
                    );

                    smells.add(smell);
                }
            }
        }
    }
}