package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.Precedence;
import org.zeith.hammeranims.molang.parser.PrefixParselet;
import org.zeith.hammeranims.molang.ast.StatementExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;
import org.zeith.hammeranims.molang.parser.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class BracketScopeParselet implements PrefixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token) {
        List<Expression> exprs = new ArrayList<>();

        if (!parser.matchToken(TokenType.CURLY_BRACKET_RIGHT)) {
            do {
                if (parser.matchToken(TokenType.CURLY_BRACKET_RIGHT, false)) {
                    break;
                }

                exprs.add(parser.parseExpression(Precedence.SCOPE));
            } while (parser.matchToken(TokenType.SEMICOLON));

            parser.consumeToken(TokenType.CURLY_BRACKET_RIGHT);
        }

        return new StatementExpression(exprs.toArray(new Expression[0]));
    }
}
