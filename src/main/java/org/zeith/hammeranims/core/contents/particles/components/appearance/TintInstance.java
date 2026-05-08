package org.zeith.hammeranims.core.contents.particles.components.appearance;

import dev.zeith.lzvm.jvm.LzExpression;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.joml.Math;

import java.util.List;

public abstract class TintInstance
{
	public abstract void compute(BedrockParticle particle);
	
	public static class SolidInstance
			extends TintInstance
	{
		public LzExpression r;
		public LzExpression g;
		public LzExpression b;
		public LzExpression a;
		
		public SolidInstance(LzExpression r, LzExpression g, LzExpression b, LzExpression a)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
		
		public SolidInstance()
		{
			this.r = InterpolatedDouble.one();
			this.g = InterpolatedDouble.one();
			this.b = InterpolatedDouble.one();
			this.a = InterpolatedDouble.one();
		}
		
		@Override
		public void compute(BedrockParticle particle)
		{
			particle.r = (float) this.r.get();
			particle.g = (float) this.g.get();
			particle.b = (float) this.b.get();
			particle.a = (float) this.a.get();
		}
		
		public void lerp(BedrockParticle particle, float factor)
		{
			particle.r = org.zeith.hammeranims.joml.Math.lerp(particle.r, (float) this.r.get(), factor);
			particle.g = org.zeith.hammeranims.joml.Math.lerp(particle.g, (float) this.g.get(), factor);
			particle.b = org.zeith.hammeranims.joml.Math.lerp(particle.b, (float) this.b.get(), factor);
			particle.a = Math.lerp(particle.a, (float) this.a.get(), factor);
		}
	}
	
	public static class GradientInstance
			extends TintInstance
	{
		public List<ColorStopInstance> stops;
		public LzExpression interpolant;
		public float range = 1;
		public boolean equal;
		
		public GradientInstance(List<ColorStopInstance> stops, LzExpression interpolant, float range, boolean equal)
		{
			this.stops = stops;
			this.interpolant = interpolant;
			this.range = range;
			this.equal = equal;
		}
		
		@Override
		public void compute(BedrockParticle particle)
		{
			int length = this.stops.size();
			
			if(length == 0)
			{
				particle.r = particle.g = particle.b = particle.a = 1;
				
				return;
			} else if(length == 1)
			{
				this.stops.get(0).color.compute(particle);
				
				return;
			}
			
			double factor = this.interpolant.get();
			
			factor = Math.clamp(0, 1, factor);
			
			ColorStopInstance prev = this.stops.get(0);
			
			if(factor < prev.getStop(this.range))
			{
				prev.color.compute(particle);
				
				return;
			}
			
			for(int i = 1; i < length; i++)
			{
				ColorStopInstance stop = this.stops.get(i);
				
				if(stop.getStop(this.range) > factor)
				{
					prev.color.compute(particle);
					stop.color.lerp(particle, (float) (factor - prev.getStop(this.range)) / (stop.getStop(this.range) - prev.getStop(this.range)));
					
					return;
				}
				
				prev = stop;
			}
			
			prev.color.compute(particle);
		}
		
		public static class ColorStopInstance
		{
			public float stop;
			public SolidInstance color;
			
			public ColorStopInstance(float stop, SolidInstance color)
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