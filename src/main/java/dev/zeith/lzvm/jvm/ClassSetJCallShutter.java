package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.program.LzCallInsn;

import java.util.Set;

public class ClassSetJCallShutter
		implements LzJCallShutter
{
	private final Set<String> permittedClasses;
	
	public ClassSetJCallShutter(Set<String> permittedClasses)
	{
		this.permittedClasses = permittedClasses;
	}
	
	@Override
	public boolean permits(String className, LzCallInsn call)
	{
		return permittedClasses.contains(className);
	}
}