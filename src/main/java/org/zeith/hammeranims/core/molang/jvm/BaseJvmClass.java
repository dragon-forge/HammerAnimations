package org.zeith.hammeranims.core.molang.jvm;

import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;
import dev.zeith.lzvm.program.*;
import org.zeith.hammeranims.standalone.jvm.*;

import java.util.*;
import java.util.function.*;

public abstract class BaseJvmClass
		implements JvmClass
{
	protected final Map<LzCallInsn, JvmMethod> methods = new HashMap<>();
	
	protected final String name;
	
	public BaseJvmClass(String name)
	{
		this.name = name;
		registerMethods();
	}
	
	protected abstract void registerMethods();
	
	protected void registerMethod(LzCallInsn call, JvmMethod method)
	{
		methods.put(call, method);
	}
	
	protected void registerDuOperator(String name, DoubleUnaryOperator op)
	{
		registerMethod(LzCallInsn.ofDbl(name, ArgType.DOUBLE), args -> op.applyAsDouble((double) args[0]));
	}
	
	protected void registerDbOperator(String name, DoubleBinaryOperator op)
	{
		registerMethod(LzCallInsn.ofDbl(name, ArgType.DOUBLE, ArgType.DOUBLE), args -> op.applyAsDouble((double) args[0], (double) args[1]));
	}
	
	protected void registerDtOperator(String name, MoMathLibrary.DoubleTernaryOperator op)
	{
		registerMethod(LzCallInsn.ofDbl(name, ArgType.DOUBLE, ArgType.DOUBLE, ArgType.DOUBLE), args -> op.applyAsDouble((double) args[0], (double) args[1], (double) args[2]));
	}
	
	@Override
	public JvmMethod getDeclaredMethod(LzCallInsn call)
	{
		var method = methods.get(call);
		if(method == null) throw new RuntimeException("Method " + call.name + call.getDescriptor() + " not found in class " + name);
		return method;
	}
}