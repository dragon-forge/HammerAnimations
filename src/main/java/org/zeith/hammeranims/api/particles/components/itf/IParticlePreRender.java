package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public interface IParticlePreRender
		extends IParticleCompInstance
{
	void preRender(ParticleEmitter emitter, float partialTicks);
}