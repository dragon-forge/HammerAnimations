package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.InfixParselet;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.Precedence;
import org.zeith.hammeranims.molang.ast.AssignExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

public class AssignParselet implements InfixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token, Expression leftExpr) {
        return new AssignExpression(leftExpr, parser.parseExpression(getPrecedence()));
    }

    @Override
    public Precedence getPrecedence() {
        return Precedence.ASSIGNMENT;
    }
}
