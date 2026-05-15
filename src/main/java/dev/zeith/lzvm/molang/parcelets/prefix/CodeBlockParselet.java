package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.*;

import java.util.*;

public class CodeBlockParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		List<MLExpression> exprs = new ArrayList<>();
		
		if(!parser.matchToken(ETokenType.CURLY_BRACKET_RIGHT))
		{
			do
			{
				if(parser.matchToken(ETokenType.CURLY_BRACKET_RIGHT, false))
					break;
				exprs.add(parser.parseExpression(EPrecedence.SCOPE));
			} while(parser.matchToken(ETokenType.SEMICOLON));
			
			parser.consumeToken(ETokenType.CURLY_BRACKET_RIGHT);
		}
		
		return new CodeBlockExpression(exprs.toArray(MLExpression.EMPTY_ARRAY));
	}
}