package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.JsonElement;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomShapePoint
		extends ParcomShapeBase
{
	public ParcomShapePoint(JsonElement elem)
	{
		super(elem);
	}
	
	@Override
	public ParcomShapeBaseInstance createInstance(LzVariableStore vars)
	{
		return new ParcomShapePointInstance(
				LzFactory.instantiate(vars, offset),
				direction.apply(vars),
				surface
		);
	}
	
	public static class ParcomShapePointInstance
			extends ParcomShapeBaseInstance
	{
		public ParcomShapePointInstance(LzExpression[] offset, ShapeDirection direction, boolean surface)
		{
			super(offset, direction, surface);
		}
		
		@Override
		public void apply(ParticleEmitter emitter, BedrockParticle particle)
		{
			particle.position.x = (float) this.offset[0].get();
			particle.position.y = (float) this.offset[1].get();
			particle.position.z = (float) this.offset[2].get();
			
			if(this.direction instanceof ShapeDirection.Vector)
			{
				this.direction.applyDirection(particle, particle.position.x, particle.position.y, particle.position.z);
			}
		}
	}
}
