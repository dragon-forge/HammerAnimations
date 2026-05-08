package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.Token;

public class StringParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		return new StringExpression(token.getText());
	}
}