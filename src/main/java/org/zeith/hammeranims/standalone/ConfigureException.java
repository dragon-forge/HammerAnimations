package org.zeith.hammeranims.standalone;

public class ConfigureException
		extends RuntimeException
{
	public ConfigureException(String message)
	{
		super(message);
	}
	
	public ConfigureException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
