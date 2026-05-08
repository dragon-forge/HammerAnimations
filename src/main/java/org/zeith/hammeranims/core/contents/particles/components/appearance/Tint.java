package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.LzFactory;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;

import java.util.*;

public abstract class Tint
{
	/**
	 * Parse a single color either in hex string format or JSON array
	 * (this should parse both RGB and RGBA expressions)
	 */
	public static Tint.Solid parseColor(JsonElement element)
	{
		LzFactory r = InterpolatedDouble.one();
		LzFactory g = InterpolatedDouble.one();
		LzFactory b = InterpolatedDouble.one();
		LzFactory a = InterpolatedDouble.one();
		
		if(element.isJsonPrimitive())
		{
			String hex = element.getAsString();
			
			if(hex.startsWith("#") && (hex.length() == 7 || hex.length() == 9))
			{
				boolean hasAlpha = hex.length() == 9;
				
				try
				{
					/* Apparently, Integer.parseInt() can't parse hex
					 * numbers that are longer than 6 hexadecimals... */
					int color = Integer.parseInt(hex.substring(hasAlpha ? 3 : 1), 16);
					float hr = (color >> 16 & 0xff) / 255F;
					float hg = (color >> 8 & 0xff) / 255F;
					float hb = (color & 0xff) / 255F;
					float ha = hasAlpha ? Integer.parseInt(hex.substring(1, 3), 16) / 255F : 1;
					
					r = InterpolatedDouble.constant(hr);
					g = InterpolatedDouble.constant(hg);
					b = InterpolatedDouble.constant(hb);
					a = InterpolatedDouble.constant(ha);
				} catch(Exception e)
				{
				}
			}
		} else if(element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();
			
			if(array.size() == 3 || array.size() == 4)
			{
				r = InterpolatedDouble.parse(array.get(0));
				g = InterpolatedDouble.parse(array.get(1));
				b = InterpolatedDouble.parse(array.get(2));
				if(array.size() == 4) a = InterpolatedDouble.parse(array.get(3));
			}
		}
		
		return new Tint.Solid(r, g, b, a);
	}
	
	/**
	 * Parse a gradient
	 */
	public static Tint parseGradient(JsonObject color)
	{
		JsonElement gradient = color.get("gradient");
		
		LzFactory expression = InterpolatedDouble.zero();
		List<Gradient.ColorStop> colorStops = new ArrayList<>();
		boolean equal = true;
		
		if(gradient.isJsonObject())
		{
			for(Map.Entry<String, JsonElement> entry : gradient.getAsJsonObject().entrySet())
			{
				Tint.Solid stopColor = parseColor(entry.getValue());
				
				colorStops.add(new Tint.Gradient.ColorStop(Float.parseFloat(entry.getKey()), stopColor));
			}
			
			colorStops.sort((a, b) -> Float.compare(a.stop, b.stop));
			equal = false;
		} else if(gradient.isJsonArray())
		{
			JsonArray colors = gradient.getAsJsonArray();
			
			int i = 0;
			
			for(JsonElement stop : colors)
			{
				colorStops.add(new Tint.Gradient.ColorStop(i / (float) (colors.size() - 1), parseColor(stop)));
				
				i++;
			}
		}
		
		float range = colorStops.get(colorStops.size() - 1).stop;
		
		for(Gradient.ColorStop stop : colorStops)
		{
			stop.stop /= range;
		}
		
		if(color.has("interpolant"))
		{
			expression = InterpolatedDouble.parse(color.get("interpolant"));
		}
		
		return new Tint.Gradient(colorStops, range, expression, equal);
	}
	
	public abstract TintInstance newInstance(LzVariableStore vars);
	
	/**
	 * Solid color (not necessarily static)
	 */
	public static class Solid
			extends Tint
	{
		public LzFactory r;
		public LzFactory g;
		public LzFactory b;
		public LzFactory a;
		
		public Solid(LzFactory r, LzFactory g, LzFactory b, LzFactory a)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
		
		public Solid()
		{
			this.r = InterpolatedDouble.one();
			this.g = InterpolatedDouble.one();
			this.b = InterpolatedDouble.one();
			this.a = InterpolatedDouble.one();
		}
		
		@Override
		public TintInstance.SolidInstance newInstance(LzVariableStore vars)
		{
			return new TintInstance.SolidInstance(
					r.instantiate(vars),
					g.instantiate(vars),
					b.instantiate(vars),
					a.instantiate(vars)
			);
		}
	}
	
	/**
	 * Gradient color, instead of using formulas, you can just specify a couple of colors
	 * and an expression at which color it would stop
	 */
	public static class Gradient
			extends Tint
	{
		public List<ColorStop> stops;
		public LzFactory interpolant;
		public float range = 1;
		public boolean equal;
		
		public Gradient(List<ColorStop> stops, float range, LzFactory interpolant, boolean equal)
		{
			this.stops = stops;
			this.range = range;
			this.interpolant = interpolant;
			this.equal = equal;
		}
		
		public Gradient()
		{
			this.stops = new ArrayList<>();
			this.stops.add(new ColorStop(0, new Tint.Solid(InterpolatedDouble.constant(1F), InterpolatedDouble.constant(1F), InterpolatedDouble.constant(1F), InterpolatedDouble.constant(1F))));
			this.stops.add(new ColorStop(1, new Tint.Solid(InterpolatedDouble.constant(0F), InterpolatedDouble.constant(0F), InterpolatedDouble.constant(0F), InterpolatedDouble.constant(1F))));
			this.interpolant = InterpolatedDouble.zero();
			this.equal = false;
		}
		
		public void sort()
		{
			this.stops.sort((a, b) -> Float.compare(a.stop, b.stop));
		}
		
		@Override
		public TintInstance newInstance(LzVariableStore vars)
		{
			return new TintInstance.GradientInstance(
					stops.stream().map(st -> st.newInstance(vars)).toList(),
					interpolant.instantiate(vars),
					range,
					equal
			);
		}
		
		public static class ColorStop
		{
			public float stop;
			public Tint.Solid color;
			
			public ColorStop(float stop, Tint.Solid color)
			{
				this.stop = stop;
				this.color = color;
			}
			
			public TintInstance.GradientInstance.ColorStopInstance newInstance(LzVariableStore vars)
			{
				return new TintInstance.GradientInstance.ColorStopInstance(stop, color.newInstance(vars));
			}
		}
	}
}