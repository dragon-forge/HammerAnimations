package dev.zeith.lzvm.vm.jvm;

import dev.zeith.lzvm.program.ArgType;
import dev.zeith.lzvm.vm.JMethod;

import java.lang.reflect.*;

public class JvmJMethod
		implements JMethod
{
	protected final Method jvm;
	protected final ArgType returnType;
	
	public JvmJMethod(Method jvm, ArgType returnType)
	{
		this.jvm = jvm;
		this.returnType = returnType;
	}
	
	@Override
	public Object invoke(Object... args)
	{
		try
		{
			return jvm.invoke(null, args);
		} catch(InvocationTargetException e)
		{
			throwAsUnchecked(e.getCause());
			return null;
		} catch(ReflectiveOperationException e)
		{
			return null;
		}
	}
	
	public static <E extends Throwable> void throwAsUnchecked(Throwable exception)
			throws E
	{
		throw (E) exception;
	}
}
