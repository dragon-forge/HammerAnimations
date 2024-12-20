package org.zeith.hammeranims.api.time;

import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.util.Objects;

public class TimeFunctionInstance
{
	public static final TimeFunctionInstance EMPTY = new TimeFunctionInstance(DefaultsHA.LINEAR_TIME);
	
	protected final TimeFunction function;
	
	public TimeFunctionInstance(TimeFunction function)
	{
		this.function = function;
	}
	
	public final boolean isEmpty()
	{
		return function == DefaultsHA.LINEAR_TIME;
	}
	
	public double getTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation)
	{
		return function.getTime(system, sysTime, partialTicks, animation, this);
	}
	
	public double getLengthSeconds(ActiveAnimation animation)
	{
		return function.getLengthSeconds(animation, this);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		TimeFunctionInstance that = (TimeFunctionInstance) o;
		return Objects.equals(function, that.function);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(function);
	}
}