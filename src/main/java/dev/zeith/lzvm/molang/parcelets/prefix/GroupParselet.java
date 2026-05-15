package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.*;

public class GroupParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		MLExpression expr = parser.parseExpression();
		parser.consumeToken(ETokenType.BRACKET_RIGHT);
		return expr;
	}
}