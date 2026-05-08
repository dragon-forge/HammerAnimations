package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.Token;

import java.util.*;

public class NameParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		String str = token.getText();
		
		int index = str.indexOf('.');
		if(index > 0)
		{
			String top = str.substring(0, index);
			String resolved = parser.resolveTopLevelAlias(top);
			if(!Objects.equals(resolved, top))
				str = resolved + '.' + str.substring(index + 1);
		}
		
		NameExpression name = new NameExpression(str);
		ArrayList<MLExpression> args = parser.parseArgs();
		return !args.isEmpty() ? new FuncCallExpression(name, args.toArray(MLExpression.EMPTY_ARRAY)) : name;
	}
}
