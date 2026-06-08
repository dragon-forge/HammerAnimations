package dev.zeith.lzvm.vm;

import dev.zeith.lzvm.program.LzCallInsn;

public interface JClass
{
	JMethod getDeclaredMethod(LzCallInsn call);
}
