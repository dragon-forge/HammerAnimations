package dev.zeith.lzvm.program;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ArgType
{
	DOUBLE("D", "D", double.class),
	STRING("S", "Ljava/lang/String;", String.class),
	;
	
	public final String desc, jvmDesc;
	public final Class<?> javaType;
	
	private static final ArgType[] ARGS = values();
	
	public static ArgType byOrdinal(int ordinal)
	{
		return ARGS[ordinal];
	}
	
	public static Class<?>[] toJavaArgs(ArgType... argTypes)
	{
		Class<?>[] javaArgs = new Class<?>[argTypes.length];
		for(int i = 0; i < argTypes.length; i++)
			javaArgs[i] = argTypes[i].javaType;
		return javaArgs;
	}
	
	public static String descriptor(ArgType returnType, ArgType... argTypes)
	{
		StringBuilder sb = new StringBuilder("(");
		for(ArgType at : argTypes) sb.append(at.desc);
		return sb.append(")").append(returnType.desc).toString();
	}
	
	public static String jvmDescriptor(ArgType returnType, ArgType... argTypes)
	{
		StringBuilder sb = new StringBuilder("(");
		for(ArgType at : argTypes) sb.append(at.jvmDesc);
		return sb.append(")").append(returnType.jvmDesc).toString();
	}
}
