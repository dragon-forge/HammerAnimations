package dev.zeith.lzvm.exception;

public class LzVMStackUnderflowException
		extends LzVMException
{
	public LzVMStackUnderflowException()
	{
	}
	
	public LzVMStackUnderflowException(String message)
	{
		super(message);
	}
	
	public LzVMStackUnderflowException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public LzVMStackUnderflowException(Throwable cause)
	{
		super(cause);
	}
	
	public LzVMStackUnderflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
