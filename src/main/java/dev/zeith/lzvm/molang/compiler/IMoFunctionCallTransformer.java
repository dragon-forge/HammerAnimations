package dev.zeith.lzvm.molang.compiler;

import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.program.*;

public interface IMoFunctionCallTransformer
{
	boolean appendCall(MoLangCompiler compiler, LzProgramBuilder builder, FuncCallExpression expression, LzCallInsn call, ExpressionScope scope);
	
	default MLExpression optimizeCall(MoLangCompiler compiler, FuncCallExpression expression, LzCallInsn call)
	{
		return expression;
	}
	
	boolean isPure();
}