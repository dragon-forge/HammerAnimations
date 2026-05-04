package org.zeith.hammeranims.molang.runtime.struct;

import com.google.common.collect.Iterators;
import org.zeith.hammeranims.api.animation.interp.IVariableStorage;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.Iterator;

public interface MoStruct
		extends MoValue, IVariableStorage
{
	MoLangEnvironment getEnvironment();
	
	void set(Iterator<String> names, MoValue value);
	
	MoValue get(Iterator<String> names);
	
	MoValue get(Iterator<String> names, MoParams params);
	
	void clear();
	
	@Override
	default MoStruct value()
	{
		return this;
	}
	
	@Override
	default void store(String[] key, double value)
	{
		set(Iterators.forArray(key), MoValue.of(value));
	}
	
	@Override
	default void store(String[] key, String value)
	{
		set(Iterators.forArray(key), MoValue.of(value));
	}
}
