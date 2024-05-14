package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.joml.Math;

import java.util.*;

public abstract class Tint
{
	/**
	 * Parse a single color either in hex string format or JSON array
	 * (this should parse both RGB and RGBA expressions)
	 */
	public static Tint.Solid parseColor(JsonElement element)
	{
		InterpolatedDouble<ParticleVariables> r = InterpolatedDouble.one();
		InterpolatedDouble<ParticleVariables> g = InterpolatedDouble.one();
		InterpolatedDouble<ParticleVariables> b = InterpolatedDouble.one();
		InterpolatedDouble<ParticleVariables> a = InterpolatedDouble.one();
		
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
		
		InterpolatedDouble<ParticleVariables> expression = InterpolatedDouble.zero();
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
	
	public abstract void compute(ParticleVariables vars, BedrockParticle particle);
	
	/**
	 * Solid color (not necessarily static)
	 */
	public static class Solid
			extends Tint
	{
		public InterpolatedDouble<ParticleVariables> r;
		public InterpolatedDouble<ParticleVariables> g;
		public InterpolatedDouble<ParticleVariables> b;
		public InterpolatedDouble<ParticleVariables> a;
		
		public Solid(InterpolatedDouble<ParticleVariables> r, InterpolatedDouble<ParticleVariables> g, InterpolatedDouble<ParticleVariables> b, InterpolatedDouble<ParticleVariables> a)
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
		public void compute(ParticleVariables vars, BedrockParticle particle)
		{
			particle.r = (float) this.r.get(vars);
			particle.g = (float) this.g.get(vars);
			particle.b = (float) this.b.get(vars);
			particle.a = (float) this.a.get(vars);
		}
		
		public void lerp(BedrockParticle particle, float factor, ParticleVariables vars)
		{
			particle.r = Math.lerp(particle.r, (float) this.r.get(vars), factor);
			particle.g = Math.lerp(particle.g, (float) this.g.get(vars), factor);
			particle.b = Math.lerp(particle.b, (float) this.b.get(vars), factor);
			particle.a = Math.lerp(particle.a, (float) this.a.get(vars), factor);
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
		public InterpolatedDouble<ParticleVariables> interpolant;
		public float range = 1;
		public boolean equal;
		
		public Gradient(List<ColorStop> stops, float range, InterpolatedDouble<ParticleVariables> interpolant, boolean equal)
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
		public void compute(ParticleVariables vars, BedrockParticle particle)
		{
			int length = this.stops.size();
			
			if(length == 0)
			{
				particle.r = particle.g = particle.b = particle.a = 1;
				
				return;
			} else if(length == 1)
			{
				this.stops.get(0).color.compute(vars, particle);
				
				return;
			}
			
			double factor = this.interpolant.get(vars);
			
			factor = Math.clamp(0, 1, factor);
			
			ColorStop prev = this.stops.get(0);
			
			if(factor < prev.getStop(this.range))
			{
				prev.color.compute(vars, particle);
				
				return;
			}
			
			for(int i = 1; i < length; i++)
			{
				ColorStop stop = this.stops.get(i);
				
				if(stop.getStop(this.range) > factor)
				{
					prev.color.compute(vars, particle);
					stop.color.lerp(particle, (float) (factor - prev.getStop(this.range)) / (stop.getStop(this.range) - prev.getStop(this.range)), vars);
					
					return;
				}
				
				prev = stop;
			}
			
			prev.color.compute(vars, particle);
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
			
			public float getStop(float range)
			{
				return this.stop * range;
			}
		}
	}
}