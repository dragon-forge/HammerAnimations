package dev.zeith.lzvm.molang.parcelets.infix;

import dev.zeith.lzvm.exception.LzVMException;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IInfixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.Token;
import dev.zeith.lzvm.op.LzBinaryOp;

public class CompoundAssignParselet
		implements IInfixParselet
{
	public static final CompoundAssignParselet INSTANCE = new CompoundAssignParselet();
	
	@Override
	public MLExpression parse(MoParser parser, Token token, MLExpression leftExpr)
	{
		if(!(leftExpr instanceof IVarAccessExpression))
			throw new LzVMException("Compound assignment can only happen to a IVarAccessExpression, but tried with " + leftExpr.getClass().getSimpleName());
		
		LzBinaryOp op;
		switch(token.getType())
		{
			case COMP_PLUS:
				op = LzBinaryOp.ADD;
				break;
			case COMP_MINUS:
				op = LzBinaryOp.SUB;
				break;
			case COMP_ASTERISK:
				op = LzBinaryOp.MUL;
				break;
			case COMP_SLASH:
				op = LzBinaryOp.DIV;
				break;
			case COMP_PERCENT:
				op = LzBinaryOp.MOD;
				break;
			default:
				throw new LzVMException("Unrecognized token type: " + token.getType());
		}
		
		return new AssignExpression(
				(IVarAccessExpression) leftExpr,
				new BinaryExpression(
						op,
						leftExpr,
						parser.parseExpression()
				)
		);
	}
	
	@Override
	public EPrecedence getPrecedence()
	{
		return EPrecedence.ASSIGNMENT;
	}
}