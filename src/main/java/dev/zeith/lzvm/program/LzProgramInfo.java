package dev.zeith.lzvm.program;

import lombok.*;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LzProgramInfo
{
	public final int maxStack;
	public final int maxLocals;
	public final int labelCount;
	
	public LzProgramStack mallocStack(int argCount)
	{
		return new LzProgramStack(
				new Object[maxStack],
				new Object[argCount + maxLocals],
				argCount
		);
	}
}