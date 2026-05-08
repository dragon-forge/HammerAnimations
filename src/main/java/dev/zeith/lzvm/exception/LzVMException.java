package dev.zeith.lzvm.exception;

public class LzVMException
		extends RuntimeException
{
	public LzVMException()
	{
	}
	
	public LzVMException(String message)
	{
		super(message);
	}
	
	public LzVMException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public LzVMException(Throwable cause)
	{
		super(cause);
	}
	
	public LzVMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
