package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.PrefixParselet;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;
import org.zeith.hammeranims.molang.parser.tokenizer.TokenType;

public class GroupParselet implements PrefixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token) {
        // this only for conditions
        Expression expr = parser.parseExpression();
        parser.consumeToken(TokenType.BRACKET_RIGHT);

        return expr;
    }
}
