package dev.zeith.lzvm.molang.parcelets.infix;

import dev.zeith.lzvm.exception.LzVMException;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IInfixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.Token;

public class AssignParselet
		implements IInfixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token, MLExpression leftExpr)
	{
		if(!(leftExpr instanceof IVarAccessExpression))
			throw new LzVMException("Assignment can only happen to a IVarAccessExpression, but tried with " + leftExpr.getClass().getSimpleName());
		return new AssignExpression((IVarAccessExpression) leftExpr, parser.parseExpression(getPrecedence()));
	}
	
	@Override
	public EPrecedence getPrecedence()
	{
		return EPrecedence.ASSIGNMENT;
	}
}