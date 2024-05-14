package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomShapePoint
		extends ParcomShapeBase
{
	public ParcomShapePoint(JsonElement elem)
	{
		super(elem);
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		particle.position.x = (float) this.offset[0].get(v);
		particle.position.y = (float) this.offset[1].get(v);
		particle.position.z = (float) this.offset[2].get(v);
		
		if(this.direction instanceof ShapeDirection.Vector)
		{
			this.direction.applyDirection(particle, particle.position.x, particle.position.y, particle.position.z);
		}
	}
}
