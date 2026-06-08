package dev.zeith.lzvm.vm.jvm;

import dev.zeith.lzvm.program.*;
import dev.zeith.lzvm.vm.*;

import java.lang.reflect.Method;

public class JvmJClass
		implements JClass
{
	protected final Class<?> jvm;
	
	public JvmJClass(Class<?> jvm)
	{
		this.jvm = jvm;
	}
	
	@Override
	public JMethod getDeclaredMethod(LzCallInsn call)
	{
		try
		{
			Class<?>[] params = ArgType.toJavaArgs(call.argTypes);
			Method m = jvm.getDeclaredMethod(call.name, params);
			if(!m.isAccessible())
				try
				{
					m.setAccessible(true);
				} catch(Exception ignored) {}  // Can't make it accessible :(
			return new JvmJMethod(m, call.returnType);
		} catch(ReflectiveOperationException ignored) {}
		return null;
	}
}