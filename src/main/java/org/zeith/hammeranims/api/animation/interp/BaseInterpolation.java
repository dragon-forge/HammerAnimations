package org.zeith.hammeranims.api.animation.interp;

import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammerlib.util.shaded.json.*;

public abstract class BaseInterpolation
{
	public abstract int getDoubleCount();
	
	public abstract double[] get(Query query);
	
	public static BaseInterpolation parse(Object o)
	{
		if(o instanceof Number || o instanceof String)
		{
			InterpolatedDouble id = InterpolatedDouble.parse(o);
			if(id == null) return null;
			return new DoubleInterpolation(id);
		}
		
		if(o instanceof JSONArray)
		{
			JSONArray arr = (JSONArray) o;
			InterpolatedDouble[] ids = new InterpolatedDouble[arr.length()];
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
				HammerAnimations.LOG.warn("Unknown interpolation type: " + obj);
			}
		}
		
		return null;
	}
}