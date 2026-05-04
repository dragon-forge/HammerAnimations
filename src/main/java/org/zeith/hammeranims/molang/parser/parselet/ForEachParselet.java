package org.zeith.hammeranims.molang.parser.parselet;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.PrefixParselet;
import org.zeith.hammeranims.molang.ast.ForEachExpression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

import java.util.List;

public class ForEachParselet implements PrefixParselet {

    @Override
    public Expression parse(MoLangParser parser, Token token) {
        List<Expression> args = parser.parseArgs();

        if (args.size() != 3) {
            throw new RuntimeException("ForEach: Expected 3 argument, " + args.size() + " argument given");
        } else {
            return new ForEachExpression(args.get(0), args.get(1), args.get(2));
        }
    }
}
