package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

public class LoopExpression
		extends MLExpression
{
	public LoopExpression(MLExpression count, MLExpression body)
	{
		super(2);
		this.children[0] = count;
		this.children[1] = body;
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		final LzLabel loopStart = new LzLabel();
		final LzLabel afterLoop = new LzLabel();
		final LzLabel toLoopIncrement = new LzLabel();
		
		final int counter = builder.allocLocal();
		
		ExpressionScope subScope = scope
				.withJumpToLoopExit(b -> b.addJump(LzOpcodes.JUMP, afterLoop))
				.withJumpToLoopStart(b -> b.addJump(LzOpcodes.JUMP, toLoopIncrement));
		
		// store the loop count into a var
		Object loopCountO = this.children[0].evalStatic();
		
		int loopCount = -1;
		double loopCountD = 0;
		if(loopCountO instanceof Number)
		{
			loopCountD = ((Number) loopCountO).doubleValue();
		} else
		{
			this.children[0].toLz(compiler, builder, subScope);
			loopCount = builder.allocLocal();
			builder.addStore(loopCount);
		}
		
		// Initialize counter to zero
		builder.addConstD(0);
		builder.addStore(counter);
		
		builder.addLabel(loopStart);
		// Check if we should jump out of the loop
		// counter < loopCount -> when its false -> exit out of the loop
		builder.addLoad(counter);
		if(loopCount == -1) builder.addConstD(loopCountD);
		else builder.addLoad(loopCount);
		builder.addInsn(LzOpcodes.LESS_THAN);
		builder.addJumpIfFalse(afterLoop);
		
		// Loop code
		this.children[1].toLz(compiler, builder, subScope);
		
		builder.addLabel(toLoopIncrement);
		
		// Update counter after code executes
		builder.addLoad(counter).addConstD(1).addInsn(LzOpcodes.ADD).addStore(counter);
		
		// Jump to the start of the loop
		builder.addJump(LzOpcodes.JUMP, loopStart);
		
		builder.addLabel(afterLoop);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}