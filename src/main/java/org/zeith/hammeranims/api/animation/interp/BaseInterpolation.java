package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.json.*;
import org.zeith.hammeranims.HammerAnimations;

public abstract class BaseInterpolation
{
	public abstract int getDoubleCount();
	
	public abstract LzExpression[] instantiate(LzVariableStore query);
	
	public static BaseInterpolation parse(Object o)
	{
		if(o instanceof Number || o instanceof String)
		{
			LzFactory id = InterpolatedDouble.parse(o);
			if(id == null) return null;
			return new DoubleInterpolation(id);
		}
		
		if(o instanceof JSONArray)
		{
			JSONArray arr = (JSONArray) o;
			LzFactory[] ids = new LzFactory[arr.length()];
			for(int i = 0; i < ids.length; i++)
			{
				ids[i] = InterpolatedDouble.parse(arr.get(i));
				if(ids[i] == null) return null;
			}
			return new DoubleInterpolation(ids);
		}
		
		if(o instanceof JSONObject)
		{
			JSONObject obj = (JSONObject) o;
			
			boolean allDoubles = true;
			for(String key : obj.keySet())
			{
				try
				{
					Double.parseDouble(key);
				} catch(NumberFormatException f)
				{
					allDoubles = false;
					break;
				}
			}
			
			if(allDoubles)
			{
				return KeyframeInterpolation.parse(3, obj);
			} else
			{
				HammerAnimations.LOG.error("Unknown interpolation type: " + obj);
			}
		}
		
		return null;
	}
}