package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.program.LzCallInsn;

public interface LzJCallShutter
{
	LzJCallShutter ALLOW_EVERYTHING = (owner, call) -> true;
	
	boolean permits(String className, LzCallInsn call);
	
	default LzJCallShutter and(LzJCallShutter other)
	{
		LzJCallShutter dis = this;
		return (className, call) -> dis.permits(className, call) && other.permits(className, call);
	}
	
	default LzJCallShutter or(LzJCallShutter other)
	{
		LzJCallShutter dis = this;
		return (className, call) -> dis.permits(className, call) || other.permits(className, call);
	}
}