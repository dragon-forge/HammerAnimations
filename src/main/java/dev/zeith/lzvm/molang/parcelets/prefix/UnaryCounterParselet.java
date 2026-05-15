package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.exception.LzVMException;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.Token;
import dev.zeith.lzvm.op.LzBinaryOp;

public class UnaryCounterParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		MLExpression expr = parser.parseExpression();
		if(!(expr instanceof IVarAccessExpression))
			throw new LzVMException("Unary counter "+token.getText()+" expects a variable name");
		
		LzBinaryOp op;
		switch(token.getType())
		{
			case PLUS_DOUBLE:
			{
				op = LzBinaryOp.ADD;
				break;
			}
			case MINUS_DOUBLE:
			{
				op = LzBinaryOp.SUB;
				break;
			}
			default:
				throw new LzVMException("Unrecognized token type: " + token.getType());
		}
		
		return new AssignExpression(
				(IVarAccessExpression) expr,
				new BinaryExpression(op, expr, new NumberExpression(1))
		);
	}
}