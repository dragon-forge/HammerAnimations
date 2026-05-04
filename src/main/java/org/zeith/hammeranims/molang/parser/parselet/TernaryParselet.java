package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.InfixParselet;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.Precedence;
import org.zeith.hammeranims.molang.ast.TernaryExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;
import org.zeith.hammeranims.molang.parser.tokenizer.TokenType;

public class TernaryParselet implements InfixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token, Expression leftExpr) {
        if (parser.matchToken(TokenType.COLON)) {
            return new TernaryExpression(leftExpr, null, parser.parseExpression(getPrecedence()));
        } else {
            Expression thenExpr = parser.parseExpression(getPrecedence());

            if (!parser.matchToken(TokenType.COLON)) {
                return new TernaryExpression(leftExpr, thenExpr, null);
            } else {
                return new TernaryExpression(leftExpr, thenExpr, parser.parseExpression(getPrecedence()));
            }
        }
    }

    @Override
    public Precedence getPrecedence() {
        return Precedence.CONDITIONAL;
    }
}
