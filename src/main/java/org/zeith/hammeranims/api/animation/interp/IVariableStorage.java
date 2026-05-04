package org.zeith.hammeranims.api.animation.interp;

public interface IVariableStorage
{
	void store(String key, double value);
	
	void store(String key, String value);
	
	void store(String[] key, double value);
	
	void store(String[] key, String value);
}