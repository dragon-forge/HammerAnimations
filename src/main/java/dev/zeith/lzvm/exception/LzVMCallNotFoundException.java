package dev.zeith.lzvm.exception;

public class LzVMCallNotFoundException
		extends LzVMException
{
	public LzVMCallNotFoundException()
	{
	}
	
	public LzVMCallNotFoundException(String message)
	{
		super(message);
	}
	
	public LzVMCallNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public LzVMCallNotFoundException(Throwable cause)
	{
		super(cause);
	}
	
	public LzVMCallNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
