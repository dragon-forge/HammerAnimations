package dev.zeith.lzvm.molang.parcelets.infix;

import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IInfixParselet;
import dev.zeith.lzvm.molang.parser.*;
import dev.zeith.lzvm.molang.tokenizer.Token;
import dev.zeith.lzvm.op.LzBinaryOp;

public class GenericBinaryOpParselet
		implements IInfixParselet
{
	private final EPrecedence precedence;
	
	public GenericBinaryOpParselet(EPrecedence precedence)
	{
		this.precedence = precedence;
	}
	
	@Override
	public MLExpression parse(MoParser parser, Token token, MLExpression leftExpr)
	{
		MLExpression rightExpr = parser.parseExpression(getPrecedence());
		LzBinaryOp binaryOp = null;
		
		switch(token.getType())
		{
			case EQUALS:
				binaryOp = LzBinaryOp.EQUALS; break;
			case NOT_EQUALS:
				binaryOp = LzBinaryOp.NOT_EQUALS; break;
			case COALESCE:
				binaryOp = LzBinaryOp.COALESCE; break;
			case AND:
				binaryOp = LzBinaryOp.AND; break;
			case OR:
				binaryOp = LzBinaryOp.OR; break;
			case GREATER_EQ:
				binaryOp = LzBinaryOp.GREATER_EQ_THAN; break;
			case LESS_EQ:
				binaryOp = LzBinaryOp.LESS_EQ_THAN; break;
			case GREATER:
				binaryOp = LzBinaryOp.GREATER_THAN; break;
			case LESS:
				binaryOp = LzBinaryOp.LESS_THAN; break;
			case PLUS:
				binaryOp = LzBinaryOp.ADD; break;
			case MINUS:
				binaryOp = LzBinaryOp.SUB; break;
			case ASTERISK:
				binaryOp = LzBinaryOp.MUL; break;
			case SLASH:
				binaryOp = LzBinaryOp.DIV; break;
			case PERCENT:
				binaryOp = LzBinaryOp.MOD; break;
		}
		
		if(binaryOp != null)
			return new BinaryExpression(binaryOp, leftExpr, rightExpr);
		return null;
	}
	
	@Override
	public EPrecedence getPrecedence()
	{
		return precedence;
	}
}
