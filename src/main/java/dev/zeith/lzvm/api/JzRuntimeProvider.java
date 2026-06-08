package dev.zeith.lzvm.api;

import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.program.LzProgramBody;

public interface JzRuntimeProvider<T extends JzRuntimeProvider<T>>
{
	LzFactory expression(LzProgramBody body);
	
	LzJCallShutter getJCallShutter();
	
	void setJCallShutter(LzJCallShutter jcallShutter);
	
	boolean isUseSineLookupTable();
	
	void setUseSineLookupTable(boolean useSineLookupTable);
	
	@SuppressWarnings("unchecked")
	default T addJCallShutter(LzJCallShutter shutter)
	{
		if(getJCallShutter() == LzJCallShutter.ALLOW_EVERYTHING)
		{
			setJCallShutter(shutter);
			return (T) this;
		}
		setJCallShutter(getJCallShutter().or(shutter));
		return (T) this;
	}
}