package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.Token;

public class UnaryPlusParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		// pass-through, value does not change.
		return parser.parseExpression(EPrecedence.PREFIX);
	}
}