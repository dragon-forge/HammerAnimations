package org.zeith.hammeranims.molang.parser.parselet;

import lombok.Value;
import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.ast.binaryop.*;
import org.zeith.hammeranims.molang.parser.*;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

@Value
public class GenericBinaryOpParselet implements InfixParselet {
    Precedence precedence;

    @Override
    public Expression parse(MoLangParser parser, Token token, Expression leftExpr) {
        Expression rightExpr = parser.parseExpression(getPrecedence());

        switch (token.getType()) {
            case ARROW:
                return new ArrowExpression(leftExpr, rightExpr);
            case AND:
                return new BooleanAndExpression(leftExpr, rightExpr);
            case OR:
                return new BooleanOrExpression(leftExpr, rightExpr);
            case COALESCE:
                return new CoalesceExpression(leftExpr, rightExpr);
            case SLASH:
                return new DivideExpression(leftExpr, rightExpr);
            case EQUALS:
                return new EqualExpression(leftExpr, rightExpr);
            case GREATER:
                return new GreaterExpression(leftExpr, rightExpr);
            case GREATER_OR_EQUALS:
                return new GreaterOrEqualExpression(leftExpr, rightExpr);
            case MINUS:
                return new MinusExpression(leftExpr, rightExpr);
            case NOT_EQUALS:
                return new NotEqualExpression(leftExpr, rightExpr);
            case PLUS:
                return new PlusExpression(leftExpr, rightExpr);
            case ASTERISK:
                return new PowExpression(leftExpr, rightExpr);
            case SMALLER:
                return new SmallerExpression(leftExpr, rightExpr);
            case SMALLER_OR_EQUALS:
                return new SmallerOrEqualExpression(leftExpr, rightExpr);
        }

        return null;
    }

    @Override
    public Precedence getPrecedence() {
        return precedence;
    }
}
