package com.refactorai.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.refactorai.model.CodeSmell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MagicNumberDetector {

    // Numbers that are typically acceptable without being constants
    private static final Set<String> ACCEPTABLE_NUMBERS = Set.of("0", "1", "-1", "2");

    public List<CodeSmell> detect(List<MethodDeclaration> methods) {
        List<CodeSmell> smells = new ArrayList<>();

        for (MethodDeclaration method : methods) {
            detectMagicNumbers(method, smells);
        }

        return smells;
    }

    private void detectMagicNumbers(Node node, List<CodeSmell> smells) {
        // Check for integer literals
        if (node instanceof IntegerLiteralExpr) {
            IntegerLiteralExpr literal = (IntegerLiteralExpr) node;
            checkNumber(literal.getValue(), literal, smells);
        }

        // Check for long literals
        if (node instanceof LongLiteralExpr) {
            LongLiteralExpr literal = (LongLiteralExpr) node;
            checkNumber(literal.getValue().replace("L", "").replace("l", ""), literal, smells);
        }

        // Check for double literals
        if (node instanceof DoubleLiteralExpr) {
            DoubleLiteralExpr literal = (DoubleLiteralExpr) node;
            checkNumber(literal.getValue(), literal, smells);
        }

        // Recursively check child nodes
        for (Node child : node.getChildNodes()) {
            detectMagicNumbers(child, smells);
        }
    }

    private void checkNumber(String value, Node node, List<CodeSmell> smells) {
        // Skip acceptable numbers (0, 1, -1, 2)
        if (ACCEPTABLE_NUMBERS.contains(value)) {
            return;
        }

        // Skip decimal points for double checking
        String cleanValue = value.replace(".", "");

        if (!ACCEPTABLE_NUMBERS.contains(cleanValue)) {
            int line = node.getBegin().get().line;

            CodeSmell smell = new CodeSmell(
                    "Magic Number",
                    "Line " + line,
                    "Low",
                    "Magic number '" + value + "' found. Consider extracting to a named constant. " +
                            "Magic numbers make code harder to understand and maintain. Use descriptive constant names like 'MAX_RETRIES' or 'TAX_RATE'."
            );

            smells.add(smell);
        }
    }
}