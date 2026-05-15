package org.zeith.hammeranims.core.contents.particles.components.shape;

import dev.zeith.lzvm.jvm.LzExpression;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.joml.Vector3d;

public abstract class ShapeDirection
{
	public static final ShapeDirection INWARDS = new Inwards(-1);
	public static final ShapeDirection OUTWARDS = new Inwards(1);
	
	public abstract void applyDirection(BedrockParticle particle, double x, double y, double z);
	
	public static class Inwards
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
		public LzExpression x, y, z;
		
		public Vector(LzExpression x, LzExpression y, LzExpression z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public void applyDirection(BedrockParticle particle, double x, double y, double z)
		{
			particle.speed.set((float) this.x.get(), (float) this.y.get(), (float) this.z.get());
			
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