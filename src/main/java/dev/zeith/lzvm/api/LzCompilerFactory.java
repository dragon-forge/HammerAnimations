package dev.zeith.lzvm.api;

import java.util.Set;

public interface LzCompilerFactory
{
	LzCompiler newAssembler();
	
	Set<String> getLanguages();
}