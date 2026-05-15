package org.zeith.hammeranims.core.molang;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.molang.compiler.jclass.*;
import dev.zeith.lzvm.molang.compiler.libs.*;
import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.program.*;
import dev.zeith.lzvm.vm.*;
import dev.zeith.lzvm.vm.jvm.opt.MathJClass;

import java.util.*;
import java.util.function.Function;

public class MolangExpressionParser
{
	static final Map<String, JClass> JVM_CLASSES = new HashMap<>();
	static final MoLangCompiler MOLANG_COMPILER = new MoLangCompiler();
	static final LzVM VM = new LzVM(JVM_CLASSES::get);
	
	static
	{
		registerClass(Math.class.getName(), MathJClass::new);
		registerClass(MoMathLibrary.class.getName(), MoMathJClass::new);
		registerClass(MoLangEasing.class.getName(), MoEasingJClass::new);
	}
	
	public static void disableOptimizations()
	{
		MOLANG_COMPILER.optimize = false;
	}
	
	public static void registerClass(String name, Function<String, JClass> factory)
	{
		JVM_CLASSES.put(name, factory.apply(name));
	}
	
	public static LzFactory parse(String expression)
	{
		// Constant expressions don't need caching since they don't involve any class generation.
		ArrayList<MLExpression> parsed = MOLANG_COMPILER.parse(expression);
		if(parsed.size() == 1)
		{
			OptionalDouble exp = parsed.get(0).asOptimizedDouble();
			if(exp.isPresent()) return new ConstantExpression(exp.getAsDouble());
		}
		
		LzProgramBody program = MOLANG_COMPILER.compile(parsed);
		LzProgramStack stack = program.computeInfo().mallocStack(0);
		
		LzFactory[] fac = new LzFactory[1];
		fac[0] = vars ->
				new LzExpression()
				{
					@Override
					public double get(double... args)
					{
						return VM.interpret(vars, program, stack);
					}
					
					@Override
					public LzExpression instantiate(LzVariableStore store)
					{
						return fac[0].instantiate(store);
					}
				};
		
		return fac[0];
	}
}