package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

public final class TernaryExpression
		extends MLExpression
{
	private final MLExpression condition;
	private final MLExpression onTrue;
	private final MLExpression onElse;
	
	public TernaryExpression(MLExpression condition, MLExpression onTrue, MLExpression onElse)
	{
		super(3);
		this.children[0] = this.condition = condition;
		this.children[1] = this.onTrue = onTrue;
		this.children[2] = this.onElse = onElse;
	}
	
	@Override
	protected Object evalStatic()
	{
		return null;
	}
	
	@Override
	public void toLz(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope)
	{
		LzLabel toFalse = new LzLabel();
		LzLabel after = new LzLabel();
		
		int local = builder.allocLocal();
		
		// First, push condition onto the stack
		condition.toLz(compiler, builder, scope);
		builder.addStore(local);
		
		// Then perform jump if false
		builder.addLoad(local).addJumpIfFalse(toFalse);
		
		// The "Hackery™️" below is used to avoid emitting anything onto the stack if the inner blocks don't emit anything.
		// This is super useful for breaks in loops, allowing for something like this
		// 									(v.test <= 5 ?: break)		(v.test > 5 ? break)
		// to generate cleaner bytecode without break turning into a zero variable that is never used.
		
		if(onTrue != null)
		{
			onTrue.toLz(compiler, builder, scope);
		} else
		{
			// Hackery 1
			if(onElse != null && onElse.getExpectedLzType() == null) ;
			else builder.addLoad(local);
		}
		builder.addJump(LzOpcodes.JUMP, after);
		
		builder.addLabel(toFalse);
		if(onElse != null)
		{
			onElse.toLz(compiler, builder, scope);
		} else
		{
			// Hackery 2
			if(onTrue != null && onTrue.getExpectedLzType() == null) ;
			else builder.addConstD(0);
		}
		
		builder.addLabel(after);
	}
	
	@Override
	public ArgType getExpectedLzType()
	{
		return null;
	}
}