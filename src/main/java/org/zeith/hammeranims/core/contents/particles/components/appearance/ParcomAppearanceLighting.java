package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.components.INonAnimatedParticleComponent;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterInitialize;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomAppearanceLighting
		implements INonAnimatedParticleComponent, IEmitterInitialize
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