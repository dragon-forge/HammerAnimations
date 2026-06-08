package dev.zeith.lzvm.molang.spi;

import dev.zeith.lzvm.api.*;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;

import java.util.*;

public class MoLangLzCompilerFactory
		implements LzCompilerFactory
{
	private static final Set<String> LANGUAGE_NAMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"molang",
			"molang++"
	)));
	
	@Override
	public LzCompiler newAssembler()
	{
		return new MoLangCompiler();
	}
	
	@Override
	public Set<String> getLanguages()
	{
		return LANGUAGE_NAMES;
	}
}
