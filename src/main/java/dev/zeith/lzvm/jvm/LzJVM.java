package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.program.LzProgramBody;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class LzJVM
{
	@SneakyThrows
	public static LzFactory compile(LzJvmCompiler compiler, LzProgramBody program, int argCount, IClassDefiner definer)
	{
		byte[] bytecode = compiler.compile(LzExpression.class.getName() + "_" + UUID.randomUUID().toString().replace('-', '_'), program, argCount);
		return (LzFactory) definer
				.defineClass(bytecode)
				.getDeclaredConstructor()
				.newInstance();
	}
	
	public static class LzClassLoader
			extends ClassLoader
			implements IClassDefiner
	{
		public LzClassLoader()
		{
			super(Thread.currentThread().getContextClassLoader());
		}
		
		public LzClassLoader(ClassLoader parent)
		{
			super(parent);
		}
		
		// @Override
		public String getName()
		{
			return "LzClassLoader";
		}
		
		@Override
		public Class<?> defineClass(byte[] bytecode)
		{
			return defineClass(null, bytecode, 0, bytecode.length);
		}
	}
}