package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public interface IParticlePreRender
		extends IParticleComponent
{
	void preRender(ParticleEmitter emitter, float partialTicks);
}