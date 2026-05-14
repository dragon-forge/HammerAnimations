package org.zeith.hammeranims.standalone.jvm;

import dev.zeith.lzvm.program.LzCallInsn;

public interface JvmClass
{
	JvmMethod getDeclaredMethod(LzCallInsn call);
}