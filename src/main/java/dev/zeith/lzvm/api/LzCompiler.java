package dev.zeith.lzvm.api;

import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.program.LzProgramBody;
import org.jetbrains.annotations.*;

import java.util.Set;

public interface LzCompiler
{
	LzProgramBody parseAndCompile(String expression);
	
	LzFactory parseFactory(LzJvmCompiler compiler, String expression, IClassDefiner definer);
	
	void includeRequiredClasses(Set<String> intoSet);
	
	void setOptimize(boolean optimize);
	
	@NotNull
	Set<String> getKnownOptions();
	
	@Nullable
	Set<String> getValidOptions(String optionName);
	
	void setOption(String optionName, @Nullable String value);
	
	@Nullable
	String getOption(String optionName);
}