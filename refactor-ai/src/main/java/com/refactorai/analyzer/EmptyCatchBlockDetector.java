package com.refactorai.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.refactorai.model.CodeSmell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmptyCatchBlockDetector {

    public List<CodeSmell> detect(List<MethodDeclaration> methods) {
        List<CodeSmell> smells = new ArrayList<>();

        for (MethodDeclaration method : methods) {
            List<TryStmt> tryStatements = method.findAll(TryStmt.class);

            for (TryStmt tryStmt : tryStatements) {
                for (CatchClause catchClause : tryStmt.getCatchClauses()) {
                    BlockStmt catchBlock = catchClause.getBody();

                    // Check if catch block is empty or only has comments
                    if (catchBlock.getStatements().isEmpty()) {
                        int line = catchClause.getBegin().get().line;
                        String exceptionType = catchClause.getParameter().getType().asString();

                        CodeSmell smell = new CodeSmell(
                                "Empty Catch Block",
                                "Line " + line,
                                "High",
                                "Empty catch block for '" + exceptionType + "'. " +
                                        "Silently swallowing exceptions makes debugging difficult and hides bugs. " +
                                        "At minimum, log the exception. Consider: throw new RuntimeException(e), logger.error(), or handle the exception appropriately."
                        );

                        smells.add(smell);
                    }
                }
            }
        }

        return smells;
    }
}