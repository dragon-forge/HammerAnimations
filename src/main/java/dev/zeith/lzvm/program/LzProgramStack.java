package dev.zeith.lzvm.program;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LzProgramStack
{
	public final Object[] stack;
	public final Object[] locals;
	public final int inputArgCount;
	
	public LzProgramStack fillArgs(Object... args)
	{
		System.arraycopy(args, 0, locals, 0, args.length);
		return this;
	}
}