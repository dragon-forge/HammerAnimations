package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.PrefixParselet;
import org.zeith.hammeranims.molang.ast.LoopExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

import java.util.List;

public class LoopParselet implements PrefixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token) {
        List<Expression> args = parser.parseArgs();

        if (args.size() != 2) {
            throw new RuntimeException("Loop: Expected 2 argument, " + args.size() + " argument given");
        } else {
            return new LoopExpression(args.get(0), args.get(1));
        }
    }
}
