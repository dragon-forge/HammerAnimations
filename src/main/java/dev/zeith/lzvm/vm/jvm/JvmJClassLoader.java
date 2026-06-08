package dev.zeith.lzvm.vm.jvm;

import dev.zeith.lzvm.vm.*;

public class JvmJClassLoader
		implements JClassLoader
{
	protected final ClassLoader loader;
	
	public JvmJClassLoader(ClassLoader loader)
	{
		this.loader = loader;
	}
	
	@Override
	public JClass loadClass(String className)
	{
		try
		{
			return new JvmJClass(loader.loadClass(className));
		} catch(ClassNotFoundException e)
		{
			return null;
		}
	}
}