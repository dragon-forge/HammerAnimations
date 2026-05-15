package dev.zeith.lzvm.molang.parcelets;

import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.Token;

public interface IPrefixParselet
{
	MLExpression parse(MoParser parser, Token token);
}
