package org.zeith.hammeranims.core.impl.api.particles.components.shape;

import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.joml.Vector3d;

public abstract class ShapeDirection
{
	public static final ShapeDirection INWARDS = new Inwards(-1);
	public static final ShapeDirection OUTWARDS = new Inwards(1);
	
	public abstract void applyDirection(BedrockParticle particle, double x, double y, double z);
	
	private static class Inwards
			extends ShapeDirection
	{
		private float factor;
		
		public Inwards(float factor)
		{
			this.factor = factor;
		}
		
		@Override
		public void applyDirection(BedrockParticle particle, double x, double y, double z)
		{
			Vector3d vector = new Vector3d(particle.position);
			
			vector.sub(new Vector3d(x, y, z));
			
			if(vector.length() <= 0)
			{
				vector.set(0, 0, 0);
			} else
			{
				vector.normalize();
				vector.mul(this.factor);
			}
			
			particle.speed.set(vector);
		}
	}
	
	public static class Vector
			extends ShapeDirection
	{
		public InterpolatedDouble<ParticleVariables> x, y, z;
		
		public Vector(InterpolatedDouble<ParticleVariables> x, InterpolatedDouble<ParticleVariables> y, InterpolatedDouble<ParticleVariables> z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public void applyDirection(BedrockParticle particle, double x, double y, double z)
		{
			ParticleVariables v = particle.owner.vars;
			particle.speed.set((float) this.x.get(v), (float) this.y.get(v), (float) this.z.get(v));
			
			if(particle.speed.length() <= 0)
			{
				particle.speed.set(0, 0, 0);
			} else
			{
				particle.speed.normalize();
			}
		}
	}
}