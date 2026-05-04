package org.zeith.hammeranims.molang.parser;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

public interface InfixParselet {

    Expression parse(MoLangParser parser, Token token, Expression leftExpr);

    default Precedence getPrecedence() {
        return Precedence.ANYTHING;
    }
}
