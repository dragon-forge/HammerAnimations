package dev.zeith.lzvm.program;

import lombok.*;
import org.jetbrains.annotations.*;

@ToString
@EqualsAndHashCode
public class LzProgram
{
	public final @NotNull String name;
	public final @NotNull LzProgramInfo info;
	public final @NotNull LzProgramBody body;
	
	public LzProgram(String name, LzProgramBody body)
	{
		this(name, null, body);
	}
	
	@Builder
	public LzProgram(@NotNull String name, @Nullable LzProgramInfo info, @NotNull LzProgramBody body)
	{
		this.name = name;
		this.info = info != null ? info : body.computeInfo();
		this.body = body;
	}
}