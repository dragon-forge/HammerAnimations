package shaded.event;

public class Event
{
	private final boolean isCancelable = this instanceof Cancelable;
	
	private boolean isCanceled = false;
	
	public boolean isCancelable()
	{
		return isCancelable;
	}
	
	public boolean isCanceled()
	{
		return isCanceled;
	}
	
	public void setCanceled(boolean cancel)
	{
		if(!isCancelable())
		{
			throw new UnsupportedOperationException(
					"Attempted to call Event#setCanceled() on a non-cancelable event of type: "
					+ this.getClass().getCanonicalName()
			);
		}
		isCanceled = cancel;
	}
}