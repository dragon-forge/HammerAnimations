package dev.zeith.lzvm.molang.compiler.libs;

import dev.zeith.lzvm.molang.compiler.*;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.program.*;

import java.util.function.*;

public interface ICompilerLibrary
{
	void register(MoLangCompiler compiler);
	
	static IMoFunctionCallTransformer argsAndExtraPure(Function<FuncCallExpression, MLExpression> optimizer, Consumer<LzProgramBuilder> extra)
	{
		return argsAndExtra(true, optimizer, extra);
	}
	
	static IMoFunctionCallTransformer argsAndExtra(boolean pure, Function<FuncCallExpression, MLExpression> optimizer, Consumer<LzProgramBuilder> extra)
	{
		return new IMoFunctionCallTransformer()
		{
			@Override
			public boolean appendCall(MoLangCompiler compiler, LzProgramBuilder builder, FuncCallExpression expression, LzCallInsn call, ExpressionScope scope)
			{
				for(MLExpression child : expression.getChildren()) child.toLz(compiler, builder, scope);
				extra.accept(builder);
				return true;
			}
			
			@Override
			public MLExpression optimizeCall(MoLangCompiler compiler, FuncCallExpression expression, LzCallInsn call)
			{
				if(optimizer != null)
				{
					MLExpression apply = optimizer.apply(expression);
					if(apply != null) return apply;
				}
				return expression;
			}
			
			@Override
			public boolean isPure()
			{
				return pure;
			}
		};
	}
}