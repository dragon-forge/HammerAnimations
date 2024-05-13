package org.zeith.hammeranims.core.impl.api.particles.components.lifetime;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomLifetimeOnce
		extends ParcomLifetime
{
	public ParcomLifetimeOnce(JsonElement elem)
	{
		super(elem);
	}
	
	@Override
	public void update(ParticleEmitter emitter)
	{
		double time = this.activeTime.get(emitter.vars);
		
		emitter.lifetime = (int) (time * 20);
		
		if(emitter.getAge() >= time)
		{
			emitter.stop();
		}
	}
}