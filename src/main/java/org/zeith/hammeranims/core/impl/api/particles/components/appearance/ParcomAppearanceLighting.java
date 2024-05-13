package org.zeith.hammeranims.core.impl.api.particles.components.appearance;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterInitialize;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomAppearanceLighting
		implements IEmitterInitialize
{
	public ParcomAppearanceLighting(JsonElement element)
	{
	}
	
	@Override
	public void apply(ParticleEmitter emitter)
	{
		emitter.lit = false;
	}
}