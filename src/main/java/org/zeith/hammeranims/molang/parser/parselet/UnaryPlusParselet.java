package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.Precedence;
import org.zeith.hammeranims.molang.parser.PrefixParselet;
import org.zeith.hammeranims.molang.ast.UnaryPlusExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

public class UnaryPlusParselet implements PrefixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token) {
        return new UnaryPlusExpression(parser.parseExpression(Precedence.PREFIX));
    }
}
