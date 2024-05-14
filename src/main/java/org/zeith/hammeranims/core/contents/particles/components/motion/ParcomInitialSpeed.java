package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomInitialSpeed
		implements IParticleInitialize
{
	public InterpolatedDouble<ParticleVariables> speed = InterpolatedDouble.one();
	public InterpolatedDouble<ParticleVariables>[] direction;
	
	public ParcomInitialSpeed(JsonElement element)
	{
		if(element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();
			
			if(array.size() >= 3)
			{
				this.direction = new InterpolatedDouble[] {
						InterpolatedDouble.parse(array.get(0)),
						InterpolatedDouble.parse(array.get(1)),
						InterpolatedDouble.parse(array.get(2))
				};
			}
		} else if(element.isJsonPrimitive())
		{
			this.speed = InterpolatedDouble.parse(element);
		}
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(this.direction == null)
		{
			float v = (float) this.speed.get(emitter.vars);
			particle.speed.mul(v);
			return;
		}
		
		ParticleVariables v = emitter.vars;
		particle.speed.set(
				(float) this.direction[0].get(v),
				(float) this.direction[1].get(v),
				(float) this.direction[2].get(v)
		);
	}
	
	@Override
	public int getSortingIndex()
	{
		return 5;
	}
}