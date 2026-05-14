package dev.zeith.lzvm.molang.expression;

import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.program.LzProgramBuilder;

import java.util.function.Consumer;

public interface IVarAccessExpression
{
	void addWrite(MoLangCompiler compiler, LzProgramBuilder builder, ExpressionScope scope, Consumer<LzProgramBuilder> whatToWrite);
}