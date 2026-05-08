package dev.zeith.lzvm.molang.parcelets.prefix;

import dev.zeith.lzvm.exception.LzVMException;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parcelets.IPrefixParselet;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.Token;
import dev.zeith.lzvm.op.LzBinaryOp;

import java.util.ArrayList;

public class LoopParselet
		implements IPrefixParselet
{
	@Override
	public MLExpression parse(MoParser parser, Token token)
	{
		ArrayList<MLExpression> args = parser.parseArgs();
		if(args.size() != 2) throw new LzVMException("loop() expects 2 arguments (loop_count, {code}), " + args.size() + " aguments provided.");
		
		MLExpression loopCount = args.get(0);
		
		// Place safe-ish limits
		if(parser.loopCountLimiter > 0)
			loopCount = new BinaryExpression(
					LzBinaryOp.MIN,
					loopCount,
					new NumberExpression(parser.loopCountLimiter)
			);
		
		return new LoopExpression(loopCount, args.get(1));
	}
}