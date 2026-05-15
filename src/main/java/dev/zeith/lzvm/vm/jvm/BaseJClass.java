package dev.zeith.lzvm.vm.jvm;

import dev.zeith.lzvm.program.*;
import dev.zeith.lzvm.util.DoubleTernaryOperator;
import dev.zeith.lzvm.vm.*;

import java.util.*;
import java.util.function.*;

public abstract class BaseJClass
		implements JClass
{
	protected final Map<LzCallInsn, JMethod> methods = new HashMap<>();
	
	protected final String name;
	
	public BaseJClass(String name)
	{
		this.name = name;
		registerMethods();
	}
	
	protected abstract void registerMethods();
	
	protected void registerMethod(LzCallInsn call, JMethod method)
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
	
	protected void registerDtOperator(String name, DoubleTernaryOperator op)
	{
		registerMethod(LzCallInsn.ofDbl(name, ArgType.DOUBLE, ArgType.DOUBLE, ArgType.DOUBLE), args -> op.applyAsDouble((double) args[0], (double) args[1], (double) args[2]));
	}
	
	@Override
	public JMethod getDeclaredMethod(LzCallInsn call)
	{
		JMethod method = methods.get(call);
		if(method == null) throw new RuntimeException("Method " + call.name + call.getDescriptor() + " not found in class " + name);
		return method;
	}
}