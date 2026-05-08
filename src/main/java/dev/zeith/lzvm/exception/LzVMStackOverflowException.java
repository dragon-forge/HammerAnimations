package dev.zeith.lzvm.exception;

public class LzVMStackOverflowException
		extends LzVMException
{
	public LzVMStackOverflowException()
	{
	}
	
	public LzVMStackOverflowException(String message)
	{
		super(message);
	}
	
	public LzVMStackOverflowException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public LzVMStackOverflowException(Throwable cause)
	{
		super(cause);
	}
	
	public LzVMStackOverflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
