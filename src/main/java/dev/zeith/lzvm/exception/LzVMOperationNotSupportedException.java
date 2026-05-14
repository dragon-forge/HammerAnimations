package dev.zeith.lzvm.exception;

public class LzVMOperationNotSupportedException
		extends LzVMException
{
	public LzVMOperationNotSupportedException()
	{
	}
	
	public LzVMOperationNotSupportedException(String message)
	{
		super(message);
	}
	
	public LzVMOperationNotSupportedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public LzVMOperationNotSupportedException(Throwable cause)
	{
		super(cause);
	}
	
	public LzVMOperationNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
