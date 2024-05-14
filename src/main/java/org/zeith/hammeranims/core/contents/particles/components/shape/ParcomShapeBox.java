package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomShapeBox
		extends ParcomShapeBase
{
	@SuppressWarnings("rawtypes")
	public InterpolatedDouble[] halfDimensions = { InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero() };
	
	public ParcomShapeBox(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(!element.has("half_dimensions")) return;
		JsonArray array = element.getAsJsonArray("half_dimensions");
		
		if(array.size() >= 3)
		{
			this.halfDimensions[0] = InterpolatedDouble.parse(array.get(0));
			this.halfDimensions[1] = InterpolatedDouble.parse(array.get(1));
			this.halfDimensions[2] = InterpolatedDouble.parse(array.get(2));
		}
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		
		float centerX = (float) this.offset[0].get(v);
		float centerY = (float) this.offset[1].get(v);
		float centerZ = (float) this.offset[2].get(v);
		
		float w = (float) this.halfDimensions[0].get(v);
		float h = (float) this.halfDimensions[1].get(v);
		float d = (float) this.halfDimensions[2].get(v);
		
		particle.position.x = centerX + ((float) Math.random() * 2 - 1F) * w;
		particle.position.y = centerY + ((float) Math.random() * 2 - 1F) * h;
		particle.position.z = centerZ + ((float) Math.random() * 2 - 1F) * d;
		
		if(this.surface)
		{
			int roll = (int) (Math.random() * 6 * 100) % 6;
			
			if(roll == 0) particle.position.x = centerX + w;
			else if(roll == 1) particle.position.x = centerX - w;
			else if(roll == 2) particle.position.y = centerY + h;
			else if(roll == 3) particle.position.y = centerY - h;
			else if(roll == 4) particle.position.z = centerZ + d;
			else if(roll == 5) particle.position.z = centerZ - d;
		}
		
		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}
