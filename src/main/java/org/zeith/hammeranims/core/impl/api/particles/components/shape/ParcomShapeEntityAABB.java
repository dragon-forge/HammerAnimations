package org.zeith.hammeranims.core.impl.api.particles.components.shape;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomShapeEntityAABB
		extends ParcomShapeBase
{
	public ParcomShapeEntityAABB(JsonElement elem)
	{
		super(elem);
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		float centerX = (float) this.offset[0].get(v);
		float centerY = (float) this.offset[1].get(v);
		float centerZ = (float) this.offset[2].get(v);
		
		float w = 0;
		float h = 0;
		float d = 0;
		
		if(emitter.target != null)
		{
			w = emitter.target.getAnimatedObjectWidth();
			h = emitter.target.getAnimatedObjectHeight();
			d = emitter.target.getAnimatedObjectDepth();
		}
		
		particle.position.x = centerX + ((float) Math.random() - 0.5F) * w;
		particle.position.y = centerY + ((float) Math.random() - 0.5F) * h;
		particle.position.z = centerZ + ((float) Math.random() - 0.5F) * d;
		
		if(this.surface)
		{
			int roll = (int) (Math.random() * 6 * 100) % 6;
			
			if(roll == 0) particle.position.x = centerX + w / 2F;
			else if(roll == 1) particle.position.x = centerX - w / 2F;
			else if(roll == 2) particle.position.y = centerY + h / 2F;
			else if(roll == 3) particle.position.y = centerY - h / 2F;
			else if(roll == 4) particle.position.z = centerZ + d / 2F;
			else if(roll == 5) particle.position.z = centerZ - d / 2F;
		}
		
		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}