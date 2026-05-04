package org.zeith.hammeranims.molang.parser;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.parser.tokenizer.Token;

public interface PrefixParselet {

    Expression parse(MoLangParser parser, Token token);
}
