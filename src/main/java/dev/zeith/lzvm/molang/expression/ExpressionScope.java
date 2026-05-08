package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.program.LzProgramBuilder;
import lombok.*;

import java.util.function.Consumer;

@Value
@With
public class ExpressionScope
{
	public static final ExpressionScope EMPTY = new ExpressionScope(null, null);
	
	Consumer<LzProgramBuilder> jumpToLoopExit;
	Consumer<LzProgramBuilder> jumpToLoopStart;
}